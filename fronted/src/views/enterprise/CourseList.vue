<template>
  <div class="courses-container">
    <el-row :gutter="20">
      <el-col :span="6" v-for="course in courses" :key="course.id">
        <el-card @click="viewCourseDetail(course)" class="course-card">
          <img :src="course.imageUrl || '/default-course.jpg'" class="course-image" />
          <div class="course-info">
            <h3>{{ course.title }}</h3>
            <p>{{ course.description }}</p>
            <el-tag :type="course.isFree ? 'success' : 'warning'" size="small">
              {{ course.isFree ? '免费' : '付费' }}
            </el-tag>
            <el-tag :type="getStatusType(course.status)" size="small" style="margin-left:8px;">
              {{ getStatusText(course.status) }}
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
import { ElMessage } from 'element-plus'
const router = useRouter()
const courses = ref([])
const loadCourses = async () => {
  const res = await courseApi.getCourseList()
  if (res.code === 200) courses.value = res.data.content || res.data || []
}
const viewCourseDetail = (course) => {
  if (course.status === 'published') {
    router.push(`/enterprise/course/${course.id}`)
  } else {
    ElMessage.warning('课程尚未发布，无法查看详情')
  }
}
function getStatusType(status) {
  if (status === 'published') return 'success'
  if (status === 'pending') return 'info'
  if (status === 'rejected') return 'danger'
  return 'warning'
}
function getStatusText(status) {
  if (status === 'published') return '已发布'
  if (status === 'pending') return '待审核'
  if (status === 'rejected') return '未通过'
  if (status === 'draft') return '草稿'
  return status
}
onMounted(loadCourses)
</script>
<style scoped>
.courses-container { padding: 24px; }
.course-card { cursor: pointer; margin-bottom: 20px; }
.course-image { width: 100%; height: 160px; object-fit: cover; border-radius: 8px; }
.course-info { padding: 12px 0; }
</style> 