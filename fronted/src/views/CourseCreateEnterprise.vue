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
        <el-button type="primary" @click="submitCreate">创建</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>
<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import UploadMedia from '../components/UploadMedia.vue'
const router = useRouter()
const form = ref({ title: '', description: '', businessLicense: '', coverImage: '', videoUrl: '' })
const licenseUploadUrl = 'http://localhost:9000/company-files/license'
const coverUploadUrl = 'http://localhost:9000/course-files/cover'
const videoUploadUrl = 'http://localhost:9000/course-files/video'
const onLicenseSuccess = (res) => { form.value.businessLicense = res.url || res.data?.url }
const onCoverSuccess = (res) => { form.value.coverImage = res.url || res.data?.url }
const onVideoSuccess = (res) => { form.value.videoUrl = res.url || res.data?.url }
const submitCreate = async () => {
  // 构造后端需要的payload
  const payload = {
    title: form.value.title,
    description: form.value.description,
    imageUrl: form.value.coverImage ? form.value.coverImage.replace(/^https?:\/\/[^/]+/, '') : '',
    videoUrl: form.value.videoUrl ? form.value.videoUrl.replace(/^https?:\/\/[^/]+/, '') : '',
    category: '编程开发', // 可根据实际需求让用户选择
    instructorId: 1 // TODO: 替换为当前登录用户ID
  }
  if (!payload.title || !payload.description || !payload.imageUrl || !payload.videoUrl) {
    alert('请填写所有必填项并上传封面和视频')
    return
  }
  try {
    const res = await axios.post('/api/course/create', payload)
    if (res.data && res.data.code === 200) {
      router.push(`/course/${res.data.data.course.id}`)
    } else {
      alert(res.data?.message || '创建失败')
    }
  } catch (e) {
    alert(e?.response?.data?.message || '请求失败')
  }
}
</script> 