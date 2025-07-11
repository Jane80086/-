import { defineStore } from 'pinia'
import { logout } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: {
      id: '',
      nickname: '',
      phone: '',
      email: '',
      gender: '',
      createdAt: '',
      role: 'user', // user, enterprise, admin
      avatar: ''
    }
  }),
  actions: {
    setUser(user) {
      // 确保用户角色信息正确设置
      // 注意：后端返回的 userType 是大写的（如 "ENTERPRISE"），需要转换为小写
      let role = user.role || user.userType || (user.account && String(user.account).startsWith('0000') ? 'admin' : 'user')
      
      // 将大写的用户类型转换为小写
      if (role && typeof role === 'string') {
        role = role.toLowerCase()
      }
      
      const userWithRole = {
        ...user,
        role: role
      }
      this.user = userWithRole
      localStorage.setItem('user', JSON.stringify(userWithRole))
    },
    initUser() {
      const user = localStorage.getItem('user')
      if (user) this.user = JSON.parse(user)
    },
    async logout() {
      try {
        // 调用退出登录API，忽略错误
        await logout()
      } catch (error) {
        // 只打印日志，不弹窗
        console.warn('退出登录API调用失败，已忽略：', error)
      } finally {
        // 无论API是否成功，都清除本地状态
        this.user = {
          id: '',
          nickname: '',
          phone: '',
          email: '',
          gender: '',
          createdAt: '',
          role: 'user',
          avatar: ''
        }
        // 清除本地存储
        localStorage.removeItem('user')
        localStorage.removeItem('token')
      }
    }
  }
}) 