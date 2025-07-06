<template>
  <div class="enterprise-courses-container">
    <!-- 课程统计区 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon primary"><el-icon><Calendar /></el-icon></div>
            <div class="stat-content">
              <div class="stat-number">{{ publishedCourseCount }}</div>
              <div class="stat-label">已发布课程</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon info"><el-icon><Medal /></el-icon></div>
            <div class="stat-content">
              <div class="stat-number">{{ totalPlayCount }}</div>
              <div class="stat-label">累计播放量</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
    <!-- 操作区 -->
    <div class="actions-section" style="margin-bottom: 18px;">
      <el-button type="primary" size="large" @click="showAddDialog = true">
        <el-icon><Plus /></el-icon> 添加课程
      </el-button>
      <el-input v-model="searchKeyword" placeholder="搜索课程..." style="width: 260px; margin-left: 24px;" clearable @input="filterCourses" size="large">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
    </div>
    <!-- 课程表格区 -->
    <el-table :data="displayCourses" style="margin-top: 10px" border stripe>
      <el-table-column prop="title" label="课程名" min-width="160">
        <template #default="scope">
          <span>{{ scope.row.title }}</span>
          <el-tag v-if="scope.row.isFree" type="success" size="small" style="margin-left: 8px;">免费</el-tag>
          <el-tag v-else type="warning" size="small" style="margin-left: 8px;">付费</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="price" label="价格" min-width="80">
        <template #default="scope">
          <span v-if="scope.row.isFree">0</span>
          <span v-else>{{ scope.row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="duration" label="时长(分钟)" min-width="100" />
      <el-table-column prop="level" label="难度" min-width="100" />
      <el-table-column prop="category" label="分类" min-width="100" />
      <el-table-column prop="viewCount" label="播放量" min-width="100" />
      <el-table-column prop="favoriteCount" label="收藏数" min-width="100" />
      <el-table-column prop="likeCount" label="点赞数" min-width="100" />
      <el-table-column prop="description" label="简介" min-width="180" show-overflow-tooltip />
      <el-table-column prop="progress" label="进度" min-width="120">
        <template #default="scope">
          <el-progress :percentage="scope.row.progress" :color="getProgressColor(scope.row.progress)" :stroke-width="16" />
        </template>
      </el-table-column>
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
    <!-- 分页 -->
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
    <!-- 课程发布记录区 -->
    <div class="publish-record-section" style="margin-top: 32px;">
      <h3 style="margin-bottom: 16px;">课程发布记录</h3>
      <el-table :data="publishRecords" border stripe>
        <el-table-column prop="title" label="课程名" min-width="160" />
        <el-table-column prop="publishTime" label="发布时间" min-width="160" />
        <el-table-column prop="reviewer" label="审核人" min-width="120" />
        <el-table-column prop="result" label="审核结果" min-width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.result === '通过'" type="success">通过</el-tag>
            <el-tag v-else-if="scope.row.result === '未通过'" type="danger">未通过</el-tag>
            <el-tag v-else type="info">审核中</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="180" />
      </el-table>
    </div>
    <!-- 添加/编辑课程弹窗 -->
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
          <el-form-item label="课程视频" required>
            <el-upload
              class="video-uploader"
              :action="videoUploadUrl"
              :show-file-list="false"
              :before-upload="beforeVideoUpload"
              :on-success="handleVideoSuccess"
              :headers="uploadHeaders"
              accept="video/*"
            >
              <el-button type="primary">{{ editForm.videoObjectName ? '重新上传' : '上传视频' }}</el-button>
              <span v-if="editForm.videoObjectName" style="margin-left: 12px; color: #67c23a;">已上传</span>
            </el-upload>
            <div v-if="editForm.videoObjectName" style="margin-top: 8px;">
              <video :src="getVideoPreviewUrl(editForm.videoObjectName)" controls style="max-width: 320px; max-height: 180px; border-radius: 6px; margin-top: 6px;" />
            </div>
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Calendar, Medal } from '@element-plus/icons-vue'
const stats = ref({ totalCourses: 3, totalTime: '360分钟', certificates: 1, avgProgress: 80 })
const allCourses = ref([
  { id: 1, title: '企业前端开发', description: '企业级HTML、CSS、JS', progress: 75, isFree: false, price: 99, duration: 120, level: '中级', category: '前端开发', status: 'approved', viewCount: 1234, favoriteCount: 56, likeCount: 88 },
  { id: 2, title: 'Java后端实战', description: 'Spring Boot企业开发', progress: 100, isFree: false, price: 199, duration: 180, level: '高级', category: '后端开发', status: 'approved', viewCount: 888, favoriteCount: 32, likeCount: 45 },
  { id: 3, title: 'MySQL数据库优化', description: '性能优化与调优', progress: 100, isFree: false, price: 0, duration: 60, level: '高级', category: '数据库', status: 'approved', viewCount: 1567, favoriteCount: 78, likeCount: 120 }
])
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const totalCourses = ref(allCourses.value.length)
const displayCourses = computed(() => {
  let filtered = allCourses.value.filter(c => c.status === 'approved')
  if (searchKeyword.value) {
    filtered = filtered.filter(c => c.title.includes(searchKeyword.value) || c.description.includes(searchKeyword.value))
  }
  totalCourses.value = filtered.length
  return filtered.slice((currentPage.value-1)*pageSize.value, currentPage.value*pageSize.value)
})
const handleSizeChange = (size) => { pageSize.value = size; currentPage.value = 1 }
const handleCurrentChange = (page) => { currentPage.value = page }
const filterCourses = () => { currentPage.value = 1 }
const showAddDialog = ref(false)
const editForm = ref({ id: null, title: '', description: '', isFree: true, price: 0, duration: 60, level: '', category: '', videoObjectName: '' })
let editingId = null
function openEditDialog(row) { editingId = row.id; editForm.value = { ...row }; showAddDialog.value = true }
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
    allCourses.value.push({ ...editForm.value, id: Date.now(), progress: 0, status: 'draft' })
    ElMessage.success('课程添加成功')
  }
  showAddDialog.value = false
  editingId = null
}
function deleteCourse(row) {
  ElMessageBox.confirm('确定要删除该课程吗？', '提示', { type: 'warning' })
    .then(() => {
      allCourses.value = allCourses.value.filter(c => c.id !== row.id)
      ElMessage.success('删除成功')
    })
}
function submitAudit(row) { row.status = 'pending'; ElMessage.success('已提交审核，等待管理员审核') }
function aiOptimize(field) { if (field === 'title') { editForm.value.title = '【AI优化】' + editForm.value.title } if (field === 'description') { editForm.value.description = '【AI优化】' + editForm.value.description } }
const videoUploadUrl = '/api/file/upload/course-video'
const uploadHeaders = {}
function beforeVideoUpload(file) { const isVideo = file.type.startsWith('video/'); if (!isVideo) { ElMessage.error('只能上传视频文件') } return isVideo }
function handleVideoSuccess(res, file) { if (res.code === 200 && res.data && res.data.fileUrl) { editForm.value.videoObjectName = res.data.fileUrl; ElMessage.success('视频上传成功') } else { ElMessage.error(res.msg || '视频上传失败') } }
function getVideoPreviewUrl(objectName) { return `/api/file/stream?objectName=${encodeURIComponent(objectName)}` }
const publishedCourses = computed(() => allCourses.value.filter(c => c.status === 'approved'))
const publishedCourseCount = computed(() => publishedCourses.value.length)
const totalPlayCount = computed(() => publishedCourses.value.reduce((sum, c) => sum + (c.viewCount || 0), 0))
const publishRecords = ref([
  { title: '企业前端开发', publishTime: '2024-07-01 10:00', reviewer: '管理员A', result: '通过', remark: '内容合规' },
  { title: 'Java后端实战', publishTime: '2024-07-02 14:30', reviewer: '管理员B', result: '通过', remark: '' },
  { title: 'Python数据分析', publishTime: '2024-07-03 09:20', reviewer: '管理员A', result: '审核中', remark: '' },
  { title: 'Vue.js实战', publishTime: '2024-07-04 11:10', reviewer: '管理员C', result: '未通过', remark: '简介不完整' }
])
function getStatusType(status) { if (status === 'approved') return 'success'; if (status === 'pending') return 'info'; if (status === 'rejected') return 'danger'; return 'warning' }
function getStatusText(status) { if (status === 'approved') return '已通过'; if (status === 'pending') return '审核中'; if (status === 'rejected') return '未通过'; return '草稿' }
function getProgressColor(progress) { if (progress >= 100) return '#67c23a'; if (progress >= 60) return '#e6a23c'; return '#f56c6c' }
</script>
<style scoped>
.enterprise-courses-container {
  background: #f6faff;
  min-height: 100vh;
  padding: 0 0 32px 0;
}
.stats-section {
  margin: 32px 0 24px 0;
}
.stat-card {
  background: linear-gradient(135deg, #6d8ba6 0%, #a3bce2 100%);
  border-radius: 18px;
  box-shadow: 0 4px 24px #a3bce244;
  padding: 32px 36px 24px 36px;
  display: flex;
  align-items: center;
  gap: 18px;
  margin-bottom: 12px;
  color: #fff;
  transition: box-shadow 0.2s;
}
.stat-card:hover {
  box-shadow: 0 8px 32px #6d8ba666;
}
.stat-icon {
  font-size: 38px;
  background: #fff3;
  border-radius: 12px;
  padding: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.stat-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}
.stat-number {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 2px;
}
.stat-label {
  font-size: 16px;
  opacity: 0.85;
}
.actions-section {
  display: flex;
  align-items: center;
  margin-bottom: 18px;
}
.actions-section .el-button {
  border-radius: 10px;
  font-size: 18px;
  padding: 10px 32px;
  background: linear-gradient(90deg, #6d8ba6 0%, #a3bce2 100%);
  color: #fff;
  border: none;
  box-shadow: 0 2px 8px #a3bce222;
  transition: background 0.2s;
}
.actions-section .el-button:hover {
  background: linear-gradient(90deg, #a3bce2 0%, #6d8ba6 100%);
}
.el-input {
  border-radius: 10px;
  font-size: 16px;
}
.el-table {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px #b3c6e033;
  margin-bottom: 18px;
  overflow: hidden;
}
.el-table th, .el-table td {
  border-bottom: 1px solid #e3e8ee !important;
}
.el-table th {
  background: #f6faff;
  color: #6d8ba6;
  font-weight: bold;
}
.el-table__row:hover {
  background: #eaf3ff !important;
}
.el-tag {
  border-radius: 8px;
  font-size: 14px;
}
.el-progress {
  border-radius: 8px;
}
.pagination-section {
  margin: 32px 0 0 0;
  display: flex;
  justify-content: center;
}
.publish-record-section {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px #b3c6e033;
  padding: 24px 24px 12px 24px;
  margin-top: 32px;
}
.publish-record-section h3 {
  color: #2d3a4b;
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 16px;
}
.el-dialog {
  border-radius: 18px;
}
.el-card {
  border-radius: 14px;
  margin-bottom: 18px;
  box-shadow: 0 2px 8px #a3bce222;
}
.el-form-item {
  margin-bottom: 18px;
}
.video-uploader .el-button {
  border-radius: 8px;
  font-size: 16px;
  background: #6d8ba6;
  color: #fff;
  border: none;
}
.video-uploader .el-button:hover {
  background: #a3bce2;
}
@media (max-width: 1200px) {
  .stat-card, .publish-record-section, .el-table {
    padding: 12px 8px;
  }
  .stat-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
@media (max-width: 800px) {
  .enterprise-courses-container {
    padding: 0 2px;
  }
  .stat-card, .publish-record-section, .el-table {
    padding: 8px 2px;
  }
  .actions-section {
    flex-direction: column;
    gap: 8px;
  }
}
</style> 
  <div class="enterprise-courses-container">
    <!-- 课程统计区 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon primary"><el-icon><Calendar /></el-icon></div>
            <div class="stat-content">
              <div class="stat-number">{{ publishedCourseCount }}</div>
              <div class="stat-label">已发布课程</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon info"><el-icon><Medal /></el-icon></div>
            <div class="stat-content">
              <div class="stat-number">{{ totalPlayCount }}</div>
              <div class="stat-label">累计播放量</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
    <!-- 操作区 -->
    <div class="actions-section" style="margin-bottom: 18px;">
      <el-button type="primary" size="large" @click="showAddDialog = true">
        <el-icon><Plus /></el-icon> 添加课程
      </el-button>
      <el-input v-model="searchKeyword" placeholder="搜索课程..." style="width: 260px; margin-left: 24px;" clearable @input="filterCourses" size="large">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
    </div>
    <!-- 课程表格区 -->
    <el-table :data="displayCourses" style="margin-top: 10px" border stripe>
      <el-table-column prop="title" label="课程名" min-width="160">
        <template #default="scope">
          <span>{{ scope.row.title }}</span>
          <el-tag v-if="scope.row.isFree" type="success" size="small" style="margin-left: 8px;">免费</el-tag>
          <el-tag v-else type="warning" size="small" style="margin-left: 8px;">付费</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="price" label="价格" min-width="80">
        <template #default="scope">
          <span v-if="scope.row.isFree">0</span>
          <span v-else>{{ scope.row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="duration" label="时长(分钟)" min-width="100" />
      <el-table-column prop="level" label="难度" min-width="100" />
      <el-table-column prop="category" label="分类" min-width="100" />
      <el-table-column prop="viewCount" label="播放量" min-width="100" />
      <el-table-column prop="favoriteCount" label="收藏数" min-width="100" />
      <el-table-column prop="likeCount" label="点赞数" min-width="100" />
      <el-table-column prop="description" label="简介" min-width="180" show-overflow-tooltip />
      <el-table-column prop="progress" label="进度" min-width="120">
        <template #default="scope">
          <el-progress :percentage="scope.row.progress" :color="getProgressColor(scope.row.progress)" :stroke-width="16" />
        </template>
      </el-table-column>
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
    <!-- 分页 -->
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
    <!-- 课程发布记录区 -->
    <div class="publish-record-section" style="margin-top: 32px;">
      <h3 style="margin-bottom: 16px;">课程发布记录</h3>
      <el-table :data="publishRecords" border stripe>
        <el-table-column prop="title" label="课程名" min-width="160" />
        <el-table-column prop="publishTime" label="发布时间" min-width="160" />
        <el-table-column prop="reviewer" label="审核人" min-width="120" />
        <el-table-column prop="result" label="审核结果" min-width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.result === '通过'" type="success">通过</el-tag>
            <el-tag v-else-if="scope.row.result === '未通过'" type="danger">未通过</el-tag>
            <el-tag v-else type="info">审核中</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="180" />
      </el-table>
    </div>
    <!-- 添加/编辑课程弹窗 -->
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
          <el-form-item label="课程视频" required>
            <el-upload
              class="video-uploader"
              :action="videoUploadUrl"
              :show-file-list="false"
              :before-upload="beforeVideoUpload"
              :on-success="handleVideoSuccess"
              :headers="uploadHeaders"
              accept="video/*"
            >
              <el-button type="primary">{{ editForm.videoObjectName ? '重新上传' : '上传视频' }}</el-button>
              <span v-if="editForm.videoObjectName" style="margin-left: 12px; color: #67c23a;">已上传</span>
            </el-upload>
            <div v-if="editForm.videoObjectName" style="margin-top: 8px;">
              <video :src="getVideoPreviewUrl(editForm.videoObjectName)" controls style="max-width: 320px; max-height: 180px; border-radius: 6px; margin-top: 6px;" />
            </div>
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Calendar, Medal } from '@element-plus/icons-vue'
const stats = ref({ totalCourses: 3, totalTime: '360分钟', certificates: 1, avgProgress: 80 })
const allCourses = ref([
  { id: 1, title: '企业前端开发', description: '企业级HTML、CSS、JS', progress: 75, isFree: false, price: 99, duration: 120, level: '中级', category: '前端开发', status: 'approved', viewCount: 1234, favoriteCount: 56, likeCount: 88 },
  { id: 2, title: 'Java后端实战', description: 'Spring Boot企业开发', progress: 100, isFree: false, price: 199, duration: 180, level: '高级', category: '后端开发', status: 'approved', viewCount: 888, favoriteCount: 32, likeCount: 45 },
  { id: 3, title: 'MySQL数据库优化', description: '性能优化与调优', progress: 100, isFree: false, price: 0, duration: 60, level: '高级', category: '数据库', status: 'approved', viewCount: 1567, favoriteCount: 78, likeCount: 120 }
])
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const totalCourses = ref(allCourses.value.length)
const displayCourses = computed(() => {
  let filtered = allCourses.value.filter(c => c.status === 'approved')
  if (searchKeyword.value) {
    filtered = filtered.filter(c => c.title.includes(searchKeyword.value) || c.description.includes(searchKeyword.value))
  }
  totalCourses.value = filtered.length
  return filtered.slice((currentPage.value-1)*pageSize.value, currentPage.value*pageSize.value)
})
const handleSizeChange = (size) => { pageSize.value = size; currentPage.value = 1 }
const handleCurrentChange = (page) => { currentPage.value = page }
const filterCourses = () => { currentPage.value = 1 }
const showAddDialog = ref(false)
const editForm = ref({ id: null, title: '', description: '', isFree: true, price: 0, duration: 60, level: '', category: '', videoObjectName: '' })
let editingId = null
function openEditDialog(row) { editingId = row.id; editForm.value = { ...row }; showAddDialog.value = true }
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
    allCourses.value.push({ ...editForm.value, id: Date.now(), progress: 0, status: 'draft' })
    ElMessage.success('课程添加成功')
  }
  showAddDialog.value = false
  editingId = null
}
function deleteCourse(row) {
  ElMessageBox.confirm('确定要删除该课程吗？', '提示', { type: 'warning' })
    .then(() => {
      allCourses.value = allCourses.value.filter(c => c.id !== row.id)
      ElMessage.success('删除成功')
    })
}
function submitAudit(row) { row.status = 'pending'; ElMessage.success('已提交审核，等待管理员审核') }
function aiOptimize(field) { if (field === 'title') { editForm.value.title = '【AI优化】' + editForm.value.title } if (field === 'description') { editForm.value.description = '【AI优化】' + editForm.value.description } }
const videoUploadUrl = '/api/file/upload/course-video'
const uploadHeaders = {}
function beforeVideoUpload(file) { const isVideo = file.type.startsWith('video/'); if (!isVideo) { ElMessage.error('只能上传视频文件') } return isVideo }
function handleVideoSuccess(res, file) { if (res.code === 200 && res.data && res.data.fileUrl) { editForm.value.videoObjectName = res.data.fileUrl; ElMessage.success('视频上传成功') } else { ElMessage.error(res.msg || '视频上传失败') } }
function getVideoPreviewUrl(objectName) { return `/api/file/stream?objectName=${encodeURIComponent(objectName)}` }
const publishedCourses = computed(() => allCourses.value.filter(c => c.status === 'approved'))
const publishedCourseCount = computed(() => publishedCourses.value.length)
const totalPlayCount = computed(() => publishedCourses.value.reduce((sum, c) => sum + (c.viewCount || 0), 0))
const publishRecords = ref([
  { title: '企业前端开发', publishTime: '2024-07-01 10:00', reviewer: '管理员A', result: '通过', remark: '内容合规' },
  { title: 'Java后端实战', publishTime: '2024-07-02 14:30', reviewer: '管理员B', result: '通过', remark: '' },
  { title: 'Python数据分析', publishTime: '2024-07-03 09:20', reviewer: '管理员A', result: '审核中', remark: '' },
  { title: 'Vue.js实战', publishTime: '2024-07-04 11:10', reviewer: '管理员C', result: '未通过', remark: '简介不完整' }
])
function getStatusType(status) { if (status === 'approved') return 'success'; if (status === 'pending') return 'info'; if (status === 'rejected') return 'danger'; return 'warning' }
function getStatusText(status) { if (status === 'approved') return '已通过'; if (status === 'pending') return '审核中'; if (status === 'rejected') return '未通过'; return '草稿' }
function getProgressColor(progress) { if (progress >= 100) return '#67c23a'; if (progress >= 60) return '#e6a23c'; return '#f56c6c' }
</script>
<style scoped>
.enterprise-courses-container {
  background: #f6faff;
  min-height: 100vh;
  padding: 0 0 32px 0;
}
.stats-section {
  margin: 32px 0 24px 0;
}
.stat-card {
  background: linear-gradient(135deg, #6d8ba6 0%, #a3bce2 100%);
  border-radius: 18px;
  box-shadow: 0 4px 24px #a3bce244;
  padding: 32px 36px 24px 36px;
  display: flex;
  align-items: center;
  gap: 18px;
  margin-bottom: 12px;
  color: #fff;
  transition: box-shadow 0.2s;
}
.stat-card:hover {
  box-shadow: 0 8px 32px #6d8ba666;
}
.stat-icon {
  font-size: 38px;
  background: #fff3;
  border-radius: 12px;
  padding: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.stat-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}
.stat-number {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 2px;
}
.stat-label {
  font-size: 16px;
  opacity: 0.85;
}
.actions-section {
  display: flex;
  align-items: center;
  margin-bottom: 18px;
}
.actions-section .el-button {
  border-radius: 10px;
  font-size: 18px;
  padding: 10px 32px;
  background: linear-gradient(90deg, #6d8ba6 0%, #a3bce2 100%);
  color: #fff;
  border: none;
  box-shadow: 0 2px 8px #a3bce222;
  transition: background 0.2s;
}
.actions-section .el-button:hover {
  background: linear-gradient(90deg, #a3bce2 0%, #6d8ba6 100%);
}
.el-input {
  border-radius: 10px;
  font-size: 16px;
}
.el-table {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px #b3c6e033;
  margin-bottom: 18px;
  overflow: hidden;
}
.el-table th, .el-table td {
  border-bottom: 1px solid #e3e8ee !important;
}
.el-table th {
  background: #f6faff;
  color: #6d8ba6;
  font-weight: bold;
}
.el-table__row:hover {
  background: #eaf3ff !important;
}
.el-tag {
  border-radius: 8px;
  font-size: 14px;
}
.el-progress {
  border-radius: 8px;
}
.pagination-section {
  margin: 32px 0 0 0;
  display: flex;
  justify-content: center;
}
.publish-record-section {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px #b3c6e033;
  padding: 24px 24px 12px 24px;
  margin-top: 32px;
}
.publish-record-section h3 {
  color: #2d3a4b;
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 16px;
}
.el-dialog {
  border-radius: 18px;
}
.el-card {
  border-radius: 14px;
  margin-bottom: 18px;
  box-shadow: 0 2px 8px #a3bce222;
}
.el-form-item {
  margin-bottom: 18px;
}
.video-uploader .el-button {
  border-radius: 8px;
  font-size: 16px;
  background: #6d8ba6;
  color: #fff;
  border: none;
}
.video-uploader .el-button:hover {
  background: #a3bce2;
}
@media (max-width: 1200px) {
  .stat-card, .publish-record-section, .el-table {
    padding: 12px 8px;
  }
  .stat-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
@media (max-width: 800px) {
  .enterprise-courses-container {
    padding: 0 2px;
  }
  .stat-card, .publish-record-section, .el-table {
    padding: 8px 2px;
  }
  .actions-section {
    flex-direction: column;
    gap: 8px;
  }
}
</style> 