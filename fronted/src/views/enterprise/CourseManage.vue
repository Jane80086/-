<template>
  <div class="enterprise-course-manage-container">
    <div class="page-header">
      <h1>课程管理</h1>
      <el-button type="primary" @click="showAddDialog = true">
        <el-icon><Plus /></el-icon> 添加课程
      </el-button>
    </div>
    <el-table :data="displayCourses" style="margin-top: 20px" border stripe>
      <el-table-column prop="title" label="课程名" min-width="180">
        <template #default="scope">
          <span>{{ scope.row.title }}</span>
          <el-tag v-if="scope.row.isFree" type="success" size="small" style="margin-left: 8px;">免费</el-tag>
          <el-tag v-else type="warning" size="small" style="margin-left: 8px;">付费</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="price" label="价格" min-width="100">
        <template #default="scope">
          <span v-if="scope.row.isFree">0</span>
          <span v-else>{{ scope.row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="duration" label="时长(分钟)" min-width="100" />
      <el-table-column prop="level" label="难度" min-width="100" />
      <el-table-column prop="category" label="分类" min-width="100" />
      <el-table-column prop="description" label="简介" min-width="220" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" min-width="120">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="260">
        <template #default="scope">
          <el-button size="small" @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteCourse(scope.row)">删除</el-button>
          <el-button size="small" type="primary" v-if="scope.row.status === 'draft' || scope.row.status === 'rejected'" @click="submitAudit(scope.row)">提交审核</el-button>
          <el-tag v-if="scope.row.status === 'pending'" type="info">审核中</el-tag>
          <el-tag v-if="scope.row.status === 'approved'" type="success">已通过</el-tag>
          <el-tag v-if="scope.row.status === 'rejected'" type="danger">未通过</el-tag>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-section">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 30]"
        :total="totalCourses"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    <el-dialog v-model="showAddDialog" title="添加课程" width="700px" :body-style="{maxHeight: '75vh', overflowY: 'auto', padding: '32px 24px'}">
      <el-form :model="editForm" label-width="100px" label-position="top">
        <el-card shadow="never" style="margin-bottom: 18px;">
          <el-form-item label="课程名" required>
            <el-input v-model="editForm.title" placeholder="请输入课程名" size="large" />
            <el-button size="small" style="margin-left:8px;" @click="aiOptimize('title')">AI优化</el-button>
          </el-form-item>
          <el-form-item label="简介" required>
            <el-input v-model="editForm.description" type="textarea" rows="3" placeholder="请输入简介" size="large" />
            <el-button size="small" style="margin-left:8px;" @click="aiOptimize('description')">AI优化</el-button>
          </el-form-item>
        </el-card>
        <el-card shadow="never" style="margin-bottom: 18px;">
          <el-form-item label="类型" required>
            <el-radio-group v-model="editForm.isFree">
              <el-radio :label="true">免费</el-radio>
              <el-radio :label="false">付费</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="价格" v-if="!editForm.isFree" required>
            <el-input-number v-model="editForm.price" :min="0" :step="1" placeholder="请输入价格" size="large" style="width: 200px;" />
          </el-form-item>
        </el-card>
        <el-card shadow="never" style="margin-bottom: 18px;">
          <el-form-item label="时长(分钟)" required>
            <el-input-number v-model="editForm.duration" :min="1" :step="1" placeholder="请输入时长" size="large" style="width: 200px;" />
          </el-form-item>
          <el-form-item label="难度" required>
            <el-select v-model="editForm.level" placeholder="请选择难度" size="large" style="width: 200px;">
              <el-option label="初级" value="初级" />
              <el-option label="中级" value="中级" />
              <el-option label="高级" value="高级" />
            </el-select>
          </el-form-item>
          <el-form-item label="分类" required>
            <el-select v-model="editForm.category" placeholder="请选择分类" size="large" style="width: 200px;">
              <el-option label="前端开发" value="前端开发" />
              <el-option label="后端开发" value="后端开发" />
              <el-option label="数据库" value="数据库" />
              <el-option label="移动开发" value="移动开发" />
              <el-option label="人工智能" value="人工智能" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
        </el-card>
        <div style="text-align:center;margin-top:18px;">
          <el-button @click="showAddDialog = false" size="large">取消</el-button>
          <el-button type="primary" @click="saveCourse" size="large">保存</el-button>
        </div>
      </el-form>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
const allCourses = ref([
  { id: 1, title: '企业前端开发', description: '企业级HTML、CSS、JS', isFree: false, price: 99, duration: 120, level: '中级', category: '前端开发', status: 'draft' },
  { id: 2, title: 'Java后端实战', description: 'Spring Boot企业开发', isFree: false, price: 199, duration: 180, level: '高级', category: '后端开发', status: 'approved' },
  { id: 3, title: 'Python数据分析', description: '数据分析与可视化', isFree: true, price: 0, duration: 90, level: '初级', category: '人工智能', status: 'pending' },
  { id: 4, title: 'Vue.js实战', description: 'Vue3企业项目', isFree: true, price: 0, duration: 60, level: '中级', category: '前端开发', status: 'rejected' },
])
const currentPage = ref(1)
const pageSize = ref(10)
const totalCourses = ref(allCourses.value.length)
const displayCourses = computed(() => {
  totalCourses.value = allCourses.value.length
  return allCourses.value.slice((currentPage.value-1)*pageSize.value, currentPage.value*pageSize.value)
})
const showAddDialog = ref(false)
const editForm = ref({ id: null, title: '', description: '', isFree: true, price: 0, duration: 60, level: '', category: '' })
let editingId = null
function openEditDialog(row) {
  editingId = row.id
  editForm.value = { ...row }
  showAddDialog.value = true
}
function saveCourse() {
  if (!editForm.value.title) return ElMessage.error('课程名不能为空')
  if (!editForm.value.isFree && (!editForm.value.price || editForm.value.price < 0)) return ElMessage.error('请输入有效价格')
  if (!editForm.value.duration || editForm.value.duration <= 0) return ElMessage.error('请输入课程时长')
  if (!editForm.value.level) return ElMessage.error('请选择难度')
  if (!editForm.value.category) return ElMessage.error('请选择分类')
  if (editingId) {
    const idx = allCourses.value.findIndex(c => c.id === editingId)
    if (idx !== -1) allCourses.value[idx] = { ...editForm.value, id: editingId }
    ElMessage.success('课程编辑成功')
  } else {
    allCourses.value.push({ ...editForm.value, id: Date.now(), status: 'draft' })
    ElMessage.success('课程添加成功')
  }
  showAddDialog.value = false
  editingId = null
}
function deleteCourse(row) {
  allCourses.value = allCourses.value.filter(c => c.id !== row.id)
  ElMessage.success('课程已删除')
}
function submitAudit(row) {
  row.status = 'pending'
  ElMessage.success('已提交审核')
}
function aiOptimize(field) {
  if (field === 'title') editForm.value.title += '（AI优化）'
  if (field === 'description') editForm.value.description += '（AI优化）'
}
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}
const handleCurrentChange = (page) => {
  currentPage.value = page
}
function getStatusType(status) {
  if (status === 'draft') return 'info'
  if (status === 'pending') return 'warning'
  if (status === 'approved') return 'success'
  if (status === 'rejected') return 'danger'
  return ''
}
function getStatusText(status) {
  if (status === 'draft') return '草稿'
  if (status === 'pending') return '审核中'
  if (status === 'approved') return '已通过'
  if (status === 'rejected') return '未通过'
  return ''
}
</script>
<style scoped>
.enterprise-course-manage-container { padding: 24px; }
.page-header { display: flex; align-items: center; margin-bottom: 20px; }
.pagination-section { margin-top: 24px; text-align: right; }
:deep(.el-dialog__body) {
  max-height: 60vh;
  overflow-y: auto;
}
</style> 
  <div class="enterprise-course-manage-container">
    <div class="page-header">
      <h1>课程管理</h1>
      <el-button type="primary" @click="showAddDialog = true">
        <el-icon><Plus /></el-icon> 添加课程
      </el-button>
    </div>
    <el-table :data="displayCourses" style="margin-top: 20px" border stripe>
      <el-table-column prop="title" label="课程名" min-width="180">
        <template #default="scope">
          <span>{{ scope.row.title }}</span>
          <el-tag v-if="scope.row.isFree" type="success" size="small" style="margin-left: 8px;">免费</el-tag>
          <el-tag v-else type="warning" size="small" style="margin-left: 8px;">付费</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="price" label="价格" min-width="100">
        <template #default="scope">
          <span v-if="scope.row.isFree">0</span>
          <span v-else>{{ scope.row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="duration" label="时长(分钟)" min-width="100" />
      <el-table-column prop="level" label="难度" min-width="100" />
      <el-table-column prop="category" label="分类" min-width="100" />
      <el-table-column prop="description" label="简介" min-width="220" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" min-width="120">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="260">
        <template #default="scope">
          <el-button size="small" @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteCourse(scope.row)">删除</el-button>
          <el-button size="small" type="primary" v-if="scope.row.status === 'draft' || scope.row.status === 'rejected'" @click="submitAudit(scope.row)">提交审核</el-button>
          <el-tag v-if="scope.row.status === 'pending'" type="info">审核中</el-tag>
          <el-tag v-if="scope.row.status === 'approved'" type="success">已通过</el-tag>
          <el-tag v-if="scope.row.status === 'rejected'" type="danger">未通过</el-tag>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-section">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 30]"
        :total="totalCourses"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    <el-dialog v-model="showAddDialog" title="添加课程" width="700px" :body-style="{maxHeight: '75vh', overflowY: 'auto', padding: '32px 24px'}">
      <el-form :model="editForm" label-width="100px" label-position="top">
        <el-card shadow="never" style="margin-bottom: 18px;">
          <el-form-item label="课程名" required>
            <el-input v-model="editForm.title" placeholder="请输入课程名" size="large" />
            <el-button size="small" style="margin-left:8px;" @click="aiOptimize('title')">AI优化</el-button>
          </el-form-item>
          <el-form-item label="简介" required>
            <el-input v-model="editForm.description" type="textarea" rows="3" placeholder="请输入简介" size="large" />
            <el-button size="small" style="margin-left:8px;" @click="aiOptimize('description')">AI优化</el-button>
          </el-form-item>
        </el-card>
        <el-card shadow="never" style="margin-bottom: 18px;">
          <el-form-item label="类型" required>
            <el-radio-group v-model="editForm.isFree">
              <el-radio :label="true">免费</el-radio>
              <el-radio :label="false">付费</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="价格" v-if="!editForm.isFree" required>
            <el-input-number v-model="editForm.price" :min="0" :step="1" placeholder="请输入价格" size="large" style="width: 200px;" />
          </el-form-item>
        </el-card>
        <el-card shadow="never" style="margin-bottom: 18px;">
          <el-form-item label="时长(分钟)" required>
            <el-input-number v-model="editForm.duration" :min="1" :step="1" placeholder="请输入时长" size="large" style="width: 200px;" />
          </el-form-item>
          <el-form-item label="难度" required>
            <el-select v-model="editForm.level" placeholder="请选择难度" size="large" style="width: 200px;">
              <el-option label="初级" value="初级" />
              <el-option label="中级" value="中级" />
              <el-option label="高级" value="高级" />
            </el-select>
          </el-form-item>
          <el-form-item label="分类" required>
            <el-select v-model="editForm.category" placeholder="请选择分类" size="large" style="width: 200px;">
              <el-option label="前端开发" value="前端开发" />
              <el-option label="后端开发" value="后端开发" />
              <el-option label="数据库" value="数据库" />
              <el-option label="移动开发" value="移动开发" />
              <el-option label="人工智能" value="人工智能" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
        </el-card>
        <div style="text-align:center;margin-top:18px;">
          <el-button @click="showAddDialog = false" size="large">取消</el-button>
          <el-button type="primary" @click="saveCourse" size="large">保存</el-button>
        </div>
      </el-form>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
const allCourses = ref([
  { id: 1, title: '企业前端开发', description: '企业级HTML、CSS、JS', isFree: false, price: 99, duration: 120, level: '中级', category: '前端开发', status: 'draft' },
  { id: 2, title: 'Java后端实战', description: 'Spring Boot企业开发', isFree: false, price: 199, duration: 180, level: '高级', category: '后端开发', status: 'approved' },
  { id: 3, title: 'Python数据分析', description: '数据分析与可视化', isFree: true, price: 0, duration: 90, level: '初级', category: '人工智能', status: 'pending' },
  { id: 4, title: 'Vue.js实战', description: 'Vue3企业项目', isFree: true, price: 0, duration: 60, level: '中级', category: '前端开发', status: 'rejected' },
])
const currentPage = ref(1)
const pageSize = ref(10)
const totalCourses = ref(allCourses.value.length)
const displayCourses = computed(() => {
  totalCourses.value = allCourses.value.length
  return allCourses.value.slice((currentPage.value-1)*pageSize.value, currentPage.value*pageSize.value)
})
const showAddDialog = ref(false)
const editForm = ref({ id: null, title: '', description: '', isFree: true, price: 0, duration: 60, level: '', category: '' })
let editingId = null
function openEditDialog(row) {
  editingId = row.id
  editForm.value = { ...row }
  showAddDialog.value = true
}
function saveCourse() {
  if (!editForm.value.title) return ElMessage.error('课程名不能为空')
  if (!editForm.value.isFree && (!editForm.value.price || editForm.value.price < 0)) return ElMessage.error('请输入有效价格')
  if (!editForm.value.duration || editForm.value.duration <= 0) return ElMessage.error('请输入课程时长')
  if (!editForm.value.level) return ElMessage.error('请选择难度')
  if (!editForm.value.category) return ElMessage.error('请选择分类')
  if (editingId) {
    const idx = allCourses.value.findIndex(c => c.id === editingId)
    if (idx !== -1) allCourses.value[idx] = { ...editForm.value, id: editingId }
    ElMessage.success('课程编辑成功')
  } else {
    allCourses.value.push({ ...editForm.value, id: Date.now(), status: 'draft' })
    ElMessage.success('课程添加成功')
  }
  showAddDialog.value = false
  editingId = null
}
function deleteCourse(row) {
  allCourses.value = allCourses.value.filter(c => c.id !== row.id)
  ElMessage.success('课程已删除')
}
function submitAudit(row) {
  row.status = 'pending'
  ElMessage.success('已提交审核')
}
function aiOptimize(field) {
  if (field === 'title') editForm.value.title += '（AI优化）'
  if (field === 'description') editForm.value.description += '（AI优化）'
}
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}
const handleCurrentChange = (page) => {
  currentPage.value = page
}
function getStatusType(status) {
  if (status === 'draft') return 'info'
  if (status === 'pending') return 'warning'
  if (status === 'approved') return 'success'
  if (status === 'rejected') return 'danger'
  return ''
}
function getStatusText(status) {
  if (status === 'draft') return '草稿'
  if (status === 'pending') return '审核中'
  if (status === 'approved') return '已通过'
  if (status === 'rejected') return '未通过'
  return ''
}
</script>
<style scoped>
.enterprise-course-manage-container { padding: 24px; }
.page-header { display: flex; align-items: center; margin-bottom: 20px; }
.pagination-section { margin-top: 24px; text-align: right; }
:deep(.el-dialog__body) {
  max-height: 60vh;
  overflow-y: auto;
}
</style> 