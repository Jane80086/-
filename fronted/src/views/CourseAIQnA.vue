<template>
  <div class="ai-qna-panel">
    <el-card>
      <div style="margin-bottom: 16px;">
        <el-input v-model="inputQuestion" placeholder="请输入你的问题..." @keyup.enter="submitAIQuestion" />
        <el-button type="primary" :loading="loading" @click="submitAIQuestion" style="margin-left: 8px;">AI问答</el-button>
      </div>
      <div v-if="aiAnswer" class="ai-answer">
        <el-alert :title="'AI回复：' + aiAnswer" type="success" show-icon />
      </div>
      <el-divider>历史问答</el-divider>
      <el-skeleton v-if="historyLoading" :rows="4" animated />
      <div v-else>
        <div v-for="item in aiQnaList" :key="item.id" class="qna-item">
          <div><b>Q:</b> {{ item.question }}</div>
          <div><b>A:</b> {{ item.aiAnswer }}</div>
          <el-divider />
        </div>
        <el-empty v-if="aiQnaList.length === 0" description="暂无AI问答历史" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { askCourseAIQna, getCourseAIQnaHistory } from '@/api/course'
import { ElMessage } from 'element-plus'

const props = defineProps({
  courseId: {
    type: [String, Number],
    required: true
  },
  userId: {
    type: [String, Number],
    default: 1
  }
})

const inputQuestion = ref('')
const aiAnswer = ref('')
const aiQnaList = ref([])
const loading = ref(false)
const historyLoading = ref(false)

async function submitAIQuestion() {
  if (!inputQuestion.value.trim()) {
    ElMessage.warning('请输入问题')
    return
  }
  loading.value = true
  try {
    const res = await askCourseAIQna(props.courseId, inputQuestion.value, props.userId)
    if (res.code === 200) {
      aiAnswer.value = res.data.aiReply
      inputQuestion.value = ''
      await loadAIQnaHistory()
    } else {
      ElMessage.error(res.message || 'AI问答失败')
    }
  } finally {
    loading.value = false
  }
}

async function loadAIQnaHistory() {
  historyLoading.value = true
  try {
    const res = await getCourseAIQnaHistory(props.courseId)
    if (res.code === 200) {
      aiQnaList.value = res.data.records || res.data.content || []
    }
  } finally {
    historyLoading.value = false
  }
}

onMounted(() => {
  loadAIQnaHistory()
})
</script>

<style scoped>
.ai-qna-panel {
  max-width: 600px;
  margin: 0 auto;
}
.ai-answer {
  margin-bottom: 16px;
}
.qna-item {
  margin-bottom: 8px;
}
</style> 