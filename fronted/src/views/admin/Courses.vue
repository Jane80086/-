<template>
  <div class="admin-courses">
    <div class="page-header">
      <h1>课程管理</h1>
      <p>管理系统中的所有课程</p>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>课程列表</span>
          <el-button type="primary" @click="addCourse">
            <el-icon><Plus /></el-icon>
            添加课程
          </el-button>
        </div>
      </template>

      <el-table :data="courses" style="width: 100%">
        <el-table-column prop="title" label="课程名称" />
        <el-table-column prop="instructor" label="讲师" />
        <el-table-column prop="category" label="分类" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'active' ? 'success' : 'warning'">
              {{ scope.row.status === 'active' ? '已发布' : '待审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="enrollment" label="报名人数" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="viewCourse(scope.row)">查看</el-button>
            <el-button size="small" @click="editCourse(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteCourse(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const courses = ref([
  {
    id: 1,
    title: 'Vue.js 完全指南 2024',
    instructor: '张老师',
    category: '编程开发',
    status: 'active',
    enrollment: 156
  },
  {
    id: 2,
    title: 'Spring Boot 实战开发',
    instructor: '李老师',
    category: '编程开发',
    status: 'pending',
    enrollment: 89
  }
])

const addCourse = () => {
  ElMessage.info('添加课程功能')
}

const viewCourse = (course) => {
  ElMessage.info(`查看课程：${course.title}`)
}

const editCourse = (course) => {
  ElMessage.info(`编辑课程：${course.title}`)
}

const deleteCourse = (course) => {
  ElMessage.info(`删除课程：${course.title}`)
}
</script>

<style scoped>
.admin-courses {
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