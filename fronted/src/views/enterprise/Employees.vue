<template>
  <div class="enterprise-employees">
    <div class="page-header">
      <h1>员工管理</h1>
      <p>管理企业员工信息和学习情况</p>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>员工列表</span>
          <el-button type="primary" @click="addEmployee">
            <el-icon><Plus /></el-icon>
            添加员工
          </el-button>
        </div>
      </template>

      <el-table :data="employees" style="width: 100%">
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="department" label="部门" />
        <el-table-column prop="position" label="职位" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="enrolledCourses" label="已报名课程" />
        <el-table-column prop="completionRate" label="完成率">
          <template #default="scope">
            <el-progress :percentage="scope.row.completionRate" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="viewEmployee(scope.row)">查看</el-button>
            <el-button size="small" @click="editEmployee(scope.row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const employees = ref([
  {
    id: 1,
    name: '张三',
    department: '技术部',
    position: '前端工程师',
    email: 'zhangsan@company.com',
    enrolledCourses: 5,
    completionRate: 80
  },
  {
    id: 2,
    name: '李四',
    department: '产品部',
    position: '产品经理',
    email: 'lisi@company.com',
    enrolledCourses: 3,
    completionRate: 60
  }
])

const addEmployee = () => {
  ElMessage.info('添加员工功能')
}

const viewEmployee = (employee) => {
  ElMessage.info(`查看员工：${employee.name}`)
}

const editEmployee = (employee) => {
  ElMessage.info(`编辑员工：${employee.name}`)
}
</script>

<style scoped>
.enterprise-employees {
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 