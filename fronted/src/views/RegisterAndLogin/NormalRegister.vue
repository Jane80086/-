<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h2>普通用户注册</h2>
        <p>创建您的个人账户</p>
      </div>
      
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        label-width="100px"
      >
        <el-form-item label="账号" prop="account">
          <el-input
            v-model="registerForm.account"
            placeholder="请输入8位数字账号"
          />
        </el-form-item>
        
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
          />
        </el-form-item>
        
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="registerForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码（6-12位字母和数字）"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入11位手机号" />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        
        <el-form-item label="验证码" prop="verificationCode">
          <div class="captcha-container">
            <el-input
              v-model="registerForm.verificationCode"
              placeholder="请输入验证码"
            />
            <img
              :src="captchaUrl"
              alt="验证码"
              class="captcha-image"
              @click="refreshCaptcha"
            />
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            class="register-button"
            @click="handleRegister"
          >
            注册
          </el-button>
          <el-button @click="$router.push('/login')">
            返回登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userRegister } from '@/api/auth'
import { v4 as uuidv4 } from 'uuid'

export default {
  name: 'NormalRegister',
  setup() {
    const userStore = useUserStore()
    const router = useRouter()
    const registerFormRef = ref(null)
    const registerForm = reactive({
      account: '',
      username: '',
      realName: '',
      password: '',
      confirmPassword: '',
      phone: '',
      email: '',
      verificationCode: '',
      userType: 'NORMAL'
    })
    const uuid = ref(uuidv4())
    const captchaUrl = ref(`/api/auth/captcha?uuid=${uuid.value}&t=${Date.now()}`)
    const loading = ref(false)
    
    const registerRules = reactive({
      account: [
        { required: true, message: '请输入账号', trigger: 'blur' },
        { pattern: /^\d{8}$/, message: '账号需为8位数字', trigger: 'blur' }
      ],
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度在3-20个字符', trigger: 'blur' }
      ],
      realName: [
        { required: true, message: '请输入真实姓名', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { pattern: /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{6,12}$/, message: '密码需为6-12位且包含字母和数字', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, message: '请确认密码', trigger: 'blur' },
        { validator: (rule, value, callback) => {
            if (value !== registerForm.password) {
              callback(new Error('两次输入的密码不一致'))
            } else {
              callback()
            }
          }, trigger: ['blur', 'change'] }
      ],
      phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }
      ],
      email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
      ],
      verificationCode: [
        { required: true, message: '请输入验证码', trigger: 'blur' }
      ]
    })
    
    const refreshCaptcha = () => {
      uuid.value = uuidv4()
      captchaUrl.value = `/api/auth/captcha?uuid=${uuid.value}&t=${Date.now()}`
    }
    
    const handleRegister = async () => {
      try {
        loading.value = true
        await registerFormRef.value.validate();
        const res = await userRegister({ ...registerForm, uuid: uuid.value });
        console.log('注册返回：', res); // 增加详细日志
        if (res && res.success) {
          ElMessage.success('注册成功');
          router.push('/login');
        } else {
          // 显示后端返回的详细错误信息
          ElMessage.error(res?.message || JSON.stringify(res) || '注册失败');
          refreshCaptcha();
        }
      } catch (error) {
        console.error('注册错误：', error);
        // 优先显示后端返回的 message
        if (error?.response?.data?.message) {
          ElMessage.error(error.response.data.message);
        } else if (error?.message) {
          ElMessage.error(error.message);
        } else {
          ElMessage.error('注册失败，请重试');
        }
        refreshCaptcha();
      } finally {
        loading.value = false
      }
    }
    
    onMounted(() => {
      refreshCaptcha()
    })
    
    return {
      registerFormRef,
      registerForm,
      registerRules,
      captchaUrl,
      loading,
      refreshCaptcha,
      handleRegister
    }
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-box {
  width: 500px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h2 {
  color: #333;
  margin-bottom: 10px;
  font-size: 28px;
}

.register-header p {
  color: #666;
  font-size: 14px;
}

.register-form {
  margin-top: 20px;
}

.captcha-container {
  display: flex;
  gap: 10px;
  align-items: center;
}

.captcha-image {
  height: 40px;
  border-radius: 4px;
  cursor: pointer;
  border: 1px solid #dcdfe6;
}

.register-button {
  margin-right: 10px;
}
</style> 