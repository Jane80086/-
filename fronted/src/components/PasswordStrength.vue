<template>
  <div>
    <el-input v-model="password" placeholder="请输入密码" @input="checkStrength" />
    <div>密码强度：<span :style="{color: color}">{{ strength }}</span></div>
  </div>
</template>
<script setup>
import { ref } from 'vue'
const password = ref('')
const strength = ref('弱')
const color = ref('red')
function checkStrength() {
  if (password.value.length < 6) {
    strength.value = '弱'
    color.value = 'red'
  } else if (/\d/.test(password.value) && /[a-zA-Z]/.test(password.value)) {
    strength.value = '中'
    color.value = 'orange'
  } else if (password.value.length >= 10 && /\d/.test(password.value) && /[a-zA-Z]/.test(password.value) && /[^a-zA-Z0-9]/.test(password.value)) {
    strength.value = '强'
    color.value = 'green'
  } else {
    strength.value = '弱'
    color.value = 'red'
  }
}
</script> 