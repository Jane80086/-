<template>
  <el-card>
    <div>昵称：{{ user.nickname }}</div>
    <div>手机号：{{ user.phone }}</div>
    <div>邮箱：{{ user.email }}</div>
    <div>性别：{{ user.gender }}</div>
    <div>注册时间：{{ user.createdAt }}</div>
    <el-button @click="edit">编辑资料</el-button>
    <el-button @click="changePassword">修改密码</el-button>
    <el-button @click="bindThird">绑定第三方</el-button>
  </el-card>
</template>
<script setup>
import { useUserStore } from '@/store/user'
const user = useUserStore().user
function edit() {}
function changePassword() {}
function bindThird() {}
</script> 