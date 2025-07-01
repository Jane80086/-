<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import meetingService from '../services/meetingService';
import fileService from '../services/fileService';

const props = defineProps({
  meetings: {
    type: Array,
    required: true 
  },
  currentUser: {
    type: Object,
    required: true 
  },
  onDelete: {
    type: Function,
    required: true 
  },
  onReview: {
    type: Function,
    required: true 
  }
});

const emit = defineEmits(['update-meeting']);

const router = useRouter();
const showEditModal = ref(false);
const currentMeeting = ref(null);
const editUploading = ref(false);

// 计算用户权限
const isAdmin = computed(() => props.currentUser?.userType === 'ADMIN');
const isEnterprise = computed(() => props.currentUser?.userType === 'ENTERPRISE');

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

// 查看会议详情
const viewMeeting = (id) => {
  router.push({ name: 'meeting-detail', params: { id } });
};

// 编辑会议
const editMeeting = (meeting) => {
  currentMeeting.value = { ...meeting };
  showEditModal.value = true;
};

// 关闭模态框
const closeModal = () => {
  showEditModal.value = false;
  currentMeeting.value = null;
};

// 保存编辑
const saveEdit = () => {
  emit('update-meeting', currentMeeting.value);
  closeModal();
};

// 检查是否有编辑权限
const canEdit = (meeting) => {
  return isAdmin.value || (isEnterprise.value && meeting.creator === props.currentUser?.username);
};

// 检查是否有删除权限
const canDelete = (meeting) => {
  return isAdmin.value || (isEnterprise.value && meeting.creator === props.currentUser?.username);
};

// 检查是否有审核权限
const canReview = (meeting) => {
  return isAdmin.value && meeting.status === 0;
};

// 处理编辑会议图片上传
const onEditImageChange = async (event) => {
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
  
  editUploading.value = true;
  try {
    const res = await meetingService.uploadMeetingImage(file);
    // 处理后端ApiResponse格式
    if (res.data && res.data.code === 200 && res.data.data) {
      // 对于私有bucket，返回的是对象名称，需要获取预签名URL
      if (!res.data.data.startsWith('http://') && !res.data.data.startsWith('https://')) {
        const presignedUrl = await fileService.getImageUrl(res.data.data);
        currentMeeting.value.imageUrl = presignedUrl || res.data.data;
      } else {
        currentMeeting.value.imageUrl = res.data.data;
      }
    } else {
      alert('图片上传失败: ' + (res.data.message || '未知错误'));
    }
  } catch (e) {
    console.error('图片上传错误:', e);
    if (e.response?.status === 413) {
      alert('文件太大，请选择小于10MB的图片');
    } else {
      alert('图片上传失败: ' + (e.response?.data?.message || e.message || '请重试'));
    }
  } finally {
    editUploading.value = false;
  }
};

// 处理会议图片URL，支持私有bucket
const processMeetingImageUrl = async (meeting) => {
  if (meeting.imageUrl && !meeting.imageUrl.startsWith('http://') && !meeting.imageUrl.startsWith('https://')) {
    try {
      const presignedUrl = await fileService.getImageUrl(meeting.imageUrl);
      if (presignedUrl) {
        meeting.imageUrl = presignedUrl;
      }
    } catch (error) {
      console.error('获取会议图片URL失败:', error);
    }
  }
};

// 批量处理会议图片URL
const processMeetingsImageUrls = async (meetings) => {
  if (!meetings || meetings.length === 0) return;
  
  const promises = meetings.map(meeting => processMeetingImageUrl(meeting));
  await Promise.all(promises);
};

// 监听会议数据变化，自动处理图片URL
watch(() => props.meetings, async (newMeetings) => {
  if (newMeetings && newMeetings.length > 0) {
    await processMeetingsImageUrls(newMeetings);
  }
}, { immediate: true });

onMounted(async () => {
  console.log('会议列表数据:', props.meetings);
  if (props.meetings && props.meetings.length > 0) {
    // 处理图片URL
    await processMeetingsImageUrls(props.meetings);
    
    props.meetings.forEach((m, i) => {
      console.log(`会议${i + 1} imageUrl:`, m.imageUrl);
    });
  }
});
</script>

<template>
  <div class="meeting-list">
    <div v-if="meetings.length === 0" class="no-results">
      <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M9 11H15M9 15H15M9 7H15M5 3H19C20.1046 3 21 3.89543 21 5V19C21 20.1046 20.1046 21 19 21H5C3.89543 21 3 20.1046 3 19V5C3 3.89543 3.89543 3 5 3Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <p>没有找到匹配的会议</p>
      <span>尝试调整搜索条件或筛选器</span>
    </div>
    
    <div class="meetings-grid">
      <div 
        v-for="meeting in meetings" 
        :key="meeting.id"   
        class="meeting-card"
      >
        <div class="meeting-content" @click="viewMeeting(meeting.id)"> 
          <div class="meeting-header">
            <div class="meeting-title">
              <h3>{{ meeting.meetingName }}</h3>
              <span 
                class="status-badge"
                :style="{ backgroundColor: getStatusColor(meeting.status) }"
              >
                {{ getStatusText(meeting.status) }}
              </span>
            </div>
            <div class="meeting-time">
              <div class="time-item">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <polyline points="12,6 12,12 16,14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <span>{{ formatDateTime(meeting.startTime) }}</span>
              </div>
              <div class="time-item">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <polyline points="12,6 12,12 16,14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <span>{{ formatDateTime(meeting.endTime) }}</span>
              </div>
            </div>
          </div>
          <!-- 新增会议图片展示 -->
          <div v-if="meeting.imageUrl" class="meeting-image-preview">
            <img :src="meeting.imageUrl" alt="会议图片" style="max-width: 100%; max-height: 160px; border-radius: 8px; margin-bottom: 12px; object-fit: contain; background: #f8f9fa;" />
          </div>
          
          <div class="meeting-meta">
            <div class="meta-item">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span class="label">创建人:</span>
              <span class="value">{{ meeting.creator }}</span>
            </div>
            
            <div v-if="meeting.reviewer" class="meta-item">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M14 2H6C5.46957 2 4.96086 2.21071 4.58579 2.58579C4.21071 2.96086 4 3.46957 4 4V20C4 20.5304 4.21071 21.0391 4.58579 21.4142C4.96086 21.7893 5.46957 22 6 22H18C18.5304 22 19.0391 21.7893 19.4142 21.4142C19.7893 21.0391 20 20.5304 20 20V8L14 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <polyline points="14,2 14,8 20,8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <line x1="16" y1="13" x2="8" y2="13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <line x1="16" y1="17" x2="8" y2="17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <polyline points="10,9 9,9 8,9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span class="label">审核人:</span>
              <span class="value">{{ meeting.reviewer }}</span>
            </div>
            
            <div v-if="meeting.reviewTime" class="meta-item">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <polyline points="12,6 12,12 16,14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span class="label">审核时间:</span>
              <span class="value">{{ formatDateTime(meeting.reviewTime) }}</span>
            </div>
          </div>
          
          <div class="meeting-content-preview">
            <p>{{ meeting.meetingContent?.substring(0, 120) }}{{ meeting.meetingContent?.length > 120 ? '...' : '' }}</p>
          </div>
          
          <div v-if="meeting.reviewComment" class="review-comment">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M21 15C21 15.5304 20.7893 16.0391 20.4142 16.4142C20.0391 16.7893 19.5304 17 19 17H7L3 21V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H19C19.5304 3 20.0391 3.21071 20.4142 3.58579C20.7893 3.96086 21 4.46957 21 5V15Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <span class="comment-text">{{ meeting.reviewComment }}</span>
          </div>
        </div>
        
        <div class="meeting-actions">
          <button 
            v-if="canEdit(meeting)"
            class="action-btn edit-btn" 
            @click.stop="editMeeting(meeting)"
            title="编辑会议"
          > 
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M11 4H4C3.46957 4 2.96086 4.21071 2.58579 4.58579C2.21071 4.96086 2 5.46957 2 6V20C2 20.5304 2.21071 21.0391 2.58579 21.4142C2.96086 21.7893 3.46957 22 4 22H18C18.5304 22 18.9391 21.7893 19.3142 21.4142C19.6893 21.0391 19.9 20.5304 19.9 20V13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M18.5 2.50023C18.8978 2.10243 19.4374 1.87891 20 1.87891C20.5626 1.87891 21.1022 2.10243 21.5 2.50023C21.8978 2.89804 22.1213 3.43762 22.1213 4.00023C22.1213 4.56284 21.8978 5.10243 21.5 5.50023L12 15.0002L8 16.0002L9 12.0002L18.5 2.50023Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            编辑
          </button>
          
          <button 
            v-if="canReview(meeting)"
            class="action-btn review-btn" 
            @click.stop="onReview(meeting)"
            title="审核会议"
          > 
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M14 2H6C5.46957 2 4.96086 2.21071 4.58579 2.58579C4.21071 2.96086 4 3.46957 4 4V20C4 20.5304 4.21071 21.0391 4.58579 21.4142C4.96086 21.7893 5.46957 22 6 22H18C18.5304 22 19.0391 21.7893 19.4142 21.4142C19.7893 21.0391 20 20.5304 20 20V8L14 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="14,2 14,8 20,8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="16" y1="13" x2="8" y2="13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="16" y1="17" x2="8" y2="17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="10,9 9,9 8,9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            审核
          </button>
          
          <button 
            v-if="canDelete(meeting)"
            class="action-btn delete-btn" 
            @click.stop="onDelete(meeting.id)"
            title="删除会议"
          > 
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <polyline points="3,6 5,6 21,6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M19,6V20C19,20.5304 18.7893,21.0391 18.4142,21.4142C18.0391,21.7893 17.5304,22 17,22H7C6.46957,22 5.96086,21.7893 5.58579,21.4142C5.21071,21.0391 5,20.5304 5,20V6M8,6V4C8,3.46957 8.21071,2.96086 8.58579,2.58579C8.96086,2.21071 9.46957,2 10,2H14C14.5304,2 15.0391,2.21071 15.4142,2.58579C15.7893,2.96086 16,3.46957 16,4V6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            删除
          </button>
        </div>
      </div>
    </div>

    <!-- 编辑模态框 -->
    <div v-if="showEditModal" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>编辑会议信息</h3>
          <button @click="closeModal" class="close-btn">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <line x1="18" y1="6" x2="6" y2="18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="6" y1="6" x2="18" y2="18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>
        <form @submit.prevent="saveEdit" class="modal-form"> 
          <div class="form-group">
            <label>会议名称:</label>
            <input v-model="currentMeeting.meetingName" required placeholder="请输入会议名称">
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>开始时间:</label>
              <input v-model="currentMeeting.startTime" type="datetime-local" required>
            </div>
            <div class="form-group">
              <label>结束时间:</label>
              <input v-model="currentMeeting.endTime" type="datetime-local" required>
            </div>
          </div>
          <div class="form-group">
            <label>会议图片:</label>
            <input type="file" accept="image/*" @change="onEditImageChange" />
            <div v-if="editUploading">图片上传中...</div>
            <div v-if="currentMeeting.imageUrl">
              <img :src="currentMeeting.imageUrl" alt="会议图片" style="max-width:200px;max-height:120px;" />
            </div>
          </div>
          <div class="form-group">
            <label>会议内容:</label>
            <textarea v-model="currentMeeting.meetingContent" rows="4" required placeholder="请输入会议详细内容"></textarea>
          </div>
          <div class="modal-actions">
            <button type="button" @click="closeModal" class="cancel-btn">取消</button>
            <button type="submit" class="submit-btn">保存修改</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.meeting-list {
  width: 100%;
}

.no-results {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  color: #6c757d;
}

.no-results svg {
  color: #adb5bd;
  margin-bottom: 20px;
}

.no-results p {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #495057;
}

.no-results span {
  font-size: 14px;
  color: #6c757d;
}

.meetings-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 24px;
}

.meeting-card {
  background: white;
  border: 1px solid #e9ecef;
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.meeting-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
  border-color: #667eea;
}

.meeting-content {
  padding: 24px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.meeting-content:hover {
  background: #f8f9fa;
}

.meeting-header {
  margin-bottom: 20px;
}

.meeting-title {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.meeting-title h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1.4;
  flex: 1;
  margin-right: 12px;
}

.status-badge {
  padding: 6px 12px;
  border-radius: 20px;
  color: white;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  white-space: nowrap;
  flex-shrink: 0;
}

.meeting-time {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.time-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #6c757d;
}

.time-item svg {
  color: #667eea;
  flex-shrink: 0;
}

.meeting-meta {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.meta-item svg {
  color: #667eea;
  flex-shrink: 0;
}

.label {
  font-weight: 600;
  color: #495057;
  min-width: 60px;
}

.value {
  color: #2c3e50;
  font-weight: 500;
}

.meeting-content-preview {
  margin-bottom: 16px;
}

.meeting-content-preview p {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
  color: #495057;
  
  /* -webkit-line-clamp 完整实现 */
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  line-clamp: 3; /* 标准属性 */
  overflow: hidden;
  
  /* 跨浏览器兼容性 */
  max-height: calc(1.6em * 3);
  position: relative;
  
  /* 确保文本不会换行 */
  word-wrap: break-word;
  word-break: break-word;
}

/* 为不支持 -webkit-line-clamp 的浏览器提供备用方案 */
@supports not (-webkit-line-clamp: 3) {
  .meeting-content-preview p {
    /* 使用 JavaScript 检测文本是否被截断 */
    overflow: hidden;
    text-overflow: ellipsis;
  }
  
  .meeting-content-preview p::after {
    content: '...';
    position: absolute;
    bottom: 0;
    right: 0;
    background: white;
    padding-left: 4px;
    color: #495057;
  }
}

.review-comment {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 3px solid #667eea;
}

.review-comment svg {
  color: #667eea;
  flex-shrink: 0;
  margin-top: 2px;
}

.comment-text {
  font-size: 13px;
  color: #495057;
  line-height: 1.5;
  font-style: italic;
}

.meeting-actions {
  display: flex;
  border-top: 1px solid #e9ecef;
  background: #f8f9fa;
}

.action-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 12px 16px;
  border: none;
  background: transparent;
  color: #6c757d;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.action-btn:hover {
  background: white;
  color: #495057;
  transform: translateY(-1px);
}

.action-btn:not(:last-child) {
  border-right: 1px solid #e9ecef;
}

.action-btn svg {
  flex-shrink: 0;
}

.edit-btn:hover {
  color: #007bff;
}

.review-btn:hover {
  color: #ffc107;
}

.delete-btn:hover {
  color: #dc3545;
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
  .meetings-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .meeting-card {
    margin: 0 10px;
  }
  
  .meeting-title {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .meeting-title h3 {
    margin-right: 0;
  }
  
  .meeting-time {
    flex-direction: row;
    justify-content: space-between;
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
  
  .meeting-actions {
    flex-direction: column;
  }
  
  .action-btn:not(:last-child) {
    border-right: none;
    border-bottom: 1px solid #e9ecef;
  }
}

@media (max-width: 480px) {
  .meeting-content {
    padding: 16px;
  }
  
  .meeting-title h3 {
    font-size: 16px;
  }
  
  .status-badge {
    font-size: 10px;
    padding: 4px 8px;
  }
  
  .time-item {
    font-size: 12px;
  }
  
  .meta-item {
    font-size: 12px;
  }
  
  .action-btn {
    padding: 10px 12px;
    font-size: 12px;
  }
}
</style> 