<script setup>
import {ref, onMounted} from 'vue'
import {useRouter} from 'vue-router'
// 假设这些 API 方法已经正确实现，并且会返回完整的响应对象
import {getAllNewsList, adminDeleteNews, adminEditNews} from '@/api/news'
import {ElMessage, ElMessageBox} from 'element-plus'

const router = useRouter()

const newsList = ref([])
const total = ref(0)
const loading = ref(false)

const searchForm = ref({
  keyword: '',
  status: '',
  page: 1,
  size: 10
})

const editDialogVisible = ref(false)
const currentNewsId = ref(null) // 只保存 ID，避免直接绑定整个对象
const editForm = ref({
  title: '',
  content: '',
  summary: '',
  author: '',
  image: ''
})

const statusOptions = [
  {label: '全部', value: ''},
  {label: '待审核', value: 0},
  {label: '已发布', value: 1},
  {label: '审核拒绝', value: 2},
  {label: '已下线', value: 3}
]

const statusMap = {
  0: {text: '待审核', type: 'warning'},
  1: {text: '已发布', type: 'success'},
  2: {text: '审核拒绝', type: 'danger'},
  3: {text: '已下线', type: 'info'}
}

const loadAllNews = async () => {
  loading.value = true
  try {
    const response = await getAllNewsList(searchForm.value)

    console.log('Received all news response:', response)

    // 修复点 1：检查响应状态码并正确提取数据
    if (response && response.code === '0' && response.data) {
      newsList.value = response.data.list
      total.value = response.data.total
    } else {
      ElMessage.error(response.msg || '加载所有动态失败：未收到有效数据')
    }
  } catch (error) {
    console.error('Network or request exception for all news:', error)
    ElMessage.error('加载动态失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  searchForm.value.page = 1
  loadAllNews()
}

const handleReset = () => {
  searchForm.value = {
    keyword: '',
    status: '',
    page: 1,
    size: 10
  }
  loadAllNews()
}

const handlePageChange = (page) => {
  searchForm.value.page = page
  loadAllNews()
}

const showEditDialog = (news) => {
  // 修复点 3：在打开对话框时，使用 news 对象来填充表单
  currentNewsId.value = news.id // 记住 ID
  editForm.value = {
    title: news.title,
    content: news.content,
    summary: news.summary,
    author: news.author,
    image: news.image || ''
  }
  editDialogVisible.value = true
}

const handleEdit = async () => {
  try {
    // 修复点 3：使用保存的 ID 和表单数据
    await adminEditNews(currentNewsId.value, editForm.value)
    ElMessage.success('编辑成功')
    editDialogVisible.value = false
    loadAllNews()
  } catch (error) {
    ElMessage.error('编辑失败')
  }
}

const handleDelete = async (newsId, title) => {
  try {
    await ElMessageBox.confirm(
        `确定要删除动态 "${title}" 吗？删除后不可恢复！`,
        '确认删除',
        {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )

    await adminDeleteNews(newsId)
    ElMessage.success('删除成功')
    loadAllNews()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const viewDetail = (newsId) => {
  // 注意：这里需要根据你的路由配置来修改
  // 如果新闻详情页的路由是 /admin/news/:id，那么应该这样写
  router.push(`/admin/news/${newsId}`)
}

onMounted(() => {
  loadAllNews()
})
</script>

<template>
  <div class="news-manage">
    <el-card class="search-card">
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item class="search-item-keyword">
          <el-input
              v-model="searchForm.keyword"
              placeholder="请输入搜索内容"
              @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态" class="search-item-status">
          <el-select v-model="searchForm.status" placeholder="选择状态">
            <el-option
                v-for="option in statusOptions"
                :key="option.value"
                :label="option.label"
                :value="option.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item class="search-buttons">
          <el-button type="primary" @click="handleSearch" :loading="loading">
            搜索
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px;">
      <template #header>
        <span>动态管理</span>
      </template>

      <el-table
          :data="newsList"
          v-loading="loading"
          stripe
      >
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <el-link @click="viewDetail(row.id)" type="primary">
              {{ row.title }}
            </el-link>
          </template>
        </el-table-column>

        <el-table-column prop="author" label="作者" width="120"/>

        <el-table-column prop="statusDisplayName" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type">
              {{ row.statusDisplayName }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="viewCount" label="浏览量" width="100"/>

        <el-table-column prop="formattedCreateTime" label="发布时间" width="180"/>

        <el-table-column prop="auditTime" label="审核时间" width="180">
          <template #default="{ row }">
            <span v-if="row.auditTime">{{ row.auditTime }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewDetail(row.id)">
              查看
            </el-button>
            <el-button
                size="small"
                type="primary"
                @click="showEditDialog(row)"
                :disabled="!row.canEdit"
            >
              编辑
            </el-button>
            <el-button
                size="small"
                type="danger"
                @click="handleDelete(row.id, row.title)"
                :disabled="!row.canDelete"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
          v-if="total > 0"
          :current-page="searchForm.page"
          :page-size="searchForm.size"
          :total="total"
          layout="total, prev, pager, next, jumper"
          @current-change="handlePageChange"
          style="margin-top: 20px; text-align: center;"
      />
    </el-card>

    <el-dialog v-model="editDialogVisible" title="编辑动态" width="800px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="editForm.title" maxlength="100" show-word-limit/>
        </el-form-item>

        <el-form-item label="作者">
          <el-input v-model="editForm.author" maxlength="50"/>
        </el-form-item>

        <el-form-item label="图片链接">
          <el-input v-model="editForm.image" placeholder="请输入图片链接地址"/>
          <div v-if="editForm.image" style="margin-top: 10px;">
            <el-image
                :src="editForm.image"
                style="width: 200px; height: 150px;"
                fit="cover"
                :preview-src-list="[editForm.image]"
            />
          </div>
        </el-form-item>

        <el-form-item label="简介">
          <el-input
              v-model="editForm.summary"
              type="textarea"
              :rows="3"
              maxlength="200"
              show-word-limit
          />
        </el-form-item>

        <el-form-item label="内容">
          <el-input
              v-model="editForm.content"
              type="textarea"
              :rows="8"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
/* 样式保持不变 */
</style>