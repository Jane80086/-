<template>
  <div style="padding: 20px;">
    <h1>企业测试页面</h1>
    <p>如果你能看到这个页面，说明路由正常工作。</p>
    <p>当前路由: {{ $route.path }}</p>
    <p>用户角色: {{ userStore.user?.role }}</p>
    <div style="margin: 20px 0;">
      <el-button @click="$router.push('/enterprise/courses')" type="primary">跳转到课程搜索</el-button>
      <el-button @click="$router.push('/enterprise/news')" type="success">跳转到新闻</el-button>
      <el-button @click="$router.push('/enterprise/meeting')" type="warning">跳转到会议</el-button>
      <el-button @click="$router.push('/enterprise/settings')" type="info">跳转到设置</el-button>
    </div>
    <div style="margin: 20px 0;">
      <h3>所有企业路由:</h3>
      <ul>
        <li v-for="route in enterpriseRoutes" :key="route.path">
          {{ route.path }} - {{ route.name }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const enterpriseRoutes = computed(() => {
  return router.getRoutes().filter(r => r.path.startsWith('/enterprise'))
})
</script> 