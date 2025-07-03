<template>
  <div class="admin-manage-page">
    <h2>课程管理（超级管理员）</h2>
    <el-table :data="courses" style="width:100%">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="instructorName" label="讲师" />
      <el-table-column prop="status" label="状态" />
      <el-table-column label="封面">
        <template #default="scope">
          <img v-if="scope.row.coverImage" :src="scope.row.coverImage" style="width:60px;" />
          <UploadMedia :uploadUrl="coverUploadUrl" @success="url => updateCover(scope.row, url)" />
        </template>
      </el-table-column>
      <el-table-column label="视频">
        <template #default="scope">
          <video v-if="scope.row.videoUrl" :src="scope.row.videoUrl" controls style="width:80px;" />
          <UploadMedia :uploadUrl="videoUploadUrl" @success="url => updateVideo(scope.row, url)" />
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" @click="approve(scope.row)">审核通过</el-button>
          <el-button size="small" @click="reject(scope.row)">驳回</el-button>
          <el-button size="small" @click="recommend(scope.row)">推荐</el-button>
          <el-button size="small" type="danger" @click="remove(scope.row)">下架</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import UploadMedia from '../components/UploadMedia.vue'
const courses = ref([])
const coverUploadUrl = 'http://localhost:9000/course-files/cover'
const videoUploadUrl = 'http://localhost:9000/course-files/video'
const fetchCourses = async () => {
  const res = await axios.get('/api/course/list')
  courses.value = res.data.data || []
}
const updateCover = async (row, url) => {
  row.coverImage = url.url || url.data?.url
  await axios.put(`/api/course/${row.id}`, row)
}
const updateVideo = async (row, url) => {
  row.videoUrl = url.url || url.data?.url
  await axios.put(`/api/course/${row.id}`, row)
}
const approve = async (row) => { await axios.post(`/api/course/${row.id}/approve`); fetchCourses() }
const reject = async (row) => { await axios.post(`/api/course/${row.id}/reject`); fetchCourses() }
const recommend = async (row) => { await axios.post(`/api/course/${row.id}/recommend`); fetchCourses() }
const remove = async (row) => { await axios.delete(`/api/course/${row.id}`); fetchCourses() }
onMounted(fetchCourses)
</script> 