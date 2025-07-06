<template>
  <el-card>
    <div class="audit-header">
      <el-input v-model="search" placeholder="搜索课程名/创建人" clearable style="width: 240px; margin-right: 16px;" @keyup.enter="loadCourses" />
      <el-select v-model="status" placeholder="状态" clearable style="width: 120px; margin-right: 16px;">
        <el-option label="全部" value="" />
        <el-option label="待审核" value="pending" />
        <el-option label="已通过" value="approved" />
        <el-option label="已驳回" value="rejected" />
      </el-select>
      <el-button type="primary" @click="loadCourses">搜索</el-button>
    </div>
    <el-table :data="filteredCourses" style="width: 100%; margin-top: 20px;" border>
      <el-table-column prop="title" label="课程名称" min-width="180">
        <template #default="scope">
          <el-link type="primary" @click="showDetail(scope.row)">{{ scope.row.title }}</el-link>
        </template>
      </el-table-column>
      <el-table-column prop="creator" label="创建人" width="120" />
      <el-table-column prop="createdAt" label="提交时间" width="160" />
      <el-table-column prop="description" label="简介" min-width="200" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.status==='pending'" type="warning">待审核</el-tag>
          <el-tag v-else-if="scope.row.status==='approved'" type="success">已通过</el-tag>
          <el-tag v-else type="danger">已驳回</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button v-if="scope.row.status==='pending'" type="success" size="small" @click="approve(scope.row)">通过</el-button>
          <el-button v-if="scope.row.status==='pending'" type="danger" size="small" @click="reject(scope.row)">驳回</el-button>
          <el-button v-if="scope.row.status!=='pending'" size="small" @click="showDetail(scope.row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      style="margin-top: 20px; text-align: right;"
      background
      layout="prev, pager, next, jumper"
      :total="filteredCourses.length"
      :page-size="pageSize"
      v-model:current-page="currentPage"
    />
    <!-- 课程详情弹窗 -->
    <el-dialog v-model="detailVisible" title="课程详情" width="600px">
      <div v-if="currentCourse">
        <h3>{{ currentCourse.title }}</h3>
        <el-image :src="currentCourse.cover" style="width: 120px; height: 80px; border-radius: 8px; margin-bottom: 12px;" fit="cover" />
        <p><b>创建人：</b>{{ currentCourse.creator }}</p>
        <p><b>简介：</b>{{ currentCourse.description }}</p>
        <p><b>提交时间：</b>{{ currentCourse.createdAt }}</p>
        <p><b>状态：</b>
          <el-tag v-if="currentCourse.status==='pending'" type="warning">待审核</el-tag>
          <el-tag v-else-if="currentCourse.status==='approved'" type="success">已通过</el-tag>
          <el-tag v-else type="danger">已驳回</el-tag>
        </p>
        <h4 style="margin-top: 16px;">章节</h4>
        <el-timeline>
          <el-timeline-item v-for="(ch, idx) in currentCourse.chapters" :key="ch.id" :timestamp="'第'+(idx+1)+'章'">
            <b>{{ ch.title }}</b> <span style="color:#888;">({{ ch.duration }}分钟)</span>
            <div style="color:#666; font-size:13px;">{{ ch.description }}</div>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-dialog>
    <!-- 驳回理由弹窗 -->
    <el-dialog v-model="rejectVisible" title="驳回理由" width="400px">
      <el-input v-model="rejectReason" type="textarea" :rows="4" placeholder="请输入驳回理由" />
      <template #footer>
        <el-button @click="rejectVisible=false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认驳回</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>
<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
// 假数据
const allCourses = ref([
  { id: 1, title: 'Java入门', creator: '张三', createdAt: '2024-06-01 10:00', description: '零基础Java课程', status: 'pending', cover: '/default-course.jpg', chapters: [ {id:1,title:'基础语法',description:'Java基本语法',duration:30}, {id:2,title:'面向对象',description:'OOP核心',duration:40} ] },
  { id: 2, title: '前端开发', creator: '李四', createdAt: '2024-06-02 11:20', description: 'HTML/CSS/JS全栈', status: 'approved', cover: '/default-course.jpg', chapters: [ {id:1,title:'HTML',description:'网页结构',duration:20}, {id:2,title:'CSS',description:'样式美化',duration:25} ] },
  { id: 3, title: 'Python数据分析', creator: '王五', createdAt: '2024-06-03 09:30', description: '数据分析与可视化', status: 'rejected', cover: '/default-course.jpg', chapters: [ {id:1,title:'Numpy',description:'数值计算',duration:35}, {id:2,title:'Pandas',description:'数据处理',duration:40} ] },
  { id: 4, title: 'AI智能应用', creator: '赵六', createdAt: '2024-06-04 14:10', description: 'AI基础与实战', status: 'pending', cover: '/default-course.jpg', chapters: [ {id:1,title:'AI基础',description:'AI原理',duration:30}, {id:2,title:'实战案例',description:'AI项目实操',duration:50} ] },
])
const search = ref('')
const status = ref('')
const pageSize = 5
const currentPage = ref(1)
const detailVisible = ref(false)
const currentCourse = ref(null)
const rejectVisible = ref(false)
const rejectReason = ref('')
const filteredCourses = computed(() => {
  let list = allCourses.value
  if (search.value) {
    list = list.filter(c => c.title.includes(search.value) || c.creator.includes(search.value))
  }
  if (status.value) {
    list = list.filter(c => c.status === status.value)
  }
  // 分页
  const start = (currentPage.value-1)*pageSize
  return list.slice(start, start+pageSize)
})
function loadCourses() {
  currentPage.value = 1
}
function showDetail(row) {
  currentCourse.value = row
  detailVisible.value = true
}
function approve(row) {
  row.status = 'approved'
  ElMessage.success('已通过')
}
function reject(row) {
  currentCourse.value = row
  rejectReason.value = ''
  rejectVisible.value = true
}
function confirmReject() {
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请输入驳回理由')
    return
  }
  currentCourse.value.status = 'rejected'
  rejectVisible.value = false
  ElMessage.success('已驳回')
}
</script>
<style scoped>
.audit-header { margin-bottom: 10px; }
</style> 
        <el-option label="已通过" value="approved" />
        <el-option label="已驳回" value="rejected" />
      </el-select>
      <el-button type="primary" @click="loadCourses">搜索</el-button>
    </div>
    <el-table :data="filteredCourses" style="width: 100%; margin-top: 20px;" border>
      <el-table-column prop="title" label="课程名称" min-width="180">
        <template #default="scope">
          <el-link type="primary" @click="showDetail(scope.row)">{{ scope.row.title }}</el-link>
        </template>
      </el-table-column>
      <el-table-column prop="creator" label="创建人" width="120" />
      <el-table-column prop="createdAt" label="提交时间" width="160" />
      <el-table-column prop="description" label="简介" min-width="200" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.status==='pending'" type="warning">待审核</el-tag>
          <el-tag v-else-if="scope.row.status==='approved'" type="success">已通过</el-tag>
          <el-tag v-else type="danger">已驳回</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button v-if="scope.row.status==='pending'" type="success" size="small" @click="approve(scope.row)">通过</el-button>
          <el-button v-if="scope.row.status==='pending'" type="danger" size="small" @click="reject(scope.row)">驳回</el-button>
          <el-button v-if="scope.row.status!=='pending'" size="small" @click="showDetail(scope.row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      style="margin-top: 20px; text-align: right;"
      background
      layout="prev, pager, next, jumper"
      :total="filteredCourses.length"
      :page-size="pageSize"
      v-model:current-page="currentPage"
    />
    <!-- 课程详情弹窗 -->
    <el-dialog v-model="detailVisible" title="课程详情" width="600px">
      <div v-if="currentCourse">
        <h3>{{ currentCourse.title }}</h3>
        <el-image :src="currentCourse.cover" style="width: 120px; height: 80px; border-radius: 8px; margin-bottom: 12px;" fit="cover" />
        <p><b>创建人：</b>{{ currentCourse.creator }}</p>
        <p><b>简介：</b>{{ currentCourse.description }}</p>
        <p><b>提交时间：</b>{{ currentCourse.createdAt }}</p>
        <p><b>状态：</b>
          <el-tag v-if="currentCourse.status==='pending'" type="warning">待审核</el-tag>
          <el-tag v-else-if="currentCourse.status==='approved'" type="success">已通过</el-tag>
          <el-tag v-else type="danger">已驳回</el-tag>
        </p>
        <h4 style="margin-top: 16px;">章节</h4>
        <el-timeline>
          <el-timeline-item v-for="(ch, idx) in currentCourse.chapters" :key="ch.id" :timestamp="'第'+(idx+1)+'章'">
            <b>{{ ch.title }}</b> <span style="color:#888;">({{ ch.duration }}分钟)</span>
            <div style="color:#666; font-size:13px;">{{ ch.description }}</div>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-dialog>
    <!-- 驳回理由弹窗 -->
    <el-dialog v-model="rejectVisible" title="驳回理由" width="400px">
      <el-input v-model="rejectReason" type="textarea" :rows="4" placeholder="请输入驳回理由" />
      <template #footer>
        <el-button @click="rejectVisible=false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认驳回</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>
<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
// 假数据
const allCourses = ref([
  { id: 1, title: 'Java入门', creator: '张三', createdAt: '2024-06-01 10:00', description: '零基础Java课程', status: 'pending', cover: '/default-course.jpg', chapters: [ {id:1,title:'基础语法',description:'Java基本语法',duration:30}, {id:2,title:'面向对象',description:'OOP核心',duration:40} ] },
  { id: 2, title: '前端开发', creator: '李四', createdAt: '2024-06-02 11:20', description: 'HTML/CSS/JS全栈', status: 'approved', cover: '/default-course.jpg', chapters: [ {id:1,title:'HTML',description:'网页结构',duration:20}, {id:2,title:'CSS',description:'样式美化',duration:25} ] },
  { id: 3, title: 'Python数据分析', creator: '王五', createdAt: '2024-06-03 09:30', description: '数据分析与可视化', status: 'rejected', cover: '/default-course.jpg', chapters: [ {id:1,title:'Numpy',description:'数值计算',duration:35}, {id:2,title:'Pandas',description:'数据处理',duration:40} ] },
  { id: 4, title: 'AI智能应用', creator: '赵六', createdAt: '2024-06-04 14:10', description: 'AI基础与实战', status: 'pending', cover: '/default-course.jpg', chapters: [ {id:1,title:'AI基础',description:'AI原理',duration:30}, {id:2,title:'实战案例',description:'AI项目实操',duration:50} ] },
])
const search = ref('')
const status = ref('')
const pageSize = 5
const currentPage = ref(1)
const detailVisible = ref(false)
const currentCourse = ref(null)
const rejectVisible = ref(false)
const rejectReason = ref('')
const filteredCourses = computed(() => {
  let list = allCourses.value
  if (search.value) {
    list = list.filter(c => c.title.includes(search.value) || c.creator.includes(search.value))
  }
  if (status.value) {
    list = list.filter(c => c.status === status.value)
  }
  // 分页
  const start = (currentPage.value-1)*pageSize
  return list.slice(start, start+pageSize)
})
function loadCourses() {
  currentPage.value = 1
}
function showDetail(row) {
  currentCourse.value = row
  detailVisible.value = true
}
function approve(row) {
  row.status = 'approved'
  ElMessage.success('已通过')
}
function reject(row) {
  currentCourse.value = row
  rejectReason.value = ''
  rejectVisible.value = true
}
function confirmReject() {
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请输入驳回理由')
    return
  }
  currentCourse.value.status = 'rejected'
  rejectVisible.value = false
  ElMessage.success('已驳回')
}
</script>
<style scoped>
.audit-header { margin-bottom: 10px; }
</style> 