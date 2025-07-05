<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const currentUser = ref(null);
const pendingMeetings = ref([]);
const reviewRecords = ref([]);
const isLoading = ref(true);
const error = ref(null);
const activeTab = ref('pending'); // pending, records

// 审核表单
const reviewForm = ref({
  meetingId: null,
  status: 1,
  reviewComment: ''
});

// 计算用户权限
const isAdmin = computed(() => currentUser.value?.userType === 'ADMIN');

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    // const response = await meetingService.getUserInfo(); // Original line commented out
    // if (response.data.code === 200) { // Original line commented out
    //   currentUser.value = response.data.data; // Original line commented out
    // } // Original line commented out
  } catch (err) {
    console.error('获取用户信息失败:', err);
  }
};

// 获取待审核会议
const fetchPendingMeetings = async () => {
  try {
    // const response = await meetingService.getPendingMeetings(); // Original line commented out
    // if (response.data.code === 200) { // Original line commented out
    //   pendingMeetings.value = response.data.data || []; // Original line commented out
    // } // Original line commented out
  } catch (err) {
    error.value = '加载待审核会议失败，请稍后重试';
    console.error(err);
  }
};

// 获取审核记录
const fetchReviewRecords = async () => {
  try {
    // const response = await meetingService.getReviewRecordsByReviewer(); // Original line commented out
    // if (response.data.code === 200) { // Original line commented out
    //   reviewRecords.value = response.data.data || []; // Original line commented out
    // } // Original line commented out
  } catch (err) {
    error.value = '加载审核记录失败，请稍后重试';
    console.error(err);
  } finally {
    isLoading.value = false;
  }
};

// 审核会议
const handleReview = async () => {
  try {
    // await meetingService.reviewMeeting(reviewForm.value); // Original line commented out
    reviewForm.value = {
      meetingId: null,
      status: 1,
      reviewComment: ''
    };
    await fetchPendingMeetings();
    await fetchReviewRecords();
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

// 切换标签页
const switchTab = (tab) => {
  activeTab.value = tab;
  if (tab === 'pending') {
    fetchPendingMeetings();
  } else if (tab === 'records') {
    fetchReviewRecords();
  }
};

onMounted(() => {
  fetchUserInfo();
  fetchPendingMeetings();
  fetchReviewRecords();
});
</script>

<template>
  <div class="review-page">
    <div class="header">
      <h1>审核管理</h1>
      <button @click="router.push('/')" class="back-btn">返回首页</button>
    </div>

    <div v-if="!isAdmin" class="no-permission">
      <p>您没有权限访问审核管理页面</p>
      <button @click="router.push('/')">返回首页</button>
    </div>

    <div v-else>
      <div class="tabs">
        <button 
          :class="{ active: activeTab === 'pending' }"
          @click="switchTab('pending')"
        >
          待审核会议 ({{ pendingMeetings.length }})
        </button>
        <button 
          :class="{ active: activeTab === 'records' }"
          @click="switchTab('records')"
        >
          审核记录 ({{ reviewRecords.length }})
        </button>
      </div>

      <div v-if="isLoading" class="loading">加载数据中...</div>
      <div v-if="error" class="error">{{ error }}</div>

      <!-- 待审核会议列表 -->
      <div v-if="activeTab === 'pending' && !isLoading && !error" class="pending-meetings">
        <div v-if="pendingMeetings.length === 0" class="no-data">
          <p>暂无待审核的会议</p>
        </div>
        
        <div 
          v-for="meeting in pendingMeetings" 
          :key="meeting.id"   
          class="meeting-item"
        >
          <div class="meeting-content">
            <div class="meeting-header">
              <h3>{{ meeting.meetingName }}</h3>
              <span class="status-badge pending">待审核</span>
            </div>
            
            <div class="meeting-meta">
              <div class="time-info">
                <span class="label">开始时间:</span>
                <span>{{ formatDateTime(meeting.startTime) }}</span>
              </div>
              <div class="time-info">
                <span class="label">结束时间:</span>
                <span>{{ formatDateTime(meeting.endTime) }}</span>
              </div>
              <div class="creator-info">
                <span class="label">创建人:</span>
                <span>{{ meeting.creator }}</span>
              </div>
              <div class="content-info">
                <span class="label">会议内容:</span>
                <span>{{ meeting.meetingContent?.substring(0, 100) }}{{ meeting.meetingContent?.length > 100 ? '...' : '' }}</span>
              </div>
            </div>
          </div>
          
          <div class="actions">
            <button 
              class="review-btn" 
              @click="openReviewModal(meeting)"
            > 
              审核 
            </button>
          </div>
        </div>
      </div>

      <!-- 审核记录列表 -->
      <div v-if="activeTab === 'records' && !isLoading && !error" class="review-records">
        <div v-if="reviewRecords.length === 0" class="no-data">
          <p>暂无审核记录</p>
        </div>
        
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
              <div class="creator-info">
                <span class="label">创建人:</span>
                <span>{{ record.creator }}</span>
              </div>
              <div class="reviewer-info">
                <span class="label">审核人:</span>
                <span>{{ record.reviewer }}</span>
              </div>
              <div class="review-time-info">
                <span class="label">审核时间:</span>
                <span>{{ formatDateTime(record.reviewTime) }}</span>
              </div>
              <div v-if="record.reviewComment" class="comment-info">
                <span class="label">审核意见:</span>
                <span>{{ record.reviewComment }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 审核模态框 -->
    <div v-if="reviewForm.meetingId" class="modal">
      <div class="modal-content">
        <h3>审核会议</h3>
        <form @submit.prevent="handleReview"> 
          <div class="form-group">
            <label>审核结果:</label>
            <select v-model="reviewForm.status" required>
              <option value="1">通过</option>
              <option value="2">拒绝</option>
            </select>
          </div>
          <div class="form-group">
            <label>审核意见:</label>
            <textarea v-model="reviewForm.reviewComment" rows="4" required></textarea>
          </div>
          <div class="button-group">
            <button type="submit">提交审核</button>
            <button type="button" @click="reviewForm.meetingId = null">取消</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.review-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #eee;
}

.back-btn {
  padding: 8px 16px;
  background-color: #6c757d;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.no-permission {
  text-align: center;
  padding: 40px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.tabs button {
  padding: 12px 20px;
  border: none;
  background-color: #f8f9fa;
  color: #6c757d;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
}

.tabs button.active {
  background-color: #007bff;
  color: white;
}

.loading {
  text-align: center;
  padding: 40px;
  font-size: 18px;
  color: #666;
}

.error {
  background-color: #ffebee;
  color: #c62828;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.no-data {
  text-align: center;
  padding: 40px;
  color: #666;
}

.meeting-item, .record-item {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
}

.meeting-content, .record-content {
  flex: 1;
}

.meeting-header, .record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.meeting-header h3, .record-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 18px;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  color: white;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.pending {
  background-color: #FF9800;
}

.meeting-meta, .record-meta {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 10px;
  margin-bottom: 15px;
}

.time-info, .creator-info, .reviewer-info, .review-time-info, .comment-info, .content-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.label {
  font-weight: 500;
  color: #666;
  font-size: 14px;
}

.actions {
  display: flex;
  gap: 10px;
}

.review-btn {
  padding: 8px 16px;
  background-color: #FF9800;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
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
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
}

.modal-content h3 {
  margin-bottom: 20px;
  color: #2c3e50;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #2c3e50;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.button-group {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.button-group button {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
}

.button-group button[type="submit"] {
  background-color: #4CAF50;
  color: white;
}

.button-group button[type="button"] {
  background-color: #9e9e9e;
  color: white;
}
</style> 