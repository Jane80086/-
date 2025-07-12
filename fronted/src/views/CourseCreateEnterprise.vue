<template>
  <el-card>
    <h2>创建新课程（企业用户）</h2>
    <el-form :model="form" label-width="100px" style="max-width:600px;">
      <el-form-item label="标题">
        <el-input v-model="form.title" />
      </el-form-item>
      <el-form-item label="简介">
        <el-input v-model="form.description" type="textarea" :rows="3" />
      </el-form-item>
      <el-form-item label="企业资质">
        <UploadMedia :uploadUrl="licenseUploadUrl" @success="onLicenseSuccess" />
        <img v-if="form.businessLicense" :src="form.businessLicense" style="width:100px;margin-top:8px;" />
      </el-form-item>
      <el-form-item label="封面">
        <UploadMedia :uploadUrl="coverUploadUrl" @success="onCoverSuccess" />
        <img v-if="form.coverImage" :src="form.coverImage" style="width:100px;margin-top:8px;" />
      </el-form-item>
      <el-form-item label="视频">
        <UploadMedia :uploadUrl="videoUploadUrl" @success="onVideoSuccess" />
        <video v-if="form.videoUrl" :src="form.videoUrl" controls style="width:200px;margin-top:8px;" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="submitting" @click="submitCreate">创建</el-button>
      </el-form-item>
      <el-form-item v-if="createdCourseId">
        <el-button type="success" @click="submitReview">提交审核</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>
<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import UploadMedia from '../components/UploadMedia.vue'
import { ElMessage } from 'element-plus'
const router = useRouter()
const form = ref({ title: '', description: '', businessLicense: '', coverImage: '/class.jpg', videoUrl: '' })
const licenseUploadUrl = '/api/file/upload'
const coverUploadUrl = '/api/file/upload'
const videoUploadUrl = '/api/file/upload'
const onLicenseSuccess = (res) => { form.value.businessLicense = res.url || res.data?.url }
const onCoverSuccess = (res) => { form.value.coverImage = res.url || res.data?.url }
const onVideoSuccess = (res) => { form.value.videoUrl = res.url || res.data?.url }
const submitting = ref(false)
const createdCourseId = ref(null)
const submitCreate = async () => {
  if (!form.value.coverImage) {
    ElMessage.error('请先上传封面');
    return;
  }
  if (!form.value.videoUrl) {
    ElMessage.error('请先上传视频');
    return;
  }
  try {
    submitting.value = true
    const res = await axios.post('/api/course/create', form.value)
    const courseId = res.data.data.course?.id || res.data.data.id
    createdCourseId.value = courseId
    ElMessage.success('课程创建成功，请点击下方按钮提交审核！')
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '创建失败')
  } finally {
    submitting.value = false
  }
}
const submitReview = async () => {
  if (!createdCourseId.value) return
  try {
    await axios.post(`/api/course/${createdCourseId.value}/submit-review`)
    ElMessage.success('课程已提交审核，等待管理员审核！')
  } catch (e) {
    ElMessage.error('提交审核失败')
  }
}
</script> 