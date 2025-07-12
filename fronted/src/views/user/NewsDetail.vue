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
      ElMessage.error('新闻ID不存在，无法加载详情')
      router.back()
      return
    }

    const response = await getNewsDetail(newsId)

    // 检查响应是否成功
    if (response && response.code === '0') {
      // 核心修改：将 newsDetail.value 赋值为 response.data
      newsDetail.value = response.data
      console.log('新闻详情加载成功:', newsDetail.value)
    } else {
      ElMessage.error(response.msg || '加载新闻详情失败')
      router.back()
    }
  } catch (error) {
    console.error('加载新闻详情时出错:', error)
    ElMessage.error('网络或服务器错误，加载详情失败')
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

      <div v-if="newsDetail.id">
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

<style scoped>
.news-detail {
  padding: 20px;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.article-header {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.article-title {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 28px;
  font-weight: bold;
  line-height: 1.4;
}

.article-meta {
  color: #909399;
  font-size: 14px;
}

.article-meta span {
  margin-right: 20px;
}

.article-image {
  margin: 30px 0;
  text-align: center;
}

.article-summary {
  margin: 30px 0;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.article-summary h3 {
  margin: 0 0 10px 0;
  color: #303133;
}

.article-summary p {
  margin: 0;
  color: #606266;
  line-height: 1.6;
}

.article-content {
  margin: 30px 0;
}

.article-content h3 {
  margin: 0 0 20px 0;
  color: #303133;
}

.content-html {
  color: #303133;
  line-height: 1.8;
  font-size: 16px;
}

.content-html :deep(p) {
  margin-bottom: 15px;
}

.content-html :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 15px 0;
}
</style>