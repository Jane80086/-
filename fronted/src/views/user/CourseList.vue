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
          <div class="course-title">{{ course.title }}</div>
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
  let filtered = courses.value
  if (selectedCategory.value) {
    filtered = filtered.filter(course => course.category === selectedCategory.value)
  }
  if (selectedLevel.value) {
    filtered = filtered.filter(course => course.level === selectedLevel.value)
  }
  switch (sortBy.value) {
    case 'latest':
      filtered.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
      break
    case 'popular':
      filtered.sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0))
      break
    case 'rating':
      filtered.sort((a, b) => (b.rating || 0) - (a.rating || 0))
      break
    case 'price-asc':
      filtered.sort((a, b) => (a.price || 0) - (b.price || 0))
      break
    case 'price-desc':
      filtered.sort((a, b) => (b.price || 0) - (a.price || 0))
      break
  }
  return filtered
})

const loadCourses = async (keyword = '') => {
  try {
    loading.value = true
    const response = await courseApi.getCourseList()
    if (response.code === 200) {
      let allCourses = response.data || []
      if (keyword) {
        const kw = keyword.trim().toLowerCase()
        allCourses = allCourses.filter(course =>
          course.title.toLowerCase().includes(kw) ||
          course.description.toLowerCase().includes(kw) ||
          (course.instructorName && course.instructorName.toLowerCase().includes(kw))
        )
      }
      courses.value = allCourses
      totalCourses.value = courses.value.length
    } else {
      ElMessage.error('获取课程列表失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
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
const searchCourses = async () => {
  await loadCourses(searchKeyword.value)
}
const searchByKeyword = (keyword) => {
  searchKeyword.value = keyword
  router.push({ path: '/user/course', query: { keyword } })
  loadCourses(keyword)
}
const filterCourses = () => {}
const sortCourses = () => {}
const resetFilters = () => {
  selectedCategory.value = ''
  selectedLevel.value = ''
  sortBy.value = 'latest'
  searchKeyword.value = ''
  loadCourses()
}
const viewCourseDetail = (courseId) => {
  router.push(`/user/course/${courseId}`)
}
const playCourse = (courseId) => {
  router.push(`/user/course/${courseId}/play`)
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
  loadCourses()
}
const handleCurrentChange = (page) => {
  currentPage.value = page
  loadCourses()
}
// 吸顶效果
const handleScroll = () => {
  if (!searchBarRef.value) return
  const rect = searchBarRef.value.getBoundingClientRect()
  isSticky.value = rect.top <= 0
}
onMounted(() => {
  let kw = route.query.keyword || ''
  searchKeyword.value = kw
  loadCourses(kw)
  loadHotKeywords()
  window.addEventListener('scroll', handleScroll)
})
</script>

<style scoped>
/* 恢复初始样式，仅保留最基础的element-plus风格和简单布局 */
.course-list-page {
  background: #fff;
  min-height: 100vh;
  padding-bottom: 40px;
}
.search-bar {
  padding: 32px 24px 12px 24px;
  box-shadow: 0 2px 12px rgba(64,158,255,0.08);
  border-radius: 0 0 18px 18px;
  margin-bottom: 18px;
  position: relative;
  z-index: 10;
  transition: box-shadow 0.3s;
}
.search-bar.sticky {
  position: sticky;
  top: 0;
  box-shadow: 0 4px 16px rgba(64,158,255,0.12);
}
.search-input {
  width: 100%;
  max-width: 600px;
  margin-bottom: 10px;
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
.hot-trends-list .el-tag {
  margin-right: 8px;
}
.filter-bar {
  display: flex;
  gap: 16px;
  align-items: center;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(64,158,255,0.06);
  padding: 18px 24px;
  margin-bottom: 24px;
  position: sticky;
  top: 80px;
  z-index: 5;
}
.filter-item {
  min-width: 120px;
}
.courses-masonry {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 32px 24px;
  padding: 0 24px;
}
.course-card {
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 4px 24px rgba(64,158,255,0.10);
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  position: relative;
  min-height: 420px;
}
.course-card:hover {
  transform: translateY(-8px) scale(1.03);
  box-shadow: 0 12px 32px rgba(64,158,255,0.18);
}
.card-image-wrap {
  height: 180px;
  overflow: hidden;
  background: #eaf3ff;
}
.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 0 0 12px 12px;
  transition: transform 0.3s;
}
.course-card:hover .card-image {
  transform: scale(1.06);
}
.card-badge {
  position: absolute;
  top: 14px;
  left: 14px;
  background: #67c23a;
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  border-radius: 12px;
  padding: 2px 12px;
  z-index: 2;
}
.card-badge.paid {
  background: #409eff;
}
.card-title {
  font-size: 18px;
  font-weight: 700;
  color: #222;
  margin-bottom: 2px;
  line-height: 1.3;
  height: 48px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.card-meta {
  display: flex;
  gap: 18px;
  color: #888;
  font-size: 13px;
  align-items: center;
}
.card-desc {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
  height: 38px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.card-stats {
  display: flex;
  align-items: center;
  gap: 18px;
  margin-top: 6px;
}
.card-play-btn {
  position: absolute;
  right: 18px;
  bottom: 18px;
  z-index: 2;
  box-shadow: 0 2px 8px rgba(64,158,255,0.18);
}
.pagination-section {
  display: flex;
  justify-content: center;
  margin: 36px 0 0 0;
}
.loading {
  padding: 40px;
  text-align: center;
}
@media (max-width: 900px) {
  .courses-masonry {
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
    gap: 18px 10px;
  }
  .filter-bar {
    flex-wrap: wrap;
    gap: 8px;
    padding: 10px 8px;
  }
}
@media (max-width: 600px) {
  .search-bar {
    padding: 18px 6px 8px 6px;
  }
  .courses-masonry {
    padding: 0 4px;
  }
}
</style> 
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
          <div class="course-title">{{ course.title }}</div>
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
  let filtered = courses.value
  if (selectedCategory.value) {
    filtered = filtered.filter(course => course.category === selectedCategory.value)
  }
  if (selectedLevel.value) {
    filtered = filtered.filter(course => course.level === selectedLevel.value)
  }
  switch (sortBy.value) {
    case 'latest':
      filtered.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
      break
    case 'popular':
      filtered.sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0))
      break
    case 'rating':
      filtered.sort((a, b) => (b.rating || 0) - (a.rating || 0))
      break
    case 'price-asc':
      filtered.sort((a, b) => (a.price || 0) - (b.price || 0))
      break
    case 'price-desc':
      filtered.sort((a, b) => (b.price || 0) - (a.price || 0))
      break
  }
  return filtered
})

const loadCourses = async (keyword = '') => {
  try {
    loading.value = true
    const response = await courseApi.getCourseList()
    if (response.code === 200) {
      let allCourses = response.data || []
      if (keyword) {
        const kw = keyword.trim().toLowerCase()
        allCourses = allCourses.filter(course =>
          course.title.toLowerCase().includes(kw) ||
          course.description.toLowerCase().includes(kw) ||
          (course.instructorName && course.instructorName.toLowerCase().includes(kw))
        )
      }
      courses.value = allCourses
      totalCourses.value = courses.value.length
    } else {
      ElMessage.error('获取课程列表失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
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
const searchCourses = async () => {
  await loadCourses(searchKeyword.value)
}
const searchByKeyword = (keyword) => {
  searchKeyword.value = keyword
  router.push({ path: '/user/course', query: { keyword } })
  loadCourses(keyword)
}
const filterCourses = () => {}
const sortCourses = () => {}
const resetFilters = () => {
  selectedCategory.value = ''
  selectedLevel.value = ''
  sortBy.value = 'latest'
  searchKeyword.value = ''
  loadCourses()
}
const viewCourseDetail = (courseId) => {
  router.push(`/user/course/${courseId}`)
}
const playCourse = (courseId) => {
  router.push(`/user/course/${courseId}/play`)
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
  loadCourses()
}
const handleCurrentChange = (page) => {
  currentPage.value = page
  loadCourses()
}
// 吸顶效果
const handleScroll = () => {
  if (!searchBarRef.value) return
  const rect = searchBarRef.value.getBoundingClientRect()
  isSticky.value = rect.top <= 0
}
onMounted(() => {
  let kw = route.query.keyword || ''
  searchKeyword.value = kw
  loadCourses(kw)
  loadHotKeywords()
  window.addEventListener('scroll', handleScroll)
})
</script>

<style scoped>
/* 恢复初始样式，仅保留最基础的element-plus风格和简单布局 */
.course-list-page {
  background: #fff;
  min-height: 100vh;
  padding-bottom: 40px;
}
.search-bar {
  padding: 32px 24px 12px 24px;
  box-shadow: 0 2px 12px rgba(64,158,255,0.08);
  border-radius: 0 0 18px 18px;
  margin-bottom: 18px;
  position: relative;
  z-index: 10;
  transition: box-shadow 0.3s;
}
.search-bar.sticky {
  position: sticky;
  top: 0;
  box-shadow: 0 4px 16px rgba(64,158,255,0.12);
}
.search-input {
  width: 100%;
  max-width: 600px;
  margin-bottom: 10px;
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
.hot-trends-list .el-tag {
  margin-right: 8px;
}
.filter-bar {
  display: flex;
  gap: 16px;
  align-items: center;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(64,158,255,0.06);
  padding: 18px 24px;
  margin-bottom: 24px;
  position: sticky;
  top: 80px;
  z-index: 5;
}
.filter-item {
  min-width: 120px;
}
.courses-masonry {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 32px 24px;
  padding: 0 24px;
}
.course-card {
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 4px 24px rgba(64,158,255,0.10);
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  position: relative;
  min-height: 420px;
}
.course-card:hover {
  transform: translateY(-8px) scale(1.03);
  box-shadow: 0 12px 32px rgba(64,158,255,0.18);
}
.card-image-wrap {
  height: 180px;
  overflow: hidden;
  background: #eaf3ff;
}
.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 0 0 12px 12px;
  transition: transform 0.3s;
}
.course-card:hover .card-image {
  transform: scale(1.06);
}
.card-badge {
  position: absolute;
  top: 14px;
  left: 14px;
  background: #67c23a;
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  border-radius: 12px;
  padding: 2px 12px;
  z-index: 2;
}
.card-badge.paid {
  background: #409eff;
}
.card-title {
  font-size: 18px;
  font-weight: 700;
  color: #222;
  margin-bottom: 2px;
  line-height: 1.3;
  height: 48px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.card-meta {
  display: flex;
  gap: 18px;
  color: #888;
  font-size: 13px;
  align-items: center;
}
.card-desc {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
  height: 38px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.card-stats {
  display: flex;
  align-items: center;
  gap: 18px;
  margin-top: 6px;
}
.card-play-btn {
  position: absolute;
  right: 18px;
  bottom: 18px;
  z-index: 2;
  box-shadow: 0 2px 8px rgba(64,158,255,0.18);
}
.pagination-section {
  display: flex;
  justify-content: center;
  margin: 36px 0 0 0;
}
.loading {
  padding: 40px;
  text-align: center;
}
@media (max-width: 900px) {
  .courses-masonry {
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
    gap: 18px 10px;
  }
  .filter-bar {
    flex-wrap: wrap;
    gap: 8px;
    padding: 10px 8px;
  }
}
@media (max-width: 600px) {
  .search-bar {
    padding: 18px 6px 8px 6px;
  }
  .courses-masonry {
    padding: 0 4px;
  }
}
</style> 