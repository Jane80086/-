<template>
  <div class="profile">
    <div class="page-header">
      <h1>个人资料</h1>
      <p>管理您的个人信息和账户设置</p>
    </div>

    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>个人信息</span>
          </template>
          <div class="profile-info">
            <el-avatar :src="userInfo.avatar" :size="100" />
            <h3>{{ userInfo.name }}</h3>
            <p>{{ userInfo.email }}</p>
            <el-tag :type="getUserTypeTag(userInfo.userType)">
              {{ getUserTypeText(userInfo.userType) }}
            </el-tag>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>编辑资料</span>
          </template>
          <el-form :model="profileForm" :rules="profileRules" ref="profileFormRef" label-width="100px">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="profileForm.username" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="profileForm.phone" />
            </el-form-item>
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="profileForm.nickname" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveProfile">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const userInfo = ref({
  name: '张三',
  email: 'zhangsan@example.com',
  userType: 'NORMAL',
  avatar: 'https://randomuser.me/api/portraits/men/32.jpg'
})

const profileForm = ref({
  username: 'zhangsan',
  email: 'zhangsan@example.com',
  phone: '13800138000',
  nickname: '张三'
})

const profileRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const profileFormRef = ref()

const getUserTypeTag = (userType) => {
  const tagMap = {
    'NORMAL': '',
    'ENTERPRISE': 'warning',
    'ADMIN': 'danger'
  }
  return tagMap[userType] || ''
}

const getUserTypeText = (userType) => {
  const textMap = {
    'NORMAL': '普通用户',
    'ENTERPRISE': '企业用户',
    'ADMIN': '管理员'
  }
  return textMap[userType] || '未知'
}

const saveProfile = async () => {
  try {
    await profileFormRef.value.validate()
    ElMessage.success('个人资料保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}
</script>

<style scoped>
.profile {
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

.profile-info {
  text-align: center;
}

.profile-info h3 {
  margin: 16px 0 8px 0;
  color: #333;
}

.profile-info p {
  margin: 0 0 16px 0;
  color: #666;
}
</style> 