<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>测盟汇系统</h2>
        <p>欢迎登录</p>
      </div>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
      >
        <el-form-item prop="account">
          <el-input
            v-model="loginForm.account"
            placeholder="请输入账号"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        
        <el-form-item prop="verificationCode">
          <div class="verificationCode-container">
            <el-input
              v-model="loginForm.verificationCode"
              placeholder="请输入验证码"
              size="large"
            />
            <img
              :src="verificationCodeUrl"
              alt="验证码"
              class="verificationCode-image"
              @click="refreshCaptcha"
            />
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-button"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
        
        <div class="login-footer">
          <el-link type="primary" @click="$router.push('/register')">
            还没有账号？立即注册
          </el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/api'
import { useUserStore } from '@/store/user'

export default {
  name: 'Login',
  setup() {
    const userStore = useUserStore()
    const router = useRouter()
    const loginFormRef = ref(null)
    const loginForm = reactive({
      account: '',
      password: '',
      verificationCode: ''
    })
    const verificationCodeUrl = ref('/api/auth/captcha?' + Date.now())
    const loading = ref(false)
    const loginRules = {
      account: [
        { required: true, message: '请输入账号', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
      ],
      verificationCode: [
        { required: true, message: '请输入验证码', trigger: 'blur' }
      ]
    }
    const refreshCaptcha = () => {
      verificationCodeUrl.value = '/api/auth/captcha?' + Date.now();
    }
    const handleLogin = async () => {
      if (loading.value) return
      if (!loginFormRef.value) return
      try {
        await loginFormRef.value.validate()
        loading.value = true
        // 这里请替换为你实际的登录API调用
        const response = await api.login(loginForm)
        if (response.data && response.data.success) {
          ElMessage.success('登录成功')
          const user = response.data.user
          const token = response.data.token
          console.log('登录成功后user:', user)
          console.log('登录成功后token:', token)
          if (token) localStorage.setItem('token', token)
          if (user) userStore.setUser(user)
          await nextTick()
          if (user.role === 'admin') {
            router.push('/admin/dashboard')
          } else if (user.role === 'enterprise') {
            router.push('/enterprise/home')
          } else {
            router.push('/user/home')
          }
        } else {
          ElMessage.error(response.data.message || '登录失败')
          refreshCaptcha()
          return
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '登录失败，请重试')
        refreshCaptcha()
      } finally {
        loading.value = false
      }
    }
    onMounted(() => {
      refreshCaptcha()
    })
    return {
      loginFormRef,
      loginForm,
      loginRules,
      verificationCodeUrl,
      loading,
      refreshCaptcha,
      handleLogin
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  color: #333;
  margin-bottom: 10px;
  font-size: 28px;
}

.login-header p {
  color: #666;
  font-size: 14px;
}

.login-form {
  margin-top: 20px;
}

.verificationCode-container {
  display: flex;
  gap: 10px;
  align-items: center;
}

.verificationCode-image {
  height: 40px;
  border-radius: 4px;
  cursor: pointer;
  border: 1px solid #dcdfe6;
}

.login-button {
  width: 100%;
  height: 45px;
  font-size: 16px;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
}
</style> 