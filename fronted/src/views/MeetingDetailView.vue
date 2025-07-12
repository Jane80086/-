<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/store/user';
import { meetingAPI } from '../api/meeting.js';
import fileService from '../api/file.js';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const meeting = ref(null);
const reviewRecords = ref([]);
const isLoading = ref(true);
const error = ref(null);

// 计算用户信息 - 使用统一的用户状态管理
const currentUser = computed(() => userStore.user);

// 获取会议详情
const fetchMeetingDetail = async () => {
  try {
    const response = await meetingAPI.getMeetingDetail(route.params.id);
    console.log('会议详情原始数据:', response);
    if (response.code === 200) {
      meeting.value = response.data;
      console.log('会议详情对象:', meeting.value);
      console.log('会议名称:', meeting.value?.meetingName);
      console.log('会议内容:', meeting.value?.meetingContent);
      console.log('开始时间:', meeting.value?.startTime);
      console.log('结束时间:', meeting.value?.endTime);
      console.log('创建人:', meeting.value?.creatorName);
      console.log('状态:', meeting.value?.status);
    } else {
      error.value = response.message || '获取会议详情失败';
    }
  } catch (err) {
    error.value = '获取会议详情失败，请稍后重试';
    console.error(err);
  } finally {
    isLoading.value = false;
  }
};

// 获取审核记录
const fetchReviewRecords = async () => {
  try {
    const response = await meetingAPI.getReviewRecordsByMeeting(route.params.id);
    if (response.code === 200) {
      reviewRecords.value = response.data || [];
    }
  } catch (err) {
    console.error('获取审核记录失败:', err);
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
  try {
    return new Date(dateTime).toLocaleString('zh-CN');
  } catch (err) {
    return dateTime;
  }
};

// 处理会议图片URL，支持私有bucket
const processMeetingImageUrl = async (meeting) => {
  if (meeting.imageUrl) {
    meeting.imageUrl = await fileService.getImageUrl(meeting.imageUrl);
  }
};

// 返回列表
const goBack = () => {
  router.push('/meeting');
};

// 监听会议数据变化，自动处理图片URL
watch(() => meeting.value, async (newMeeting) => {
  if (newMeeting) {
    await processMeetingImageUrl(newMeeting);
  }
}, { immediate: true });

onMounted(() => {
  console.log('MeetingDetailView 组件挂载');
  console.log('当前用户信息:', currentUser.value);
  console.log('用户角色:', currentUser.value?.role);
  console.log('用户类型:', currentUser.value?.userType);
  
  fetchMeetingDetail();
  fetchReviewRecords();
});
</script>

<template>
  <div class="meeting-detail">
    <div class="header">
      <button @click="goBack" class="back-btn">← 返回列表</button>
      <h1>会议详情</h1>
    </div>

    <div v-if="isLoading" class="loading">加载会议详情中...</div>
    <div v-if="error" class="error">{{ error }}</div>

    <div v-if="meeting && !isLoading && !error" class="meeting-content">
      <div class="meeting-header">
        <h2>{{ meeting.meetingName }}</h2>
        <span 
          class="status-badge"
          :style="{ backgroundColor: getStatusColor(meeting.status) }"
        >
          {{ getStatusText(meeting.status) }}
        </span>
      </div>

      <div class="meeting-info">
        <div class="info-section">
          <h3>基本信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">创建人:</span>
              <span class="value">{{ meeting.creatorName }}</span>
            </div>
            <div class="info-item">
              <span class="label">开始时间:</span>
              <span class="value">{{ formatDateTime(meeting.startTime) }}</span>
            </div>
            <div class="info-item">
              <span class="label">结束时间:</span>
              <span class="value">{{ formatDateTime(meeting.endTime) }}</span>
            </div>
            <div class="info-item">
              <span class="label">创建时间:</span>
              <span class="value">{{ formatDateTime(meeting.createTime) }}</span>
            </div>
            <div class="info-item">
              <span class="label">更新时间:</span>
              <span class="value">{{ formatDateTime(meeting.updateTime) }}</span>
            </div>
          </div>
        </div>

        <div v-if="meeting.reviewerName" class="info-section">
          <h3>审核信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">审核人:</span>
              <span class="value">{{ meeting.reviewerName }}</span>
            </div>
            <div class="info-item">
              <span class="label">审核时间:</span>
              <span class="value">{{ formatDateTime(meeting.reviewTime) }}</span>
            </div>
            <div v-if="meeting.reviewComment" class="info-item full-width">
              <span class="label">审核意见:</span>
              <span class="value">{{ meeting.reviewComment }}</span>
            </div>
          </div>
        </div>

        <div class="info-section">
          <h3>会议内容</h3>
          <div class="content-text">
            {{ meeting.meetingContent }}
          </div>
        </div>

        <div class="info-section">
          <h3>会议图片</h3>
          <div v-if="meeting.imageUrl" class="meeting-image">
            <img :src="meeting.imageUrl" alt="会议图片" class="meeting-image-display" />
          </div>
          <div v-else class="no-image">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <circle cx="8.5" cy="8.5" r="1.5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="21,15 16,10 5,21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <p>暂无会议图片</p>
          </div>
        </div>

        <!-- 审核记录部分 -->
        <div v-if="reviewRecords.length > 0" class="info-section">
          <h3>审核记录</h3>
          <div class="review-records">
            <div 
              v-for="record in reviewRecords" 
              :key="record.id"   
              class="review-record-item"
            >
              <div class="record-header">
                <div class="record-meta">
                  <span class="reviewer">审核人: {{ record.reviewerName }}</span>
                  <span class="review-time">{{ formatDateTime(record.reviewTime) }}</span>
                </div>
                <span 
                  class="status-badge"
                  :style="{ backgroundColor: getStatusColor(record.status) }"
                >
                  {{ getStatusText(record.status) }}
                </span>
              </div>
              <div v-if="record.reviewComment" class="review-comment">
                <span class="comment-label">审核意见:</span>
                <span class="comment-text">{{ record.reviewComment }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.meeting-detail {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  align-items: center;
  gap: 20px;
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
  font-size: 14px;
}

.header h1 {
  margin: 0;
  color: #2c3e50;
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

.meeting-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.meeting-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30px;
  background-color: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.meeting-header h2 {
  margin: 0;
  color: #2c3e50;
  font-size: 24px;
}

.status-badge {
  padding: 6px 16px;
  border-radius: 20px;
  color: white;
  font-size: 14px;
  font-weight: 500;
}

.meeting-info {
  padding: 30px;
}

.info-section {
  margin-bottom: 30px;
}

.info-section:last-child {
  margin-bottom: 0;
}

.info-section h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 18px;
  border-bottom: 2px solid #e9ecef;
  padding-bottom: 10px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.label {
  font-weight: 500;
  color: #666;
  font-size: 14px;
}

.value {
  color: #2c3e50;
  font-size: 16px;
}

.content-text {
  color: #2c3e50;
  font-size: 16px;
  line-height: 1.6;
  white-space: pre-wrap;
}

.review-records {
  margin-top: 20px;
}

.review-record-item {
  margin-bottom: 15px;
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.record-meta {
  display: flex;
  flex-direction: column;
}

.reviewer {
  font-weight: 500;
  color: #2c3e50;
  font-size: 16px;
}

.review-time {
  color: #666;
  font-size: 14px;
}

.review-comment {
  margin-top: 5px;
}

.comment-label {
  font-weight: 500;
  color: #666;
  font-size: 14px;
}

.comment-text {
  color: #2c3e50;
  font-size: 16px;
}

.meeting-image {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  background-color: #f8f9fa;
  border: 2px dashed #dee2e6;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.meeting-image:hover {
  border-color: #007bff;
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.15);
}

.meeting-image-display {
  max-width: 100%;
  max-height: 300px;
  object-fit: contain;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.no-image {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  background-color: #f8f9fa;
  border: 2px dashed #dee2e6;
  border-radius: 8px;
  padding: 40px 20px;
  color: #6c757d;
}

.no-image svg {
  margin-bottom: 16px;
  opacity: 0.6;
}

.no-image p {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

@media (max-width: 768px) {
  .meeting-detail {
    padding: 10px;
  }
  
  .meeting-header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style> 