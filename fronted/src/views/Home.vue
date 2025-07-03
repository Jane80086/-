<template>
  <div class="taobao-layout">
    <!-- 顶部导航与搜索 -->
    <header class="taobao-header">
      <div class="logo">AI课程商城</div>
      <el-input v-model="keyword" placeholder="搜索课程/讲师/关键词..." class="search-bar" @keyup.enter="searchCourses" />
      <div class="trends">
        <span>AI热搜：</span>
        <el-tag v-for="trend in trends" :key="trend" @click="searchByTrend(trend)" class="trend-tag">{{ trend }}</el-tag>
      </div>
      <el-button type="primary" @click="goCreate">发布课程</el-button>
    </header>
    <div class="taobao-main">
      <!-- 左侧分类栏 -->
      <aside class="category-bar">
        <h4>课程分类</h4>
        <el-menu :default-active="category" @select="selectCategory">
          <el-menu-item index="">全部</el-menu-item>
          <el-menu-item v-for="cat in categories" :key="cat" :index="cat">{{ cat }}</el-menu-item>
        </el-menu>
      </aside>
      <!-- 主内容区：课程卡片瀑布流 -->
      <main class="course-list">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="course in courses" :key="course.id">
            <CourseCard :course="course" @click="viewDetail(course.id)" />
          </el-col>
        </el-row>
      </main>
      <!-- 右侧AI推荐/热门 -->
      <aside class="ai-recommend">
        <h4>AI推荐课程</h4>
        <el-card v-for="rec in aiRecommends" :key="rec.id" class="rec-card" @click="viewDetail(rec.id)">
          <div class="rec-title">{{ rec.title }}</div>
          <div class="rec-desc">{{ rec.description }}</div>
        </el-card>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import CourseCard from '../components/CourseCard.vue'

const router = useRouter()
const keyword = ref('')
const category = ref('')
const courses = ref([])
const categories = ref([])
const trends = ref([])
const aiRecommends = ref([])

const fetchCourses = async () => {
  const params = { keyword: keyword.value, category: category.value }
  const res = await axios.get('/api/course/list', { params })
  courses.value = res.data.data || []
}
const fetchCategories = async () => {
  const res = await axios.get('/api/course/categories')
  categories.value = res.data.data || []
}
const fetchTrends = async () => {
  const res = await axios.get('/api/course/trends')
  trends.value = res.data.data || []
}
const fetchAIRecommends = async () => {
  const res = await axios.get('/api/ai/recommend')
  aiRecommends.value = res.data.data || []
}
const searchCourses = () => fetchCourses()
const searchByTrend = (trend) => {
  keyword.value = trend
  fetchCourses()
}
const selectCategory = (cat) => {
  category.value = cat
  fetchCourses()
}
const viewDetail = (id) => router.push(`/course/${id}`)
const goCreate = () => router.push('/create')

onMounted(() => {
  fetchCourses()
  fetchCategories()
  fetchTrends()
  fetchAIRecommends()
})
</script>

<style scoped>
.taobao-layout { display: flex; flex-direction: column; min-height: 100vh; background: #f5f5f5; }
.taobao-header { display: flex; align-items: center; padding: 16px 32px; background: #fff; box-shadow: 0 2px 8px #f0f1f2; }
.logo { font-size: 24px; font-weight: bold; color: #409EFF; margin-right: 32px; }
.search-bar { width: 400px; margin-right: 24px; }
.trends { margin-right: 24px; color: #888; }
.trend-tag { margin-left: 8px; cursor: pointer; }
.taobao-main { display: flex; flex: 1; padding: 24px 32px; }
.category-bar { width: 180px; background: #fff; border-radius: 8px; margin-right: 24px; padding: 16px; box-shadow: 0 2px 8px #f0f1f2; }
.course-list { flex: 1; }
.ai-recommend { width: 220px; margin-left: 24px; background: #fff; border-radius: 8px; padding: 16px; box-shadow: 0 2px 8px #f0f1f2; }
.rec-card { margin-bottom: 16px; cursor: pointer; }
.rec-title { font-weight: bold; color: #409EFF; }
.rec-desc { color: #888; font-size: 13px; }
</style> 