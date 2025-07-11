<template>
  <div class="role-selection-container">
    <div class="role-selection-box">
      <div class="role-selection-header">
        <h2>选择用户类型</h2>
        <p>请选择您要注册的用户类型</p>
      </div>
      
      <div class="role-cards">
        <div 
          class="role-card" 
          :class="{ active: selectedRole === 'normal' }"
          @click="selectRole('normal')"
        >
          <div class="role-icon">
            <el-icon size="48" color="#409EFF">
              <UserFilled />
            </el-icon>
          </div>
          <div class="role-content">
            <h3>普通用户</h3>
            <p>个人用户，可以浏览课程、参与会议、查看新闻等基础功能</p>
            <ul class="role-features">
              <li>浏览课程内容</li>
              <li>参与在线会议</li>
              <li>查看新闻动态</li>
              <li>个人资料管理</li>
            </ul>
          </div>
        </div>
        
        <div 
          class="role-card" 
          :class="{ active: selectedRole === 'enterprise' }"
          @click="selectRole('enterprise')"
        >
          <div class="role-icon">
            <el-icon size="48" color="#67C23A">
              <OfficeBuilding />
            </el-icon>
          </div>
          <div class="role-content">
            <h3>企业用户</h3>
            <p>企业用户，可以发布课程、创建会议、管理企业内容等高级功能</p>
            <ul class="role-features">
              <li>发布和管理课程</li>
              <li>创建企业会议</li>
              <li>发布企业新闻</li>
              <li>企业员工管理</li>
            </ul>
          </div>
        </div>
        
        <div 
          class="role-card" 
          :class="{ active: selectedRole === 'admin' }"
          @click="selectRole('admin')"
        >
          <div class="role-icon">
            <el-icon size="48" color="#E6A23C">
              <Setting />
            </el-icon>
          </div>
          <div class="role-content">
            <h3>管理员</h3>
            <p>系统管理员，拥有最高权限，可以管理所有用户和内容</p>
            <ul class="role-features">
              <li>用户权限管理</li>
              <li>内容审核管理</li>
              <li>系统设置管理</li>
              <li>数据统计分析</li>
            </ul>
          </div>
        </div>
      </div>
      
      <div class="role-actions">
        <el-button 
          type="primary" 
          size="large"
          :disabled="!selectedRole"
          @click="handleContinue"
        >
          继续注册
        </el-button>
        <el-button 
          size="large"
          @click="$router.push('/login')"
        >
          返回登录
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { UserFilled, OfficeBuilding, Setting } from '@element-plus/icons-vue'

const router = useRouter()
const selectedRole = ref('')

const selectRole = (role) => {
  selectedRole.value = role
}

const handleContinue = () => {
  if (!selectedRole.value) {
    return
  }
  
  // 根据选择的角色跳转到对应的注册页面
  switch (selectedRole.value) {
    case 'normal':
      router.push('/register/normal')
      break
    case 'enterprise':
      router.push('/register/enterprise')
      break
    case 'admin':
      router.push('/register/admin')
      break
  }
}
</script>

<style scoped>
.role-selection-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.role-selection-box {
  width: 900px;
  max-width: 100%;
  padding: 40px;
  background: white;
  border-radius: 15px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.role-selection-header {
  text-align: center;
  margin-bottom: 40px;
}

.role-selection-header h2 {
  color: #333;
  margin-bottom: 10px;
  font-size: 32px;
  font-weight: 600;
}

.role-selection-header p {
  color: #666;
  font-size: 16px;
}

.role-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.role-card {
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  padding: 24px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fafbfc;
}

.role-card:hover {
  border-color: #409eff;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(64, 158, 255, 0.15);
}

.role-card.active {
  border-color: #409eff;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  box-shadow: 0 8px 25px rgba(64, 158, 255, 0.2);
}

.role-icon {
  text-align: center;
  margin-bottom: 20px;
}

.role-content h3 {
  color: #333;
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 12px;
  text-align: center;
}

.role-content p {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 16px;
  text-align: center;
}

.role-features {
  list-style: none;
  padding: 0;
  margin: 0;
}

.role-features li {
  color: #555;
  font-size: 13px;
  line-height: 1.5;
  margin-bottom: 6px;
  padding-left: 16px;
  position: relative;
}

.role-features li::before {
  content: "✓";
  position: absolute;
  left: 0;
  color: #67c23a;
  font-weight: bold;
}

.role-actions {
  text-align: center;
  display: flex;
  gap: 16px;
  justify-content: center;
}

.role-actions .el-button {
  min-width: 120px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
}

@media (max-width: 768px) {
  .role-selection-box {
    width: 100%;
    padding: 20px;
  }
  
  .role-cards {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .role-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .role-actions .el-button {
    width: 100%;
    max-width: 200px;
  }
}
</style> 