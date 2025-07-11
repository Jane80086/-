<template>
  <div class="admin-users">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="main-title">用户管理</h1>
          <p class="subtitle">管理系统中的所有用户，包括普通用户、企业用户和管理员</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" size="large" @click="handleAddUser" class="add-user-btn">
            <el-icon><Plus /></el-icon>
            添加用户
          </el-button>
        </div>
      </div>
    </div>

    <!-- 搜索和过滤 -->
    <el-card class="search-card" shadow="hover">
      <div class="search-header">
        <h3 class="search-title">
          <el-icon><Search /></el-icon>
          搜索用户
        </h3>
      </div>
      <el-row :gutter="20" class="search-form">
        <el-col :span="6">
          <el-input 
            v-model="searchForm.keyword" 
            placeholder="搜索用户名/邮箱" 
            clearable
            @keyup.enter="handleSearch"
            class="search-input"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-select v-model="searchForm.userType" placeholder="用户类型" clearable class="search-select">
            <el-option label="普通用户" value="NORMAL" />
            <el-option label="企业用户" value="ENTERPRISE" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="searchForm.status" placeholder="状态" clearable class="search-select">
            <el-option label="正常" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <div class="search-buttons">
            <el-button type="primary" @click="handleSearch" class="search-btn">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="handleExport" class="export-btn">
              <el-icon><Download /></el-icon>
              导出
            </el-button>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 用户统计 -->
    <el-row :gutter="20" class="stats-section">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon total-users">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ userStats.totalUsers }}</div>
              <div class="stat-label">总用户数</div>
              <div class="stat-trend">
                <el-icon><TrendCharts /></el-icon>
                <span>+12.5%</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon normal-users">
              <el-icon><UserFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ userStats.normalUsers }}</div>
              <div class="stat-label">普通用户</div>
              <div class="stat-trend">
                <el-icon><TrendCharts /></el-icon>
                <span>+8.3%</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon enterprise-users">
              <el-icon><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ userStats.enterpriseUsers }}</div>
              <div class="stat-label">企业用户</div>
              <div class="stat-trend">
                <el-icon><TrendCharts /></el-icon>
                <span>+15.2%</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon admin-users">
              <el-icon><Setting /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ userStats.adminUsers }}</div>
              <div class="stat-label">管理员</div>
              <div class="stat-trend">
                <el-icon><TrendCharts /></el-icon>
                <span>+5.7%</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 用户列表 -->
    <el-card class="user-list-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <h3 class="list-title">
              <el-icon><List /></el-icon>
              用户列表
            </h3>
            <span class="user-count">共 {{ pagination.total }} 个用户</span>
          </div>
          <div class="header-actions">
            <el-button size="small" @click="handleBatchDelete" :disabled="!selectedUsers.length" class="batch-btn">
              <el-icon><Delete /></el-icon>
              批量删除
            </el-button>
            <el-button size="small" @click="handleBatchEnable" :disabled="!selectedUsers.length" class="batch-btn">
              <el-icon><Check /></el-icon>
              批量启用
            </el-button>
            <el-button size="small" @click="handleBatchDisable" :disabled="!selectedUsers.length" class="batch-btn">
              <el-icon><Close /></el-icon>
              批量禁用
            </el-button>
          </div>
        </div>
      </template>

      <el-table 
        :data="userList" 
        style="width: 100%"
        @selection-change="handleSelectionChange"
        v-loading="loading"
        class="user-table"
        stripe
        border
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="头像" width="80">
          <template #default="scope">
            <el-avatar :src="scope.row.avatar || 'https://via.placeholder.com/40x40'" class="user-avatar" />
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column label="用户类型" width="100">
          <template #default="scope">
            <el-tag :type="getUserTypeTag(scope.row.userType)" class="user-type-tag">
              {{ getUserTypeText(scope.row.userType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" class="status-tag">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="注册时间" width="160" />
        <el-table-column prop="lastLoginTime" label="最后登录" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button size="small" @click="handleEditUser(scope.row)" class="edit-btn">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button 
                size="small" 
                :type="scope.row.status === 1 ? 'warning' : 'success'"
                @click="handleToggleStatus(scope.row)"
                class="toggle-btn"
              >
                <el-icon><Switch /></el-icon>
                {{ scope.row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button size="small" type="danger" @click="handleDeleteUser(scope.row)" class="delete-btn">
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

    <!-- 用户编辑对话框 -->
    <el-dialog 
      v-model="userDialog.visible" 
      :title="userDialog.isEdit ? '编辑用户' : '添加用户'"
      width="600px"
      class="user-dialog"
      :close-on-click-modal="false"
    >
      <el-form :model="userForm" :rules="userRules" ref="userFormRef" label-width="100px" class="user-form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名" class="form-input" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" class="form-input" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" class="form-input" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="userForm.status" class="status-radio">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!userDialog.isEdit">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码" class="form-input" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="userDialog.visible = false" class="cancel-btn">取消</el-button>
          <el-button type="primary" @click="handleSaveUser" :loading="userDialog.loading" class="save-btn">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Search, 
  Plus, 
  Download, 
  User, 
  UserFilled, 
  OfficeBuilding, 
  Setting,
  TrendCharts,
  List,
  Delete,
  Check,
  Close,
  Edit,
  Switch
} from '@element-plus/icons-vue'

// 搜索表单
const searchForm = ref({
  keyword: '',
  userType: '',
  status: ''
})

// 用户统计
const userStats = ref({
  totalUsers: 1256,
  normalUsers: 892,
  enterpriseUsers: 234,
  adminUsers: 130
})

// 用户列表
const userList = ref([
  {
    id: 1,
    username: 'admin',
    email: 'admin@example.com',
    phone: '13800138000',
    userType: 'ADMIN',
    status: 1,
    avatar: 'https://via.placeholder.com/40x40',
    createdTime: '2024-01-01 10:00:00',
    lastLoginTime: '2024-07-02 15:30:00'
  },
  {
    id: 2,
    username: 'user001',
    email: 'user001@example.com',
    phone: '13800138001',
    userType: 'NORMAL',
    status: 1,
    avatar: 'https://via.placeholder.com/40x40',
    createdTime: '2024-01-15 14:20:00',
    lastLoginTime: '2024-07-02 12:15:00'
  },
  {
    id: 3,
    username: 'enterprise001',
    email: 'enterprise001@example.com',
    phone: '13800138002',
    userType: 'ENTERPRISE',
    status: 1,
    avatar: 'https://via.placeholder.com/40x40',
    createdTime: '2024-02-01 09:30:00',
    lastLoginTime: '2024-07-02 16:45:00'
  }
])

// 分页
const pagination = ref({
  current: 1,
  size: 20,
  total: 1256
})

// 选中的用户
const selectedUsers = ref([])

// 加载状态
const loading = ref(false)

// 用户对话框
const userDialog = ref({
  visible: false,
  isEdit: false,
  loading: false
})

// 用户表单
const userForm = ref({
  username: '',
  email: '',
  phone: '',
  status: 1,
  password: ''
})

// 表单验证规则
const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const userFormRef = ref()

// 获取用户类型标签
const getUserTypeTag = (userType) => {
  const tagMap = {
    'NORMAL': '',
    'ENTERPRISE': 'warning',
    'ADMIN': 'danger'
  }
  return tagMap[userType] || ''
}

// 获取用户类型文本
const getUserTypeText = (userType) => {
  const textMap = {
    'NORMAL': '普通用户',
    'ENTERPRISE': '企业用户',
    'ADMIN': '管理员'
  }
  return textMap[userType] || '未知'
}

// 搜索
const handleSearch = () => {
  loading.value = true
  // 模拟API调用
  setTimeout(() => {
    loading.value = false
    ElMessage.success('搜索完成')
  }, 1000)
}

// 添加用户
const handleAddUser = () => {
  userDialog.value.isEdit = false
  userDialog.value.visible = true
  userForm.value = {
    username: '',
    email: '',
    phone: '',
    status: 1,
    password: ''
  }
}

// 编辑用户
const handleEditUser = (user) => {
  userDialog.value.isEdit = true
  userDialog.value.visible = true
  userForm.value = { ...user }
}

// 保存用户
const handleSaveUser = async () => {
  try {
    await userFormRef.value.validate()
    userDialog.value.loading = true
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success(userDialog.value.isEdit ? '用户更新成功' : '用户添加成功')
    userDialog.value.visible = false
    handleSearch() // 刷新列表
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    userDialog.value.loading = false
  }
}

// 切换用户状态
const handleToggleStatus = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定要${user.status === 1 ? '禁用' : '启用'}用户 ${user.username} 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    user.status = user.status === 1 ? 0 : 1
    ElMessage.success(`用户${user.status === 1 ? '启用' : '禁用'}成功`)
  } catch (error) {
    // 用户取消操作
  }
}

// 删除用户
const handleDeleteUser = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 ${user.username} 吗？此操作不可恢复！`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    const index = userList.value.findIndex(item => item.id === user.id)
    if (index > -1) {
      userList.value.splice(index, 1)
    }
    
    ElMessage.success('用户删除成功')
  } catch (error) {
    // 用户取消操作
  }
}

// 批量操作
const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

const handleBatchDelete = async () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请选择要删除的用户')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedUsers.value.length} 个用户吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.success('批量删除成功')
    selectedUsers.value = []
  } catch (error) {
    // 用户取消操作
  }
}

const handleBatchEnable = async () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请选择要启用的用户')
    return
  }
  
  selectedUsers.value.forEach(user => user.status = 1)
  ElMessage.success('批量启用成功')
}

const handleBatchDisable = async () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请选择要禁用的用户')
    return
  }
  
  selectedUsers.value.forEach(user => user.status = 0)
  ElMessage.success('批量禁用成功')
}

// 导出
const handleExport = () => {
  ElMessage.success('用户数据导出成功')
}

// 分页
const handleSizeChange = (size) => {
  pagination.value.size = size
  handleSearch()
}

const handleCurrentChange = (current) => {
  pagination.value.current = current
  handleSearch()
}

// 组件挂载时获取数据
onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.admin-users {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.page-header {
  margin-bottom: 20px;
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 10px;
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
  color: #333;
  font-size: 28px;
  font-weight: bold;
}

.subtitle {
  margin: 0;
  color: #666;
  font-size: 16px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.add-user-btn {
  background-color: #409eff;
  border-color: #409eff;
}

.add-user-btn:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

.search-card {
  margin-bottom: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.search-header {
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.search-title {
  margin: 0;
  color: #333;
  font-size: 18px;
  font-weight: bold;
  display: flex;
  align-items: center;
}

.search-title .el-icon {
  margin-right: 8px;
  color: #409eff;
}

.search-form {
  padding: 0 20px 20px;
}

.search-input .el-input__inner {
  border-radius: 6px;
  border-color: #dcdfe6;
}

.search-input .el-input__inner:focus {
  border-color: #409eff;
}

.search-select .el-input__inner {
  border-radius: 6px;
  border-color: #dcdfe6;
}

.search-select .el-input__inner:focus {
  border-color: #409eff;
}

.search-buttons {
  display: flex;
  gap: 10px;
}

.search-btn {
  background-color: #409eff;
  border-color: #409eff;
}

.search-btn:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

.export-btn {
  background-color: #67c23a;
  border-color: #67c23a;
}

.export-btn:hover {
  background-color: #85ce61;
  border-color: #85ce61;
}

.stats-section {
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.stat-card {
  flex: 1;
  min-width: 280px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: space-around;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

  .stat-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 25px 0 rgba(0, 0, 0, 0.15);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }

.stat-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stat-icon {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  font-size: 30px; /* Adjust icon size */
}

.stat-icon .el-icon {
  color: white;
}

.total-users {
  background: linear-gradient(135deg, #409eff, #53a8ff);
}

.normal-users {
  background: linear-gradient(135deg, #67c23a, #85ce61);
}

.enterprise-users {
  background: linear-gradient(135deg, #e6a23c, #eebe77);
}

.admin-users {
  background: linear-gradient(135deg, #f56c6c, #f78989);
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.stat-trend {
  display: flex;
  align-items: center;
  margin-top: 8px;
  font-size: 14px;
  color: #67c23a; /* Green color for trend */
}

.stat-trend .el-icon {
  margin-right: 4px;
  font-size: 16px;
}

.user-list-card {
  margin-bottom: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
}

.header-left {
  display: flex;
  align-items: center;
}

.list-title {
  margin: 0;
  color: #333;
  font-size: 18px;
  font-weight: bold;
  display: flex;
  align-items: center;
}

.list-title .el-icon {
  margin-right: 8px;
  color: #409eff;
}

.user-count {
  margin-left: 15px;
  color: #909399;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.batch-btn {
  background-color: #f56c6c;
  border-color: #f56c6c;
}

.batch-btn:hover {
  background-color: #f78989;
  border-color: #f78989;
}

.user-table {
  border-radius: 0 0 8px 8px;
}

.user-table .el-table__header-wrapper th {
  background-color: #f5f7fa;
  color: #333;
  font-weight: bold;
}

.user-table .el-table__row {
  transition: background-color 0.3s ease;
}

.user-table .el-table__row:hover {
  background-color: #f9fafc;
}

.user-table .el-table__cell {
  padding: 12px 0;
}

.user-table .el-table__cell:first-child {
  padding-left: 20px;
}

.user-table .el-table__cell:last-child {
  padding-right: 20px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 1px solid #ebeef5;
  box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.1);
}

.user-type-tag {
  border-radius: 4px;
  padding: 4px 8px;
  font-size: 13px;
  font-weight: bold;
}

.status-tag {
  border-radius: 4px;
  padding: 4px 8px;
  font-size: 13px;
  font-weight: bold;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding: 0 20px 20px;
}

.pagination .el-pagination__total,
.pagination .el-pagination__sizes,
.pagination .el-pagination__jump {
  color: #333;
}

.pagination .el-pagination__total .el-pagination__text,
.pagination .el-pagination__sizes .el-pagination__text {
  font-weight: bold;
}

.pagination .el-pagination__sizes .el-input .el-input__inner {
  border-radius: 6px;
  border-color: #dcdfe6;
}

.pagination .el-pagination__sizes .el-input .el-input__inner:focus {
  border-color: #409eff;
}

.user-dialog .el-dialog__header {
  background-color: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
  padding: 15px 20px;
  border-radius: 8px 8px 0 0;
}

.user-dialog .el-dialog__title {
  color: #333;
  font-size: 18px;
  font-weight: bold;
}

.user-dialog .el-dialog__body {
  padding: 20px;
}

.user-form .el-form-item__label {
  color: #333;
  font-weight: bold;
}

.user-form .el-input__inner,
.user-form .el-select .el-input__inner,
.user-form .el-radio-group .el-radio {
  border-radius: 6px;
  border-color: #dcdfe6;
}

.user-form .el-input__inner:focus,
.user-form .el-select .el-input__inner:focus,
.user-form .el-radio-group .el-radio:hover {
  border-color: #409eff;
}

.user-form .el-form-item__error {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 15px 20px;
  border-top: 1px solid #ebeef5;
  background-color: #f5f7fa;
  border-radius: 0 0 8px 8px;
}

.cancel-btn {
  background-color: #dcdfe6;
  border-color: #dcdfe6;
}

.cancel-btn:hover {
  background-color: #c0c4cc;
  border-color: #c0c4cc;
}

.save-btn {
  background-color: #409eff;
  border-color: #409eff;
}

.save-btn:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.edit-btn {
  background-color: #67c23a;
  border-color: #67c23a;
}

.edit-btn:hover {
  background-color: #85ce61;
  border-color: #85ce61;
}

.toggle-btn {
  background-color: #e6a23c;
  border-color: #e6a23c;
}

.toggle-btn:hover {
  background-color: #eebe77;
  border-color: #eebe77;
}

.delete-btn {
  background-color: #f56c6c;
  border-color: #f56c6c;
}

  .delete-btn:hover {
    background-color: #f78989;
    border-color: #f78989;
  }

  /* 响应式设计 */
  @media (max-width: 1200px) {
    .stats-section {
      flex-direction: column;
    }
    
    .stat-card {
      min-width: 100%;
      margin-bottom: 15px;
    }
  }

  @media (max-width: 768px) {
    .admin-users {
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

  /* 加载动画 */
  .loading-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(255, 255, 255, 0.8);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 9999;
  }

  /* 表格行悬停效果 */
  .user-table .el-table__row {
    transition: all 0.2s ease;
  }

  .user-table .el-table__row:hover {
    background-color: #f0f9ff !important;
    transform: scale(1.01);
  }

  /* 按钮悬停效果 */
  .search-btn, .export-btn, .add-user-btn, .batch-btn, .edit-btn, .toggle-btn, .delete-btn {
    transition: all 0.2s ease;
  }

  .search-btn:hover, .export-btn:hover, .add-user-btn:hover, .batch-btn:hover, .edit-btn:hover, .toggle-btn:hover, .delete-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }

  /* 卡片悬停效果 */
  .search-card, .user-list-card {
    transition: all 0.3s ease;
  }

  .search-card:hover, .user-list-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  }
</style> 