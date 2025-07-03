<template>
  <el-card>
    <el-button type="primary" @click="addNews">发布动态</el-button>
    <el-table :data="myNews" style="margin-top: 20px">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="status" label="审核状态" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" @click="editNews(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteNews(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>
<script setup>
import { ref } from 'vue'
const myNews = ref([])
function addNews() {}
function editNews(row) {}
function deleteNews(row) {}
</script> 