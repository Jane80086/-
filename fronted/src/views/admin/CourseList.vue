<template>
  <div class="course-list-page">
    <el-table :data="courses" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="instructorName" label="讲师" />
      <el-table-column prop="category" label="分类" />
      <el-table-column prop="status" label="状态" />
      <el-table-column prop="price" label="价格" />
      <el-table-column prop="createTime" label="创建时间" />
    </el-table>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { courseApi } from '@/api/course'
const courses = ref([])
const loadCourses = async () => {
  const res = await courseApi.getCourseList()
  if (res.code === 200) courses.value = res.data.content || res.data || []
}
onMounted(loadCourses)
</script> 