<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAllNewsList, adminDeleteNews, adminEditNews } from '@/api/news'
import { ElMessage, ElMessageBox } from 'element-plus'

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
const currentNews = ref({})
const editForm = ref({
  title: '',
  content: '',
  summary: '',
  author: '',
  image: ''
})

const statusOptions = [
  { label: '全部', value: '' },
  { label: '待审核', value: 0 },
  { label: '已发布', value: 1 },
  { label: '审核拒绝', value: 2 },
]

const statusMap = {
  0: { text: '待审核', type: 'warning' },
  1: { text: '已发布', type: 'success' },
  2: { text: '审核拒绝', type: 'danger' },
  3: { text: '已下线', type: 'info' } // Keep this for display logic even if not selectable
}

const loadAllNews = async () => {
  loading.value = true
  try {
    const pageResult = await getAllNewsList(searchForm.value)

    console.log('Received all news pageResult:', pageResult)

    if (pageResult) {
      newsList.value = pageResult.list
      total.value = pageResult.total
    } else {
      ElMessage.error('加载所有动态失败：未收到有效数据')
    }
  } catch (error) {
    console.error('Network or request exception for all news:', error)
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
  currentNews.value = news
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
    await adminEditNews(currentNews.value.id, editForm.value)
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
  router.push(`/normal/news-detail/${newsId}`)
}

onMounted(() => {
  loadAllNews()
})
</script>

<template>
  <div class="news-manage">
    <el-card class="search-card">
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item class="search-item-keyword"> <el-input
            v-model="searchForm.keyword"
            placeholder="请输入搜索内容"
            @keyup.enter="handleSearch"
        />
        </el-form-item>
        <el-form-item label="状态" class="search-item-status"> <el-select v-model="searchForm.status" placeholder="选择状态">
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

        <el-table-column prop="author" label="作者" width="120" />

        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type">
              {{ statusMap[row.status]?.text }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="viewCount" label="浏览量" width="100" />

        <el-table-column prop="createTime" label="发布时间" width="180" />

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
            <el-button size="small" type="primary" @click="showEditDialog(row)">
              编辑
            </el-button>
            <el-button
                size="small"
                type="danger"
                @click="handleDelete(row.id, row.title)"
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
          <el-input v-model="editForm.title" maxlength="100" show-word-limit />
        </el-form-item>

        <el-form-item label="作者">
          <el-input v-model="editForm.author" maxlength="50" />
        </el-form-item>

        <el-form-item label="图片链接">
          <el-input v-model="editForm.image" placeholder="请输入图片链接地址" />
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
.news-manage {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 15px; /* 减小间距，让元素更紧凑 */
  align-items: center;
}

/* 针对搜索关键词输入框的样式 */
.search-item-keyword {
  flex-basis: 250px; /* 基础宽度，当空间足够时保持此宽度 */
  flex-grow: 1; /* 允许在必要时稍微拉伸 */
  min-width: 180px; /* 确保在小屏幕下也有最小宽度 */
}

/* 针对状态筛选下拉框的样式 */
.search-item-status {
  flex-basis: 150px; /* 基础宽度，足以容纳“审核拒绝” */
  flex-shrink: 0; /* 不允许被压缩 */
  min-width: 120px; /* 最小宽度，确保显示完整文本 */
}

/* 按钮组 */
.search-buttons {
  flex-shrink: 0; /* 不允许被压缩 */
  margin-left: auto; /* 将按钮组推到右侧（如果空间允许） */
  display: flex; /* 让按钮在内部也使用flex布局，保持间距 */
  gap: 10px; /* 按钮之间的间距 */
}

/* 移除 el-button 默认的 margin-left */
.search-buttons .el-button {
  margin-left: 0 !important; /* 使用 !important 确保覆盖Element UI的默认样式 */
}


.text-muted {
  color: #909399;
}
</style>