<template>
  <div class="role-management">
    <h1>角色权限管理</h1>
    <el-card>
      <div style="margin-bottom: 16px;">
        <el-button type="primary" @click="showAddDialog = true">新增角色</el-button>
      </div>
      <el-table :data="userList" border style="width: 100%">
        <el-table-column prop="username" label="用户账号" width="160" />
        <el-table-column prop="realName" label="名称" width="120" />
        <el-table-column prop="userType" label="用户类型" width="160">
          <template #default="{ row }">
            <el-select v-model="row.userType" placeholder="请选择用户类型" @change="val => handleUserTypeChange(row, val)">
              <el-option label="管理员" value="ADMIN" />
              <el-option label="企业用户" value="ENTERPRISE" />
              <el-option label="普通用户" value="NORMAL" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="saveUserType(row)">保存</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { roleAPI } from '@/api/role.js'
import { userAPI } from '@/api/index.js'

const userList = ref([])
const roleList = ref([])
const showAddDialog = ref(false)
const isEdit = ref(false)
const roleForm = reactive({ id: '', roleName: '', description: '' })
const showPermissionDialog = ref(false)
const permissionList = ref([])
const checkedPermissionIds = ref([])
const currentRoleId = ref(null)
const permissionTree = ref(null)

const fetchRoles = async () => {
  const res = await roleAPI.getRoles()
  roleList.value = res.data || []
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(roleForm, row)
  showAddDialog.value = true
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定要删除角色 "${row.roleName}" 吗？`, '提示', { type: 'warning' })
  await roleAPI.deleteRole(row.id)
  ElMessage.success('删除成功')
  fetchRoles()
}

const handleSubmit = async () => {
  if (isEdit.value) {
    await roleAPI.updateRole(roleForm.id, roleForm)
    ElMessage.success('编辑成功')
  } else {
    await roleAPI.createRole(roleForm)
    ElMessage.success('新增成功')
  }
  showAddDialog.value = false
  fetchRoles()
}

const handlePermission = async (row) => {
  currentRoleId.value = row.id
  // 获取所有权限（可扩展为API）
  permissionList.value = [
    { id: 1, permissionName: '用户管理' },
    { id: 2, permissionName: '角色管理' },
    { id: 3, permissionName: '数据统计' }
  ]
  // 获取当前角色已分配权限
  const res = await roleAPI.getRolePermissions(row.id)
  checkedPermissionIds.value = res.data || []
  showPermissionDialog.value = true
}

const handleAssignPermissions = async () => {
  const checked = permissionTree.value.getCheckedKeys()
  await roleAPI.assignPermissions(currentRoleId.value, checked)
  ElMessage.success('权限分配成功')
  showPermissionDialog.value = false
}

const fetchUsers = async () => {
  const res = await userAPI.getUsers({})
  userList.value = res.data.records || res.data || []
}

const handleUserTypeChange = (row, val) => {
  row.userType = val
}
const saveUserType = async (row) => {
  await userAPI.updateUser(row.id, row)
  ElMessage.success('用户类型已更新')
  fetchUsers()
}

onMounted(() => {
  fetchRoles()
  fetchUsers()
})
</script>

<style scoped>
.role-management {
  padding: 24px;
}
</style> 