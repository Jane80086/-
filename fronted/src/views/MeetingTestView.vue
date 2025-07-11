<script setup>
import { ref, onMounted } from 'vue';
import MeetingList from '../components/MeetingList.vue';
import meetingService from '../api/meeting.js';
import fileService from '../api/file.js';

const meetings = ref([]);
const currentUser = ref({ userType: 'ADMIN', username: 'testadmin' }); // 可根据实际登录用户调整
const isLoading = ref(false);
const error = ref('');

// 新建会议表单
const newMeeting = ref({
  meetingName: '',
  startTime: '',
  endTime: '',
  meetingContent: '',
  imageUrl: ''
});
const addUploading = ref(false);

// 获取会议列表
const fetchMeetings = async () => {
  isLoading.value = true;
  error.value = '';
  try {
    const res = await meetingService.getMeetings({ page: 1, size: 20 });
    if (res.data.code === 200) {
      meetings.value = res.data.data.meetings || [];
    } else {
      error.value = res.data.message || '获取会议失败';
    }
  } catch (e) {
    error.value = e.message || '获取会议异常';
  } finally {
    isLoading.value = false;
  }
};

// 新建会议
const handleAdd = async () => {
  try {
    await meetingService.createMeeting(newMeeting.value);
    newMeeting.value = { meetingName: '', startTime: '', endTime: '', meetingContent: '', imageUrl: '' };
    await fetchMeetings();
    alert('创建成功');
  } catch (e) {
    alert(e.message || '创建失败');
  }
};

// 删除会议
const handleDelete = async (id) => {
  if (!confirm('确定删除该会议？')) return;
  try {
    await meetingService.deleteMeeting(id, true);
    await fetchMeetings();
    alert('删除成功');
  } catch (e) {
    alert(e.message || '删除失败');
  }
};

// 审核会议（通过）
const handleReview = async (meeting) => {
  try {
    await meetingService.reviewMeeting({ meetingId: meeting.id, status: 1, reviewComment: '测试通过' });
    await fetchMeetings();
    alert('审核通过');
  } catch (e) {
    alert(e.message || '审核失败');
  }
};

// 会议图片上传
const onAddImageChange = async (event) => {
  const file = event.target.files[0];
  if (!file) return;
  addUploading.value = true;
  try {
    const res = await meetingService.uploadMeetingImage(file);
    if (res.data && res.data.code === 200 && res.data.data) {
      // 获取预签名URL
      const url = await fileService.getImageUrl(res.data.data);
      newMeeting.value.imageUrl = url || res.data.data;
    } else {
      alert('图片上传失败');
    }
  } catch (e) {
    alert('图片上传异常: ' + (e.message || '未知错误'));
  } finally {
    addUploading.value = false;
  }
};

onMounted(() => {
  fetchMeetings();
});
</script>

<template>
  <div style="max-width:900px;margin:0 auto;padding:32px;">
    <h2>会议功能一站式测试页</h2>
    <div v-if="error" style="color:red;margin-bottom:12px;">{{ error }}</div>
    <div v-if="isLoading">加载中...</div>
    <form @submit.prevent="handleAdd" style="margin-bottom:24px;">
      <h3>新建会议</h3>
      <input v-model="newMeeting.meetingName" placeholder="会议名称" required style="margin-right:8px;" />
      <input v-model="newMeeting.startTime" type="datetime-local" required style="margin-right:8px;" />
      <input v-model="newMeeting.endTime" type="datetime-local" required style="margin-right:8px;" />
      <input v-model="newMeeting.meetingContent" placeholder="会议内容" required style="margin-right:8px;width:200px;" />
      <input type="file" accept="image/*" @change="onAddImageChange" :disabled="addUploading" style="margin-right:8px;" />
      <button type="submit" :disabled="addUploading">新建</button>
      <span v-if="addUploading" style="margin-left:8px;">图片上传中...</span>
      <div v-if="newMeeting.imageUrl" style="margin-top:8px;">
        <img :src="newMeeting.imageUrl" alt="会议图片" style="max-width:120px;max-height:80px;" />
      </div>
    </form>
    <MeetingList :meetings="meetings" :currentUser="currentUser" :onDelete="handleDelete" :onReview="handleReview" @update-meeting="fetchMeetings" />
  </div>
</template> 