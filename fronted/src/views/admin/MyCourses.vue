<template>
  <div class="admin-courses-page">
    <el-card>
      <template #header>
        <div class="page-header">
          <h2>我的课程管理</h2>
          <p>管理员可以管理所有课程</p>
        </div>
      </template>
      
      <!-- 统计卡片 -->
      <div class="stats-section">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-content">
                <div class="stat-number">{{ totalCourses }}</div>
                <div class="stat-label">总课程数</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-content">
                <div class="stat-number">{{ approvedCourses }}</div>
                <div class="stat-label">已审核</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-content">
                <div class="stat-number">{{ pendingCourses }}</div>
                <div class="stat-label">待审核</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-content">
                <div class="stat-number">{{ totalViews }}</div>
                <div class="stat-label">总播放量</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      
      <!-- 操作栏 -->
      <div class="actions-section">
        <el-button type="primary" @click="createDialogVisible = true">
          <el-icon><Plus /></el-icon> 创建课程
        </el-button>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索课程名称..."
          style="width: 300px; margin-left: 16px;"
          clearable
          @input="filterCourses"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      
      <!-- 课程列表 -->
      <div class="course-list">
        <el-table
          v-loading="loading"
          :data="displayCourses"
          style="width: 100%"
          border
          stripe
        >
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="课程名称" min-width="200" />
          <el-table-column prop="instructorName" label="讲师" width="120" />
          <el-table-column prop="category" label="分类" width="100" />
          <el-table-column prop="level" label="难度" width="80" />
          <el-table-column prop="price" label="价格" width="100">
            <template #default="scope">
              <span v-if="scope.row.price === 0" class="free-tag">免费</span>
              <span v-else class="price-tag">¥{{ scope.row.price }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="viewCount" label="播放量" width="100" />
          <el-table-column prop="rating" label="评分" width="120">
            <template #default="scope">
              <el-rate v-model="scope.row.rating" disabled show-score text-color="#409eff" score-template="{value}" />
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button @click="viewCourse(scope.row.id)" type="primary" size="small">查看</el-button>
              <el-button @click="editCourse(scope.row.id)" type="warning" size="small">编辑</el-button>
              <el-button @click="deleteCourse(scope.row.id)" type="danger" size="small">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="totalCourses"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
      
      <!-- 创建课程弹窗 -->
      <el-dialog v-model="createDialogVisible" title="创建课程" width="500px" @close="resetCreateForm">
        <el-form :model="createForm" label-width="80px">
          <el-form-item label="课程名称" required>
            <el-input v-model="createForm.title" autocomplete="off" />
          </el-form-item>
          <el-form-item label="讲师" required>
            <el-input v-model="createForm.instructorName" autocomplete="off" />
          </el-form-item>
          <el-form-item label="分类">
            <el-input v-model="createForm.category" autocomplete="off" />
          </el-form-item>
          <el-form-item label="难度">
            <el-select v-model="createForm.level" placeholder="请选择">
              <el-option label="初级" value="初级" />
              <el-option label="中级" value="中级" />
              <el-option label="高级" value="高级" />
            </el-select>
          </el-form-item>
          <el-form-item label="价格">
            <el-input-number v-model="createForm.price" :min="0" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleCreateCourse">创建</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { courseApi } from '@/api/course'
import { adminApi } from '@/api/course'

const router = useRouter()
const loading = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const totalCourses = ref(0)
const courses = ref([])

const fetchCourses = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value
    }
    // 优先用adminApi.getCourses
    const res = await adminApi.getCourses(params)
    let data = res.data
    if (res.code === 200) {
      if (Array.isArray(data)) {
        courses.value = data
        totalCourses.value = data.length
      } else if (data && Array.isArray(data.records)) {
        courses.value = data.records
        totalCourses.value = data.total || data.records.length
      } else if (data && Array.isArray(data.content)) {
        courses.value = data.content
        totalCourses.value = data.totalElements || data.content.length
      } else {
        courses.value = []
        totalCourses.value = 0
      }
    } else {
      courses.value = []
      totalCourses.value = 0
      ElMessage.error(res.message || '获取课程列表失败')
    }
  } catch (e) {
    courses.value = []
    totalCourses.value = 0
    ElMessage.error('网络错误，获取课程失败')
  } finally {
    loading.value = false
  }
}

onMounted(fetchCourses)

const filterCourses = () => {
  currentPage.value = 1
  fetchCourses()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  fetchCourses()
}
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchCourses()
}

const displayCourses = computed(() => {
  let filtered = Array.isArray(courses.value) ? courses.value.slice() : []
  // 可扩展筛选/排序逻辑，如：
  // if (searchKeyword.value) { ... }
  // if (sortBy.value) { ... }
  return filtered
})

const totalViews = computed(() => {
  return Array.isArray(courses.value) ? courses.value.reduce((sum, course) => sum + (course.viewCount || 0), 0) : 0
})
const approvedCourses = computed(() => {
  return Array.isArray(courses.value) ? courses.value.filter(course => course.status === 'approved').length : 0
})
const pendingCourses = computed(() => {
  return Array.isArray(courses.value) ? courses.value.filter(course => course.status === 'pending').length : 0
})

const viewCourse = (id) => {
  router.push(`/admin/course/${id}`)
}
const editCourse = (id) => {
  router.push(`/admin/course/${id}/edit`)
}
const deleteCourse = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个课程吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await courseApi.deleteCourse(id)
    ElMessage.success('删除成功')
    fetchCourses()
  } catch {}
}

// 创建课程弹窗相关
const createDialogVisible = ref(false)
const createForm = ref({
  title: '',
  instructorName: '',
  category: '',
  level: '',
  price: 0
})
const resetCreateForm = () => {
  createForm.value = {
    title: '',
    instructorName: '',
    category: '',
    level: '',
    price: 0
  }
}
const handleCreateCourse = async () => {
  try {
    await courseApi.createCourse(createForm.value)
    ElMessage.success('创建成功')
    createDialogVisible.value = false
    resetCreateForm()
    fetchCourses()
  } catch {
    ElMessage.error('创建失败')
  }
}

const getStatusType = (status) => {
  switch (status) {
    case 'approved': return 'success'
    case 'pending': return 'warning'
    case 'rejected': return 'danger'
    default: return 'info'
  }
}
const getStatusText = (status) => {
  switch (status) {
    case 'approved': return '已审核'
    case 'pending': return '待审核'
    case 'rejected': return '已驳回'
    default: return '未知'
  }
}

// 组件挂载
onMounted(() => {
  console.log('=== Admin MyCourses 组件挂载 ===')
})
</script>

<style scoped>
.admin-courses-page {
  padding: 20px;
}

.page-header {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.page-header h2 {
  margin: 0;
  color: #2D3A4B;
  font-size: 24px;
}

.page-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.stats-section {
  margin-bottom: 24px;
}

.stat-card {
  text-align: center;
  padding: 20px;
}

.stat-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.actions-section {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}

.course-list {
  margin-bottom: 24px;
}

.free-tag {
  color: #67c23a;
  font-weight: bold;
}

.price-tag {
  color: #409eff;
  font-weight: bold;
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style> 