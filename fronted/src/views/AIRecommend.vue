<template>
  <div class="ai-recommend">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>AI智能推荐</h1>
      <p>基于您的学习行为和偏好，为您推荐最适合的课程</p>
    </div>

    <!-- 推荐类型选择 -->
    <el-card class="recommendation-types">
      <template #header>
        <span>推荐类型</span>
      </template>
      <el-radio-group v-model="activeType" @change="handleTypeChange">
        <el-radio-button label="personalized">个性化推荐</el-radio-button>
        <el-radio-button label="popular">热门推荐</el-radio-button>
        <el-radio-button label="trending">趋势推荐</el-radio-button>
        <el-radio-button label="similar">相似课程</el-radio-button>
      </el-radio-group>
    </el-card>

    <!-- 推荐结果 -->
    <el-row :gutter="20" class="recommendation-content">
      <el-col :span="18">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>{{ getTypeTitle(activeType) }}</span>
              <el-button type="primary" @click="refreshRecommendations" :loading="loading">
                <el-icon><Refresh /></el-icon>
                刷新推荐
              </el-button>
            </div>
          </template>

          <div v-loading="loading" class="recommendation-list">
            <div 
              v-for="course in recommendations" 
              :key="course.course_id"
              class="recommendation-item"
              @click="viewCourse(course)"
            >
              <div class="course-image">
                <el-image 
                  :src="course.coverImage || '/class.jpg'" 
                  fit="cover"
                  style="width: 120px; height: 80px; border-radius: 8px;"
                />
              </div>
              <div class="course-info">
                <h3 class="course-title">{{ course.title }}</h3>
                <p class="course-description">{{ course.description || '暂无描述' }}</p>
                <div class="course-meta">
                  <el-tag size="small" :type="getCategoryType(course.category)">
                    {{ course.category }}
                  </el-tag>
                  <el-tag size="small" type="warning">{{ course.level }}</el-tag>
                  <span class="instructor">讲师: {{ course.instructor }}</span>
                </div>
                <div class="course-stats">
                  <span class="score">推荐分数: {{ course.recommendation_score.toFixed(1) }}</span>
                  <span class="price" v-if="course.price > 0">¥{{ course.price }}</span>
                  <span class="price free" v-else>免费</span>
                </div>
                <p class="recommendation-reason">{{ course.recommendation_reason }}</p>
              </div>
              <div class="course-actions">
                <el-button type="primary" size="small" @click.stop="enrollCourse(course)">
                  立即学习
                </el-button>
                <el-button size="small" @click.stop="addToFavorites(course)">
                  <el-icon><Star /></el-icon>
                  收藏
                </el-button>
              </div>
            </div>

            <el-empty v-if="!loading && recommendations.length === 0" description="暂无推荐课程" />
          </div>
        </el-card>
      </el-col>

      <!-- 侧边栏 -->
      <el-col :span="6">
        <!-- 用户偏好设置 -->
        <el-card class="sidebar-card">
          <template #header>
            <span>偏好设置</span>
          </template>
          <div class="preference-form">
            <el-form :model="preferences" label-width="80px">
              <el-form-item label="偏好分类">
                <el-select v-model="preferences.categories" multiple placeholder="选择分类" style="width: 100%;">
                  <el-option label="编程开发" value="programming" />
                  <el-option label="设计创意" value="design" />
                  <el-option label="商业管理" value="business" />
                  <el-option label="语言学习" value="language" />
                  <el-option label="职业技能" value="skill" />
                </el-select>
              </el-form-item>
              <el-form-item label="难度等级">
                <el-select v-model="preferences.level" placeholder="选择难度" style="width: 100%;">
                  <el-option label="初级" value="beginner" />
                  <el-option label="中级" value="intermediate" />
                  <el-option label="高级" value="advanced" />
                </el-select>
              </el-form-item>
              <el-form-item label="最大价格">
                <el-input-number v-model="preferences.maxPrice" :min="0" :max="10000" style="width: 100%;" />
              </el-form-item>
              <el-button type="primary" @click="updatePreferences" style="width: 100%;">
                更新偏好
              </el-button>
            </el-form>
          </div>
        </el-card>

        <!-- 推荐统计 -->
        <el-card class="sidebar-card" style="margin-top: 20px;">
          <template #header>
            <span>推荐统计</span>
          </template>
          <div class="stats-content">
            <div class="stat-item">
              <div class="stat-number">{{ stats.totalRecommendations }}</div>
              <div class="stat-label">总推荐数</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ stats.accuracyRate }}%</div>
              <div class="stat-label">推荐准确率</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ stats.avgScore }}</div>
              <div class="stat-label">平均推荐分数</div>
            </div>
          </div>
        </el-card>

        <!-- 热门标签 -->
        <el-card class="sidebar-card" style="margin-top: 20px;">
          <template #header>
            <span>热门标签</span>
          </template>
          <div class="tags-content">
            <el-tag 
              v-for="tag in hotTags" 
              :key="tag.name"
              :type="tag.type"
              size="small"
              style="margin: 4px; cursor: pointer;"
              @click="selectTag(tag)"
            >
              {{ tag.name }} ({{ tag.count }})
            </el-tag>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, Refresh } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const activeType = ref('personalized')

// 推荐课程列表
const recommendations = ref([
  {
    course_id: 1,
    title: 'Vue.js 完全指南 2024',
    description: '从零开始学习Vue.js，掌握现代前端开发技术',
    category: 'programming',
    level: 'intermediate',
    instructor: '张老师',
    price: 299,
    recommendation_score: 9.2,
    recommendation_reason: '基于您的Vue学习历史推荐',
    coverImage: 'https://via.placeholder.com/120x80'
  },
  {
    course_id: 2,
    title: 'Spring Boot 实战开发',
    description: '企业级Java应用开发实战课程',
    category: 'programming',
    level: 'advanced',
    instructor: '李老师',
    price: 399,
    recommendation_score: 8.8,
    recommendation_reason: '与您已学课程高度相关',
    coverImage: 'https://via.placeholder.com/120x80'
  },
  {
    course_id: 3,
    title: 'UI/UX 设计基础',
    description: '学习现代UI/UX设计原则和实践',
    category: 'design',
    level: 'beginner',
    instructor: '王老师',
    price: 0,
    recommendation_score: 8.5,
    recommendation_reason: '热门免费课程，适合初学者',
    coverImage: 'https://via.placeholder.com/120x80'
  }
])

// 用户偏好
const preferences = ref({
  categories: ['programming'],
  level: 'intermediate',
  maxPrice: 500
})

// 推荐统计
const stats = ref({
  totalRecommendations: 156,
  accuracyRate: 87,
  avgScore: 8.6
})

// 热门标签
const hotTags = ref([
  { name: 'Vue.js', count: 1250, type: 'success' },
  { name: 'React', count: 980, type: 'primary' },
  { name: 'Spring Boot', count: 756, type: 'warning' },
  { name: 'Python', count: 632, type: 'info' },
  { name: 'Docker', count: 589, type: 'danger' },
  { name: '微服务', count: 456, type: '' }
])

// 获取类型标题
const getTypeTitle = (type) => {
  const titles = {
    personalized: '个性化推荐',
    popular: '热门推荐',
    trending: '趋势推荐',
    similar: '相似课程'
  }
  return titles[type] || '推荐课程'
}

// 获取分类类型
const getCategoryType = (category) => {
  const types = {
    programming: 'success',
    design: 'warning',
    business: 'primary',
    language: 'info',
    skill: 'danger'
  }
  return types[category] || ''
}

// 处理类型变化
const handleTypeChange = (type) => {
  loading.value = true
  // 模拟API调用
  setTimeout(() => {
    // 这里应该调用不同的推荐API
    loading.value = false
    ElMessage.success(`已切换到${getTypeTitle(type)}`)
  }, 1000)
}

// 刷新推荐
const refreshRecommendations = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 2000))
    ElMessage.success('推荐已刷新')
  } catch (error) {
    ElMessage.error('刷新失败')
  } finally {
    loading.value = false
  }
}

// 查看课程详情
const viewCourse = (course) => {
  router.push(`/course/${course.course_id}`)
}

// 报名课程
const enrollCourse = (course) => {
  ElMessage.success(`已报名课程：${course.title}`)
}

// 添加到收藏
const addToFavorites = (course) => {
  ElMessage.success(`已添加到收藏：${course.title}`)
}

// 更新偏好设置
const updatePreferences = () => {
  ElMessage.success('偏好设置已更新')
  refreshRecommendations()
}

// 选择标签
const selectTag = (tag) => {
  ElMessage.info(`已选择标签：${tag.name}`)
  // 这里可以根据标签筛选课程
}

// 组件挂载时获取推荐
onMounted(() => {
  refreshRecommendations()
})
</script>

<style scoped>
.ai-recommend {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  text-align: center;
}

.page-header h1 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 28px;
}

.page-header p {
  margin: 0;
  color: #666;
  font-size: 16px;
}

.recommendation-types {
  margin-bottom: 20px;
}

.recommendation-content {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.recommendation-list {
  min-height: 400px;
}

.recommendation-item {
  display: flex;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.3s;
}

.recommendation-item:hover {
  background-color: #f5f7fa;
}

.recommendation-item:last-child {
  border-bottom: none;
}

.course-image {
  margin-right: 16px;
}

.course-info {
  flex: 1;
}

.course-title {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #333;
}

.course-description {
  margin: 0 0 12px 0;
  color: #666;
  font-size: 14px;
  line-height: 1.4;
}

.course-meta {
  margin-bottom: 8px;
}

.instructor {
  margin-left: 12px;
  color: #666;
  font-size: 12px;
}

.course-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.score {
  color: #409eff;
  font-weight: 500;
}

.price {
  color: #f56c6c;
  font-weight: 500;
}

.price.free {
  color: #67c23a;
}

.recommendation-reason {
  margin: 0;
  color: #999;
  font-size: 12px;
  font-style: italic;
}

.course-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-left: 16px;
}

.sidebar-card {
  margin-bottom: 20px;
}

.preference-form {
  padding: 10px 0;
}

.stats-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stat-item {
  text-align: center;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}

.tags-content {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
</style> 