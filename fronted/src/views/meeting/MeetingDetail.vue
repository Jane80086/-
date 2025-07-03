<template>
  <el-card>
    <h2>{{ meeting.name }}</h2>
    <div>时间：{{ meeting.date }}</div>
    <div>创建人：{{ meeting.creator }}</div>
    <el-divider />
    <div>内容：{{ meeting.content }}</div>
  </el-card>
</template>
<script setup>
import { ref } from 'vue'
const meeting = ref({})
// onMounted获取详情
</script> 