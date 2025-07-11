import { createStore } from 'vuex'
import { login, register, logout } from '@/api/auth'

export default createStore({
  state: {
    user: null,
    token: localStorage.getItem('token') || '',
    isAuthenticated: false
  },
  
  mutations: {
    SET_USER(state, user) {
      state.user = user
      state.isAuthenticated = !!user
      if (user) {
        localStorage.setItem('user', JSON.stringify(user))
      } else {
        localStorage.removeItem('user')
      }
    },
    
    SET_TOKEN(state, token) {
      state.token = token
      if (token) {
        localStorage.setItem('token', token)
      } else {
        localStorage.removeItem('token')
      }
    },
    
    CLEAR_AUTH(state) {
      state.user = null
      state.token = ''
      state.isAuthenticated = false
      localStorage.removeItem('user')
      localStorage.removeItem('token')
    }
  },
  
  actions: {
    async login({ commit }, credentials) {
      try {
        const response = await login(credentials)
        if (response.code === 200) {
          const { user, token } = response.data
          commit('SET_USER', user)
          commit('SET_TOKEN', token)
          return { success: true, data: response.data }
        } else {
          return { success: false, message: response.msg || '登录失败' }
        }
      } catch (error) {
        console.error('登录失败:', error)
        return { success: false, message: error.message || '登录失败' }
      }
    },
    
    async register({ commit }, userData) {
      try {
        const response = await register(userData)
        if (response.code === 200) {
          return { success: true, data: response.data }
        } else {
          return { success: false, message: response.msg || '注册失败' }
        }
      } catch (error) {
        console.error('注册失败:', error)
        return { success: false, message: error.message || '注册失败' }
      }
    },
    
    async logout({ commit }) {
      try {
        // 调用退出登录API
        await logout()
      } catch (error) {
        console.error('退出登录API调用失败:', error)
      } finally {
        // 无论API是否成功，都清除本地状态
        commit('CLEAR_AUTH')
      }
    },
    
    initAuth({ commit }) {
      const user = localStorage.getItem('user')
      const token = localStorage.getItem('token')
      
      if (user && token) {
        try {
          const userData = JSON.parse(user)
          commit('SET_USER', userData)
          commit('SET_TOKEN', token)
        } catch (error) {
          console.error('解析用户信息失败:', error)
          commit('CLEAR_AUTH')
        }
      }
    }
  },
  
  getters: {
    currentUser: state => state.user,
    isAuthenticated: state => state.isAuthenticated,
    token: state => state.token
  }
}) 