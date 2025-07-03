<template>
  <el-card>
    <h2>编辑课程</h2>
    <el-form :model="form" label-width="80px" style="max-width:500px;">
      <el-form-item label="标题">
        <el-input v-model="form.title" />
      </el-form-item>
      <el-form-item label="简介">
        <el-input v-model="form.description" type="textarea" rows="3" />
      </el-form-item>
      <el-form-item label="分类">
        <el-input v-model="form.category" />
      </el-form-item>
      <el-form-item label="价格">
        <el-input-number v-model="form.price" :min="0" />
      </el-form-item>
      <el-form-item label="封面">
        <el-input v-model="form.coverImage" placeholder="图片URL" />
      </el-form-item>
      <el-form-item label="视频">
        <el-input v-model="form.videoUrl" placeholder="视频URL" />
      </el-form-item>
      <el-form-item>
        <el-button type="success" @click="aiOptimize" plain>AI一键优化</el-button>
        <el-button type="primary" @click="submitEdit">保存</el-button>
        <el-button type="warning" @click="submitForReview">提交审核</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const form = ref({})

const fetchCourse = async () => {
  try {
    const res = await axios.get(`/api/course/${route.params.id}`)
    form.value = res.data.data || {}
  } catch (e) {
    ElMessage.error('获取课程信息失败')
  }
}

const aiOptimize = async () => {
  try {
    const res = await axios.post('/api/ai/optimize', form.value)
    Object.assign(form.value, res.data.data)
    ElMessage.success('AI优化成功')
  } catch (e) {
    ElMessage.error('AI优化失败')
  }
}

const submitEdit = async () => {
  try {
    await axios.put(`/api/course/${route.params.id}`, form.value)
    ElMessage.success('保存成功')
    router.push(`/course/${route.params.id}`)
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const submitForReview = async () => {
  try {
    await axios.post(`/api/course/${route.params.id}/submit`)
    ElMessage.success('已提交审核')
  } catch (e) {
    ElMessage.error('提交审核失败')
  }
}

onMounted(fetchCourse)
</script> 