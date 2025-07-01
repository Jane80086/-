import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import meetingService from '../services/meetingService';

export const useUserStore = defineStore('user', () => {
  // 状态
  const currentUser = ref(null);
  const token = ref(localStorage.getItem('token') || '');
  const isLoading = ref(false);

  // 计算属性
  const isLoggedIn = computed(() => !!token.value);
  const isAdmin = computed(() => currentUser.value?.userType === 'ADMIN');
  const isEnterprise = computed(() => currentUser.value?.userType === 'ENTERPRISE');
  const userTypeText = computed(() => {
    if (isAdmin.value) return '管理员';
    if (isEnterprise.value) return '企业用户';
    return '普通用户';
  });

  // 动作
  const setToken = (newToken) => {
    token.value = newToken;
    localStorage.setItem('token', newToken);
  };

  const clearToken = () => {
    token.value = '';
    localStorage.removeItem('token');
  };

  const setUser = (user) => {
    currentUser.value = user;
  };

  const clearUser = () => {
    currentUser.value = null;
  };

  const fetchUserInfo = async () => {
    if (!token.value) return;
    
    try {
      isLoading.value = true;
      const response = await meetingService.getUserInfo();
      if (response.data.code === 200) {
        setUser(response.data.data);
      }
    } catch (error) {
      console.error('获取用户信息失败:', error);
      // Token可能已过期，清除本地存储
      clearToken();
      clearUser();
    } finally {
      isLoading.value = false;
    }
  };

  const logout = () => {
    clearToken();
    clearUser();
  };

  return {
    // 状态
    currentUser,
    token,
    isLoading,
    
    // 计算属性
    isLoggedIn,
    isAdmin,
    isEnterprise,
    userTypeText,
    
    // 动作
    setToken,
    clearToken,
    setUser,
    clearUser,
    fetchUserInfo,
    logout
  };
}); 