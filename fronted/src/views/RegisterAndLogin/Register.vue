<template>
    <div class="register-container">
      <div class="register-box">
        <div class="register-header">
          <h2>企业用户注册</h2>
          <p>创建您的企业账户</p>
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
              placeholder="请输入8位账号"
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
          
          <el-form-item label="企业ID" prop="enterpriseId">
            <el-input v-model="registerForm.enterpriseId" placeholder="请输入企业ID" />
          </el-form-item>
          
          <el-form-item label="企业类型" prop="enterpriseType">
            <el-select v-model="registerForm.enterpriseType" placeholder="请选择企业类型" style="width: 100%">
              <el-option label="科技企业" value="科技企业" />
              <el-option label="制造企业" value="制造企业" />
              <el-option label="服务企业" value="服务企业" />
              <el-option label="贸易企业" value="贸易企业" />
              <el-option label="金融企业" value="金融企业" />
              <el-option label="教育企业" value="教育企业" />
              <el-option label="医疗企业" value="医疗企业" />
              <el-option label="建筑企业" value="建筑企业" />
              <el-option label="能源企业" value="能源企业" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="电话号" prop="phone">
            <el-input v-model="registerForm.phone" placeholder="请输入11位手机号" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="registerForm.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="部门" prop="department">
            <el-input v-model="registerForm.department" placeholder="请输入部门" />
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
  import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useUserStore } from '@/store/user'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register, enterpriseRegister } from '@/api/auth'
import { v4 as uuidv4 } from 'uuid'

  
  export default {
    name: 'Register',
    setup() {
      const userStore = useUserStore()
      const router = useRouter()
      const route = useRoute()
      const registerFormRef = ref(null)
      
      const registerForm = reactive({
        account: '',
        realName: '',
        password: '',
        confirmPassword: '',
        enterpriseId: '',
        enterpriseType: '',
        phone: '',
        email: '',
        department: '',
        verificationCode: '',
        userType: 'ENTERPRISE' // 企业用户类型
      })
      const uuid = ref(uuidv4())
      const captchaUrl = ref(`/api/auth/captcha?uuid=${uuid.value}&t=${Date.now()}`)
      const loading = ref(false)
      const isAdmin = computed(() => registerForm.account.startsWith('0000'))
      // 动态校验规则
      const registerRules = reactive({
        account: [
          { required: true, message: '请输入账号', trigger: 'blur' },
          { pattern: /^\d{8}$/, message: '账号需为8位数字', trigger: 'blur' }
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
        enterpriseId: [
          { required: true, message: '请输入企业ID', trigger: 'blur' }
        ],
        enterpriseType: [
          { required: true, message: '请选择企业类型', trigger: 'change' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
        ],
        department: [
          { required: true, message: '请输入部门', trigger: 'blur' }
        ],
        verificationCode: [
          { required: true, message: '请输入验证码', trigger: 'blur' }
        ]
      })
      // 监听账号变化，动态调整企业ID和企业类型校验
      watch(() => registerForm.account, () => {
        registerRules.enterpriseId[0].required = !isAdmin.value
        registerRules.enterpriseType[0].required = !isAdmin.value
      })
      const refreshCaptcha = () => {
        uuid.value = uuidv4()
        captchaUrl.value = `/api/auth/captcha?uuid=${uuid.value}&t=${Date.now()}`
      }
      const handleRegister = async () => {
        try {
          loading.value = true
          await registerFormRef.value.validate();
          // 注册时带上 uuid
          const res = await enterpriseRegister({ ...registerForm, uuid: uuid.value });
          console.log('注册返回：', res);
          if (res && res.success) {
            ElMessage.success('注册成功');
            // 根据用户类型决定跳转路径
            if (registerForm.userType === 'ENTERPRISE') {
              // 企业用户注册成功后跳转到登录页面
              ElMessage.success('企业用户注册成功，请登录');
              router.push('/login');
            } else {
              // 其他用户注册成功后跳转到登录页面
              router.push('/login');
            }
          } else {
            if (res && res.message && res.message.includes('企业不存在')) {
              ElMessage.error('企业不存在，请先注册企业信息');
              router.push({ path: '/enterprise-register', query: { enterpriseId: registerForm.enterpriseId } });
            } else {
              ElMessage.error(res?.message || '注册失败');
            }
            refreshCaptcha();
          }
        } catch (error) {
          console.error('注册错误：', error);
          ElMessage.error(error.response?.data?.message || error.message || '注册失败，请重试');
          refreshCaptcha();
        } finally {
          loading.value = false
        }
      }
      onMounted(() => {
        refreshCaptcha()
        // 如果是从企业注册页面跳转过来的，设置企业ID
        if (route.query.enterpriseId) {
          registerForm.enterpriseId = route.query.enterpriseId;
        }
      })
      return {
        registerFormRef,
        registerForm,
        registerRules,
        captchaUrl,
        loading,
        refreshCaptcha,
        handleRegister,
        isAdmin
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