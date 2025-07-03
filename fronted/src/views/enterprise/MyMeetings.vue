<template>
  <el-card>
    <el-button type="primary" @click="addMeeting">创建会议</el-button>
    <el-table :data="myMeetings" style="margin-top: 20px">
      <el-table-column prop="name" label="会议名称" />
      <el-table-column prop="status" label="审核状态" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" @click="editMeeting(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteMeeting(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>
<script setup>
import { ref } from 'vue'
const myMeetings = ref([])
function addMeeting() {}
function editMeeting(row) {}
function deleteMeeting(row) {}
</script> 