<template>
  <el-card>
    <h2>{{ course.title }}</h2>
    <el-image :src="course.cover" style="max-width: 400px" />
    <div>简介：{{ course.summary }}</div>
    <div>创建时间：{{ course.createdAt }}</div>
    <el-divider />
    <el-button @click="playCourse">播放课程</el-button>
    <el-divider />
    <div>问答区（智能回复）</div>
    <!-- 问答区组件 -->
  </el-card>
</template>
<script setup>
import { ref } from 'vue'
const course = ref({})
function playCourse() {}
</script> 