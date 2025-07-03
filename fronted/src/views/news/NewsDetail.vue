<template>
  <el-card>
    <h2>{{ news.title }}</h2>
    <div>作者：{{ news.author }} 发布时间：{{ news.createdAt }}</div>
    <el-divider />
    <div v-html="news.content"></div>
    <el-image v-if="news.image" :src="news.image" style="margin-top: 20px;max-width: 400px" />
  </el-card>
</template>
<script setup>
import { ref } from 'vue'
const news = ref({})
// onMounted获取详情
</script> 