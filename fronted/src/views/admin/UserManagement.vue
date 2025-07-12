<template>
  <div class="user-management">
    <!-- AI自动分析用户活跃度区域 -->
    <el-card class="ai-active-analysis-card" shadow="hover">
      <template #header>
        <div class="ai-header">
          <el-icon><DataAnalysis /></el-icon>
          <span>AI自动分析用户活跃度</span>
        </div>
      </template>
      <div class="ai-active-stats">
        <div class="stat-item">
          <div class="stat-label">总用户数</div>
          <div class="stat-value">
            <el-icon class="trend-icon up"><TrendCharts /></el-icon>
            {{ activeStats.totalUsers }}
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-label">活跃用户数</div>
          <div class="stat-value active">
            <el-icon class="trend-icon flat"><TrendCharts /></el-icon>
            {{ activeStats.activeUsers }}
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-label">活跃率</div>
          <div class="stat-value rate">
            <el-icon class="trend-icon down"><TrendCharts /></el-icon>
            {{ activeStats.activeRate }}%
          </div>
        </div>
        <div class="chart-container">
          <div id="active-trend-chart"></div>
        </div>
      </div>
      <div class="ai-active-conclusion">
        <el-icon><InfoFilled /></el-icon>
        <span>{{ activeStats.aiConclusion }}</span>
      </div>
    </el-card>

    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="main-title">用户管理</h1>
          <p class="subtitle">管理系统中的所有用户信息</p>
        </div>
        <div class="header-actions">
          <el-button type="success" size="large" @click="handleExport" class="export-btn">
            <el-icon><Download /></el-icon>
            导出Excel
          </el-button>
          <el-button type="primary" size="large" @click="handleAdd" class="add-btn">
            <el-icon><Plus /></el-icon>
            添加用户
          </el-button>
        </div>
      </div>
    </div>
        <el-card class="user-management-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <h3 class="list-title">
              <el-icon><List /></el-icon>
              用户列表
            </h3>
            <span class="user-count">共 {{ pagination.total }} 个用户</span>
          </div>
        </div>
      </template>
      
            <!-- 搜索区域 -->
      <div class="search-section">
        <div class="search-header">
          <h4 class="search-title">
            <el-icon><Search /></el-icon>
            搜索用户
          </h4>
        </div>
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="真实姓名">
            <el-input v-model="searchForm.realName" placeholder="请输入真实姓名" clearable class="search-input" />
          </el-form-item>
          <el-form-item label="账号">
            <el-input v-model="searchForm.account" placeholder="请输入账号" clearable class="search-input" />
          </el-form-item>
          <el-form-item label="企业名称">
            <el-input v-model="searchForm.enterpriseName" placeholder="请输入企业名称" clearable class="search-input" />
          </el-form-item>
          <el-form-item label="手机号码">
            <el-input v-model="searchForm.phone" placeholder="请输入手机号码" clearable class="search-input" />
          </el-form-item>
          <el-form-item>
            <div class="search-buttons">
              <el-button type="primary" @click="handleSearch" class="search-btn">
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
              <el-button @click="resetSearch" class="reset-btn">
                <el-icon><Refresh /></el-icon>
                重置
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 用户表格 -->
      <el-table
        v-loading="loading"
        :data="userList"
        border
        class="user-table"
        stripe
      >
        <el-table-column prop="id" label="用户ID" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="username" label="账号" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="userType" label="用户类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getUserTypeTagType(row.userType)">
              {{ getUserTypeLabel(row.userType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="enterpriseId" label="企业ID" width="120" />
        <el-table-column prop="enterpriseName" label="企业名称" width="200" />
        <el-table-column prop="phone" label="手机号码" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button size="small" @click="handleEdit(row)" class="edit-btn">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button size="small" type="info" @click="handleHistory(row)" class="history-btn">
                <el-icon><Clock /></el-icon>
                历史
              </el-button>
              <el-button size="small" type="danger" @click="handleDelete(row)" class="delete-btn">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          class="pagination"
        />
      </div>
    </el-card>
    
    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="isEdit ? '编辑用户' : '添加用户'"
      width="700px"
      class="user-dialog"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form
        ref="userForm"
        :model="userFormData"
        :rules="userRules"
        label-width="120px"
        class="user-form"
      >
        <!-- 用户类型选择 -->
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="userFormData.userType" placeholder="请选择用户类型" @change="handleUserTypeChange" class="form-input">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="企业用户" value="ENTERPRISE" />
            <el-option label="普通用户" value="NORMAL" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="userFormData.realName" placeholder="请输入真实姓名" class="form-input" />
        </el-form-item>
        <el-form-item label="账号" prop="username">
          <el-input v-model="userFormData.username" placeholder="请输入账号" class="form-input" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="userFormData.nickname" placeholder="请输入昵称" class="form-input" />
        </el-form-item>
        
        <!-- 企业用户特有字段 -->
        <template v-if="userFormData.userType === 'ENTERPRISE' || isEdit">
          <el-form-item label="企业ID" prop="enterpriseId">
            <el-input v-model="userFormData.enterpriseId" placeholder="请输入企业ID" class="form-input" />
          </el-form-item>
          <el-form-item label="企业名称" prop="enterpriseName">
            <el-input v-model="userFormData.enterpriseName" placeholder="请输入企业名称" class="form-input" />
          </el-form-item>
        </template>
        
        <!-- 管理员用户特有字段 -->
        <template v-if="userFormData.userType === 'ADMIN'">
          <el-form-item label="部门" prop="department">
            <el-input v-model="userFormData.department" placeholder="请输入部门" class="form-input" />
          </el-form-item>
        </template>
        
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
              <el-option label="账号" value="username" />
              <el-option label="手机号码" value="phone" />
              <el-option label="邮箱" value="email" />
              <el-option label="企业ID" value="enterpriseId" />
              <el-option label="企业名称" value="enterpriseName" />
              <el-option label="用户类型" value="userType" />
              <el-option label="部门" value="department" />
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

<script setup>
import { ref, reactive, onMounted, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Download, Plus, Search, Refresh, DataAnalysis, InfoFilled, List, Edit, Clock, Delete, TrendCharts, UserFilled, OfficeBuilding, User, Setting } from '@element-plus/icons-vue'
import { userAPI } from '@/api/index.js'
import AIChat from './AIChat.vue'
import * as echarts from 'echarts'
import { getAIReportAnalysis } from '@/api/register.js'

// 定义组件名称
defineOptions({
  name: 'UserManagement'
})

// 响应式数据
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
  userType: '',
  enterpriseName: '',
  enterpriseType: '',
  phone: ''
})

const userFormData = reactive({
  id: '',
  realName: '',
  username: '',
  nickname: '',
  enterpriseId: '',
  enterpriseName: '',
  userType: 'ENTERPRISE',
  phone: '',
  email: '',
  password: '',
  status: 1,
  isRemembered: false,
  department: '',
  avatar: '',
  dynamicCode: ''
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
  userType: [
    { required: true, message: '请选择用户类型', trigger: 'change' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  username: [
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

// 活跃度统计
const activeStats = reactive({
  totalUsers: 0,
  activeUsers: 0,
  activeRate: 0,
  trend: [0, 0, 0, 0, 0, 0, 0],
  days: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
  aiConclusion: ''
})

// 获取用户列表
const fetchUserList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.current,
      pageSize: pagination.size,
      ...searchForm
    }
    const response = await userAPI.getUsers(params)
    console.log('用户列表接口返回：', response)
    if (response.code === 200) {
      userList.value = response.data.records || []
      pagination.total = response.data.total || 0
      // 统计活跃度数据（示例：假设有isActive字段，实际请按后端数据结构调整）
      const allUsers = userList.value
      activeStats.totalUsers = pagination.total
      activeStats.activeUsers = allUsers.filter(u => u.isActive).length
      activeStats.activeRate = activeStats.totalUsers > 0 ? ((activeStats.activeUsers / activeStats.totalUsers) * 100).toFixed(1) : 0
      // 统计近7天活跃趋势（示例：假设有lastActiveDate字段，实际请按后端数据结构调整）
      const today = new Date()
      activeStats.trend = activeStats.days.map((d, i) => {
        const day = new Date(today)
        day.setDate(today.getDate() - (6 - i))
        const dayStr = day.toISOString().slice(0, 10)
        return allUsers.filter(u => u.lastActiveDate && u.lastActiveDate.startsWith(dayStr)).length
      })
      // 调用AI分析接口
      const reportData = {
        totalUsers: activeStats.totalUsers,
        activeUsers: activeStats.activeUsers,
        activeRate: activeStats.activeRate,
        trend: activeStats.trend,
        days: activeStats.days
      }
      getAIReportAnalysis(reportData).then(res => {
        activeStats.aiConclusion = res.analysis || 'AI分析服务暂时不可用';
      })
    } else {
      ElMessage.error('用户列表接口异常：' + (response.msg || response.code || '未知错误'))
      userList.value = []
      pagination.total = 0
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
    const response = await userAPI.exportUsers(params)
    
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

// 添加用户
const handleAdd = () => {
  isEdit.value = false
  resetForm()
  showAddDialog.value = true
}

// 用户类型选择对话框
const showUserTypeDialog = ref(false)

// 用户类型选项
const userTypes = [
  { label: '管理员用户', value: 'ADMIN', description: '系统管理员，拥有最高权限' },
  { label: '企业用户', value: 'ENTERPRISE', description: '企业用户，可以管理企业相关功能' },
  { label: '普通用户', value: 'NORMAL', description: '普通用户，基础功能使用' },
  { label: '系统用户', value: 'SYSTEM', description: '系统内部用户，用于系统集成' }
]

// 选择用户类型
const selectUserType = (userType) => {
  userFormData.userType = userType
  showUserTypeDialog.value = false
  isEdit.value = false
  resetForm()
  showAddDialog.value = true
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
    // 修正：用row.id作为用户ID
    const response = await userAPI.deleteUser(row.id)
    if (response.code === 200) {
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
      const response = await userAPI.updateUser(userFormData.id, userFormData)
      if (response.code === 200) {
        ElMessage.success('更新成功')
        showAddDialog.value = false
        fetchUserList()
      }
    } else {
      // 设置默认值
      const createData = {
        ...userFormData,
        userType: userFormData.userType || 'ENTERPRISE',
        status: userFormData.status || 1,
        isRemembered: userFormData.isRemembered || false
      }
      console.log('[DEBUG] 准备创建用户，数据:', createData)
      const response = await userAPI.createUser(createData)
      console.log('[DEBUG] 创建用户响应:', response)
      if (response.code === 200) {
        ElMessage.success('创建成功')
        showAddDialog.value = false
        fetchUserList()
      } else {
        ElMessage.error('创建失败: ' + (response.msg || response.message || '未知错误'))
      }
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败: ' + error.message)
  }
}

// 重置表单
const resetForm = () => {
  if (userForm.value) {
    userForm.value.resetFields()
  }
  Object.keys(userFormData).forEach(key => {
    if (key === 'userType') {
      userFormData[key] = 'ENTERPRISE' // 默认企业用户
    } else if (key === 'status') {
      userFormData[key] = 1 // 默认启用
    } else if (key === 'isRemembered') {
      userFormData[key] = false // 默认不记住
    } else {
      userFormData[key] = ''
    }
  })
  isEdit.value = false
}

// 监听对话框关闭
const handleDialogClose = () => {
  resetForm()
}

// 查看用户修改历史
const handleHistory = (row) => {
  currentUserId.value = row.id
  historyPagination.current = 1
  showHistoryDialog.value = true
  fetchHistoryList()
}

// 获取历史记录列表
const fetchHistoryList = async () => {
  historyLoading.value = true
  try {
    const params = {
      userId: Number(currentUserId.value), // 确保userId是数字类型
      page: historyPagination.current,
      pageSize: historyPagination.size,
      ...historySearchForm
    }
    console.log('[DEBUG] 获取历史记录参数:', params)
    const response = await userAPI.getUserHistory(params)
    console.log('[DEBUG] 历史记录响应:', response)
    
    // 修复响应数据结构处理
    if (response.code === 200) {
      historyList.value = response.data.records || []
      historyPagination.total = response.data.total || 0
      console.log('[DEBUG] 历史记录数据:', historyList.value)
      console.log('[DEBUG] 历史记录总数:', historyPagination.total)
    } else {
      console.error('[ERROR] 获取历史记录失败:', response.msg)
      ElMessage.error('获取历史记录失败: ' + response.msg)
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
    'username': '账号',
    'phone': '手机号码',
    'email': '邮箱',
    'enterpriseId': '企业ID',
    'enterpriseName': '企业名称',
    'userType': '用户类型',
    'department': '部门'
  }
  return fieldMap[fieldName] || fieldName
}

// 获取字段名称标签类型
const getFieldNameTagType = (fieldName) => {
  const typeMap = {
    'realName': 'primary',
    'username': 'success',
    'phone': 'warning',
    'email': 'info',
    'enterpriseId': 'danger',
    'enterpriseName': 'primary',
    'userType': 'success',
    'department': 'warning'
  }
  return typeMap[fieldName] || 'info'
}

// 获取用户类型标签
const getUserTypeLabel = (userType) => {
  const typeMap = {
    'ADMIN': '管理员',
    'ENTERPRISE': '企业用户',
    'NORMAL': '普通用户'
  }
  return typeMap[userType] || userType
}

// 获取用户类型标签类型
const getUserTypeTagType = (userType) => {
  const typeMap = {
    'ADMIN': 'danger',
    'ENTERPRISE': 'primary',
    'NORMAL': 'success'
  }
  return typeMap[userType] || 'info'
}

// 处理用户类型变化
const handleUserTypeChange = (userType) => {
  // 根据用户类型重置相关字段
  if (userType !== 'ENTERPRISE') {
    userFormData.enterpriseId = ''
    userFormData.enterpriseName = ''
  }
  
  // 根据用户类型设置默认部门
  if (userType === 'ADMIN') {
    userFormData.department = '系统管理部'
  } else if (userType === 'ENTERPRISE') {
    userFormData.department = ''
  } else if (userType === 'NORMAL') {
    userFormData.department = '普通用户'
  }
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
    
    const response = await userAPI.restoreUserHistory(row.historyId)
    if (response.code === 200) {
      ElMessage.success('恢复成功')
      // 刷新历史记录列表
      fetchHistoryList()
      // 刷新用户列表
      fetchUserList()
    } else {
      ElMessage.error('恢复失败: ' + (response.msg || '未知错误'))
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('恢复失败:', error)
      ElMessage.error('恢复失败')
    }
  }
}

function renderTrendChart() {
  nextTick(() => {
    const chartDom = document.getElementById('active-trend-chart');
    if (!chartDom) return;
    const myChart = echarts.init(chartDom);
    myChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 20, right: 20, top: 20, bottom: 20, containLabel: true },
      xAxis: {
        type: 'category',
        data: activeStats.days || [],
        axisLine: { lineStyle: { color: '#ccc' } },
        axisLabel: { color: '#666' }
      },
      yAxis: {
        type: 'value',
        axisLine: { lineStyle: { color: '#ccc' } },
        axisLabel: { color: '#666' },
        splitLine: { lineStyle: { color: '#eee' } }
      },
      series: [{
        data: activeStats.trend || [],
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: { color: '#409eff', width: 3 },
        itemStyle: { color: '#409eff' },
        areaStyle: { color: 'rgba(64,158,255,0.15)' }
      }]
    });
    myChart.resize();
  });
}

onMounted(() => {
  fetchUserList()
  renderTrendChart();
})

watch(() => [activeStats.trend, activeStats.days], () => {
  renderTrendChart();
});

</script>

<style scoped>
.user-management {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.user-type-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  padding: 20px 0;
}

.user-type-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.user-type-card:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
  transform: translateY(-2px);
}

.user-type-icon {
  margin-right: 15px;
  font-size: 24px;
  color: #409eff;
}

.user-type-content h3 {
  margin: 0 0 5px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.user-type-content p {
  margin: 0;
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}

/* AI分析卡片样式 */
.ai-active-analysis-card {
  margin-bottom: 24px;
  background: #fff;
  border: none;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.08);
}

.ai-header {
  display: flex;
  align-items: center;
  color: #333;
  font-size: 18px;
  font-weight: bold;
  padding: 16px 0 8px 0;
}

.ai-header .el-icon {
  margin-right: 8px;
  font-size: 20px;
  color: #409eff;
}

.ai-active-stats {
  display: flex;
  gap: 40px;
  align-items: center;
  padding: 20px 0 10px 0;
}

.stat-item {
  text-align: center;
  color: #333;
  flex: 1;
}

.stat-label {
  font-size: 14px;
  opacity: 0.85;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 4px;
  color: #222;
}

.stat-value.active {
  color: #67c23a;
}

.stat-value.rate {
  color: #409eff;
}

.chart-container {
  flex: 2;
  min-width: 300px;
  height: 120px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(64,158,255,0.06);
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}
#active-trend-chart {
  width: 100%;
  height: 120px;
}

.ai-active-conclusion {
  margin-top: 16px;
  color: #666;
  display: flex;
  align-items: center;
  font-size: 14px;
  background: #f5f7fa;
  border-radius: 6px;
  padding: 10px 16px;
}

.ai-active-conclusion .el-icon {
  margin-right: 8px;
  font-size: 16px;
  color: #409eff;
}

/* 页面标题样式 */
.page-header {
  margin-bottom: 20px;
  background-color: #fff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 15px;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.title-section {
  flex: 1;
}

.main-title {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 28px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  margin: 0;
  color: #7f8c8d;
  font-size: 16px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.export-btn, .add-btn {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.export-btn:hover, .add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
}

/* 用户管理卡片样式 */
.user-management-card {
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: none;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
}

.header-left {
  display: flex;
  align-items: center;
}

.list-title {
  margin: 0;
  color: #2c3e50;
  font-size: 20px;
  font-weight: bold;
  display: flex;
  align-items: center;
}

.list-title .el-icon {
  margin-right: 8px;
  color: #409eff;
  font-size: 20px;
}

.user-count {
  margin-left: 15px;
  color: #909399;
  font-size: 14px;
  background: #f5f7fa;
  padding: 4px 12px;
  border-radius: 20px;
}

/* 搜索区域样式 */
.search-section {
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
}

.search-header {
  margin-bottom: 15px;
}

.search-title {
  margin: 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: bold;
  display: flex;
  align-items: center;
}

.search-title .el-icon {
  margin-right: 8px;
  color: #409eff;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: flex-end;
}

.search-input .el-input__inner {
  border-radius: 8px;
  border: 1px solid #dcdfe6;
  transition: all 0.3s ease;
}

.search-input .el-input__inner:focus {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.search-buttons {
  display: flex;
  gap: 10px;
}

.search-btn, .reset-btn {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.search-btn:hover, .reset-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 表格样式 */
.user-table {
  border-radius: 8px;
  overflow: hidden;
}

.user-table .el-table__header-wrapper th {
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  color: #2c3e50;
  font-weight: bold;
  border-bottom: 2px solid #ebeef5;
}

.user-table .el-table__row {
  transition: all 0.3s ease;
}

.user-table .el-table__row:hover {
  background-color: #f0f9ff !important;
  transform: scale(1.01);
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.edit-btn, .history-btn, .delete-btn {
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.edit-btn:hover, .history-btn:hover, .delete-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 分页样式 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding: 20px;
}

.pagination .el-pagination__total,
.pagination .el-pagination__sizes,
.pagination .el-pagination__jump {
  color: #2c3e50;
}

/* 对话框样式 */
.user-dialog .el-dialog__header {
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  border-bottom: 1px solid #ebeef5;
  padding: 20px;
  border-radius: 12px 12px 0 0;
}

.user-dialog .el-dialog__title {
  color: #2c3e50;
  font-size: 18px;
  font-weight: bold;
}

.user-dialog .el-dialog__body {
  padding: 20px;
}

.user-form .el-form-item__label {
  color: #2c3e50;
  font-weight: bold;
}

.form-input .el-input__inner {
  border-radius: 8px;
  border: 1px solid #dcdfe6;
  transition: all 0.3s ease;
}

.form-input .el-input__inner:focus {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px;
  border-top: 1px solid #ebeef5;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  border-radius: 0 0 12px 12px;
}

.cancel-btn, .save-btn {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.cancel-btn:hover, .save-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 历史对话框样式 */
.history-dialog .el-dialog__header {
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  border-bottom: 1px solid #ebeef5;
  padding: 20px;
  border-radius: 12px 12px 0 0;
}

.history-search-section {
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 20px;
}

.history-table {
  border-radius: 8px;
  overflow: hidden;
}

.field-tag {
  border-radius: 6px;
  font-weight: 500;
}

.restore-btn {
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.restore-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-management {
    padding: 10px;
  }
  
  .page-header {
    flex-direction: column;
    text-align: center;
  }
  
  .header-content {
    flex-direction: column;
    gap: 15px;
  }
  
  .ai-active-stats {
    flex-direction: column;
    gap: 20px;
  }
  
  .search-form {
    flex-direction: column;
  }
  
  .search-buttons {
    flex-direction: column;
    gap: 10px;
  }
  
  .action-buttons {
    flex-direction: column;
    gap: 5px;
  }
  
  .user-table {
    font-size: 12px;
  }
}

/* 动画效果 */
.user-management-card {
  transition: all 0.3s ease;
}

.user-management-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.ai-active-analysis-card {
  transition: all 0.3s ease;
}

.ai-active-analysis-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.3);
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

.ai-active-analysis-card {
  background: #fafdff;
}
.ai-active-stats > div {
  min-width: 120px;
  text-align: center;
}
.ai-active-conclusion {
  font-size: 15px;
  background: #f4f8fb;
  border-radius: 6px;
  padding: 8px 16px;
  display: flex;
  align-items: center;
}
.trend-icon {
  margin-right: 6px;
  font-size: 20px;
  vertical-align: middle;
  transition: transform 0.5s cubic-bezier(0.4,0,0.2,1);
}
.trend-icon.up {
  color: #409eff;
  animation: trend-bounce 1.2s infinite alternate;
}
.trend-icon.flat {
  color: #67c23a;
  animation: trend-fade 1.2s infinite alternate;
}
.trend-icon.down {
  color: #e6a23c;
  animation: trend-rotate 1.2s infinite linear;
}
@keyframes trend-bounce {
  0% { transform: translateY(0); }
  100% { transform: translateY(-6px); }
}
@keyframes trend-fade {
  0% { opacity: 1; }
  100% { opacity: 0.5; }
}
@keyframes trend-rotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
