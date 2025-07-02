<template>
  <div class="user-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <div class="header-actions">
            <el-button type="success" @click="handleExport">
              <el-icon><Download /></el-icon>
              导出Excel
            </el-button>
            <el-button type="primary" @click="showAddDialog = true">
              <el-icon><Plus /></el-icon>
              添加用户
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="真实姓名">
            <el-input v-model="searchForm.realName" placeholder="请输入真实姓名" clearable />
          </el-form-item>
          <el-form-item label="账号">
            <el-input v-model="searchForm.account" placeholder="请输入账号" clearable />
          </el-form-item>
          <el-form-item label="企业名称">
            <el-input v-model="searchForm.enterpriseName" placeholder="请输入企业名称" clearable />
          </el-form-item>
          <el-form-item label="企业类型">
            <el-select v-model="searchForm.enterpriseType" placeholder="请选择企业类型" clearable>
              <el-option label="科技企业" value="科技企业" />
              <el-option label="制造企业" value="制造企业" />
              <el-option label="服务企业" value="服务企业" />
              <el-option label="贸易企业" value="贸易企业" />
              <el-option label="金融企业" value="金融企业" />
              <el-option label="教育企业" value="教育企业" />
              <el-option label="医疗企业" value="医疗企业" />
              <el-option label="建筑企业" value="建筑企业" />
              <el-option label="能源企业" value="能源企业" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
          <el-form-item label="手机号码">
            <el-input v-model="searchForm.phone" placeholder="请输入手机号码" clearable />
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
      
      <!-- 用户表格 -->
      <el-table
        v-loading="loading"
        :data="userList"
        border
        style="width: 100%"
      >
        <el-table-column prop="userId" label="用户ID" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="account" label="账号" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="enterpriseId" label="企业ID" width="120" />
        <el-table-column prop="enterpriseName" label="企业名称" width="200" />
        <el-table-column prop="enterpriseType" label="企业类型" width="120" />
        <el-table-column prop="phone" label="手机号码" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="info" @click="handleHistory(row)">历史</el-button>
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
    
    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="isEdit ? '编辑用户' : '添加用户'"
      width="700px"
    >
      <el-form
        ref="userForm"
        :model="userFormData"
        :rules="userRules"
        label-width="120px"
        class="user-form"
      >
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="userFormData.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="账号" prop="account">
          <el-input v-model="userFormData.account" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="userFormData.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="企业ID" prop="enterpriseId">
          <el-input v-model="userFormData.enterpriseId" placeholder="请输入企业ID" />
        </el-form-item>
        <el-form-item label="企业名称" prop="enterpriseName">
          <el-input v-model="userFormData.enterpriseName" placeholder="请输入企业名称" />
        </el-form-item>
        <el-form-item label="企业类型" prop="enterpriseType">
          <el-select v-model="userFormData.enterpriseType" placeholder="请选择企业类型" style="width: 100%">
            <el-option label="科技企业" value="科技企业" />
            <el-option label="制造企业" value="制造企业" />
            <el-option label="服务企业" value="服务企业" />
            <el-option label="贸易企业" value="贸易企业" />
            <el-option label="金融企业" value="金融企业" />
            <el-option label="教育企业" value="教育企业" />
            <el-option label="医疗企业" value="医疗企业" />
            <el-option label="建筑企业" value="建筑企业" />
            <el-option label="能源企业" value="能源企业" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="userFormData.phone" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userFormData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="userFormData.password" type="password" placeholder="请输入密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 用户修改历史对话框 -->
    <el-dialog
      v-model="showHistoryDialog"
      title="用户修改历史"
      width="1000px"
    >
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :inline="true" :model="historySearchForm">
          <el-form-item label="操作人">
            <el-input v-model="historySearchForm.operatorId" placeholder="请输入操作人ID" clearable />
          </el-form-item>
          <el-form-item label="修改字段">
            <el-select v-model="historySearchForm.fieldName" placeholder="请选择修改字段" clearable>
              <el-option label="真实姓名" value="realName" />
              <el-option label="账号" value="account" />
              <el-option label="手机号码" value="phone" />
              <el-option label="邮箱" value="email" />
              <el-option label="企业ID" value="enterpriseId" />
              <el-option label="企业名称" value="enterpriseName" />
              <el-option label="企业类型" value="enterpriseType" />
            </el-select>
          </el-form-item>
          <el-form-item label="开始时间">
            <el-date-picker
              v-model="historySearchForm.startTime"
              type="datetime"
              placeholder="选择开始时间"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
            />
          </el-form-item>
          <el-form-item label="结束时间">
            <el-date-picker
              v-model="historySearchForm.endTime"
              type="datetime"
              placeholder="选择结束时间"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleHistorySearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetHistorySearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 历史记录表格 -->
      <el-table
        v-loading="historyLoading"
        :data="historyList"
        border
        style="width: 100%"
      >
        <el-table-column prop="historyId" label="历史ID" width="120" />
        <el-table-column prop="userId" label="用户ID" width="120" />
        <el-table-column prop="fieldName" label="修改字段" width="120">
          <template #default="{ row }">
            <el-tag :type="getFieldNameTagType(row.fieldName)">
              {{ getFieldNameLabel(row.fieldName) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="oldValue" label="旧值" width="150" />
        <el-table-column prop="newValue" label="新值" width="150" />
        <el-table-column prop="operatorId" label="操作人" width="120" />
        <el-table-column prop="modifyTime" label="修改时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="warning" @click="handleRestore(row)">
              <el-icon><Refresh /></el-icon>
              恢复
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="historyPagination.current"
          v-model:page-size="historyPagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="historyPagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleHistorySizeChange"
          @current-change="handleHistoryCurrentChange"
        />
      </div>
    </el-dialog>
    <AIChat :visible="showAIChat" @close="showAIChat=false" />
    <button class="ai-float-btn" @click="showAIChat=true">AI助手</button>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Download, Plus, Search, Refresh } from '@element-plus/icons-vue'
import api from '@/api'
import AIChat from './AIChat.vue'

export default {
  name: 'UserManagement',
  components: {
    Download,
    Plus,
    Search,
    Refresh,
    AIChat
  },
  setup() {
    const loading = ref(false)
    const userList = ref([])
    const showAddDialog = ref(false)
    const showHistoryDialog = ref(false)
    const isEdit = ref(false)
    const userForm = ref(null)
    const showAIChat = ref(false)
    
    const searchForm = reactive({
      realName: '',
      account: '',
      enterpriseName: '',
      enterpriseType: '',
      phone: ''
    })
    
    const userFormData = reactive({
      userId: '',
      realName: '',
      account: '',
      nickname: '',
      enterpriseId: '',
      enterpriseName: '',
      enterpriseType: '',
      phone: '',
      email: '',
      password: ''
    })
    
    const pagination = reactive({
      current: 1,
      size: 10,
      total: 0
    })

    // 历史记录相关
    const historyList = ref([])
    const historyLoading = ref(false)
    const currentUserId = ref('')
    
    const historySearchForm = reactive({
      operatorId: '',
      fieldName: '',
      startTime: '',
      endTime: ''
    })
    
    const historyPagination = reactive({
      current: 1,
      size: 10,
      total: 0
    })
    
    const userRules = {
      realName: [
        { required: true, message: '请输入真实姓名', trigger: 'blur' }
      ],
      account: [
        { required: true, message: '请输入账号', trigger: 'blur' }
      ],
      phone: [
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
      ],
      email: [
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
      ]
    }
    
    // 获取用户列表
    const fetchUserList = async () => {
      loading.value = true
      try {
        const params = {
          page: pagination.current,
          pageSize: pagination.size,
          ...searchForm
        }
        const response = await api.user.getUsers(params)
        if (response.data.code === 200) {
          userList.value = response.data.data.records || []
          pagination.total = response.data.data.total || 0
        }
      } catch (error) {
        console.error('获取用户列表失败:', error)
        ElMessage.error('获取用户列表失败')
      } finally {
        loading.value = false
      }
    }
    
    // 搜索
    const handleSearch = () => {
      pagination.current = 1
      fetchUserList()
    }
    
    // 重置搜索
    const resetSearch = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = ''
      })
      pagination.current = 1
      fetchUserList()
    }
    
    // 分页大小改变
    const handleSizeChange = (size) => {
      pagination.size = size
      pagination.current = 1
      fetchUserList()
    }
    
    // 当前页改变
    const handleCurrentChange = (current) => {
      pagination.current = current
      fetchUserList()
    }
    
    // 导出Excel
    const handleExport = async () => {
      try {
        loading.value = true
        const params = {
          ...searchForm
        }
        const response = await api.user.exportUsers(params)
        
        // 创建下载链接
        const blob = new Blob([response.data], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `用户列表_${new Date().toISOString().slice(0, 10)}.xlsx`
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
    
    // 编辑用户
    const handleEdit = (row) => {
      isEdit.value = true
      Object.assign(userFormData, row)
      showAddDialog.value = true
    }
    
    // 删除用户
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除用户 "${row.realName}" 吗？`,
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        const response = await api.user.deleteUser(row.userId)
        if (response.data.code === 200) {
          ElMessage.success('删除成功')
          fetchUserList()
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除用户失败:', error)
          ElMessage.error('删除用户失败')
        }
      }
    }
    
    // 提交表单
    const handleSubmit = async () => {
      if (!userForm.value) return
      
      try {
        await userForm.value.validate()
        
        if (isEdit.value) {
          const response = await api.user.updateUser(userFormData.userId, userFormData)
          if (response.data.code === 200) {
            ElMessage.success('更新成功')
            showAddDialog.value = false
            fetchUserList()
          }
        } else {
          const response = await api.user.createUser(userFormData)
          if (response.data.code === 200) {
            ElMessage.success('创建成功')
            showAddDialog.value = false
            fetchUserList()
          }
        }
      } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error('提交失败')
      }
    }
    
    // 重置表单
    const resetForm = () => {
      if (userForm.value) {
        userForm.value.resetFields()
      }
      Object.keys(userFormData).forEach(key => {
        userFormData[key] = ''
      })
      isEdit.value = false
    }
    
    // 监听对话框关闭
    const handleDialogClose = () => {
      resetForm()
    }

    // 查看用户修改历史
    const handleHistory = (row) => {
      currentUserId.value = row.userId
      historyPagination.current = 1
      showHistoryDialog.value = true
      fetchHistoryList()
    }

    // 获取历史记录列表
    const fetchHistoryList = async () => {
      historyLoading.value = true
      try {
        const params = {
          userId: currentUserId.value,
          page: historyPagination.current,
          pageSize: historyPagination.size,
          ...historySearchForm
        }
        const response = await api.user.getUserHistory(params)
        if (response.data.code === 200) {
          historyList.value = response.data.data.records || []
          historyPagination.total = response.data.data.total || 0
        }
      } catch (error) {
        console.error('获取历史记录失败:', error)
        ElMessage.error('获取历史记录失败')
      } finally {
        historyLoading.value = false
      }
    }

    // 历史记录搜索
    const handleHistorySearch = () => {
      historyPagination.current = 1
      fetchHistoryList()
    }

    // 重置历史记录搜索
    const resetHistorySearch = () => {
      Object.keys(historySearchForm).forEach(key => {
        historySearchForm[key] = ''
      })
      historyPagination.current = 1
      fetchHistoryList()
    }

    // 历史记录分页大小改变
    const handleHistorySizeChange = (size) => {
      historyPagination.size = size
      historyPagination.current = 1
      fetchHistoryList()
    }

    // 历史记录当前页改变
    const handleHistoryCurrentChange = (current) => {
      historyPagination.current = current
      fetchHistoryList()
    }

    // 获取字段名称标签
    const getFieldNameLabel = (fieldName) => {
      const fieldMap = {
        'realName': '真实姓名',
        'account': '账号',
        'phone': '手机号码',
        'email': '邮箱',
        'enterpriseId': '企业ID',
        'enterpriseName': '企业名称',
        'enterpriseType': '企业类型'
      }
      return fieldMap[fieldName] || fieldName
    }

    // 获取字段名称标签类型
    const getFieldNameTagType = (fieldName) => {
      const typeMap = {
        'realName': 'primary',
        'account': 'success',
        'phone': 'warning',
        'email': 'info',
        'enterpriseId': 'danger',
        'enterpriseName': 'primary',
        'enterpriseType': 'success'
      }
      return typeMap[fieldName] || 'info'
    }

    // 恢复历史记录
    const handleRestore = async (row) => {
      try {
        await ElMessageBox.confirm(
          `确定要恢复 "${getFieldNameLabel(row.fieldName)}" 字段到 "${row.oldValue}" 吗？`,
          '确认恢复',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        const response = await api.user.restoreUserHistory(row.historyId)
        if (response.data.code === 200) {
          ElMessage.success('恢复成功')
          // 刷新历史记录列表
          fetchHistoryList()
          // 刷新用户列表
          fetchUserList()
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('恢复失败:', error)
          ElMessage.error('恢复失败')
        }
      }
    }
    
    onMounted(() => {
      fetchUserList()
    })
    
    return {
      loading,
      userList,
      showAddDialog,
      showHistoryDialog,
      isEdit,
      userForm,
      searchForm,
      userFormData,
      pagination,
      userRules,
      historyList,
      historyLoading,
      historySearchForm,
      historyPagination,
      handleSearch,
      resetSearch,
      handleSizeChange,
      handleCurrentChange,
      handleExport,
      handleEdit,
      handleHistory,
      handleDelete,
      handleSubmit,
      handleDialogClose,
      handleHistorySearch,
      resetHistorySearch,
      handleHistorySizeChange,
      handleHistoryCurrentChange,
      getFieldNameLabel,
      getFieldNameTagType,
      handleRestore,
      showAIChat
    }
  }
}
</script>

<style scoped>
.user-management {
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
  background-color: #f5f7fa;
  border-radius: 4px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.user-form {
  max-height: 400px;
  overflow-y: auto;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
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
