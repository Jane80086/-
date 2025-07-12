<template>
  <el-card class="course-card" shadow="hover">
    <img :src="course.coverImage || '/class.jpg'" class="cover" />
    <div class="title">{{ course.title }}</div>
    <div class="instructor">讲师：{{ course.instructorName || course.teacher }}</div>
    <div class="desc">{{ course.description }}</div>
    <div class="meta">
      <el-rate v-model="course.aiScore" disabled show-score text-color="#ff9900" score-template="AI评分：{value}" />
      <span class="price">￥{{ course.price || 0 }}</span>
    </div>
    <div class="actions">
      <el-button type="primary" size="small" @click="viewDetail">查看详情</el-button>
      <el-button type="success" size="small" @click="startLearning" plain>
        <el-icon><VideoPlay /></el-icon>
        开始学习
      </el-button>
      <el-button type="warning" size="small" plain @click="collect">收藏</el-button>
      <el-button type="info" size="small" plain @click="like">点赞</el-button>
    </div>
  </el-card>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { VideoPlay } from '@element-plus/icons-vue'

const props = defineProps({ course: { type: Object, required: true } })
const router = useRouter()

const viewDetail = () => {
  router.push(`/course/${props.course.id}`)
}

const startLearning = () => {
  router.push(`/course/${props.course.id}/play`)
}

const collect = () => {
  ElMessage.success('已收藏')
}

const like = () => {
  ElMessage.success('已点赞')
}
</script>

<style scoped>
.course-card { cursor: pointer; margin-bottom: 24px; transition: box-shadow .2s; }
.cover { width: 100%; height: 120px; object-fit: cover; border-radius: 6px; margin-bottom: 8px; }
.title { font-size: 18px; font-weight: bold; margin-bottom: 4px; color: #222; }
.instructor { color: #666; font-size: 13px; margin-bottom: 4px; }
.desc { color: #888; font-size: 13px; margin-bottom: 8px; }
.meta { display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px; }
.price { color: #f40; font-weight: bold; font-size: 16px; }
.actions { display: flex; gap: 8px; flex-wrap: wrap; }
</style> 