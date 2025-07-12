<template>
  <div class="course-audit-page">
    <!-- 吸顶搜索栏 -->
    <div class="search-bar" :class="{ sticky: isSticky }" ref="searchBarRef">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索课程名称/创建人/描述..."
        class="search-input"
        @keyup.enter="searchCourses"
        clearable
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
        <template #append>
          <el-button @click="searchCourses" type="primary">搜索</el-button>
        </template>
      </el-input>
      <div class="hot-trends-scroll">
        <div class="hot-trends-title">快速筛选：</div>
        <div class="hot-trends-list">
          <el-tag
            v-for="(keyword, index) in hotKeywords"
            :key="index"
            @click="searchByKeyword(keyword)"
            class="trend-tag"
            :type="index < 3 ? 'primary' : 'info'"
            effect="dark"
          >
            <el-icon v-if="index === 0"><StarFilled /></el-icon>
            {{ keyword }}
          </el-tag>
        </div>
      </div>
    </div>

    <!-- 筛选区 -->
    <div class="filter-bar">
      <el-select v-model="statusFilter" placeholder="审核状态" @change="filterCourses" class="filter-item">
        <el-option label="全部状态" value=""></el-option>
        <el-option label="待审核" value="pending"></el-option>
        <el-option label="已通过" value="approved"></el-option>
        <el-option label="已驳回" value="rejected"></el-option>
      </el-select>
      <el-select v-model="selectedCategory" placeholder="分类" @change="filterCourses" class="filter-item">
        <el-option label="全部" value=""></el-option>
        <el-option label="编程开发" value="编程开发"></el-option>
        <el-option label="前端开发" value="前端开发"></el-option>
        <el-option label="后端开发" value="后端开发"></el-option>
        <el-option label="数据库" value="数据库"></el-option>
        <el-option label="移动开发" value="移动开发"></el-option>
      </el-select>
      <el-select v-model="sortBy" placeholder="排序" @change="sortCourses" class="filter-item">
        <el-option label="最新提交" value="latest"></el-option>
        <el-option label="最早提交" value="earliest"></el-option>
        <el-option label="课程名称" value="title"></el-option>
        <el-option label="创建人" value="creator"></el-option>
      </el-select>
      <el-button @click="resetFilters" class="filter-item" type="info" plain>重置</el-button>
    </div>

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
              <div class="stat-number">{{ pendingCourses }}</div>
              <div class="stat-label">待审核</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ approvedCourses }}</div>
              <div class="stat-label">已通过</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ rejectedCourses }}</div>
              <div class="stat-label">已驳回</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 课程卡片瀑布流 -->
    <div class="courses-masonry">
      <div
        v-for="course in displayCourses"
        :key="course.id"
        class="course-card"
        @click="() => { console.log('卡片被点击，课程:', course); viewCourseDetail(course.id); }"
      >
        <div class="card-image-wrap">
                      <img :src="course.imageUrl || '/class.jpg'" :alt="course.title" class="card-image" />
          <div class="card-badge" :class="getStatusClass(course.status)">
            {{ getStatusText(course.status) }}
          </div>
          <el-button
            class="card-play-btn"
            type="primary"
            circle
            size="large"
            @click.stop="viewCourseDetail(course.id)"
          >
            <el-icon><View /></el-icon>
          </el-button>
        </div>
        <div class="card-info">
          <div class="card-title">{{ course.title }}</div>
          <div class="card-meta">
            <span class="creator"><el-icon><User /></el-icon>{{ course.creator }}</span>
            <span class="created-time"><el-icon><Clock /></el-icon>{{ formatDate(course.createdAt) }}</span>
          </div>
          <div class="card-desc">{{ course.description }}</div>
          <div class="card-stats">
            <span class="chapters-count"><el-icon><Document /></el-icon>{{ course.chapters?.length || 0 }} 章节</span>
            <div class="card-actions">
              <el-button v-if="(course.status || '').toLowerCase() === 'pending'" type="success" size="small" @click.stop="approveCourse(course)">
                通过
              </el-button>
              <el-button v-if="(course.status || '').toLowerCase() === 'pending'" type="danger" size="small" @click.stop="rejectCourse(course)">
                驳回
              </el-button>
              <el-button size="small" @click.stop="viewCourseDetail(course.id)">
                查看详情
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-section">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[12, 24, 36, 48]"
        :total="totalCourses"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <div v-if="loading" class="loading">
      <el-skeleton :rows="6" animated />
    </div>

    <!-- 驳回理由弹窗 -->
    <el-dialog v-model="rejectVisible" title="驳回理由" width="400px">
      <el-form>
        <el-form-item label="驳回理由" required>
          <el-input
            v-model="rejectReason"
            type="textarea"
            :rows="4"
            placeholder="请输入驳回理由..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, StarFilled, View, User, Clock, Document } from '@element-plus/icons-vue'
import { adminApi } from '@/api/course'
import { useUserStore } from '@/store/user'
const userStore = useUserStore()

const router = useRouter()
const loading = ref(false)
const hotKeywords = ref(['待审核', '已通过', '已驳回', 'Java', '前端', 'Python'])
const searchKeyword = ref('')
const statusFilter = ref('')
const selectedCategory = ref('')
const sortBy = ref('latest')
const currentPage = ref(1)
const pageSize = ref(12)
const isSticky = ref(false)
const searchBarRef = ref(null)
const rejectVisible = ref(false)
const rejectReason = ref('')
const currentCourse = ref(null)

// 模拟审核数据
const courses = ref([])
const loadCourses = async () => {
  loading.value = true
  console.log('开始加载课程数据...')
  const res = await adminApi.getPendingCourses()
  console.log('API响应:', res)
  if (res.code === 200) {
    // 兼容 status 大小写，允许后端返回所有课程，前端统一过滤
    const allCourses = res.data.content || res.data || []
    console.log('所有课程:', allCourses)
    courses.value = allCourses.filter(c => (c.status || '').toLowerCase() === 'pending')
    console.log('过滤后的待审核课程:', courses.value)
  } else {
    courses.value = []
  }
  loading.value = false
}

// 计算属性
const filteredCourses = computed(() => {
  let filtered = courses.value
  if (searchKeyword.value) {
    const kw = searchKeyword.value.trim().toLowerCase()
    filtered = filtered.filter(course =>
      (course.title || '').toLowerCase().includes(kw) ||
      (course.creator || '').toLowerCase().includes(kw) ||
      (course.description || '').toLowerCase().includes(kw)
    )
  }
  if (statusFilter.value) {
    filtered = filtered.filter(course => (course.status || '').toLowerCase() === statusFilter.value.toLowerCase())
  }
  if (selectedCategory.value) {
    filtered = filtered.filter(course => course.category === selectedCategory.value)
  }
  switch (sortBy.value) {
    case 'latest':
      filtered = [...filtered].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
      break
    case 'earliest':
      filtered = [...filtered].sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
      break
    case 'title':
      filtered = [...filtered].sort((a, b) => (a.title || '').localeCompare(b.title || ''))
      break
    case 'creator':
      filtered = [...filtered].sort((a, b) => (a.creator || '').localeCompare(b.creator || ''))
      break
  }
  return filtered
})

const displayCourses = computed(() => {
  totalCourses.value = filteredCourses.value.length
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredCourses.value.slice(start, end)
})

const totalCourses = computed(() => filteredCourses.value.length)
const pendingCourses = computed(() => filteredCourses.value.filter(course => course.status === 'pending').length)
const approvedCourses = computed(() => filteredCourses.value.filter(course => course.status === 'approved').length)
const rejectedCourses = computed(() => filteredCourses.value.filter(course => course.status === 'rejected').length)

// 方法
const searchCourses = () => {
  currentPage.value = 1
}

const searchByKeyword = (keyword) => {
  if (['待审核', '已通过', '已驳回'].includes(keyword)) {
    statusFilter.value = keyword === '待审核' ? 'pending' : keyword === '已通过' ? 'approved' : 'rejected'
  } else {
    searchKeyword.value = keyword
  }
  searchCourses()
}

const filterCourses = () => {
  currentPage.value = 1
}

const sortCourses = () => {
  currentPage.value = 1
}

const resetFilters = () => {
  statusFilter.value = ''
  selectedCategory.value = ''
  sortBy.value = 'latest'
  searchKeyword.value = ''
  currentPage.value = 1
}

const viewCourseDetail = (courseId) => {
  console.log('=== 点击课程卡片 ===')
  console.log('课程ID:', courseId)
  console.log('课程ID类型:', typeof courseId)
  console.log('router对象:', router)
  
  if (!courseId || isNaN(Number(courseId)) || Number(courseId) <= 0) {
    ElMessage.error(`课程ID无效：${courseId}`)
    return
  }
  
  const targetPath = `/admin/course/${courseId}`
  console.log('准备跳转到:', targetPath)
  
  try {
    router.push(targetPath)
    console.log('路由跳转成功')
  } catch (error) {
    console.error('路由跳转失败:', error)
    ElMessage.error('跳转失败：' + error.message)
  }
}

const approveCourse = async (course) => {
  try {
    const reviewerId = userStore.user?.id || ''
    const res = await adminApi.reviewCourse(course.id, 'approved', '', reviewerId)
    ElMessage.success('审核通过！')
    loadCourses()
  } catch (e) {
    ElMessage.error('审核失败')
  }
}

const rejectCourse = (course) => {
  currentCourse.value = course
  rejectVisible.value = true
}

const confirmReject = async () => {
  try {
    const reviewerId = userStore.user?.id || ''
    const res = await adminApi.reviewCourse(currentCourse.value.id, 'rejected', rejectReason.value, reviewerId)
    ElMessage.success('已驳回！')
    rejectVisible.value = false
    rejectReason.value = ''
    loadCourses()
  } catch (e) {
    ElMessage.error('驳回失败')
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
    case 'approved': return '已通过'
    case 'pending': return '待审核'
    case 'rejected': return '已驳回'
    default: return '未知'
  }
}

const getStatusClass = (status) => {
  switch (status) {
    case 'approved': return 'approved'
    case 'pending': return 'pending'
    case 'rejected': return 'rejected'
    default: return ''
  }
}

const formatDate = (date) => {
  if (!date) return '未知'
  return new Date(date).toLocaleDateString('zh-CN')
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

// 组件挂载
onMounted(() => {
  console.log('=== CourseAudit 组件挂载 ===')
  console.log('router对象:', router)
  console.log('useRouter函数:', useRouter)
  loadCourses()
})
</script>

<style scoped>
.course-audit-page {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.search-bar {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.search-bar.sticky {
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.search-input {
  margin-bottom: 16px;
}

.hot-trends-scroll {
  display: flex;
  align-items: center;
  gap: 12px;
  overflow-x: auto;
  padding: 8px 0;
}

.hot-trends-title {
  font-size: 14px;
  color: #666;
  white-space: nowrap;
}

.hot-trends-list {
  display: flex;
  gap: 8px;
  flex-wrap: nowrap;
}

.trend-tag {
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.3s ease;
}

.trend-tag:hover {
  transform: translateY(-2px);
}

.filter-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.filter-item {
  min-width: 120px;
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

.courses-masonry {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.course-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
}

.course-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

.card-image-wrap {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.course-card:hover .card-image {
  transform: scale(1.05);
}

.card-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
  color: white;
}

.card-badge.pending {
  background: #e6a23c;
}

.card-badge.approved {
  background: #67c23a;
}

.card-badge.rejected {
  background: #f56c6c;
}

.card-play-btn {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  opacity: 0;
  transition: all 0.3s ease;
}

.course-card:hover .card-play-btn {
  opacity: 1;
}

.card-info {
  padding: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
  color: #2D3A4B;
  margin-bottom: 12px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
  font-size: 14px;
  color: #666;
}

.card-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-desc {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chapters-count {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #409eff;
  font-size: 14px;
}

.card-actions {
  display: flex;
  gap: 8px;
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

.loading {
  padding: 40px;
  text-align: center;
}

@media (max-width: 768px) {
  .courses-masonry {
    grid-template-columns: 1fr;
  }
  
  .filter-bar {
    flex-direction: column;
  }
  
  .filter-item {
    min-width: auto;
  }
  
  .card-actions {
    flex-direction: column;
    gap: 4px;
  }
}
</style> 