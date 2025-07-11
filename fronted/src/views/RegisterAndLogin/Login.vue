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
                @error="onCaptchaError"
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
  import { login } from '@/api/auth'
  import { useUserStore } from '@/store/user'
  import { v4 as uuidv4 } from 'uuid'
  
  export default {
    name: 'Login',
    setup() {
      const router = useRouter()
      const userStore = useUserStore()
      const loginFormRef = ref(null)
      const loginForm = reactive({
        account: '',
        password: '',
        verificationCode: ''
      })
      
      const uuid = ref(uuidv4())
      const verificationCodeUrl = ref(`/api/auth/captcha?uuid=${uuid.value}&t=${Date.now()}`)
      const loading = ref(false)
      let captchaRetryCount = ref(0)
      const maxCaptchaRetry = 3
      
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
        uuid.value = uuidv4()
        verificationCodeUrl.value = `/api/auth/captcha?uuid=${uuid.value}&t=${Date.now()}`
        loginForm.verificationCode = '' // 清空验证码输入框
        captchaRetryCount.value = 0
      }
      
      const onCaptchaError = () => {
        if (captchaRetryCount.value < maxCaptchaRetry) {
          captchaRetryCount.value++
          verificationCodeUrl.value = `/api/auth/captcha?uuid=${uuid.value}&t=${Date.now()}&retry=${captchaRetryCount.value}`
        } else {
          ElMessage.error('验证码加载失败，请稍后重试或联系管理员')
        }
      }
      
      const handleLogin = async () => {
        if (loading.value) return
        if (!loginFormRef.value) return
        
        try {
          await loginFormRef.value.validate()
          loading.value = true
          // 调试：打印当前uuid和验证码
          console.log('登录请求uuid:', uuid.value, '验证码:', loginForm.verificationCode)
  
          console.log('发送登录请求:', { 
            ...loginForm, 
            uuid: uuid.value 
          })
          
          // 使用正确的API调用方式，带上uuid参数
          const response = await login({ 
            ...loginForm, 
            uuid: uuid.value 
          })
          
          if (response && response.success) {
            ElMessage.success('登录成功')
            // 兼容后端 userType + adminUser/enterpriseUser/normalUser
            let user = null
            if (response.userType === 'admin') {
              user = response.adminUser
            } else if (response.userType === 'enterprise') {
              user = response.enterpriseUser
            } else if (response.userType === 'user') {
              user = response.normalUser
            }
            const token = response.token
            if (token) {
              const trimmedToken = token.trim();
              // 打印 token 长度和内容前后10字符
              console.log('Token长度:', trimmedToken.length, '前10:', trimmedToken.slice(0, 10), '后10:', trimmedToken.slice(-10));
              // 检查格式
              if (!/^[A-Za-z0-9-_]+\.[A-Za-z0-9-_]+\.[A-Za-z0-9-_]+$/.test(trimmedToken)) {
                ElMessage.error('登录返回的token格式不合法，请联系管理员！');
                localStorage.removeItem('token');
                return;
              }
              localStorage.setItem('token', trimmedToken);
            }
            
            // 保存用户信息到Pinia store
            if (user) {
              userStore.setUser(user)
            }
            
            await nextTick()
            
            // 跳转到对应首页
            if (response.userType === 'admin') {
              console.log('跳转到管理员首页: /admin/dashboard')
              router.push('/admin/dashboard')
            } else if (response.userType === 'enterprise') {
              console.log('跳转到企业首页: /enterprise/home')
              router.push('/enterprise/home')
            } else {
              console.log('跳转到普通用户首页: /user/home')
              router.push('/user/home')
            }
          } else {
            // 登录失败，弹出后端返回的错误信息
            ElMessage.error(response?.message || '登录失败')
            refreshCaptcha()
          }
        } catch (error) {
          console.error('登录错误:', error)
          ElMessage.error(error.response?.data?.message || error.message || '登录失败，请重试')
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
        handleLogin,
        onCaptchaError
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