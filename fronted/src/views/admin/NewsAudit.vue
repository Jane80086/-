<script setup>
import {ref, onMounted} from 'vue'
import {getPendingNewsList, auditNews, batchAuditNews} from '@/api/news'
import {ElMessage, ElMessageBox} from 'element-plus'

const newsList = ref([])
const total = ref(0)
const loading = ref(false)
const pageParams = ref({
  page: 1,
  size: 10
})

const selectedNews = ref([])
const auditDialogVisible = ref(false)
const batchAuditDialogVisible = ref(false)
const currentNewsId = ref(null)
const auditForm = ref({
  status: 1,
  auditComment: ''
})

const loadPendingNews = async () => {
  loading.value = true
  try {
    const response = await getPendingNewsList(pageParams.value)

    // 检查响应的 code 和 data 字段
    if (response && response.code === '0' && response.data) {
      const pageResult = response.data; // 正确获取 data 字段的内容
      newsList.value = pageResult.list;
      total.value = pageResult.total;
    } else {
      ElMessage.error('加载待审核动态失败：未收到有效数据或请求失败');
    }
  } catch (error) {
    console.error('Network or request exception for pending news:', error);
    ElMessage.error('网络或请求异常');
  } finally {
    loading.value = false;
  }
}

const handlePageChange = (page) => {
  pageParams.value.page = page
  loadPendingNews()
}

const handleSelectionChange = (selection) => {
  selectedNews.value = selection
}

const showAuditDialog = (newsId) => {
  currentNewsId.value = newsId
  auditForm.value = {
    status: 1,
    auditComment: ''
  }
  auditDialogVisible.value = true
}

const showBatchAuditDialog = () => {
  if (selectedNews.value.length === 0) {
    ElMessage.warning('请先选择要审核的动态')
    return
  }
  auditForm.value = {
    status: 1,
    auditComment: ''
  }
  batchAuditDialogVisible.value = true
}

const handleAudit = async () => {
  try {
    await auditNews(currentNewsId.value, auditForm.value)
    ElMessage.success('审核成功')
    auditDialogVisible.value = false
    loadPendingNews()
  } catch (error) {
    ElMessage.error('审核失败')
  }
}

const handleBatchAudit = async () => {
  try {
    const newsIds = selectedNews.value.map(news => news.id)

    // 关键改动：在这里构造一个名为 'auditRequest' 的嵌套对象
    const auditRequestData = {
      auditRequest: {
        status: auditForm.value.status,
        comment: auditForm.value.auditComment // 注意：如果后端DTO是comment，这里也用comment
      }
    }

    // 将新闻ID数组和这个嵌套对象作为参数传递给 news.js
    await batchAuditNews(newsIds, auditRequestData)

    ElMessage.success('批量审核成功')
    batchAuditDialogVisible.value = false
    selectedNews.value = []
    loadPendingNews()
  } catch (error) {
    ElMessage.error('批量审核失败')
  }
}

const quickApprove = async (newsId) => {
  try {
    await auditNews(newsId, {status: 1, auditComment: '审核通过'})
    ElMessage.success('审核通过')
    loadPendingNews()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const quickReject = async (newsId) => {
  try {
    // ElMessageBox.prompt 返回的是一个 Promise，其 resolve 值包含用户输入
    const {value: auditComment} = await ElMessageBox.prompt('请输入拒绝原因', '审核拒绝', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入拒绝原因'
    })

    await auditNews(newsId, {
      status: 2,
      auditComment: auditComment || '审核不通过' // 使用用户输入的值
    })
    ElMessage.success('审核拒绝')
    loadPendingNews()
  } catch (error) {
    if (error !== 'cancel') { // 避免点击取消时也提示操作失败
      ElMessage.error('操作失败')
    }
  }
}

onMounted(() => {
  loadPendingNews()
})
</script>

<template>
  <div class="news-audit">
    <el-card>
      <template #header>
        <div class="header-actions">
          <span>动态审核</span>
          <el-button
              type="primary"
              @click="showBatchAuditDialog"
              :disabled="selectedNews.length === 0"
          >
            批量审核 ({{ selectedNews.length }})
          </el-button>
        </div>
      </template>

      <el-table
          :data="newsList"
          v-loading="loading"
          stripe
          @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55"/>

        <el-table-column prop="title" label="标题" min-width="200"/>

        <el-table-column prop="author" label="作者" width="120"/>

        <el-table-column prop="summary" label="简介" min-width="200">
          <template #default="{ row }">
            <el-tooltip :content="row.summary" placement="top">
              <span class="text-ellipsis">{{ row.summary }}</span>
            </el-tooltip>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="提交时间" width="180"/>

        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="$router.push(`/admin/news/${row.id}`)">
              预览
            </el-button>
            <el-button size="small" type="success" @click="quickApprove(row.id)">
              通过
            </el-button>
            <el-button size="small" type="danger" @click="quickReject(row.id)">
              拒绝
            </el-button>
            <el-button size="small" type="primary" @click="showAuditDialog(row.id)">
              详细审核
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

    <!-- 单个审核对话框 -->
    <el-dialog v-model="auditDialogVisible" title="审核动态" width="500px">
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.status">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input
              v-model="auditForm.auditComment"
              type="textarea"
              :rows="3"
              placeholder="请输入审核意见"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAudit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 批量审核对话框 -->
    <el-dialog v-model="batchAuditDialogVisible" title="批量审核" width="500px">
      <p>将对选中的 {{ selectedNews.length }} 条动态进行批量审核</p>

      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.status">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input
              v-model="auditForm.auditComment"
              type="textarea"
              :rows="3"
              placeholder="请输入审核意见"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="batchAuditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBatchAudit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.news-audit {
  padding: 20px;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.text-ellipsis {
  display: inline-block;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
