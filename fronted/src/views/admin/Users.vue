<template>
  <div class="admin-users">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>用户管理</h1>
      <p>管理系统中的所有用户，包括普通用户、企业用户和管理员</p>
    </div>

    <!-- 搜索和过滤 -->
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input 
            v-model="searchForm.keyword" 
            placeholder="搜索用户名/邮箱" 
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-select v-model="searchForm.userType" placeholder="用户类型" clearable>
            <el-option label="普通用户" value="NORMAL" />
            <el-option label="企业用户" value="ENTERPRISE" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="searchForm.status" placeholder="状态" clearable>
            <el-option label="正常" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="success" @click="handleAddUser">
            <el-icon><Plus /></el-icon>
            添加用户
          </el-button>
          <el-button @click="handleExport">
            <el-icon><Download /></el-icon>
            导出
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 用户统计 -->
    <el-row :gutter="20" class="stats-section">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff;">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ userStats.totalUsers }}</div>
              <div class="stat-label">总用户数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a;">
              <el-icon><UserFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ userStats.normalUsers }}</div>
              <div class="stat-label">普通用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c;">
              <el-icon><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ userStats.enterpriseUsers }}</div>
              <div class="stat-label">企业用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f56c6c;">
              <el-icon><Setting /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ userStats.adminUsers }}</div>
              <div class="stat-label">管理员</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 用户列表 -->
    <el-card class="user-list-card">
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <div class="header-actions">
            <el-button size="small" @click="handleBatchDelete" :disabled="!selectedUsers.length">
              批量删除
            </el-button>
            <el-button size="small" @click="handleBatchEnable" :disabled="!selectedUsers.length">
              批量启用
            </el-button>
            <el-button size="small" @click="handleBatchDisable" :disabled="!selectedUsers.length">
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
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="头像" width="80">
          <template #default="scope">
            <el-avatar :src="scope.row.avatar || 'https://via.placeholder.com/40x40'" />
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column label="用户类型" width="100">
          <template #default="scope">
            <el-tag :type="getUserTypeTag(scope.row.userType)">
              {{ getUserTypeText(scope.row.userType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="注册时间" width="160" />
        <el-table-column prop="lastLoginTime" label="最后登录" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleEditUser(scope.row)">
              编辑
            </el-button>
            <el-button 
              size="small" 
              :type="scope.row.status === 1 ? 'warning' : 'success'"
              @click="handleToggleStatus(scope.row)"
            >
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" type="danger" @click="handleDeleteUser(scope.row)">
              删除
            </el-button>
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
        />
      </div>
    </el-card>

    <!-- 用户编辑对话框 -->
    <el-dialog 
      v-model="userDialog.visible" 
      :title="userDialog.isEdit ? '编辑用户' : '添加用户'"
      width="600px"
    >
      <el-form :model="userForm" :rules="userRules" ref="userFormRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="userForm.userType" placeholder="请选择用户类型" style="width: 100%;">
            <el-option label="普通用户" value="NORMAL" />
            <el-option label="企业用户" value="ENTERPRISE" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="userForm.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!userDialog.isEdit">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="userDialog.visible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveUser" :loading="userDialog.loading">
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
  Setting 
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
  userType: 'NORMAL',
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
  userType: [
    { required: true, message: '请选择用户类型', trigger: 'change' }
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
    userType: 'NORMAL',
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
}

.page-header {
  margin-bottom: 20px;
  text-align: center;
}

.page-header h1 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 28px;
}

.page-header p {
  margin: 0;
  color: #666;
  font-size: 16px;
}

.search-card {
  margin-bottom: 20px;
}

.stats-section {
  margin-bottom: 20px;
}

.stat-card {
  height: 100px;
}

.stat-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.stat-icon .el-icon {
  font-size: 24px;
  color: white;
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

.user-list-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style> 