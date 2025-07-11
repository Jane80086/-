<template>
  <div class="user-role-management">
    <h1>用户角色分配</h1>
    <el-card>
      <el-table :data="userList" border style="width: 100%">
        <el-table-column prop="id" label="用户ID" width="100" />
        <el-table-column prop="username" label="账号" width="150" />
        <el-table-column prop="realName" label="真实姓名" width="150" />
        <el-table-column prop="userType" label="用户类型" width="120" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleAssignRole(row)">分配角色</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 分配角色对话框 -->
    <el-dialog v-model="showRoleDialog" title="分配角色">
      <el-checkbox-group v-model="checkedRoleIds">
        <el-checkbox v-for="role in roleList" :key="role.id" :label="role.id">{{ role.roleName }}</el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="showRoleDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSaveRoles">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { userRoleAPI } from '@/api/userRole.js'
import { roleAPI } from '@/api/role.js'
import { userAPI } from '@/api/index.js'

const userList = ref([])
const roleList = ref([])
const showRoleDialog = ref(false)
const checkedRoleIds = ref([])
const currentUserId = ref(null)

const fetchUsers = async () => {
  const res = await userAPI.getUsers({ page: 1, pageSize: 100 })
  userList.value = res.data.records || []
}

const fetchRoles = async () => {
  const res = await roleAPI.getRoles()
  roleList.value = res.data || []
}

const handleAssignRole = async (row) => {
  currentUserId.value = row.id
  const res = await userRoleAPI.getUserRoles(row.id)
  checkedRoleIds.value = res.data || []
  showRoleDialog.value = true
}

const handleSaveRoles = async () => {
  await userRoleAPI.assignRoles(currentUserId.value, checkedRoleIds.value)
  ElMessage.success('分配成功')
  showRoleDialog.value = false
}

onMounted(() => {
  fetchUsers()
  fetchRoles()
})
</script>

<style scoped>
.user-role-management {
  padding: 24px;
}
</style> 