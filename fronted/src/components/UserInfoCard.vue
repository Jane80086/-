<template>
  <el-card>
    <div>昵称：{{ user.nickname }}</div>
    <div>手机号：{{ user.phone }}</div>
    <div>邮箱：{{ user.email }}</div>
    <div>性别：{{ user.gender }}</div>
    <div>注册时间：{{ user.createdAt }}</div>
    <el-button @click="$emit('edit')">编辑资料</el-button>
    <el-button @click="$emit('change-password')">修改密码</el-button>
    <el-button @click="$emit('bind-third')">绑定第三方</el-button>
  </el-card>
</template>
<script setup>
defineProps({ user: Object })
</script> 