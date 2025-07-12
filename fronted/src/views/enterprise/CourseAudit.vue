<template>
  <div class="audit-container">
    <el-table :data="courses" border stripe>
      <el-table-column prop="title" label="课程名" min-width="160" />
      <el-table-column prop="instructorName" label="讲师" min-width="120" />
      <el-table-column prop="category" label="分类" min-width="100" />
      <el-table-column prop="status" label="状态" min-width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="180">
        <template #default="scope">
          <el-button size="small" type="primary" @click="approve(scope.row)">通过</el-button>
          <el-button size="small" type="danger" @click="reject(scope.row)">拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminApi } from '@/api/course'
import { useUserStore } from '@/store/user'
const courses = ref([])
const loadCourses = async () => {
  const res = await adminApi.getPendingCourses()
  if (res.code === 200) courses.value = res.data.content || res.data || []
}
const getStatusType = (status) => {
  if (status === 'pending') return 'info'
  if (status === 'approved') return 'success'
  if (status === 'rejected') return 'danger'
  return 'default'
}
const getStatusText = (status) => {
  if (status === 'pending') return '审核中'
  if (status === 'approved') return '已通过'
  if (status === 'rejected') return '未通过'
  return '未知'
}
const userStore = useUserStore()
const approve = async (row) => {
  const reviewerId = userStore.user?.id || ''
  const res = await adminApi.approveCourse(row.id, reviewerId)
  if (res.code === 200) { ElMessage.success('审核通过'); loadCourses() }
}
const reject = async (row) => {
  const reviewerId = userStore.user?.id || ''
  const res = await adminApi.rejectCourse(row.id, reviewerId)
  if (res.code === 200) { ElMessage.success('已拒绝'); loadCourses() }
}
onMounted(loadCourses)
</script>
<style scoped>
.audit-container { padding: 24px; }
</style> 