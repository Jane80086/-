<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getNewsDetail } from '@/api/news'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const newsDetail = ref({})
const loading = ref(false)

const loadNewsDetail = async () => {
  loading.value = true
  try {
    const newsId = route.params.id
    if (!newsId) {
      ElMessage.error('无法获取新闻ID')
      router.back()
      return
    }

    // 正確地處理 API 回傳值
    const response = await getNewsDetail(newsId)
    if (response && response.code === '0' && response.data) {
      newsDetail.value = response.data
    } else {
      ElMessage.error(response.msg || '加载详情失败')
      router.back()
    }
  } catch (error) {
    ElMessage.error('加载详情失败，请检查网络或服务器')
    router.back()
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  loadNewsDetail()
})
</script>

<template>
  <div class="news-detail" v-loading="loading">
    <el-card>
      <template #header>
        <div class="detail-header">
          <el-button @click="goBack" :icon="'ArrowLeft'">返回</el-button>
          <span>动态详情</span>
        </div>
      </template>

      <div v-if="newsDetail && newsDetail.id">
        <div class="article-header">
          <h1 class="article-title">{{ newsDetail.title }}</h1>
          <div class="article-meta">
            <span>作者: {{ newsDetail.author }}</span>
            <span>发布时间: {{ newsDetail.createTime }}</span>
            <span>浏览量: {{ newsDetail.viewCount }}</span>
          </div>
        </div>
        <div class="article-image" v-if="newsDetail.image">
          <el-image
              :src="newsDetail.image"
              fit="contain"
              style="width: 100%; max-height: 400px;"
          />
        </div>

        <div class="article-summary" v-if="newsDetail.summary">
          <h3>摘要</h3>
          <p>{{ newsDetail.summary }}</p>
        </div>

        <div class="article-content">
          <h3>正文</h3>
          <div v-html="newsDetail.content" class="content-html"></div>
        </div>
      </div>
    </el-card>
  </div>
</template>