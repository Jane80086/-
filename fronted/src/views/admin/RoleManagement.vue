<template>
  <div class="role-management">
    <h1>角色权限管理</h1>
    <el-card>
      <div style="margin-bottom: 16px;">
        <el-button type="primary" @click="showAddDialog = true">新增角色</el-button>
      </div>
      <el-table :data="roleList" border style="width: 100%">
        <el-table-column prop="id" label="角色ID" width="100" />
        <el-table-column prop="roleName" label="角色名称" width="200" />
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="300">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="info" @click="handlePermission(row)">分配权限</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑角色对话框 -->
    <el-dialog v-model="showAddDialog" :title="isEdit ? '编辑角色' : '新增角色'">
      <el-form :model="roleForm" label-width="100px">
        <el-form-item label="角色名称">
          <el-input v-model="roleForm.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="roleForm.description" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配权限对话框 -->
    <el-dialog v-model="showPermissionDialog" title="分配权限">
      <el-tree
        :data="permissionList"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedPermissionIds"
        :props="{ label: 'permissionName', children: 'children' }"
        ref="permissionTree"
        style="max-height: 400px; overflow: auto;"
      />
      <template #footer>
        <el-button @click="showPermissionDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAssignPermissions">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { roleAPI } from '@/api/role.js'

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

onMounted(() => {
  fetchRoles()
})
</script>

<style scoped>
.role-management {
  padding: 24px;
}
</style> 