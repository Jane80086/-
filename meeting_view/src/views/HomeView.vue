<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import MeetingList from '../components/MeetingList.vue'; 
import meetingService from '../services/meetingService';

const router = useRouter();
const meetings = ref([]);
const filteredMeetings = ref([]);
const reviewRecords = ref([]);
const isLoading = ref(true);
const error = ref(null);
const showAddModal = ref(false);
const showReviewModal = ref(false);
const showRecordsModal = ref(false);
const currentUser = ref(null);
const activeTab = ref('meetings'); // meetings, records
const addUploading = ref(false); // 新增会议图片上传状态

// 搜索和筛选条件
const searchTerm = ref('');
const statusFilter = ref('');
const dateFilter = ref('');

// 新会议表单
const newMeeting = ref({
  meetingName: '',
  startTime: '',
  endTime: '',
  meetingContent: '',
  imageUrl: ''
});

// 审核表单
const reviewForm = ref({
  meetingId: null,
  status: 1,
  reviewComment: ''
});

// 计算用户权限
const isAdmin = computed(() => currentUser.value?.userType === 'ADMIN');
const isEnterprise = computed(() => currentUser.value?.userType === 'ENTERPRISE');

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const response = await meetingService.getUserInfo();
    console.log('用户信息原始数据:', response.data); // 添加调试信息
    if (response.data.code === 200) {
      currentUser.value = response.data.data;
      console.log('当前用户信息:', currentUser.value); // 添加调试信息
      console.log('用户类型:', currentUser.value?.userType); // 添加调试信息
    }
  } catch (err) {
    console.error('获取用户信息失败:', err);
  }
};

// 获取会议列表（根据用户权限）
const fetchMeetings = async () => {
  try {
    const query = {
      page: 1,
      size: 100,
      meetingName: searchTerm.value || null,
      status: statusFilter.value ? parseInt(statusFilter.value) : null
    };
    
    console.log('查询参数:', query); // 添加调试信息
    const response = await meetingService.getMeetings(query);
    console.log('会议列表原始数据:', response.data); // 添加调试信息
    if (response.data.code === 200) {
      meetings.value = response.data.data.meetings || [];
      console.log('会议列表对象:', meetings.value); // 添加调试信息
      filteredMeetings.value = meetings.value;
    }
  } catch (err) {
    error.value = '加载会议列表失败，请稍后重试';
    console.error(err);
  } finally {
    isLoading.value = false;
  }
};

// 获取审核记录
const fetchReviewRecords = async () => {
  try {
    let response;
    if (isAdmin.value) {
      // 管理员查看自己的审核记录
      response = await meetingService.getReviewRecordsByReviewer();
    } else if (isEnterprise.value) {
      // 企业用户查看自己的申请记录
      response = await meetingService.getReviewRecordsByCreator();
    } else {
      // 普通用户没有审核记录
      reviewRecords.value = [];
      return;
    }
    
    if (response.data.code === 200) {
      reviewRecords.value = response.data.data || [];
    }
  } catch (err) {
    console.error('获取审核记录失败:', err);
  }
};

// 搜索会议
const searchMeetings = async () => {
  try {
    isLoading.value = true;
    await fetchMeetings();
  } catch (err) {
    error.value = '搜索失败，请重试';
    console.error(err);
  } finally {
    isLoading.value = false;
  }
};

// 添加会议
const handleAdd = async () => {
  try {
    await meetingService.createMeeting(newMeeting.value);
    showAddModal.value = false;
    newMeeting.value = {
      meetingName: '',
      startTime: '',
      endTime: '',
      meetingContent: '',
      imageUrl: ''
    };
    await fetchMeetings();
  } catch (err) {
    error.value = err.response?.data?.message || '添加会议失败，请重试';
    console.error(err);
  }
};

// 删除会议
const handleDelete = async (id) => {
  if (!confirm('确定要删除这个会议吗？此操作不可撤销。')) {
    return;
  }
  
  try {
    await meetingService.deleteMeeting(id, true);
    await fetchMeetings();
  } catch (err) {
    error.value = err.response?.data?.message || '删除会议失败，请重试';
    console.error(err);
  }
};

// 更新会议
const handleUpdate = async (updatedMeeting) => {
  try {
    await meetingService.updateMeeting(updatedMeeting);
    await fetchMeetings();
  } catch (err) {
    error.value = err.response?.data?.message || '更新会议失败，请重试';
    console.error(err);
  }
};

// 审核会议
const handleReview = async () => {
  try {
    await meetingService.reviewMeeting(reviewForm.value);
    showReviewModal.value = false;
    reviewForm.value = {
      meetingId: null,
      status: 1,
      reviewComment: ''
    };
    await fetchMeetings();
  } catch (err) {
    error.value = err.response?.data?.message || '审核失败，请重试';
    console.error(err);
  }
};

// 打开审核模态框
const openReviewModal = (meeting) => {
  reviewForm.value = {
    meetingId: meeting.id,
    status: 1,
    reviewComment: ''
  };
  showReviewModal.value = true;
};

// 切换标签页
const switchTab = (tab) => {
  activeTab.value = tab;
  if (tab === 'records') {
    fetchReviewRecords();
  }
};

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 0: return '待审核';
    case 1: return '已通过';
    case 2: return '已拒绝';
    default: return '未知';
  }
};

// 获取状态颜色
const getStatusColor = (status) => {
  switch (status) {
    case 0: return '#FF9800';
    case 1: return '#4CAF50';
    case 2: return '#F44336';
    default: return '#9E9E9E';
  }
};

// 格式化时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '';
  return new Date(dateTime).toLocaleString('zh-CN');
};

// 登出
const logout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  router.push('/login');
};

// 处理新建会议图片上传
const onAddImageChange = async (event) => {
  const file = event.target.files[0];
  if (!file) return;
  
  // 检查文件大小（10MB限制）
  const maxSize = 10 * 1024 * 1024; // 10MB
  if (file.size > maxSize) {
    alert('文件大小不能超过10MB');
    event.target.value = ''; // 清空文件选择
    return;
  }
  
  // 检查文件类型
  const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
  if (!allowedTypes.includes(file.type)) {
    alert('只支持JPG、PNG、GIF格式的图片');
    event.target.value = '';
    return;
  }
  
  addUploading.value = true;
  try {
    const res = await meetingService.uploadMeetingImage(file);
    if (res.data.code === 200) {
      newMeeting.value.imageUrl = res.data.data;
    } else {
      alert('图片上传失败: ' + res.data.message);
    }
  } catch (e) {
    console.error('图片上传错误:', e);
    if (e.response?.status === 413) {
      alert('文件太大，请选择小于10MB的图片');
    } else {
      alert('图片上传失败，请重试');
    }
  } finally {
    addUploading.value = false;
  }
};

onMounted(() => {
  fetchUserInfo();
  fetchMeetings();
  fetchReviewRecords();
});
</script>

<template>
  <div class="home">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="header-left">
        <div class="logo">
          <svg width="32" height="32" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <h1>会议管理系统</h1>
        </div>
      </div>
      
      <div class="user-info">
        <div class="user-avatar">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="user-details">
          <span class="user-name">{{ currentUser?.realName || currentUser?.username }}</span>
          <span class="user-type">{{ currentUser?.userType === 'ADMIN' ? '管理员' : currentUser?.userType === 'ENTERPRISE' ? '企业用户' : '普通用户' }}</span>
        </div>
        <button @click="logout" class="logout-btn" title="退出登录">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M9 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <polyline points="16,17 21,12 16,7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <line x1="21" y1="12" x2="9" y2="12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
      </div>
    </div>
    
    <!-- 标签页导航 -->
    <div class="tabs">
      <button 
        :class="{ active: activeTab === 'meetings' }"
        @click="activeTab = 'meetings'"
        class="tab-btn"
      >
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M9 11H15M9 15H15M9 7H15M5 3H19C20.1046 3 21 3.89543 21 5V19C21 20.1046 20.1046 21 19 21H5C3.89543 21 3 20.1046 3 19V5C3 3.89543 3.89543 3 5 3Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        会议列表
      </button>
      <button 
        v-if="isAdmin || isEnterprise"
        :class="{ active: activeTab === 'records' }"
        @click="switchTab('records')"
        class="tab-btn"
      >
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M14 2H6C5.46957 2 4.96086 2.21071 4.58579 2.58579C4.21071 2.96086 4 3.46957 4 4V20C4 20.5304 4.21071 21.0391 4.58579 21.4142C4.96086 21.7893 5.46957 22 6 22H18C18.5304 22 19.0391 21.7893 19.4142 21.4142C19.7893 21.0391 20 20.5304 20 20V8L14 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <polyline points="14,2 14,8 20,8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <line x1="16" y1="13" x2="8" y2="13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <line x1="16" y1="17" x2="8" y2="17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <polyline points="10,9 9,9 8,9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        审核记录
      </button>
    </div>
    
    <!-- 会议列表标签页 -->
    <div v-if="activeTab === 'meetings'" class="meetings-section">
      <!-- 搜索和筛选控制栏 -->
      <div class="controls">
        <div class="search-filters">
          <div class="search-box">
            <div class="search-input-wrapper">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="11" cy="11" r="8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M21 21L16.65 16.65" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <input 
                v-model="searchTerm" 
                placeholder="搜索会议名称..."
                @keyup.enter="searchMeetings" 
                class="search-input"
              >
            </div>
            <button @click="searchMeetings" class="search-btn">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="11" cy="11" r="8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M21 21L16.65 16.65" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              搜索
            </button>
            <button @click="searchTerm = ''; fetchMeetings()" class="reset-btn">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M12 7V12L15 15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              重置
            </button>
          </div>
          
          <div class="filters">
            <div class="filter-group">
              <label>状态筛选:</label>
              <select v-model="statusFilter" @change="fetchMeetings" class="filter-select">
                <option value="">全部状态</option>
                <option v-if="isAdmin" value="0">待审核</option>
                <option value="1">已通过</option>
                <option v-if="isAdmin" value="2">已拒绝</option>
              </select>
            </div>
            
            <div class="filter-group">
              <label>日期筛选:</label>
              <input 
                v-model="dateFilter" 
                type="date" 
                @change="fetchMeetings"
                class="filter-input"
              >
            </div>
          </div>
        </div>
        
        <div class="actions">
          <button 
            v-if="isAdmin || isEnterprise" 
            class="add-btn" 
            @click="showAddModal = true"
          >
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <line x1="12" y1="5" x2="12" y2="19" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="5" y1="12" x2="19" y2="12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            创建会议
          </button>
          
          <button 
            v-if="isAdmin" 
            class="review-btn" 
            @click="router.push('/review')"
          >
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M14 2H6C5.46957 2 4.96086 2.21071 4.58579 2.58579C4.21071 2.96086 4 3.46957 4 4V20C4 20.5304 4.21071 21.0391 4.58579 21.4142C4.96086 21.7893 5.46957 22 6 22H18C18.5304 22 19.0391 21.7893 19.4142 21.4142C19.7893 21.0391 20 20.5304 20 20V8L14 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="14,2 14,8 20,8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="16" y1="13" x2="8" y2="13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="16" y1="17" x2="8" y2="17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="10,9 9,9 8,9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            审核管理
          </button>
        </div>
      </div>
      
      <!-- 加载和错误状态 -->
      <div v-if="isLoading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>正在加载会议数据...</p>
      </div>
      
      <div v-if="error" class="error-container">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <line x1="15" y1="9" x2="9" y2="15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <line x1="9" y1="9" x2="15" y2="15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <p>{{ error }}</p>
      </div>
      
      <!-- 会议列表 -->
      <MeetingList 
        v-if="!isLoading && !error" 
        :meetings="filteredMeetings" 
        :current-user="currentUser"
        :on-delete="handleDelete"
        :on-review="openReviewModal"
        @update-meeting="handleUpdate"
      />
    </div>

    <!-- 审核记录标签页 -->
    <div v-if="activeTab === 'records' && (isAdmin || isEnterprise)" class="records-section">
      <div class="records-header">
        <h3>{{ isAdmin ? '我的审核记录' : '我的申请记录' }}</h3>
        <p class="records-subtitle">{{ isAdmin ? '查看您审核过的所有会议记录' : '查看您申请过的所有会议记录' }}</p>
      </div>
      
      <div v-if="reviewRecords.length === 0" class="no-data">
        <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M14 2H6C5.46957 2 4.96086 2.21071 4.58579 2.58579C4.21071 2.96086 4 3.46957 4 4V20C4 20.5304 4.21071 21.0391 4.58579 21.4142C4.96086 21.7893 5.46957 22 6 22H18C18.5304 22 19.0391 21.7893 19.4142 21.4142C19.7893 21.0391 20 20.5304 20 20V8L14 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <polyline points="14,2 14,8 20,8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <line x1="16" y1="13" x2="8" y2="13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <line x1="16" y1="17" x2="8" y2="17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <polyline points="10,9 9,9 8,9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <p>暂无{{ isAdmin ? '审核' : '申请' }}记录</p>
      </div>
      
      <div v-else class="records-list">
        <div 
          v-for="record in reviewRecords" 
          :key="record.id"   
          class="record-item"
        >
          <div class="record-content">
            <div class="record-header">
              <h3>{{ record.meetingName }}</h3>
              <span 
                class="status-badge"
                :style="{ backgroundColor: getStatusColor(record.status) }"
              >
                {{ getStatusText(record.status) }}
              </span>
            </div>
            
            <div class="record-meta">
              <div class="meta-item">
                <span class="label">创建人:</span>
                <span class="value">{{ record.creator }}</span>
              </div>
              <div v-if="isAdmin" class="meta-item">
                <span class="label">审核人:</span>
                <span class="value">{{ record.reviewer }}</span>
              </div>
              <div class="meta-item">
                <span class="label">{{ isAdmin ? '审核' : '申请' }}时间:</span>
                <span class="value">{{ formatDateTime(record.reviewTime) }}</span>
              </div>
              <div v-if="record.reviewComment" class="meta-item full-width">
                <span class="label">{{ isAdmin ? '审核' : '申请' }}意见:</span>
                <span class="value">{{ record.reviewComment }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 创建会议模态框 -->
    <div v-if="showAddModal" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>创建新会议</h3>
          <button @click="showAddModal = false" class="close-btn">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <line x1="18" y1="6" x2="6" y2="18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="6" y1="6" x2="18" y2="18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>
        <form @submit.prevent="handleAdd" class="modal-form"> 
          <div class="form-group">
            <label>会议名称:</label>
            <input v-model="newMeeting.meetingName" required placeholder="请输入会议名称">
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>开始时间:</label>
              <input v-model="newMeeting.startTime" type="datetime-local" required>
            </div>
            <div class="form-group">
              <label>结束时间:</label>
              <input v-model="newMeeting.endTime" type="datetime-local" required>
            </div>
          </div>
          <div class="form-group">
            <label>会议图片:</label>
            <input type="file" accept="image/*" @change="onAddImageChange" />
            <div v-if="addUploading">图片上传中...</div>
            <div v-if="newMeeting.imageUrl">
              <img :src="newMeeting.imageUrl" alt="会议图片" style="max-width:200px;max-height:120px;" />
            </div>
          </div>
          <div class="form-group">
            <label>会议内容:</label>
            <textarea v-model="newMeeting.meetingContent" rows="4" required placeholder="请输入会议详细内容"></textarea>
          </div>
          <div class="modal-actions">
            <button type="button" @click="showAddModal = false" class="cancel-btn">取消</button>
            <button type="submit" class="submit-btn">创建会议</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 审核会议模态框 -->
    <div v-if="showReviewModal" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>审核会议</h3>
          <button @click="showReviewModal = false" class="close-btn">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <line x1="18" y1="6" x2="6" y2="18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="6" y1="6" x2="18" y2="18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>
        <form @submit.prevent="handleReview" class="modal-form"> 
          <div class="form-group">
            <label>审核结果:</label>
            <select v-model="reviewForm.status" required class="review-select">
              <option value="1">通过</option>
              <option value="2">拒绝</option>
            </select>
          </div>
          <div class="form-group">
            <label>审核意见:</label>
            <textarea v-model="reviewForm.reviewComment" rows="4" required placeholder="请输入审核意见"></textarea>
          </div>
          <div class="modal-actions">
            <button type="button" @click="showReviewModal = false" class="cancel-btn">取消</button>
            <button type="submit" class="submit-btn">提交审核</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
  background: #f8f9fa;
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px 30px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e9ecef;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #667eea;
}

.logo h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.user-type {
  color: #6c757d;
  font-size: 12px;
  background: #e9ecef;
  padding: 2px 8px;
  border-radius: 12px;
  text-align: center;
}

.logout-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  color: #6c757d;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  background: #e9ecef;
  color: #495057;
  transform: translateY(-1px);
}

.tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 30px;
  background: white;
  padding: 8px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: 1px solid #e9ecef;
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border: none;
  background: transparent;
  color: #6c757d;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
  font-size: 14px;
}

.tab-btn:hover {
  background: #f8f9fa;
  color: #495057;
}

.tab-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.meetings-section {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e9ecef;
}

.controls {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 30px;
  flex-wrap: wrap;
  gap: 20px;
}

.search-filters {
  display: flex;
  flex-direction: column;
  gap: 20px;
  flex: 1;
}

.search-box {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-input-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  background: white;
  flex: 1;
  transition: all 0.3s ease;
}

.search-input-wrapper:focus-within {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.search-input-wrapper svg {
  color: #6c757d;
}

.search-input {
  border: none;
  outline: none;
  flex: 1;
  font-size: 14px;
  background: transparent;
}

.search-input::placeholder {
  color: #adb5bd;
}

.search-btn, .reset-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 20px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
  font-size: 14px;
}

.search-btn {
  background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
  color: white;
}

.search-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(40, 167, 69, 0.3);
}

.reset-btn {
  background: #6c757d;
  color: white;
}

.reset-btn:hover {
  background: #5a6268;
  transform: translateY(-1px);
}

.filters {
  display: flex;
  gap: 20px;
  align-items: flex-end;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.filter-group label {
  font-size: 12px;
  font-weight: 600;
  color: #495057;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.filter-select,
.filter-input {
  padding: 10px 14px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 14px;
  background: white;
  transition: all 0.3s ease;
  min-width: 150px;
}

.filter-select:focus,
.filter-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.actions {
  display: flex;
  gap: 12px;
}

.add-btn, .review-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
  font-size: 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.add-btn {
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
  color: white;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 123, 255, 0.3);
}

.review-btn {
  background: linear-gradient(135deg, #ffc107 0%, #e0a800 100%);
  color: white;
}

.review-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 193, 7, 0.3);
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 20px;
}

.loading-container p {
  color: #6c757d;
  font-size: 16px;
  margin: 0;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e9ecef;
  border-top: 3px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-container {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #f8d7da;
  color: #721c24;
  padding: 16px 20px;
  border-radius: 10px;
  border: 1px solid #f5c6cb;
  margin-bottom: 20px;
}

.error-container svg {
  flex-shrink: 0;
}

.error-container p {
  margin: 0;
  font-weight: 500;
}

.records-section {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e9ecef;
}

.records-header {
  margin-bottom: 30px;
  text-align: center;
}

.records-header h3 {
  color: #2c3e50;
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 700;
}

.records-subtitle {
  color: #6c757d;
  font-size: 14px;
  margin: 0;
}

.no-data {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 20px;
  color: #6c757d;
}

.no-data svg {
  color: #adb5bd;
}

.no-data p {
  margin: 0;
  font-size: 16px;
}

.records-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.record-item {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 12px;
  padding: 24px;
  transition: all 0.3s ease;
}

.record-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  border-color: #667eea;
}

.record-content {
  flex: 1;
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.record-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

.status-badge {
  padding: 6px 16px;
  border-radius: 20px;
  color: white;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.record-meta {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.meta-item.full-width {
  grid-column: 1 / -1;
}

.label {
  font-weight: 600;
  color: #495057;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.value {
  font-size: 14px;
  color: #2c3e50;
  line-height: 1.4;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.modal-content {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  border: 1px solid #e9ecef;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 30px 0 30px;
  margin-bottom: 20px;
}

.modal-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 20px;
  font-weight: 700;
}

.close-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  color: #6c757d;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: #e9ecef;
  color: #495057;
}

.modal-form {
  padding: 0 30px 30px 30px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.form-group label {
  font-weight: 600;
  color: #2c3e50;
  font-size: 14px;
}

.form-group input,
.form-group select,
.form-group textarea {
  padding: 12px 16px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 14px;
  background: white;
  transition: all 0.3s ease;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
  font-family: inherit;
}

.review-select {
  background: white;
}

.modal-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;
}

.cancel-btn, .submit-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
  font-size: 14px;
}

.cancel-btn {
  background: #6c757d;
  color: white;
}

.cancel-btn:hover {
  background: #5a6268;
  transform: translateY(-1px);
}

.submit-btn {
  background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
  color: white;
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(40, 167, 69, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .home {
    padding: 15px;
  }
  
  .header {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .controls {
    flex-direction: column;
  }
  
  .search-box {
    flex-direction: column;
  }
  
  .filters {
    flex-direction: column;
  }
  
  .actions {
    justify-content: center;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .modal-content {
    width: 95%;
    margin: 20px;
  }
  
  .modal-form {
    padding: 0 20px 20px 20px;
  }
  
  .modal-header {
    padding: 20px 20px 0 20px;
  }
}

@media (max-width: 480px) {
  .logo h1 {
    font-size: 20px;
  }
  
  .user-details {
    display: none;
  }
  
  .tabs {
    flex-direction: column;
  }
  
  .tab-btn {
    justify-content: center;
  }
}
</style>