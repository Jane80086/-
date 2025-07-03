<template>
  <el-card>
    <el-input v-model="keyword" placeholder="搜索动态" style="width: 300px" @keyup.enter="fetchNews" />
    <el-table :data="newsList" style="margin-top: 20px">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="author" label="作者" />
      <el-table-column prop="summary" label="简介" />
      <el-table-column prop="createdAt" label="发布时间" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="page"
      :page-size="pageSize"
      :total="total"
      @current-change="fetchNews"
      layout="prev, pager, next"
      style="margin-top: 20px" />
  </el-card>
</template>
<script setup>
import { ref } from 'vue'
const keyword = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const newsList = ref([])
function fetchNews() { /* 调用API获取数据 */ }
function viewDetail(row) { /* 跳转详情 */ }
</script> 