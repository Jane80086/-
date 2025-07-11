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
        @click="viewCourseDetail(course.id)"
      >
        <div class="card-image-wrap">
          <img :src="course.imageUrl || '/default-course.jpg'" :alt="course.title" class="card-image" />
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
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, StarFilled, VideoPlay, User, Clock, View } from '@element-plus/icons-vue'
import { courseApi } from '@/api/course'
const router = useRouter()
const loading = ref(false)
const hotKeywords = ref(['前端', 'Java', 'AI', '大数据', 'Python'])
const searchKeyword = ref('')
const selectedCategory = ref('')
const selectedLevel = ref('')
const sortBy = ref('latest')
const currentPage = ref(1)
const pageSize = ref(12)
const totalCourses = ref(0)
const isSticky = ref(false)
const searchBarRef = ref(null)
const courses = ref([])
const loadCourses = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value, // 改为从1开始
      size: pageSize.value,
      keyword: searchKeyword.value,
      category: selectedCategory.value,
      level: selectedLevel.value,
      sortBy: sortBy.value
    }
    const res = await courseApi.getCourseList(params)
    if (res && res.code === 200) {
      if (Array.isArray(res.data)) {
        courses.value = res.data
        totalCourses.value = res.data.length
      } else if (res.data && Array.isArray(res.data.content)) {
        courses.value = res.data.content
        totalCourses.value = res.data.totalElements || res.data.content.length
      } else {
        courses.value = []
        totalCourses.value = 0
      }
    } else {
      courses.value = []
      totalCourses.value = 0
      ElMessage.error((res && res.message) || '获取课程列表失败')
    }
  } catch (e) {
    courses.value = []
    totalCourses.value = 0
    ElMessage.error(e?.message || '网络错误，获取课程列表失败')
  } finally {
    loading.value = false
  }
}
const filteredCourses = computed(() => {
  let filtered = courses.value
  // 只展示已审核通过/已发布的课程，兼容多种状态
  filtered = filtered.filter(course => {
    const status = (course.status || '').toLowerCase()
    return status === 'published' || status === 'approved'
  })
  if (searchKeyword.value) {
    const kw = searchKeyword.value.trim().toLowerCase()
    filtered = filtered.filter(course =>
      course.title.toLowerCase().includes(kw) ||
      course.description.toLowerCase().includes(kw) ||
      (course.instructorName && course.instructorName.toLowerCase().includes(kw))
    )
  }
  if (selectedCategory.value) {
    filtered = filtered.filter(course => course.category === selectedCategory.value)
  }
  if (selectedLevel.value) {
    filtered = filtered.filter(course => course.level === selectedLevel.value)
  }
  switch (sortBy.value) {
    case 'latest':
      filtered = [...filtered].sort((a, b) => b.id - a.id)
      break
    case 'popular':
      filtered = [...filtered].sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0))
      break
    case 'rating':
      filtered = [...filtered].sort((a, b) => (b.rating || 0) - (a.rating || 0))
      break
    case 'price-asc':
      filtered = [...filtered].sort((a, b) => (a.price || 0) - (b.price || 0))
      break
    case 'price-desc':
      filtered = [...filtered].sort((a, b) => (b.price || 0) - (a.price || 0))
      break
  }
  return filtered
})
const displayCourses = computed(() => {
  totalCourses.value = filteredCourses.value.length
  return filteredCourses.value.slice((currentPage.value-1)*pageSize.value, currentPage.value*pageSize.value)
})
const searchCourses = () => {
  currentPage.value = 1
}
const searchByKeyword = (keyword) => {
  searchKeyword.value = keyword
  searchCourses()
}
const filterCourses = () => {
  currentPage.value = 1
}
const sortCourses = () => {
  currentPage.value = 1
}
const resetFilters = () => {
  selectedCategory.value = ''
  selectedLevel.value = ''
  sortBy.value = 'latest'
  searchKeyword.value = ''
  currentPage.value = 1
}
const viewCourseDetail = (courseId) => {
  router.push(`/enterprise/course/${courseId}`)
}
const playCourse = (courseId) => {
  router.push(`/enterprise/course/${courseId}/play`)
}
const formatDuration = (minutes) => {
  if (!minutes) return '0分钟'
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return hours > 0 ? `${hours}小时${mins}分钟` : `${mins}分钟`
}
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}
const handleCurrentChange = (page) => {
  currentPage.value = page
}
const handleScroll = () => {
  if (!searchBarRef.value) return
  const rect = searchBarRef.value.getBoundingClientRect()
  isSticky.value = rect.top <= 0
}
onMounted(loadCourses)
onMounted(() => {
  window.addEventListener('scroll', handleScroll)
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
  box-shadow: 0 4px 16px #dcdfe6;
}
.search-input {
  width: 420px;
  margin-bottom: 8px;
  border-radius: 10px;
  font-size: 16px;
}
.hot-trends-scroll {
  display: flex;
  align-items: center;
  margin-top: 4px;
}
.hot-trends-title {
  color: #409eff;
  font-weight: bold;
  margin-right: 8px;
}
.hot-trends-list {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.trend-tag {
  cursor: pointer;
  font-size: 14px;
  border-radius: 8px;
  padding: 2px 12px;
  background: #f0f7ff;
  color: #409eff;
  border: 1px solid #b3d8ff;
  transition: background 0.2s, color 0.2s;
}
.trend-tag.primary {
  background: #409eff;
  color: #fff;
  border: none;
}
.trend-tag:hover {
  background: #ecf5ff;
  color: #337ecc;
}
.filter-bar {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-bottom: 18px;
  margin-left: 32px;
}
.filter-item {
  min-width: 120px;
  border-radius: 10px;
}
.el-button {
  border-radius: 10px;
  font-size: 16px;
  padding: 8px 28px;
  background: #409eff;
  color: #fff;
  border: none;
  box-shadow: 0 2px 8px #e4e7ed;
  transition: background 0.2s;
}
.el-button:hover {
  background: #337ecc;
}
.courses-masonry {
  display: flex;
  flex-wrap: wrap;
  gap: 32px 24px;
  justify-content: flex-start;
  margin: 0 32px;
}
.course-card {
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 2px 12px #e4e7ed;
  width: 340px;
  margin-bottom: 24px;
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s;
  overflow: hidden;
  position: relative;
  display: flex;
  flex-direction: column;
}
.course-card:hover {
  box-shadow: 0 8px 32px #b3c6e0;
  transform: translateY(-4px) scale(1.02);
}
.card-image-wrap {
  position: relative;
  width: 100%;
  height: 160px;
  background: #f0f7ff;
  display: flex;
  align-items: center;
  justify-content: center;
}
.card-image {
  max-width: 100%;
  max-height: 100%;
  border-radius: 12px;
  object-fit: cover;
}
.card-badge {
  position: absolute;
  top: 12px;
  left: 16px;
  background: #67c23a;
  color: #fff;
  font-size: 15px;
  font-weight: bold;
  border-radius: 8px;
  padding: 2px 14px;
  z-index: 2;
  box-shadow: 0 2px 8px #d4f4dd;
}
.card-badge.paid {
  background: #409eff;
  left: auto;
  right: 16px;
}
.card-play-btn {
  position: absolute;
  bottom: 12px;
  right: 16px;
  z-index: 2;
  box-shadow: 0 2px 8px #e4e7ed;
}
.card-info {
  padding: 18px 20px 14px 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.card-title {
  font-size: 20px;
  font-weight: bold;
  color: #222;
  margin-bottom: 2px;
}
.card-meta {
  display: flex;
  gap: 18px;
  color: #888;
  font-size: 15px;
  align-items: center;
}
.card-desc {
  color: #666;
  font-size: 15px;
  margin-bottom: 4px;
  min-height: 32px;
}
.card-stats {
  display: flex;
  align-items: center;
  gap: 18px;
  color: #888;
  font-size: 15px;
}
.pagination-section {
  margin: 32px 0 0 0;
  display: flex;
  justify-content: center;
}
.loading {
  margin: 32px 0;
  display: flex;
  justify-content: center;
}
@media (max-width: 1200px) {
  .courses-masonry {
    gap: 24px 12px;
    margin: 0 8px;
  }
  .course-card {
    width: 280px;
  }
}
@media (max-width: 800px) {
  .search-bar, .filter-bar {
    margin-left: 0;
    margin-right: 0;
    padding: 12px 8px;
  }
  .courses-masonry {
    gap: 16px 8px;
    margin: 0 2px;
  }
  .course-card {
    width: 98vw;
    min-width: 0;
    max-width: 100%;
  }
}
</style> 