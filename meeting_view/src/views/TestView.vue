<script setup>
import { ref, onMounted } from 'vue';
import meetingService from '../services/meetingService';

const testResults = ref([]);
const isLoading = ref(false);

const addResult = (title, data) => {
  testResults.value.push({
    title,
    data: JSON.stringify(data, null, 2),
    timestamp: new Date().toLocaleString()
  });
};

const testLogin = async () => {
  try {
    isLoading.value = true;
    addResult('测试登录 - admin', '开始测试管理员登录...');
    
    const response = await meetingService.login({
      username: 'admin',
      password: '123456'
    });
    
    addResult('登录响应', response.data);
    
    if (response.data.code === 200) {
      localStorage.setItem('token', response.data.data.token);
      localStorage.setItem('user', JSON.stringify(response.data.data.user));
      addResult('登录成功', 'Token已保存到localStorage');
    }
  } catch (err) {
    addResult('登录失败', err.response?.data || err.message);
  } finally {
    isLoading.value = false;
  }
};

const testUserInfo = async () => {
  try {
    isLoading.value = true;
    addResult('测试获取用户信息', '开始获取用户信息...');
    
    const response = await meetingService.getUserInfo();
    addResult('用户信息响应', response.data);
    
    if (response.data.code === 200) {
      const user = response.data.data;
      addResult('用户类型', user.userType);
      addResult('用户名', user.username);
      addResult('真实姓名', user.realName);
    }
  } catch (err) {
    addResult('获取用户信息失败', err.response?.data || err.message);
  } finally {
    isLoading.value = false;
  }
};

const testMeetings = async () => {
  try {
    isLoading.value = true;
    addResult('测试获取会议列表', '开始获取会议列表...');
    
    const response = await meetingService.getMeetings({
      page: 1,
      size: 10
    });
    
    addResult('会议列表响应', response.data);
    
    if (response.data.code === 200) {
      const meetings = response.data.data.meetings || [];
      addResult('会议数量', meetings.length);
      addResult('会议列表', meetings);
    }
  } catch (err) {
    addResult('获取会议列表失败', err.response?.data || err.message);
  } finally {
    isLoading.value = false;
  }
};

const testMeetingDetail = async () => {
  try {
    isLoading.value = true;
    addResult('测试获取会议详情', '开始获取会议详情...');
    
    // 先获取会议列表，然后使用第一个会议的ID
    const meetingsResponse = await meetingService.getMeetings({
      page: 1,
      size: 1
    });
    
    if (meetingsResponse.data.code === 200 && meetingsResponse.data.data.meetings.length > 0) {
      const firstMeetingId = meetingsResponse.data.data.meetings[0].id;
      addResult('使用会议ID', firstMeetingId);
      
      const response = await meetingService.getMeetingDetail(firstMeetingId);
      addResult('会议详情响应', response.data);
      
      if (response.data.code === 200) {
        const meeting = response.data.data;
        addResult('会议详情', meeting);
      }
    } else {
      addResult('会议详情测试失败', '没有找到可用的会议');
    }
  } catch (err) {
    addResult('获取会议详情失败', err.response?.data || err.message);
  } finally {
    isLoading.value = false;
  }
};

const testDataCheck = async () => {
  try {
    isLoading.value = true;
    addResult('测试数据检查', '开始检查数据库数据...');
    
    const response = await fetch('http://localhost:8080/api/test/check-data', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    });
    
    const data = await response.json();
    addResult('数据检查响应', data);
    
    if (data.code === 200) {
      addResult('Admin用户类型', data.data.adminUserType);
      addResult('Admin真实姓名', data.data.adminRealName);
      addResult('第一个会议名称', data.data.meetingName);
      addResult('第一个会议内容', data.data.meetingContent);
    }
  } catch (err) {
    addResult('数据检查失败', err.message);
  } finally {
    isLoading.value = false;
  }
};

const testDatabase = async () => {
  try {
    isLoading.value = true;
    addResult('测试数据库连接', '开始测试数据库连接...');
    
    const response = await fetch('http://localhost:8080/api/test/database', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    });
    
    const data = await response.json();
    addResult('数据库测试响应', data);
    
    if (data.code === 200) {
      addResult('用户数量', data.data.userCount);
      addResult('会议数量', data.data.meetingCount);
    }
  } catch (err) {
    addResult('数据库测试失败', err.message);
  } finally {
    isLoading.value = false;
  }
};

const testUserPermissions = async () => {
  try {
    isLoading.value = true;
    addResult('测试不同用户权限', '开始测试不同用户类型的权限...');
    
    // 测试普通用户登录
    const normalUserResponse = await meetingService.login({
      username: 'user1',
      password: '123456'
    });
    
    if (normalUserResponse.data.code === 200) {
      localStorage.setItem('token', normalUserResponse.data.data.token);
      addResult('普通用户登录成功', normalUserResponse.data.data.user);
      
      // 获取普通用户的会议列表
      const normalUserMeetings = await meetingService.getMeetings({
        page: 1,
        size: 10
      });
      
      addResult('普通用户会议列表', normalUserMeetings.data);
      
      // 恢复admin用户登录
      const adminResponse = await meetingService.login({
        username: 'admin',
        password: '123456'
      });
      
      if (adminResponse.data.code === 200) {
        localStorage.setItem('token', adminResponse.data.data.token);
        addResult('恢复管理员登录', '已切换回管理员账户');
      }
    }
  } catch (err) {
    addResult('权限测试失败', err.response?.data || err.message);
  } finally {
    isLoading.value = false;
  }
};

const testEnterpriseUserPermissions = async () => {
  try {
    isLoading.value = true;
    addResult('测试企业用户权限', '开始测试企业用户类型的权限...');
    
    // 测试企业用户登录
    const enterpriseResponse = await meetingService.login({
      username: 'enterprise1',
      password: '123456'
    });
    
    if (enterpriseResponse.data.code === 200) {
      localStorage.setItem('token', enterpriseResponse.data.data.token);
      addResult('企业用户登录成功', enterpriseResponse.data.data.user);
      
      // 获取企业用户的会议列表
      const enterpriseMeetings = await meetingService.getMeetings({
        page: 1,
        size: 10
      });
      
      addResult('企业用户会议列表', enterpriseMeetings.data);
      
      // 恢复admin用户登录
      const adminResponse = await meetingService.login({
        username: 'admin',
        password: '123456'
      });
      
      if (adminResponse.data.code === 200) {
        localStorage.setItem('token', adminResponse.data.data.token);
        addResult('恢复管理员登录', '已切换回管理员账户');
      }
    }
  } catch (err) {
    addResult('企业用户权限测试失败', err.response?.data || err.message);
  } finally {
    isLoading.value = false;
  }
};

const clearResults = () => {
  testResults.value = [];
};

const runAllTests = async () => {
  clearResults();
  await testLogin();
  await testUserInfo();
  await testMeetings();
  await testMeetingDetail();
  await testDataCheck();
  await testDatabase();
  await testUserPermissions();
  await testEnterpriseUserPermissions();
};

onMounted(() => {
  addResult('测试页面加载', '测试页面已准备就绪');
});
</script>

<template>
  <div class="test-page">
    <h1>API 测试页面</h1>
    
    <div class="test-controls">
      <button @click="testLogin" :disabled="isLoading">测试登录</button>
      <button @click="testUserInfo" :disabled="isLoading">测试用户信息</button>
      <button @click="testMeetings" :disabled="isLoading">测试会议列表</button>
      <button @click="testMeetingDetail" :disabled="isLoading">测试会议详情</button>
      <button @click="testDataCheck" :disabled="isLoading">数据检查</button>
      <button @click="testDatabase" :disabled="isLoading">数据库测试</button>
      <button @click="testUserPermissions" :disabled="isLoading">测试不同用户权限</button>
      <button @click="testEnterpriseUserPermissions" :disabled="isLoading">测试企业用户权限</button>
      <button @click="runAllTests" :disabled="isLoading">运行所有测试</button>
      <button @click="clearResults">清空结果</button>
    </div>
    
    <div v-if="isLoading" class="loading">
      测试中...
    </div>
    
    <div class="test-results">
      <h2>测试结果</h2>
      <div v-for="(result, index) in testResults" :key="index" class="result-item">
        <h3>{{ result.title }}</h3>
        <div class="timestamp">{{ result.timestamp }}</div>
        <pre class="result-data">{{ result.data }}</pre>
      </div>
    </div>
  </div>
</template>

<style scoped>
.test-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.test-controls {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.test-controls button {
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.test-controls button:hover:not(:disabled) {
  background-color: #0056b3;
}

.test-controls button:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
}

.loading {
  text-align: center;
  padding: 20px;
  font-size: 18px;
  color: #007bff;
}

.test-results {
  margin-top: 30px;
}

.result-item {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #f8f9fa;
}

.result-item h3 {
  margin: 0 0 10px 0;
  color: #333;
}

.timestamp {
  font-size: 12px;
  color: #666;
  margin-bottom: 10px;
}

.result-data {
  background-color: #f1f3f4;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
  white-space: pre-wrap;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  max-height: 300px;
  overflow-y: auto;
}
</style> 