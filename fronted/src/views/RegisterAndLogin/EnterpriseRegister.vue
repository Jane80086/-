<template>
  <div class="enterprise-register">
    <el-card>
      <template #header>
        <span>企业注册</span>
      </template>
      <el-form ref="enterpriseFormRef" :model="enterpriseForm" :rules="enterpriseRules" label-width="150px" class="enterprise-form">
        <el-form-item label="企业ID" prop="enterpriseId">
          <el-input v-model="enterpriseForm.enterpriseId" :disabled="true" />
        </el-form-item>
        <el-form-item label="企业名称" prop="enterpriseName">
          <el-input v-model="enterpriseForm.enterpriseName" placeholder="请输入企业名称" />
        </el-form-item>
        <el-form-item label="企业类型" prop="enterpriseType">
          <el-select v-model="enterpriseForm.enterpriseType" placeholder="请选择企业类型" style="width: 100%">
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
        <el-form-item label="法定代表人" prop="legalRepresentative">
          <el-input v-model="enterpriseForm.legalRepresentative" placeholder="请输入法定代表人" />
        </el-form-item>
        <el-form-item label="注册资本" prop="registeredCapital">
          <el-input v-model="enterpriseForm.registeredCapital" placeholder="请输入注册资本" />
        </el-form-item>
        <el-form-item label="注册地址" prop="registerAddress">
          <el-input v-model="enterpriseForm.registerAddress" placeholder="请输入注册地址" />
        </el-form-item>
        <el-form-item label="成立日期" prop="establishmentDate">
          <el-date-picker v-model="enterpriseForm.establishmentDate" type="date" placeholder="选择成立日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="统一社会信用代码" prop="creditCode">
          <el-input v-model="enterpriseForm.creditCode" placeholder="请输入统一社会信用代码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import api from '@/api/register';

const route = useRoute();
const router = useRouter();
const enterpriseFormRef = ref(null);
const enterpriseForm = reactive({
  enterpriseId: '',
  enterpriseName: '',
  enterpriseType: '',
  legalRepresentative: '',
  registeredCapital: '',
  registerAddress: '',
  establishmentDate: '',
  creditCode: ''
});

const enterpriseRules = {
  enterpriseName: [
    { required: true, message: '请输入企业名称', trigger: 'blur' }
  ],
  enterpriseType: [
    { required: true, message: '请选择企业类型', trigger: 'change' }
  ],
  legalRepresentative: [
    { required: true, message: '请输入法定代表人', trigger: 'blur' }
  ],
  registeredCapital: [
    { required: true, message: '请输入注册资本', trigger: 'blur' }
  ],
  registerAddress: [
    { required: true, message: '请输入注册地址', trigger: 'blur' }
  ],
  establishmentDate: [
    { required: true, message: '请选择成立日期', trigger: 'change' }
  ],
  creditCode: [
    { required: true, message: '请输入统一社会信用代码', trigger: 'blur' }
  ]
};

onMounted(() => {
  if (route.query.enterpriseId) {
    enterpriseForm.enterpriseId = route.query.enterpriseId;
  }
});

const handleSubmit = async () => {
  try {
    await enterpriseFormRef.value.validate();
    
    // 准备提交的数据，确保字段名称与后端实体类匹配
    const submitData = {
      enterpriseId: enterpriseForm.enterpriseId || String(Date.now()),
      enterpriseName: enterpriseForm.enterpriseName,
      enterpriseType: enterpriseForm.enterpriseType,
      legalRepresentative: enterpriseForm.legalRepresentative,
      registeredCapital: enterpriseForm.registeredCapital,
      registerAddress: enterpriseForm.registerAddress,
      creditCode: enterpriseForm.creditCode,
      // 将字符串日期转换为后端期望的格式 - 添加时间组件以兼容LocalDateTime
      establishmentDate: enterpriseForm.establishmentDate ? `${enterpriseForm.establishmentDate}T00:00:00` : null,
      // 设置默认值
      businessScope: '',
      businessTerm: '',
      registrationAuthority: '',
      enterpriseStatus: '1'
    };
    
    console.log('提交企业注册数据:', submitData);
    
    const res = await api.enterprise.createEnterprise(submitData);
    console.log('企业注册响应:', res);
    
    if (res.data && res.data.success) {
      ElMessage.success('企业注册成功，请继续注册企业用户');
      // 跳转到企业用户注册页面，并传递企业ID
      router.push({ 
        path: '/register/enterprise', 
        query: { enterpriseId: submitData.enterpriseId } 
      });
    } else {
      ElMessage.error(res.data?.message || '企业注册失败');
    }
  } catch (error) {
    console.error('企业注册错误:', error);
    console.error('错误详情:', error.response?.data);
    ElMessage.error(error.response?.data?.message || error.message || '企业注册失败');
  }
};
</script>

<style scoped>
.enterprise-register {
  max-width: 500px;
  margin: 40px auto;
  background: #f8fafd;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  padding: 32px 24px;
}

.el-card {
  border-radius: 12px;
  box-shadow: none;
  background: #fff;
}

.enterprise-form .el-form-item {
  margin-bottom: 22px;
}

.enterprise-form .el-form-item__label {
  font-weight: bold;
  color: #333;
  font-size: 15px;
  letter-spacing: 1px;
  white-space: nowrap;
}

.el-input, .el-select, .el-date-picker {
  border-radius: 6px;
}

.el-button[type='primary'] {
  width: 100%;
  border-radius: 6px;
  font-size: 16px;
  letter-spacing: 2px;
  background: linear-gradient(90deg, #4f8cff 0%, #2355e6 100%);
  border: none;
}

.el-form-item:last-child {
  margin-top: 30px;
  text-align: center;
}
</style> 