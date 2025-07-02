import { createStore } from 'vuex'
import api from '@/api'

export default createStore({
  state: {
    user: null,
    token: localStorage.getItem('token') || null,
    loading: false
  },
  
  getters: {
    isAuthenticated: state => !!state.token,
    currentUser: state => state.user,
    isLoading: state => state.loading
  },
  
  mutations: {
    SET_USER(state, user) {
      state.user = user
    },
    SET_TOKEN(state, token) {
      state.token = token
      if (token) {
        localStorage.setItem('token', token)
      } else {
        localStorage.removeItem('token')
      }
    },
    SET_LOADING(state, loading) {
      state.loading = loading
    },
    CLEAR_AUTH(state) {
      state.user = null
      state.token = null
      localStorage.removeItem('token')
    }
  },
  
  actions: {
    async login({ commit }, credentials) {
      commit('SET_LOADING', true)
      try {
        console.log('Store login action - 发送登录请求:', credentials)
        const response = await api.auth.login(credentials)
        console.log('Store login action - 收到响应:', response)
        console.log('Store login action - response.data:', response.data)
        
        // 检查登录是否成功
        if (response.data && response.data.success) {
          const token = response.data.token
          const user = response.data.user
          
          console.log('Store login action - 登录成功，token:', token)
          console.log('Store login action - 登录成功，user:', user)
          
          if (token) {
            commit('SET_TOKEN', token)
            console.log('Store login action - Token已存储')
          }
          
          if (user) {
            commit('SET_USER', user)
            console.log('Store login action - User已存储')
          }
        } else {
          console.log('Store login action - 登录失败:', response.data?.message)
        }
        
        return response
      } catch (error) {
        console.error('Store login action - 登录异常:', error)
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    async register({ commit }, userData) {
      commit('SET_LOADING', true)
      try {
        const response = await api.auth.register(userData)
        return response
      } catch (error) {
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    async logout({ commit }) {
      commit('CLEAR_AUTH')
    },
    
    async fetchUser({ commit }) {
      try {
        const response = await api.user.getCurrentUser()
        commit('SET_USER', response.data)
        return response
      } catch (error) {
        commit('CLEAR_AUTH')
        throw error
      }
    }
  }
}) 