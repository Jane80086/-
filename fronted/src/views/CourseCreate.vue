<template>
  <el-card>
    <h2>创建新课程</h2>
    <el-form :model="form" label-width="80px" style="max-width:500px;">
      <el-form-item label="标题">
        <el-input v-model="form.title" />
      </el-form-item>
      <el-form-item label="简介">
        <el-input v-model="form.description" type="textarea" :rows="3" />
      </el-form-item>
      <el-form-item label="分类">
        <el-input v-model="form.category" />
      </el-form-item>
      <el-form-item label="价格">
        <el-input-number v-model="form.price" :min="0" />
      </el-form-item>
      <el-form-item label="封面">
        <el-input v-model="form.imageUrl" placeholder="图片URL" />
      </el-form-item>
      <el-form-item label="视频">
        <el-input v-model="form.videoUrl" placeholder="视频URL" />
      </el-form-item>
      <el-form-item>
        <el-button type="success" @click="aiOptimize" plain>AI一键优化</el-button>
        <el-button type="primary" @click="submitCreate">创建</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const form = ref({
  title: '',
  description: '',
  category: '',
  price: 0,
  imageUrl: '/class.jpg',
  videoUrl: ''
})

const aiOptimize = async () => {
  try {
    const res = await axios.post('/api/ai/optimize', form.value)
    Object.assign(form.value, res.data.data)
    ElMessage.success('AI优化成功')
  } catch (e) {
    ElMessage.error('AI优化失败')
  }
}

const submitCreate = async () => {
  try {
    const res = await axios.post('/api/course/create', form.value)
    const courseId = res.data?.data?.course?.id || res.data?.data?.id
    if (courseId) {
      ElMessage.success('创建成功')
      router.push(`/course/${courseId}`)
    } else {
      ElMessage.error('创建成功但未获取到课程ID')
    }
  } catch (e) {
    ElMessage.error('创建失败')
  }
}
</script> 