<template>
  <div class="test-connection">
    <el-card>
      <template #header>
        <span>前后端连接测试</span>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>API测试</span>
            </template>
            
            <div class="test-section">
              <el-button 
                type="primary" 
                @click="testCourseAPI"
                :loading="courseLoading"
              >
                测试课程API
              </el-button>
              <el-button 
                type="success" 
                @click="testStatsAPI"
                :loading="statsLoading"
              >
                测试统计API
              </el-button>
              <el-button 
                type="warning" 
                @click="testUserAPI"
                :loading="userLoading"
              >
                测试用户API
              </el-button>
            </div>
            
            <div class="test-results">
              <h4>测试结果：</h4>
              <el-alert
                v-if="testResult"
                :title="testResult.title"
                :type="testResult.type"
                :description="testResult.message"
                show-icon
                :closable="false"
              />
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>连接状态</span>
            </template>
            
            <div class="status-section">
              <div class="status-item">
                <span class="status-label">后端服务：</span>
                <el-tag :type="backendStatus ? 'success' : 'danger'">
                  {{ backendStatus ? '在线' : '离线' }}
                </el-tag>
              </div>
              
              <div class="status-item">
                <span class="status-label">数据库：</span>
                <el-tag :type="dbStatus ? 'success' : 'danger'">
                  {{ dbStatus ? '连接正常' : '连接失败' }}
                </el-tag>
              </div>
              
              <div class="status-item">
                <span class="status-label">代理配置：</span>
                <el-tag type="info">已配置</el-tag>
              </div>
              
              <div class="status-item">
                <span class="status-label">CORS配置：</span>
                <el-tag type="info">已配置</el-tag>
              </div>
            </div>
            
            <div class="connection-info">
              <h4>连接信息：</h4>
              <p><strong>前端地址：</strong> http://localhost:3000</p>
              <p><strong>后端地址：</strong> http://localhost:8080</p>
              <p><strong>API前缀：</strong> /api</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row style="margin-top: 20px;">
        <el-col :span="24">
          <el-card>
            <template #header>
              <span>API响应示例</span>
            </template>
            
            <el-tabs v-model="activeTab">
              <el-tab-pane label="课程列表" name="courses">
                <pre v-if="apiResponse.courses">{{ JSON.stringify(apiResponse.courses, null, 2) }}</pre>
                <el-empty v-else description="暂无数据" />
              </el-tab-pane>
              
              <el-tab-pane label="统计数据" name="stats">
                <pre v-if="apiResponse.stats">{{ JSON.stringify(apiResponse.stats, null, 2) }}</pre>
                <el-empty v-else description="暂无数据" />
              </el-tab-pane>
              
              <el-tab-pane label="用户信息" name="user">
                <pre v-if="apiResponse.user">{{ JSON.stringify(apiResponse.user, null, 2) }}</pre>
                <el-empty v-else description="暂无数据" />
              </el-tab-pane>
            </el-tabs>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { courseAPI, statsAPI, userAPI } from '../api'

const courseLoading = ref(false)
const statsLoading = ref(false)
const userLoading = ref(false)
const backendStatus = ref(false)
const dbStatus = ref(false)
const testResult = ref(null)
const activeTab = ref('courses')
const apiResponse = ref({
  courses: null,
  stats: null,
  user: null
})

// 测试课程API
const testCourseAPI = async () => {
  try {
    courseLoading.value = true
    const response = await courseAPI.getCourseList()
    apiResponse.value.courses = response
    testResult.value = {
      title: '课程API测试成功',
      type: 'success',
      message: `成功获取 ${response.data?.length || 0} 条课程数据`
    }
    backendStatus.value = true
    ElMessage.success('课程API测试成功')
  } catch (error) {
    testResult.value = {
      title: '课程API测试失败',
      type: 'error',
      message: error.message || '请求失败'
    }
    backendStatus.value = false
    ElMessage.error('课程API测试失败')
  } finally {
    courseLoading.value = false
  }
}

// 测试统计API
const testStatsAPI = async () => {
  try {
    statsLoading.value = true
    const response = await statsAPI.getStats()
    apiResponse.value.stats = response
    testResult.value = {
      title: '统计API测试成功',
      type: 'success',
      message: '成功获取统计数据'
    }
    backendStatus.value = true
    ElMessage.success('统计API测试成功')
  } catch (error) {
    testResult.value = {
      title: '统计API测试失败',
      type: 'error',
      message: error.message || '请求失败'
    }
    backendStatus.value = false
    ElMessage.error('统计API测试失败')
  } finally {
    statsLoading.value = false
  }
}

// 测试用户API
const testUserAPI = async () => {
  try {
    userLoading.value = true
    const response = await userAPI.getUserInfo()
    apiResponse.value.user = response
    testResult.value = {
      title: '用户API测试成功',
      type: 'success',
      message: '成功获取用户信息'
    }
    backendStatus.value = true
    ElMessage.success('用户API测试成功')
  } catch (error) {
    testResult.value = {
      title: '用户API测试失败',
      type: 'error',
      message: error.message || '请求失败'
    }
    backendStatus.value = false
    ElMessage.error('用户API测试失败')
  } finally {
    userLoading.value = false
  }
}

// 组件挂载时自动测试连接
onMounted(() => {
  testCourseAPI()
})
</script>

<style scoped>
.test-connection {
  padding: 20px;
}

.test-section {
  margin-bottom: 20px;
}

.test-section .el-button {
  margin-right: 10px;
  margin-bottom: 10px;
}

.test-results {
  margin-top: 20px;
}

.status-section {
  margin-bottom: 20px;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.status-item:last-child {
  border-bottom: none;
}

.status-label {
  font-weight: 500;
  color: #333;
}

.connection-info {
  margin-top: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
}

.connection-info h4 {
  margin: 0 0 10px 0;
  color: #333;
}

.connection-info p {
  margin: 5px 0;
  color: #666;
}

pre {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  overflow-x: auto;
  font-size: 12px;
  line-height: 1.4;
}
</style> 