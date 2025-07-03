<template>
  <el-container style="height: 100vh">
    <el-aside width="200px">
      <el-menu :default-active="$route.path" router>
        <el-menu-item index="/admin/dashboard">数据看板</el-menu-item>
        <el-menu-item index="/admin/users">用户管理</el-menu-item>
        <el-menu-item index="/admin/roles">角色权限</el-menu-item>
        <el-menu-item index="/admin/news">动态审核</el-menu-item>
        <el-menu-item index="/admin/courses">课程审核</el-menu-item>
        <el-menu-item index="/admin/meetings">会议审核</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div style="float:right">{{ user.nickname }}（超级管理员）</div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>
<script setup>
import { useUserStore } from '@/store/user'
const user = useUserStore().user
</script> 