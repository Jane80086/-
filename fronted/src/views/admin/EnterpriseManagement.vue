<template>
  <div class="enterprise-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>企业管理</span>
          <div class="header-actions">
            <el-button type="success" @click="handleExport">
              <el-icon><Download /></el-icon>
              导出Excel
            </el-button>
            <el-button type="success" @click="handleSync">
              <el-icon><Refresh /></el-icon>
              同步企业信息
            </el-button>
            <el-button type="primary" @click="showAddDialog = true">
              <el-icon><Plus /></el-icon>
              添加企业
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="企业名称">
            <el-input v-model="searchForm.enterpriseName" placeholder="请输入企业名称" clearable />
          </el-form-item>
          <el-form-item label="统一社会信用代码">
            <el-input v-model="searchForm.creditCode" placeholder="请输入统一社会信用代码" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="正常" value="1" />
              <el-option label="异常" value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 企业表格 -->
      <el-table
        v-loading="loading"
        :data="enterpriseList"
        border
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="enterpriseName" label="企业名称" width="200" />
        <el-table-column prop="creditCode" label="统一社会信用代码" width="180" />
        <el-table-column prop="legalPerson" label="法定代表人" width="120" />
        <el-table-column prop="registeredCapital" label="注册资本" width="120" />
        <el-table-column prop="establishDate" label="成立日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '1' ? 'success' : 'danger'">
              {{ row.status === '1' ? '正常' : '异常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">查看</el-button>
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="success" @click="handleSyncOne(row)">同步</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 添加/编辑企业对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="isEdit ? '编辑企业' : '添加企业'"
      width="700px"
    >
      <el-form
        ref="enterpriseForm"
        :model="enterpriseFormData"
        :rules="enterpriseRules"
        label-width="150px"
        class="enterprise-form"
      >
        <el-form-item label="企业名称" prop="enterpriseName">
          <el-input v-model="enterpriseFormData.enterpriseName" placeholder="请输入企业名称" />
        </el-form-item>
        <el-form-item label="统一社会信用代码" prop="creditCode">
          <el-input v-model="enterpriseFormData.creditCode" placeholder="请输入统一社会信用代码" />
        </el-form-item>
        <el-form-item label="法定代表人" prop="legalPerson">
          <el-input v-model="enterpriseFormData.legalPerson" placeholder="请输入法定代表人" />
        </el-form-item>
        <el-form-item label="注册资本" prop="registeredCapital">
          <el-input v-model="enterpriseFormData.registeredCapital" placeholder="请输入注册资本" />
        </el-form-item>
        <el-form-item label="成立日期" prop="establishDate">
          <el-date-picker
            v-model="enterpriseFormData.establishDate"
            type="date"
            placeholder="选择成立日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="企业地址" prop="address">
          <el-input v-model="enterpriseFormData.address" placeholder="请输入企业地址" />
        </el-form-item>
        <el-form-item label="经营范围" prop="businessScope">
          <el-input
            v-model="enterpriseFormData.businessScope"
            type="textarea"
            :rows="3"
            placeholder="请输入经营范围"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="enterpriseFormData.status">
            <el-radio label="1">正常</el-radio>
            <el-radio label="0">异常</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 同步企业信息对话框 -->
    <el-dialog v-model="showSyncDialog" title="同步企业信息" width="500px">
      <el-form :model="syncForm" label-width="120px">
        <el-form-item label="企业名称">
          <el-input v-model="syncForm.enterpriseName" placeholder="请输入企业名称" />
        </el-form-item>
        <el-form-item label="企业ID">
          <el-input v-model="syncForm.enterpriseId" placeholder="请输入企业ID" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showSyncDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSyncSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
    <AIChat :visible="showAIChat" @close="showAIChat=false" />
    <button class="ai-float-btn" @click="showAIChat=true">AI助手</button>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Download, Plus, Refresh, Search } from '@element-plus/icons-vue'
import api from '@/api'
import AIChat from './AIChat.vue'

export default {
  name: 'EnterpriseManagement',
  components: {
    Download,
    Plus,
    Refresh,
    Search,
    AIChat
  },
  setup() {
    const loading = ref(false)
    const enterpriseList = ref([])
    const showAddDialog = ref(false)
    const showSyncDialog = ref(false)
    const isEdit = ref(false)
    const enterpriseForm = ref(null)
    const showAIChat = ref(false)
    
    const searchForm = reactive({
      enterpriseName: '',
      creditCode: '',
      status: ''
    })
    
    const enterpriseFormData = reactive({
      id: '',
      enterpriseName: '',
      creditCode: '',
      legalPerson: '',
      registeredCapital: '',
      establishDate: '',
      address: '',
      businessScope: '',
      status: '1'
    })
    
    const syncForm = reactive({
      enterpriseName: '',
      enterpriseId: ''
    })
    
    const pagination = reactive({
      current: 1,
      size: 10,
      total: 0
    })
    
    const enterpriseRules = {
      enterpriseName: [
        { required: true, message: '请输入企业名称', trigger: 'blur' }
      ],
      creditCode: [
        { required: true, message: '请输入统一社会信用代码', trigger: 'blur' }
      ],
      legalPerson: [
        { required: true, message: '请输入法定代表人', trigger: 'blur' }
      ]
    }
    
    const loadEnterprises = async () => {
      loading.value = true
      try {
        const params = {
          page: pagination.current,
          size: pagination.size,
          ...searchForm
        }
        const response = await api.enterprise.getEnterprises(params)
        enterpriseList.value = response.data.data.records || []
        pagination.total = response.data.data.total || 0
      } catch (error) {
        ElMessage.error('加载企业列表失败')
      } finally {
        loading.value = false
      }
    }
    
    const handleSearch = () => {
      pagination.current = 1
      loadEnterprises()
    }
    
    const resetSearch = () => {
      Object.assign(searchForm, {
        enterpriseName: '',
        creditCode: '',
        status: ''
      })
      handleSearch()
    }
    
    const handleSizeChange = (size) => {
      pagination.size = size
      loadEnterprises()
    }
    
    const handleCurrentChange = (current) => {
      pagination.current = current
      loadEnterprises()
    }
    
    const handleView = (row) => {
      // 查看企业详情
      ElMessage.info('查看企业详情功能待实现')
    }
    
    const handleEdit = (row) => {
      isEdit.value = true
      Object.assign(enterpriseFormData, row)
      showAddDialog.value = true
    }
    
    const handleSync = () => {
      showSyncDialog.value = true
    }

    // 导出Excel
    const handleExport = async () => {
      try {
        loading.value = true
        const params = {
          ...searchForm
        }
        const response = await api.enterprise.exportEnterprises(params)
        
        // 创建下载链接
        const blob = new Blob([response.data], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `企业列表_${new Date().toISOString().slice(0, 10)}.xlsx`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        
        ElMessage.success('导出成功')
      } catch (error) {
        console.error('导出失败:', error)
        ElMessage.error('导出失败')
      } finally {
        loading.value = false
      }
    }
    
    const handleSyncOne = async (row) => {
      try {
        await ElMessageBox.confirm('确定要同步该企业信息吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        console.log('syncEnterpriseInfoById 参数（handleSyncOne）：', row.enterpriseId)
        const response = await api.enterprise.syncEnterpriseInfoById(row.enterpriseId)
        ElMessage.success('同步成功')
        loadEnterprises()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('同步失败')
        }
      }
    }
    
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm('确定要删除该企业吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await api.enterprise.deleteEnterprise(row.enterpriseId)
        ElMessage.success('删除成功')
        loadEnterprises()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }
    
    const handleSubmit = async () => {
      if (!enterpriseForm.value) return
      
      try {
        await enterpriseForm.value.validate()
        
        if (isEdit.value) {
          await api.enterprise.updateEnterprise(enterpriseFormData.enterpriseId, enterpriseFormData)
          ElMessage.success('更新成功')
        } else {
          await api.enterprise.createEnterprise(enterpriseFormData)
          ElMessage.success('添加成功')
        }
        
        showAddDialog.value = false
        loadEnterprises()
      } catch (error) {
        ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
      }
    }
    
    const handleSyncSubmit = async () => {
      try {
        if (syncForm.enterpriseName) {
          await api.enterprise.syncEnterpriseInfo(syncForm.enterpriseName)
        } else if (syncForm.enterpriseId) {
          console.log('syncEnterpriseInfoById 参数（handleSyncSubmit）：', syncForm.enterpriseId)
          await api.enterprise.syncEnterpriseInfoById(syncForm.enterpriseId)
        } else {
          ElMessage.warning('请输入企业名称或企业ID')
          return
        }
        
        ElMessage.success('同步成功')
        showSyncDialog.value = false
        loadEnterprises()
      } catch (error) {
        ElMessage.error('同步失败')
      }
    }
    
    const resetForm = () => {
      Object.assign(enterpriseFormData, {
        id: '',
        enterpriseName: '',
        creditCode: '',
        legalPerson: '',
        registeredCapital: '',
        establishDate: '',
        address: '',
        businessScope: '',
        status: '1'
      })
      isEdit.value = false
    }
    
    onMounted(() => {
      loadEnterprises()
    })
    
    return {
      loading,
      enterpriseList,
      searchForm,
      showAddDialog,
      showSyncDialog,
      isEdit,
      enterpriseForm,
      enterpriseFormData,
      syncForm,
      enterpriseRules,
      pagination,
      handleSearch,
      resetSearch,
      handleSizeChange,
      handleCurrentChange,
      handleView,
      handleEdit,
      handleSync,
      handleExport,
      handleSyncOne,
      handleDelete,
      handleSubmit,
      handleSyncSubmit,
      resetForm,
      showAIChat
    }
  }
}
</script>

<style scoped>
.enterprise-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.search-bar {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.enterprise-form .el-form-item__label {
  font-weight: bold;
  font-size: 15px;
  color: #333;
  letter-spacing: 1px;
}
.enterprise-form .el-form-item {
  margin-bottom: 22px;
}
.enterprise-form .el-input,
.enterprise-form .el-date-picker,
.enterprise-form .el-textarea {
  width: 100%;
}

.ai-float-btn {
  position: fixed;
  right: 40px;
  bottom: 100px;
  z-index: 10000;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 50%;
  width: 56px;
  height: 56px;
  font-size: 18px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.18);
  cursor: pointer;
}
</style>