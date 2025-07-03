<template>
  <div class="help">
    <div class="page-header">
      <h1>帮助中心</h1>
      <p>常见问题和使用指南</p>
    </div>

    <el-card>
      <template #header>
        <span>常见问题</span>
      </template>
      <el-collapse v-model="activeNames">
        <el-collapse-item title="如何开始学习课程？" name="1">
          <p>在课程列表中选择您感兴趣的课程，点击"立即学习"即可开始学习。</p>
        </el-collapse-item>
        <el-collapse-item title="如何查看学习进度？" name="2">
          <p>在个人中心可以查看您的学习进度和完成情况。</p>
        </el-collapse-item>
        <el-collapse-item title="如何收藏课程？" name="3">
          <p>在课程详情页面点击"收藏"按钮即可收藏课程。</p>
        </el-collapse-item>
      </el-collapse>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const activeNames = ref(['1'])
</script>

<style scoped>
.help {
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
</style> 