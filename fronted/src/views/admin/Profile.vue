<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="profile-header">
          <el-button type="primary" @click="goBack" class="back-btn">
            <el-icon><arrow-left /></el-icon>
          </el-button>
          <el-avatar :size="64" :src="userAvatar" class="user-avatar">
            {{ user?.account?.charAt(0)?.toUpperCase() }}
          </el-avatar>
          <div class="profile-title">
            <h2>{{ user?.account }}</h2>
            <span>{{ user?.role === 'adminUser' ? '管理员' : '企业用户' }}</span>
          </div>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" class="profile-form">
        <el-form-item label="账号" prop="account">
          <el-input v-model="form.account" disabled class="input-item" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" class="input-item" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" class="input-item" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" class="input-item" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" class="input-item" />
        </el-form-item>
        <el-form-item label="企业ID" prop="enterpriseId">
          <el-input v-model="form.enterpriseId" class="input-item" />
        </el-form-item>
        <el-form-item label="部门" prop="department">
          <el-input v-model="form.department" class="input-item" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave" class="save-button">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <AIChat :visible="showAIChat" @close="showAIChat=false" />
    <button class="ai-float-btn" @click="showAIChat=true">AI助手</button>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue'
import { useStore } from 'vuex'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import api from '@/api'
import { ArrowLeft } from '@element-plus/icons-vue'
import AIChat from './AIChat.vue'
import { getAIWelcome } from '../api/index.js'

export default {
  name: 'Profile',
  components: { ArrowLeft, AIChat },
  setup() {
    const store = useStore()
    const router = useRouter()
    const user = computed(() => store.getters.currentUser)
    const userAvatar = computed(() => user.value?.avatar || '')
    
    // 初始化表单
    const form = ref({
      account: '',
      nickname: '',
      email: '',
      phone: '',
      realName: '',
      enterpriseId: '',
      department: ''
    })
    // 记录原始表单数据，用于判断是否有修改
    const originalForm = ref({...form.value})

    // 自动获取当前用户信息
    const fetchUser = async () => {
      try {
        const res = await api.user.getCurrentUser()
        Object.assign(form.value, res.data.data)
        form.value.phone = String(form.value.phone || '').trim()
        originalForm.value = {...form.value}
        return res.data.data // 返回用户数据
      } catch (e) {
        ElMessage.error('获取用户信息失败')
        return null
      }
    }

    const welcomeText = ref('欢迎来到系统，祝你工作愉快！')
    const welcomeLoading = ref(false)
    const fetchWelcome = async (userData) => {
      welcomeLoading.value = true
      try {
        const userInfo = userData || form.value
        const res = await getAIWelcome({
          nickname: userInfo.nickname || user.value?.nickname || user.value?.account || '用户',
          role: user.value?.role || '成员',
          enterprise: userInfo.enterpriseId || user.value?.enterpriseName || '本系统'
        })
        welcomeText.value = res.welcome || '欢迎来到系统，祝你工作愉快！'
        // 弹窗展示
        ElMessageBox.alert(welcomeText.value, '欢迎', {
          confirmButtonText: '我知道了',
          dangerouslyUseHTMLString: false
        })
      } catch (e) {
        welcomeText.value = '欢迎来到系统，祝你工作愉快！'
      }
      welcomeLoading.value = false
    }

    onMounted(async () => {
      const userData = await fetchUser()
      await fetchWelcome(userData)
    })
    
    // 监听表单变化，标记是否有修改
    watch(form, (newVal) => {
      if (JSON.stringify(newVal) !== JSON.stringify(originalForm.value)) {
        document.title = `* ${user.value?.account} - 资料修改`
      } else {
        document.title = `${user.value?.account} - 个人资料`
      }
    }, {deep: true})

    const rules = {
      nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
      email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
      ],
      phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        {
          validator: (rule, value, callback) => {
            const val = String(value || '').trim()
            if (!/^1[3-9]\d{9}$/.test(val)) {
              callback(new Error('请输入正确的手机号格式'))
            } else {
              callback()
            }
          },
          trigger: 'blur'
        }
      ]
    }

    const formRef = ref(null)

    const handleSave = () => {
      formRef.value.validate(async (valid) => {
        if (valid) {
          try {
            await api.user.updateCurrentUser(form.value)
            ElMessage.success('保存成功')
            fetchUser()
            originalForm.value = {...form.value} // 更新原始数据
          } catch (e) {
            ElMessage.error('保存失败')
          }
        }
      })
    }

    const goBack = () => {
      // 检查表单是否有未保存的修改
      if (JSON.stringify(form.value) !== JSON.stringify(originalForm.value)) {
        ElMessageBox.confirm(
          '你有未保存的修改，确定要返回吗？',
          '提示',
          {
            confirmButtonText: '返回',
            cancelButtonText: '取消',
            type: 'warning'
          }
        ).then(() => {
          if (window.history.length > 1) {
            router.back()
          } else {
            router.push('/admin/users')
          }
        }).catch(() => {
          // 取消返回
        })
      } else {
        if (window.history.length > 1) {
          router.back()
        } else {
          router.push('/admin/users')
        }
      }
    }

    const showAIChat = ref(false)

    // 换一句按钮也用最新数据
    const refreshWelcome = async () => {
      await fetchWelcome(form.value)
    }

    return {
      user,
      userAvatar,
      form,
      rules,
      formRef,
      handleSave,
      goBack,
      showAIChat,
      welcomeText,
      welcomeLoading,
      fetchWelcome: refreshWelcome
    }
  }
}
</script>

<style scoped>
.profile-container {
  max-width: 640px;
  margin: 40px auto;
  padding: 0 20px;
}

.profile-card {
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.10);
  transition: all 0.3s cubic-bezier(.25,.8,.25,1);
  background: linear-gradient(135deg, #f8fbff 0%, #eaf6ff 100%);
}

.profile-card:hover {
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.16);
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 24px;
  padding-bottom: 20px;
  border-bottom: 1.5px solid #e3eafc;
  justify-content: flex-start;
}

.back-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #6dd5ed 0%, #2193b0 100%);
  box-shadow: 0 2px 8px rgba(33, 147, 176, 0.12);
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  transition: background 0.3s, transform 0.3s, box-shadow 0.3s;
  color: #fff;
  font-size: 16px;
  cursor: pointer;
  margin-right: 0;
}

.back-btn:hover {
  background: linear-gradient(135deg, #2193b0 0%, #6dd5ed 100%);
  transform: rotate(-90deg) scale(1.08);
  box-shadow: 0 4px 16px rgba(33, 147, 176, 0.18);
}

.user-avatar {
  border: 3px solid #6dd5ed;
  box-shadow: 0 4px 16px rgba(33, 147, 176, 0.12);
  background: linear-gradient(135deg, #e0eafc 0%, #cfdef3 100%);
  margin-right: 12px;
}

.profile-title {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.profile-title h2 {
  margin: 0;
  font-size: 26px;
  font-weight: 700;
  color: #2193b0;
}

.profile-title span {
  color: #6dd5ed;
  font-size: 15px;
  display: block;
  margin-top: 4px;
  font-weight: 500;
}

.profile-form {
  margin-top: 36px;
}

.input-item {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(33, 147, 176, 0.06);
  transition: all 0.2s;
}

.input-item:focus-within {
  border-color: #2193b0;
  box-shadow: 0 0 0 2px rgba(33, 147, 176, 0.18);
}

.save-button {
  min-width: 140px;
  padding: 12px 20px;
  font-size: 16px;
  border-radius: 10px;
  background: linear-gradient(90deg, #2193b0 0%, #6dd5ed 100%);
  color: #fff;
  font-weight: 600;
  border: none;
  box-shadow: 0 4px 16px rgba(33, 147, 176, 0.18);
  transition: all 0.3s;
}

.save-button:hover {
  background: linear-gradient(90deg, #6dd5ed 0%, #2193b0 100%);
  transform: translateY(-2px) scale(1.04);
  box-shadow: 0 8px 24px rgba(33, 147, 176, 0.28);
}

.ai-float-btn {
  position: fixed;
  right: 40px;
  bottom: 100px;
  z-index: 10000;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 50%;
  width: 56px;
  height: 56px;
  font-size: 18px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.18);
  cursor: pointer;
}
</style>