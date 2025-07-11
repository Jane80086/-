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
      <el-table-column v-if="hasRejected" prop="rejectReason" label="驳回原因" min-width="180">
        <template #default="scope">
          <span v-if="scope.row.status === 'REJECTED'">{{ scope.row.rejectReason || '无' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="180">
        <template #default="scope">
          <el-button v-if="scope.row.status === 'PENDING'" size="small" type="primary" @click="approve(scope.row)">通过</el-button>
          <el-button v-if="scope.row.status === 'PENDING'" size="small" type="danger" @click="reject(scope.row)">拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElPrompt } from 'element-plus'
import { courseApi } from '@/api/course'
const courses = ref([])
const userId = 1 // TODO: 替换为当前登录用户ID
const loadCourses = async () => {
  try {
    const res = await courseApi.getMyCourses({ userId })
    if (res.code === 200) courses.value = res.data || []
  } catch (e) {
    ElMessage.error('课程加载失败: ' + (e?.message || '未知错误'))
    console.error('课程加载失败', e)
  }
}
const hasRejected = computed(() => courses.value.some(c => c.status === 'REJECTED'))
const getStatusType = (status) => {
  if (status === 'PENDING') return 'info'
  if (status === 'PUBLISHED') return 'success'
  if (status === 'REJECTED') return 'danger'
  if (status === 'DRAFT') return 'warning'
  return 'default'
}
const getStatusText = (status) => {
  if (status === 'PENDING') return '审核中'
  if (status === 'PUBLISHED') return '已发布'
  if (status === 'REJECTED') return '已驳回'
  if (status === 'DRAFT') return '草稿'
  return '未知'
}
const approve = async (row) => {
  const res = await courseApi.approveCourse(row.id)
  if (res.code === 200) { ElMessage.success('审核通过'); loadCourses() }
}
const reject = async (row) => {
  ElPrompt({
    title: '请输入驳回原因',
    inputType: 'textarea',
    callback: async (reason) => {
      if (!reason) return ElMessage.warning('请输入驳回原因')
      const res = await courseApi.rejectCourse(row.id, reason)
      if (res.code === 200) { ElMessage.success('已拒绝'); loadCourses() }
    }
  })
}
onMounted(loadCourses)
</script>
<style scoped>
.audit-container { padding: 24px; }
</style> 