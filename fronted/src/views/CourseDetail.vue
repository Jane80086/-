<template>
  <div class="detail-layout">
    <div class="main-info">
      <img v-if="course.coverImage" :src="course.coverImage" class="cover-big" />
      <div class="info-block">
        <h2 class="title">{{ course.title }}</h2>
        <div class="instructor">讲师：{{ course.instructorName || course.teacher }}</div>
        <div class="desc">{{ course.description }}</div>
        <el-rate v-model="course.aiScore" disabled show-score text-color="#ff9900" score-template="AI评分：{value}" />
        <div class="price">￥{{ course.price || 0 }}</div>
        <div class="actions">
          <el-button type="primary" size="large" @click="startLearning">
            <el-icon><VideoPlay /></el-icon>
            开始学习
          </el-button>
          <el-button type="success" plain @click="collect">收藏</el-button>
          <el-button type="warning" plain @click="like">点赞</el-button>
          <el-button type="info" plain @click="share">分享</el-button>
        </div>
      </div>
    </div>
    <el-card class="ai-qa">
      <h4>AI问答</h4>
      <el-input v-model="question" placeholder="请输入你的问题..." style="width:300px" @keyup.enter="askAI" />
      <el-button type="primary" @click="askAI" style="margin-left:8px;">提问</el-button>
      <div v-if="answer" style="margin-top:12px;color:#409EFF;">AI：{{ answer }}</div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { VideoPlay } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const course = ref({})
const question = ref('')
const answer = ref('')

const fetchCourse = async () => {
  try {
    const res = await axios.get(`/api/course/${route.params.id}`)
    course.value = res.data.data
    const scoreRes = await axios.get(`/api/ai/score/${route.params.id}`)
    course.value.aiScore = scoreRes.data.data || 0
  } catch (e) {
    course.value = {}
  }
}

const startLearning = () => {
  router.push(`/course/${route.params.id}/play`)
}

const askAI = async () => {
  if (!question.value) return
  try {
    const res = await axios.post('/api/ai/ask', { question: question.value, courseId: course.value.id })
    answer.value = res.data.data
  } catch (e) {
    answer.value = 'AI暂时无法回答，请稍后再试。'
  }
}

const collect = () => ElMessage.success('已收藏')
const like = () => ElMessage.success('已点赞')
const share = () => ElMessage.success('分享链接已复制')

onMounted(fetchCourse)
</script>

<style scoped>
.detail-layout { display: flex; flex-direction: column; align-items: center; padding: 32px; background: #f5f5f5; }
.main-info { display: flex; background: #fff; border-radius: 8px; box-shadow: 0 2px 8px #f0f1f2; padding: 32px; margin-bottom: 32px; }
.cover-big { width: 320px; height: 240px; object-fit: cover; border-radius: 8px; margin-right: 32px; }
.info-block { flex: 1; }
.title { font-size: 28px; font-weight: bold; margin-bottom: 8px; }
.instructor { color: #666; font-size: 15px; margin-bottom: 8px; }
.desc { color: #888; font-size: 15px; margin-bottom: 16px; }
.price { color: #f40; font-size: 22px; font-weight: bold; margin: 16px 0; }
.actions { display: flex; gap: 16px; margin-bottom: 16px; }
.ai-qa { width: 600px; margin: 0 auto; }
</style> 