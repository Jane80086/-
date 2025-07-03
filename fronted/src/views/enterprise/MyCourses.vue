<template>
  <el-card>
    <el-button type="primary" @click="addCourse">添加课程</el-button>
    <el-table :data="myCourses" style="margin-top: 20px">
      <el-table-column prop="title" label="课程名" />
      <el-table-column prop="status" label="审核状态" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" @click="editCourse(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteCourse(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>
<script setup>
import { ref } from 'vue'
const myCourses = ref([])
function addCourse() {}
function editCourse(row) {}
function deleteCourse(row) {}
</script> 