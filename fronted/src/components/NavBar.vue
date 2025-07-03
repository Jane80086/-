<template>
  <header class="nav-bar">
    <div class="logo">AI课程商城</div>
    <el-input v-model="keyword" placeholder="搜索课程/讲师/关键词..." class="search-bar" @keyup.enter="searchCourses" />
    <div class="trends">
      <span>AI热搜：</span>
      <el-tag v-for="trend in trends" :key="trend" @click="searchByTrend(trend)" class="trend-tag">{{ trend }}</el-tag>
    </div>
    <el-button type="primary" @click="goCreate">发布课程</el-button>
    <el-button @click="goMyCourses">我的课程</el-button>
  </header>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
const router = useRouter()
const keyword = ref('')
const trends = ref([])
const searchCourses = () => router.push({ path: '/', query: { keyword: keyword.value } })
const searchByTrend = (trend) => router.push({ path: '/', query: { keyword: trend } })
const goCreate = () => router.push('/create')
const goMyCourses = () => router.push('/my-courses')
const fetchTrends = async () => {
  const res = await axios.get('/api/course/trends')
  trends.value = res.data.data || []
}
onMounted(fetchTrends)
</script>

<style scoped>
.nav-bar { display: flex; align-items: center; padding: 16px 32px; background: #fff; box-shadow: 0 2px 8px #f0f1f2; }
.logo { font-size: 24px; font-weight: bold; color: #409EFF; margin-right: 32px; }
.search-bar { width: 400px; margin-right: 24px; }
.trends { margin-right: 24px; color: #888; }
.trend-tag { margin-left: 8px; cursor: pointer; }
</style> 