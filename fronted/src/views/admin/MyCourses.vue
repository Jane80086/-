<template>
  <div class="admin-courses-page">
    <el-card>
      <template #header>
        <div class="page-header">
          <h2>我的课程管理</h2>
          <p>管理员可以管理所有课程</p>
        </div>
      </template>
      
      <!-- 统计卡片 -->
      <div class="stats-section">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-content">
                <div class="stat-number">{{ totalCourses }}</div>
                <div class="stat-label">总课程数</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-content">
                <div class="stat-number">{{ approvedCourses }}</div>
                <div class="stat-label">已审核</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-content">
                <div class="stat-number">{{ pendingCourses }}</div>
                <div class="stat-label">待审核</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-content">
                <div class="stat-number">{{ totalViews }}</div>
                <div class="stat-label">总播放量</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      
      <!-- 操作栏 -->
      <div class="actions-section">
        <el-button type="primary" @click="createDialogVisible = true">
          <el-icon><Plus /></el-icon> 创建课程
        </el-button>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索课程名称..."
          style="width: 300px; margin-left: 16px;"
          clearable
          @input="filterCourses"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      
      <!-- 课程列表 -->
      <div class="course-list">
        <el-table
          v-loading="loading"
          :data="displayCourses"
          style="width: 100%"
          border
          stripe
        >
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="课程名称" min-width="200" />
          <el-table-column prop="instructorName" label="讲师" width="120" />
          <el-table-column prop="category" label="分类" width="100" />
          <el-table-column prop="level" label="难度" width="80" />
          <el-table-column prop="price" label="价格" width="100">
            <template #default="scope">
              <span v-if="scope.row.price === 0" class="free-tag">免费</span>
              <span v-else class="price-tag">¥{{ scope.row.price }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="viewCount" label="播放量" width="100" />
          <el-table-column prop="rating" label="评分" width="120">
            <template #default="scope">
              <el-rate v-model="scope.row.rating" disabled show-score text-color="#409eff" score-template="{value}" />
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button @click="viewCourse(scope.row.id)" type="primary" size="small">查看</el-button>
              <el-button @click="editCourse(scope.row.id)" type="warning" size="small">编辑</el-button>
              <el-button @click="deleteCourse(scope.row.id)" type="danger" size="small">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="totalCourses"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
      
      <!-- 创建课程弹窗 -->
      <el-dialog v-model="createDialogVisible" title="创建课程" width="500px" @close="resetCreateForm">
        <el-form :model="createForm" label-width="80px">
          <el-form-item label="课程名称" required>
            <el-input v-model="createForm.title" autocomplete="off" />
          </el-form-item>
          <el-form-item label="讲师" required>
            <el-input v-model="createForm.instructorName" autocomplete="off" />
          </el-form-item>
          <el-form-item label="分类">
            <el-input v-model="createForm.category" autocomplete="off" />
          </el-form-item>
          <el-form-item label="难度">
            <el-select v-model="createForm.level" placeholder="请选择">
              <el-option label="初级" value="初级" />
              <el-option label="中级" value="中级" />
              <el-option label="高级" value="高级" />
            </el-select>
          </el-form-item>
          <el-form-item label="价格">
            <el-input-number v-model="createForm.price" :min="0" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleCreateCourse">创建</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const totalCourses = ref(0)

// 模拟课程数据
const courses = ref([
  { 
    id: 1, 
    title: '前端开发入门', 
    description: '学习HTML、CSS、JavaScript基础', 
    instructorName: '张老师', 
    duration: 120, 
    price: 0, 
    imageUrl: '', 
    viewCount: 1234, 
    rating: 4.5, 
    category: '前端开发', 
    level: '初级',
    status: 'approved'
  },
  { 
    id: 2, 
    title: 'Java后端实战', 
    description: 'Spring Boot+MyBatis企业级开发', 
    instructorName: '李老师', 
    duration: 180, 
    price: 99, 
    imageUrl: '', 
    viewCount: 888, 
    rating: 4.8, 
    category: '后端开发', 
    level: '中级',
    status: 'approved'
  },
  { 
    id: 3, 
    title: 'Python数据分析', 
    description: '使用Python进行数据分析和可视化', 
    instructorName: '王老师', 
    duration: 240, 
    price: 199, 
    imageUrl: '', 
    viewCount: 567, 
    rating: 4.6, 
    category: '编程开发', 
    level: '高级',
    status: 'pending'
  }
])

// 计算属性
const filteredCourses = computed(() => {
  let filtered = courses.value
  
  if (searchKeyword.value) {
    const kw = searchKeyword.value.trim().toLowerCase()
    filtered = filtered.filter(course =>
      course.title.toLowerCase().includes(kw) ||
      course.description.toLowerCase().includes(kw) ||
      (course.instructorName && course.instructorName.toLowerCase().includes(kw))
    )
  }
  
  return filtered
})

const displayCourses = computed(() => {
  totalCourses.value = filteredCourses.value.length
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredCourses.value.slice(start, end)
})

const totalViews = computed(() => {
  return courses.value.reduce((sum, course) => sum + (course.viewCount || 0), 0)
})

const approvedCourses = computed(() => {
  return courses.value.filter(course => course.status === 'approved').length
})

const pendingCourses = computed(() => {
  return courses.value.filter(course => course.status === 'pending').length
})

// 方法
const filterCourses = () => {
  currentPage.value = 1
}

const createCourse = () => {
  router.push('/admin/course/create')
}

const viewCourse = (courseId) => {
  router.push(`/admin/course/${courseId}`)
}

const editCourse = (courseId) => {
  router.push(`/admin/course/${courseId}/edit`)
}

const deleteCourse = async (courseId) => {
  try {
    await ElMessageBox.confirm('确定要删除这个课程吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const index = courses.value.findIndex(c => c.id === courseId)
    if (index > -1) {
      courses.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  } catch {
    // 用户取消删除
  }
}

const getStatusType = (status) => {
  switch (status) {
    case 'approved': return 'success'
    case 'pending': return 'warning'
    case 'rejected': return 'danger'
    default: return 'info'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 'approved': return '已审核'
    case 'pending': return '待审核'
    case 'rejected': return '已驳回'
    default: return '未知'
  }
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

const createDialogVisible = ref(false)
const createForm = ref({
  title: '',
  instructorName: '',
  category: '',
  level: '',
  price: 0
})

const resetCreateForm = () => {
  createForm.value = {
    title: '',
    instructorName: '',
    category: '',
    level: '',
    price: 0
  }
}

const handleCreateCourse = () => {
  if (!createForm.value.title || !createForm.value.instructorName) {
    ElMessage.error('课程名称和讲师为必填项')
    return
  }
  // 这里可以调用后端API，或直接添加到 courses 列表（模拟）
  courses.value.unshift({
    id: Date.now(),
    ...createForm.value,
    description: '',
    imageUrl: '',
    viewCount: 0,
    rating: 0,
    status: 'pending'
  })
  ElMessage.success('课程创建成功')
  createDialogVisible.value = false
  resetCreateForm()
}

// 组件挂载
onMounted(() => {
  console.log('=== Admin MyCourses 组件挂载 ===')
})
</script>

<style scoped>
.admin-courses-page {
  padding: 20px;
}

.page-header {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.page-header h2 {
  margin: 0;
  color: #2D3A4B;
  font-size: 24px;
}

.page-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.stats-section {
  margin-bottom: 24px;
}

.stat-card {
  text-align: center;
  padding: 20px;
}

.stat-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.actions-section {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}

.course-list {
  margin-bottom: 24px;
}

.free-tag {
  color: #67c23a;
  font-weight: bold;
}

.price-tag {
  color: #409eff;
  font-weight: bold;
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style> 