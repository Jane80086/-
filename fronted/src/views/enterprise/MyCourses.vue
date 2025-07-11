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
          <el-button size="small" type="warning" v-if="scope.row.status === 'approved'" @click="unpublishCourse(scope.row)">下架</el-button>
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
            <el-input v-model="editForm.description" type="textarea" :rows="3" placeholder="请输入简介" size="large" />
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
              <el-radio :value="true">免费</el-radio>
              <el-radio :value="false">付费</el-radio>
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
import { courseApi } from '@/api/course'
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const currentUser = userStore.user
const router = useRouter()

const allCourses = ref([])
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const totalCourses = ref(0)
const loading = ref(false)

const displayCourses = computed(() => {
  let filtered = allCourses.value
  if (searchKeyword.value) {
    filtered = filtered.filter(c => c.title.includes(searchKeyword.value) || c.description.includes(searchKeyword.value))
  }
  totalCourses.value = filtered.length
  return filtered.slice((currentPage.value-1)*pageSize.value, currentPage.value*pageSize.value)
})
const handleSizeChange = (size) => { pageSize.value = size; currentPage.value = 1; loadCourses() }
const handleCurrentChange = (page) => { currentPage.value = page; loadCourses() }
const filterCourses = () => { currentPage.value = 1; loadCourses() }
const showAddDialog = ref(false)
const editForm = ref({ id: null, title: '', description: '', isFree: true, price: 0, duration: 60, level: '', category: '', videoObjectName: '' })
let editingId = null

const publishRecords = ref([])

async function loadCourses() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value, // 改为从1开始
      size: pageSize.value,
      keyword: searchKeyword.value
    }
    const res = await courseApi.getMyCourses(params)
    if (res.code === 200) {
      allCourses.value = res.data.content || res.data || []
      totalCourses.value = res.data.totalElements || allCourses.value.length
    } else {
      ElMessage.error(res.message || '获取课程失败')
    }
  } catch (e) {
    ElMessage.error(e?.message || '网络错误，获取课程失败')
  } finally {
    loading.value = false
  }
}

function openEditDialog(row) { editingId = row.id; editForm.value = { ...row }; showAddDialog.value = true }
async function saveCourse() {
  if (!editForm.value.title) return ElMessage.error('课程名不能为空')
  if (!editForm.value.isFree && (!editForm.value.price || editForm.value.price < 0)) return ElMessage.error('请输入有效价格')
  if (!editForm.value.duration || editForm.value.duration <= 0) return ElMessage.error('请输入课程时长')
  if (!editForm.value.level) return ElMessage.error('请选择难度')
  if (!editForm.value.category) return ElMessage.error('请选择分类')
  if (!editForm.value.videoObjectName) return ElMessage.error('请上传课程视频')
  try {
    let res
    const data = {
      ...editForm.value,
      videoUrl: editForm.value.videoObjectName,
      instructorId: currentUser.id,
      instructorName: currentUser.nickname || currentUser.username || '',
      createdBy: currentUser.id
      // 不传 status 字段，由后端自动设为 pending
    }
    if (editingId) {
      res = await courseApi.updateCourse(editingId, data)
    } else {
      res = await courseApi.createCourse(data)
    }
    if (res.code === 200) {
      ElMessage.success(editingId ? '课程编辑成功' : '课程添加成功，等待管理员审核')
      showAddDialog.value = false
      editingId = null
      loadCourses()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (e) {
    ElMessage.error('网络错误，操作失败')
  }
}
async function deleteCourse(row) {
  ElMessageBox.confirm('确定要删除该课程吗？', '提示', { type: 'warning' })
    .then(async () => {
      try {
        const res = await courseApi.deleteCourse(row.id)
        if (res.code === 200) {
          ElMessage.success('删除成功')
          loadCourses()
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      } catch (e) {
        ElMessage.error('网络错误，删除失败')
      }
    })
}
async function loadReviewHistory(courseId) {
  try {
    const res = await courseApi.getReviewHistory(courseId)
    if (res.code === 200) {
      publishRecords.value = res.data || []
    } else {
      publishRecords.value = []
    }
  } catch (e) {
    publishRecords.value = []
  }
}
async function submitAudit(row) {
  try {
    const res = await courseApi.submitCourseForReview(row.id)
    if (res.code === 200) {
      ElMessage.success('已提交审核，等待管理员审核')
      loadCourses()
    } else {
      ElMessage.error(res.message || '提交审核失败')
    }
  } catch (e) {
    ElMessage.error('网络错误，提交审核失败')
  }
}
async function unpublishCourse(row) {
  try {
    const res = await courseApi.unpublishCourse(row.id)
    if (res.code === 200) {
      ElMessage.success('课程已下架')
      loadCourses()
    } else {
      ElMessage.error(res.message || '下架失败')
    }
  } catch (e) {
    ElMessage.error('网络错误，下架失败')
  }
}
async function aiOptimize(field) {
  const loadingMsg = ElMessage({ message: 'AI优化中...', type: 'info', duration: 0 })
  try {
    const data = {
      title: editForm.value.title,
      description: editForm.value.description
    }
    const res = await courseApi.aiOptimize(data)
    if (res.code === 200 && res.data && res.data.optimizedText) {
      // 解析AI返回内容，假设格式为：{"title":"优化后标题","description":"优化后简介"} 或直接优化后的文本
      let aiResult
      try {
        aiResult = JSON.parse(res.data.optimizedText)
      } catch {
        aiResult = { title: res.data.optimizedText, description: res.data.optimizedText }
      }
      if (field === 'title' && aiResult.title) {
        editForm.value.title = aiResult.title
        ElMessage.success('课程名已AI优化')
      } else if (field === 'description' && aiResult.description) {
        editForm.value.description = aiResult.description
        ElMessage.success('简介已AI优化')
      } else {
        ElMessage.warning('AI未返回有效内容')
      }
    } else {
      ElMessage.error(res.message || 'AI优化失败')
    }
  } catch (e) {
    ElMessage.error('AI优化异常')
  } finally {
    loadingMsg.close()
  }
}
function getStatusType(status) {
  if (status === 'approved') return 'success'
  if (status === 'pending') return 'info'
  if (status === 'rejected') return 'danger'
  return 'warning'
}
function getStatusText(status) {
  if (status === 'approved') return '已通过'
  if (status === 'pending') return '审核中'
  if (status === 'rejected') return '未通过'
  if (status === 'draft') return '草稿'
  return status
}
function getProgressColor(progress) {
  if (progress >= 100) return '#67c23a'
  if (progress >= 60) return '#e6a23c'
  if (progress >= 30) return '#f56c6c'
  return '#909399'
}
const publishedCourses = computed(() => allCourses.value.filter(c => c.status === 'approved'))
const publishedCourseCount = computed(() => publishedCourses.value.length)
const totalPlayCount = computed(() => publishedCourses.value.reduce((sum, c) => sum + (c.viewCount || 0), 0))
const videoUploadUrl = '/api/file/upload/course-video'
const uploadHeaders = {}
function beforeVideoUpload(file) {
  const isVideo = file.type.startsWith('video/')
  if (!isVideo) { ElMessage.error('只能上传视频文件') }
  return isVideo
}
function handleVideoSuccess(res, file) {
  if (res.code === 200 && res.data && res.data.fileUrl) {
    // 只保存对象名，不保存完整 URL
    const fileUrl = res.data.fileUrl
    // 提取对象名（course-videos/xxx.mp4）
    const objectNameMatch = fileUrl.match(/course-videos\/[^/]+$/)
    if (objectNameMatch) {
      editForm.value.videoObjectName = objectNameMatch[0]
      ElMessage.success('视频上传成功')
    } else {
      ElMessage.error('视频URL解析失败')
    }
  } else {
    ElMessage.error(res.msg || '视频上传失败')
  }
}
function getVideoPreviewUrl(objectName) {
  // 只传对象名
  return `/api/file/stream?objectName=${encodeURIComponent(objectName)}`
}
const viewCourseDetail = (course) => {
  if (course.status === 'approved') {
    router.push(`/enterprise/course/${course.id}`)
  } else {
    ElMessage.warning('课程尚未发布，无法查看详情')
  }
}
onMounted(() => { loadCourses(); /* 可根据需要调用 loadReviewHistory(某课程id) */ })
</script>
<style scoped>
.enterprise-courses-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
.stats-section {
  margin-bottom: 30px;
}
.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 15px;
  transition: all 0.3s;
}
.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}
.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}
.stat-icon.primary { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.stat-icon.success { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.stat-icon.warning { background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); }
.stat-icon.info { background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%); }
.stat-content { flex: 1; }
.stat-number { font-size: 24px; font-weight: 600; color: #333; margin-bottom: 5px; }
.stat-label { font-size: 14px; color: #666; }
.actions-section { margin-bottom: 20px; display: flex; align-items: center; }
.pagination-section { display: flex; justify-content: center; margin-top: 30px; }
.publish-record-section { margin-top: 32px; }
</style> 