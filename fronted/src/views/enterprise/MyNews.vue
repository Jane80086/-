<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyNewsList, deleteNews } from '@/api/news'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

const newsList = ref([])
const total = ref(0)
const loading = ref(false)
const pageParams = ref({
  page: 1,
  size: 10
})

const statusMap = {
  0: { text: '待审核', type: 'warning' },
  1: { text: '已发布', type: 'success' },
  2: { text: '审核拒绝', type: 'danger' },
  // 修改这里：将 '已下线' 改为 '草稿' 以匹配后端枚举
  3: { text: '草稿', type: 'info' }
}

const loadMyNews = async () => {
  loading.value = true
  try {
    const pageResult = await getMyNewsList(pageParams.value)

    console.log('Received pageResult:', pageResult)

    if (pageResult) {
      newsList.value = pageResult.list
      total.value = pageResult.total
    } else {
      ElMessage.error('加载数据失败：未收到有效数据')
    }

  } catch (error) {
    console.error('Network or request exception:', error)
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page) => {
  pageParams.value.page = page
  loadMyNews()
}

const handleEdit = (newsId) => {
  router.push(`/enterprise/news-edit/${newsId}`)
}

const handleDelete = async (newsId, title) => {
  try {
    await ElMessageBox.confirm(
        `确定要删除动态 "${title}" 吗？`,
        '确认删除',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )

    await deleteNews(newsId)
    ElMessage.success('删除成功')
    loadMyNews()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Delete failed:', error)
    }
  }
}

const viewDetail = (newsId) => {
  router.push(`/normal/news-detail/${newsId}`)
}

onMounted(() => {
  loadMyNews()
})
</script>

<template>
  <div class="my-news">
    <el-card>
      <template #header>
        <div class="header-actions">
          <span>我的动态</span>
          <el-button type="primary" @click="router.push('/enterprise/news-publish')">
            发布新动态
          </el-button>
        </div>
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

        <el-table-column prop="auditComment" label="审核意见" min-width="150">
          <template #default="{ row }">
            <span v-if="row.auditComment">{{ row.auditComment }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
                size="small"
                type="primary"
                @click="handleEdit(row.id)"
                :disabled="![0, 2, 3].includes(row.status)"
            >
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
          :current-page="pageParams.page"
          :page-size="pageParams.size"
          :total="total"
          layout="total, prev, pager, next, jumper"
          @current-change="handlePageChange"
          style="margin-top: 20px; text-align: center;"
      />
    </el-card>
  </div>
</template>

<style scoped>
.my-news {
  padding: 20px;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.text-muted {
  color: #909399;
}
</style>