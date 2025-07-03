<template>
  <div class="favorites">
    <div class="page-header">
      <h1>我的收藏</h1>
      <p>管理您收藏的课程和资源</p>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>收藏的课程</span>
          <el-button type="primary" @click="refreshFavorites">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <el-table :data="favoriteCourses" style="width: 100%" v-loading="loading">
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="instructor" label="讲师" width="120" />
        <el-table-column prop="category" label="分类" width="100">
          <template #default="scope">
            <el-tag :type="getCategoryType(scope.row.category)">
              {{ scope.row.category }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="favoriteTime" label="收藏时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewCourse(scope.row)">
              查看课程
            </el-button>
            <el-button size="small" type="danger" @click="removeFavorite(scope.row)">
              取消收藏
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)

const favoriteCourses = ref([
  {
    id: 1,
    courseName: 'Vue.js 完全指南 2024',
    instructor: '张老师',
    category: 'programming',
    favoriteTime: '2024-07-01 10:30:00'
  },
  {
    id: 2,
    courseName: 'Spring Boot 实战开发',
    instructor: '李老师',
    category: 'programming',
    favoriteTime: '2024-06-30 15:20:00'
  }
])

const getCategoryType = (category) => {
  const typeMap = {
    programming: 'success',
    design: 'warning',
    business: 'primary',
    language: 'info'
  }
  return typeMap[category] || ''
}

const viewCourse = (course) => {
  router.push(`/course/${course.id}`)
}

const removeFavorite = (course) => {
  ElMessage.success(`已取消收藏：${course.courseName}`)
  const index = favoriteCourses.value.findIndex(item => item.id === course.id)
  if (index > -1) {
    favoriteCourses.value.splice(index, 1)
  }
}

const refreshFavorites = async () => {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('收藏列表已刷新')
  } catch (error) {
    ElMessage.error('刷新失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.favorites {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  text-align: center;
}

.page-header h1 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 28px;
}

.page-header p {
  margin: 0;
  color: #666;
  font-size: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 