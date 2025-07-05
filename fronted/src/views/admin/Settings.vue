<template>
  <div class="settings">
    <div class="page-header">
      <h1>系统设置</h1>
      <p>管理系统设置和配置</p>
    </div>

    <el-card>
      <template #header>
        <span>系统设置</span>
      </template>
      <el-form label-width="120px">
        <el-form-item label="通知设置">
          <el-switch v-model="settings.notifications" />
        </el-form-item>
        <el-form-item label="邮件通知">
          <el-switch v-model="settings.emailNotifications" />
        </el-form-item>
        <el-form-item label="自动播放">
          <el-switch v-model="settings.autoPlay" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveSettings">保存设置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const settings = ref({
  notifications: true,
  emailNotifications: false,
  autoPlay: true
})

const saveSettings = () => {
  ElMessage.success('系统设置保存成功')
}
</script>

<style scoped>
.settings {
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