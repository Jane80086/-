<template>
  <div class="course-list-page">
    <!-- 吸顶搜索栏 -->
    <div class="search-bar" :class="{ sticky: isSticky }" ref="searchBarRef">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索课程/讲师/标签..."
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
        <div class="hot-trends-title">热搜：</div>
        <div class="hot-trends-list">
          <el-tag
            v-for="(keyword, index) in hotKeywords"
            :key="index"
            @click.native.stop="searchByKeyword(keyword)"
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
      <el-select v-model="selectedCategory" placeholder="分类" @change="filterCourses" class="filter-item">
        <el-option label="全部" value=""></el-option>
        <el-option label="编程开发" value="编程开发"></el-option>
        <el-option label="前端开发" value="前端开发"></el-option>
        <el-option label="后端开发" value="后端开发"></el-option>
        <el-option label="数据库" value="数据库"></el-option>
        <el-option label="移动开发" value="移动开发"></el-option>
      </el-select>
      <el-select v-model="selectedLevel" placeholder="难度" @change="filterCourses" class="filter-item">
        <el-option label="全部" value=""></el-option>
        <el-option label="初级" value="初级"></el-option>
        <el-option label="中级" value="中级"></el-option>
        <el-option label="高级" value="高级"></el-option>
      </el-select>
      <el-select v-model="sortBy" placeholder="排序" @change="sortCourses" class="filter-item">
        <el-option label="最新发布" value="latest"></el-option>
        <el-option label="最多播放" value="popular"></el-option>
        <el-option label="最高评分" value="rating"></el-option>
        <el-option label="价格从低到高" value="price-asc"></el-option>
        <el-option label="价格从高到低" value="price-desc"></el-option>
      </el-select>
      <el-button @click="resetFilters" class="filter-item" type="info" plain>重置</el-button>
    </div>
    <!-- 课程卡片瀑布流 -->
    <div class="courses-masonry">
      <div
        v-for="course in displayCourses"
        :key="course.id"
        class="course-card"
        @click="() => { console.log('CourseSearch 卡片被点击，课程:', course); viewCourseDetail(course.id); }"
      >
        <div class="card-image-wrap">
          <img :src="course.imageUrl || '/class.jpg'" :alt="course.title" class="card-image" />
          <div class="card-badge" v-if="course.price === 0">免费</div>
          <div class="card-badge paid" v-else>¥{{ course.price }}</div>
          <el-button
            class="card-play-btn"
            type="primary"
            circle
            size="large"
            @click.stop="playCourse(course.id)"
          >
            <el-icon><VideoPlay /></el-icon>
          </el-button>
        </div>
        <div class="card-info">
          <div class="card-title">{{ course.title }}</div>
          <div class="card-meta">
            <span class="instructor"><el-icon><User /></el-icon>{{ course.instructorName || '未知讲师' }}</span>
            <span class="duration"><el-icon><Clock /></el-icon>{{ formatDuration(course.duration) }}</span>
          </div>
          <div class="card-desc">{{ course.description }}</div>
          <div class="card-stats">
            <span><el-icon><View /></el-icon>{{ course.viewCount || 0 }}</span>
            <el-rate v-model="course.rating" disabled show-score text-color="#409eff" score-template="{value}" />
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
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, StarFilled, VideoPlay, User, Clock, View } from '@element-plus/icons-vue'
import { courseApi } from '@/api/course'
import { adminApi } from '@/api/course'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const courses = ref([])
const hotKeywords = ref([])
const searchKeyword = ref('')
const selectedCategory = ref('')
const selectedLevel = ref('')
const sortBy = ref('latest')
const currentPage = ref(1)
const pageSize = ref(12)
const totalCourses = ref(0)
const isSticky = ref(false)
const searchBarRef = ref(null)

const displayCourses = computed(() => {
  let filtered = Array.isArray(courses.value) ? courses.value.slice() : []
  if (selectedCategory.value) {
    filtered = filtered.filter(course => course.category === selectedCategory.value)
  }
  if (selectedLevel.value) {
    filtered = filtered.filter(course => course.level === selectedLevel.value)
  }
  if (!Array.isArray(filtered)) filtered = []
  switch (sortBy.value) {
    case 'latest':
      filtered = Array.isArray(filtered) ? filtered.sort((a, b) => new Date(b.createTime) - new Date(a.createTime)) : []
      break
    case 'popular':
      filtered = Array.isArray(filtered) ? filtered.sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0)) : []
      break
    case 'rating':
      filtered = Array.isArray(filtered) ? filtered.sort((a, b) => (b.rating || 0) - (a.rating || 0)) : []
      break
    case 'price-asc':
      filtered = Array.isArray(filtered) ? filtered.sort((a, b) => (a.price || 0) - (b.price || 0)) : []
      break
    case 'price-desc':
      filtered = Array.isArray(filtered) ? filtered.sort((a, b) => (b.price || 0) - (a.price || 0)) : []
      break
  }
  return filtered
})

const loadCourses = async (keyword = '') => {
  try {
    loading.value = true
    console.log('CourseSearch 开始加载课程数据...')
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value
    }
    console.log('CourseSearch 请求参数:', params)
    // 优先用adminApi.getCourses
    const response = await adminApi.getCourses(params)
    console.log('CourseSearch API响应:', response)
    let data = response.data
    if (response.code === 200) {
      if (Array.isArray(data)) {
        courses.value = data
        totalCourses.value = data.length
      } else if (data && Array.isArray(data.records)) {
        courses.value = data.records
        totalCourses.value = data.total || data.records.length
      } else if (data && Array.isArray(data.content)) {
        courses.value = data.content
        totalCourses.value = data.totalElements || data.content.length
      } else {
        courses.value = []
        totalCourses.value = 0
      }
      console.log('CourseSearch 加载的课程数据:', courses.value)
    } else {
      ElMessage.error('获取课程列表失败')
      courses.value = []
      totalCourses.value = 0
    }
  } catch (error) {
    console.error('CourseSearch 加载课程失败:', error)
    ElMessage.error('网络错误，请稍后重试')
    courses.value = []
    totalCourses.value = 0
  } finally {
    loading.value = false
  }
}
const loadHotKeywords = async () => {
  try {
    const response = await courseApi.getHotKeywords()
    if (response.code === 200) {
      hotKeywords.value = response.data || []
    }
  } catch {}
}

const searchCourses = () => {
  loadCourses(searchKeyword.value)
}
const searchByKeyword = (keyword) => {
  searchKeyword.value = keyword
  loadCourses(keyword)
}
const filterCourses = () => {
  currentPage.value = 1
}
const sortCourses = () => {
  currentPage.value = 1
}
const resetFilters = () => {
  searchKeyword.value = ''
  selectedCategory.value = ''
  selectedLevel.value = ''
  sortBy.value = 'latest'
  loadCourses()
}
const handleSizeChange = (val) => {
  pageSize.value = val
}
const handleCurrentChange = (val) => {
  currentPage.value = val
}
const viewCourseDetail = (id) => {
  console.log('CourseSearch 点击课程卡片，ID:', id)
  router.push(`/admin/course/${id}`)
}
const playCourse = (id) => {
  console.log('CourseSearch 点击播放按钮，ID:', id)
  router.push(`/admin/course/${id}/play`)
}
const formatDuration = (min) => {
  if (!min) return '0分钟'
  const h = Math.floor(min / 60)
  const m = min % 60
  return h ? `${h}小时${m}分钟` : `${m}分钟`
}

onMounted(() => {
  loadCourses()
  loadHotKeywords()
})
</script>

<style scoped>
.course-list-page {
  background: #f6faff;
  min-height: 100vh;
  padding: 0 0 32px 0;
}
.search-bar {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px #e4e7ed;
  padding: 24px 32px 12px 32px;
  margin: 24px 0 16px 0;
  position: relative;
  z-index: 10;
  transition: box-shadow 0.2s;
}
.search-bar.sticky {
  position: sticky;
  top: 0;
}
.filter-bar {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 6px #e4e7ed;
  padding: 16px 32px;
  margin-bottom: 24px;
  display: flex;
  gap: 16px;
  align-items: center;
}
.courses-masonry {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  justify-content: flex-start;
  margin: 0 32px 32px 32px;
}
.course-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px #e4e7ed;
  width: 320px;
  margin-bottom: 24px;
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.course-card:hover {
  box-shadow: 0 6px 24px #b3c0d1;
  transform: translateY(-4px) scale(1.02);
}
.card-image-wrap {
  position: relative;
  width: 100%;
  height: 180px;
  overflow: hidden;
}
.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 16px 16px 0 0;
}
.card-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  background: #67c23a;
  color: #fff;
  border-radius: 8px;
  padding: 2px 10px;
  font-size: 13px;
  font-weight: bold;
}
.card-badge.paid {
  background: #f56c6c;
}
.card-play-btn {
  position: absolute;
  right: 16px;
  bottom: 16px;
  box-shadow: 0 2px 8px #e4e7ed;
}
.card-info {
  padding: 18px 20px 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.card-title {
  font-size: 18px;
  font-weight: bold;
  color: #222;
  margin-bottom: 4px;
}
.card-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  color: #666;
  font-size: 13px;
}
.card-desc {
  color: #888;
  font-size: 13px;
  margin-bottom: 8px;
  min-height: 36px;
}
.card-stats {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
  color: #888;
}
.loading {
  padding: 40px;
  text-align: center;
}
.hot-trends-scroll {
  display: flex;
  align-items: center;
  margin-top: 12px;
}
.hot-trends-title {
  font-size: 14px;
  color: #888;
  margin-right: 8px;
}
.hot-trends-list {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.trend-tag {
  cursor: pointer;
  font-size: 13px;
  border-radius: 8px;
  padding: 2px 10px;
}
.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style> 