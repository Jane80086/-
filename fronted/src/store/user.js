import { defineStore } from 'pinia'
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
    setUser(user) { this.user = user }
  }
}) 