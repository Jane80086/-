<template>
  <div class="courses-container">
    <el-row :gutter="20">
      <el-col :span="6" v-for="course in courses" :key="course.id">
        <el-card @click="viewCourseDetail(course.id)" class="course-card">
          <img :src="course.imageUrl || '/class.jpg'" class="course-image" />
          <div class="course-info">
            <h3>{{ course.title }}</h3>
            <p>{{ course.description }}</p>
            <el-tag :type="course.isFree ? 'success' : 'warning'" size="small">
              {{ course.isFree ? '免费' : '付费' }}
            </el-tag>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { courseApi } from '@/api/course'
const router = useRouter()
const courses = ref([])
const loadCourses = async () => {
  const res = await courseApi.getCourseList()
  if (res.code === 200) courses.value = res.data.content || res.data || []
}
const viewCourseDetail = (id) => router.push(`/enterprise/course/${id}`)
onMounted(loadCourses)
</script>
<style scoped>
.courses-container { padding: 24px; }
.course-card { cursor: pointer; margin-bottom: 20px; }
.course-image { width: 100%; height: 160px; object-fit: cover; border-radius: 8px; }
.course-info { padding: 12px 0; }
</style> 