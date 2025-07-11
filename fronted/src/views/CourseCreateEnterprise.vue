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
  try {
    const res = await axios.post('/api/course/create', form.value)
    router.push(`/course/${res.data.data.id}`)
  } catch (e) {}
}
</script> 