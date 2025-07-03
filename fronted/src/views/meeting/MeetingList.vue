<template>
  <el-card>
    <el-input v-model="keyword" placeholder="搜索会议" style="width: 300px" @keyup.enter="fetchMeetings" />
    <el-table :data="meetingList" style="margin-top: 20px">
      <el-table-column prop="name" label="会议名称" />
      <el-table-column prop="date" label="时间" />
      <el-table-column prop="creator" label="创建人" />
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
      @current-change="fetchMeetings"
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
const meetingList = ref([])
function fetchMeetings() { /* 调用API获取数据 */ }
function viewDetail(row) { /* 跳转详情 */ }
</script> 