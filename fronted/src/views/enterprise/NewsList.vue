<script setup>
import {ref, onMounted} from 'vue'
import {useRouter} from 'vue-router'
import {searchNews, getPopularNews} from '@/api/news'
import {ElMessage} from 'element-plus'

const router = useRouter()

const searchForm = ref({
  keyword: '',
  page: 1,
  size: 10
})

const newsList = ref([])
const popularNews = ref([])
const total = ref(0)
const loading = ref(false)

const handleSearch = async () => {
  loading.value = true
  try {
    // searchNews 返回的是整个 res 对象，即 { code, msg, data }
    const responseData = await searchNews(searchForm.value) // 明确变量名，表示是整个响应数据

    console.log('API完整响应数据:', responseData) // 打印出来确认结构

    // 访问实际的业务数据部分，即 responseData.data
    newsList.value = responseData.data.list || []
    total.value = responseData.data.total || 0

    if (newsList.value.length === 0) {
      console.log('没有搜索到数据')
    }
  } catch (error) {
    console.error('搜索错误:', error)
    ElMessage.error('搜索失败，请检查网络或联系管理员')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page) => {
  searchForm.value.page = page
  handleSearch()
}

const handleReset = () => {
  searchForm.value = {keyword: '', page: 1, size: 10}
  handleSearch()
}

const viewDetail = (newsId) => {
  router.push(`/enterprise/news/${newsId}`)
}

const loadPopularNews = async () => {
  try {
    const response = await getPopularNews(5) // <-- 调用 API

    // 检查响应是否成功，并提取真正的数组数据
    if (response && response.code === '0') {
      popularNews.value = response.data || [] // <-- 核心修改：访问 response.data
    } else {
      // 如果 code 不为 0，打印错误信息
      console.error('加载热门动态失败:', response.msg)
    }

  } catch (error) {
    console.error('加载热门动态失败:', error)
  }
}

onMounted(() => {
  handleSearch()
  loadPopularNews()
})
</script>

<template>
  <div class="news-list">
    <!-- 搜索区域 -->
    <div class="search-section">
      <el-card class="search-card" shadow="hover">
        <div class="search-content">
          <div class="search-input-wrapper">
            <el-input
                v-model="searchForm.keyword"
                placeholder="请输入搜索内容"
                size="large"
                clearable
                @keyup.enter="handleSearch"
                class="search-input"
            >
              <template #prefix>
                <el-icon>
                  <Search/>
                </el-icon>
              </template>
            </el-input>
          </div>
          <div class="search-buttons">
            <el-button
                type="primary"
                size="large"
                @click="handleSearch"
                :loading="loading"
                class="search-btn"
            >
              <el-icon>
                <Search/>
              </el-icon>
              搜索
            </el-button>
            <el-button
                size="large"
                @click="handleReset"
                class="reset-btn"
            >
              <el-icon>
                <Refresh/>
              </el-icon>
              重置
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 内容区域 -->
    <div class="content-section">
      <el-row :gutter="24">
        <el-col :span="16">
          <el-card class="news-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="header-title">
                  <el-icon><Document/></el-icon>
                  动态列表
                </span>
                <span class="result-count" v-if="total > 0">
                  共 {{ total }} 条结果
                </span>
              </div>
            </template>

            <div v-loading="loading" class="news-content">
              <div v-if="newsList.length === 0" class="empty-state">
                <el-empty description="暂无动态数据">
                  <el-button type="primary" @click="handleReset">
                    查看全部动态
                  </el-button>
                </el-empty>
              </div>

              <div v-else class="news-grid">
                <el-card
                    v-for="news in newsList"
                    :key="news.id"
                    class="news-item"
                    shadow="hover"
                    @click="viewDetail(news.id)"
                >
                  <el-row :gutter="16">
                    <el-col :span="6" v-if="news.image">
                      <div class="image-wrapper">
                        <el-image
                            :src="news.image"
                            fit="cover"
                            class="news-image"
                        />
                      </div>
                    </el-col>
                    <el-col :span="news.image ? 18 : 24">
                      <div class="news-content-wrapper">
                        <h3 class="news-title">{{ news.title }}</h3>
                        <p class="news-summary">{{ news.summary }}</p>
                        <div class="news-meta">
                          <el-tag size="small" type="info">
                            <el-icon>
                              <User/>
                            </el-icon>
                            {{ news.author }}
                          </el-tag>
                          <el-tag size="small" type="success">
                            <el-icon>
                              <Clock/>
                            </el-icon>
                            {{ news.createTime }}
                          </el-tag>
                          <el-tag size="small" type="warning">
                            <el-icon>
                              <View/>
                            </el-icon>
                            {{ news.viewCount }}
                          </el-tag>
                        </div>
                      </div>
                    </el-col>
                  </el-row>
                </el-card>
              </div>
            </div>

            <div class="pagination-wrapper" v-if="total > 0">
              <el-pagination
                  :current-page="searchForm.page"
                  :page-size="searchForm.size"
                  :total="total"
                  layout="total, prev, pager, next, jumper"
                  @current-change="handlePageChange"
                  background
              />
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card class="popular-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="header-title">
                  <el-icon><TrendCharts/></el-icon>
                  热门动态
                </span>
              </div>
            </template>

            <div class="popular-content">
              <div
                  v-for="(news, index) in popularNews"
                  :key="news.id"
                  class="popular-item"
                  @click="viewDetail(news.id)"
              >
                <div class="popular-rank" :class="getRankClass(index)">
                  {{ index + 1 }}
                </div>
                <div class="popular-info">
                  <div class="popular-title">{{ news.title }}</div>
                  <div class="popular-meta">
                    <el-icon>
                      <View/>
                    </el-icon>
                    {{ news.viewCount || 0 }}
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<style scoped>
.news-list {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

/* 搜索区域样式 */
.search-section {
  margin-bottom: 24px;
}

.search-card {
  border: none;
  border-radius: 12px;
  background: white;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.search-content {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.search-input-wrapper {
  flex: 1;
  min-width: 300px;
}

.search-input {
  --el-input-height: 48px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: none;
}

.search-buttons {
  display: flex;
  gap: 12px;
}

.search-btn {
  height: 48px;
  padding: 0 24px;
  border-radius: 24px;
  background: #409eff;
  border: none;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.reset-btn {
  height: 48px;
  padding: 0 20px;
  border-radius: 24px;
  background: #f5f7fa;
  border: 1px solid #dcdfe6;
  color: #606266;
}

.reset-btn:hover {
  background: #ecf5ff;
  border-color: #b3d8ff;
  color: #409eff;
}

/* 内容区域样式 */
.content-section {
  margin-top: 24px;
}

.news-card, .popular-card {
  border: none;
  border-radius: 12px;
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #303133;
}

.result-count {
  color: #909399;
  font-size: 14px;
}

/* 新闻列表样式 */
.news-content {
  min-height: 400px;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}

.news-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.news-item {
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
}

.news-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  border-color: #409eff;
}

.image-wrapper {
  height: 120px;
  overflow: hidden;
  border-radius: 8px;
}

.news-image {
  width: 100%;
  height: 100%;
  transition: transform 0.3s ease;
}

.news-item:hover .news-image {
  transform: scale(1.05);
}

.news-content-wrapper {
  padding: 8px 0;
}

.news-title {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 18px;
  font-weight: 600;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-summary {
  margin: 0 0 16px 0;
  color: #606266;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-meta {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.news-meta .el-tag {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 分页样式 */
.pagination-wrapper {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}

/* 热门动态样式 */
.popular-content {
  max-height: 500px;
  overflow-y: auto;
}

.popular-item {
  display: flex;
  align-items: center;
  padding: 16px;
  margin-bottom: 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fafafa;
}

.popular-item:hover {
  background: #f0f9ff;
  transform: translateX(4px);
}

.popular-rank {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
  margin-right: 12px;
  flex-shrink: 0;
}

.popular-rank.top-1 {
  background: linear-gradient(135deg, #ffd700, #ffb700);
  color: white;
}

.popular-rank.top-2 {
  background: linear-gradient(135deg, #c0c0c0, #a0a0a0);
  color: white;
}

.popular-rank.top-3 {
  background: linear-gradient(135deg, #cd7f32, #b8860b);
  color: white;
}

.popular-rank.normal {
  background: #e1f5fe;
  color: #0277bd;
}

.popular-info {
  flex: 1;
  overflow: hidden;
}

.popular-title {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.popular-meta {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #909399;
  font-size: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .news-list {
    padding: 16px;
  }

  .search-content {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input-wrapper {
    min-width: auto;
  }

  .search-buttons {
    justify-content: center;
  }

  .content-section .el-col {
    margin-bottom: 16px;
  }
}
</style>

<script>
export default {
  methods: {
    getRankClass(index) {
      if (index === 0) return 'top-1'
      if (index === 1) return 'top-2'
      if (index === 2) return 'top-3'
      return 'normal'
    }
  }
}
</script>