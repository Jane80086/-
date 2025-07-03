<template>
  <el-upload
    class="upload-media"
    drag
    :action="uploadUrl"
    :headers="headers"
    :on-success="handleSuccess"
    :on-error="handleError"
    :show-file-list="false"
    :before-upload="beforeUpload"
    :on-progress="handleProgress"
  >
    <i class="el-icon-upload"></i>
    <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
    <div v-if="progress > 0" class="progress-bar">
      <el-progress :percentage="progress" />
    </div>
    <img v-if="previewUrl" :src="previewUrl" class="preview-img" />
  </el-upload>
</template>

<script setup>
import { ref } from 'vue'
const props = defineProps({
  uploadUrl: { type: String, required: true },
  headers: { type: Object, default: () => ({}) }
})
const emit = defineEmits(['success'])
const progress = ref(0)
const previewUrl = ref('')
const handleSuccess = (res, file) => {
  emit('success', res)
  previewUrl.value = URL.createObjectURL(file.raw)
  progress.value = 0
}
const handleError = () => { progress.value = 0 }
const beforeUpload = () => { progress.value = 0 }
const handleProgress = (event) => { progress.value = Math.round(event.percent) }
</script>

<style scoped>
.upload-media { width: 100%; }
.preview-img { width: 100px; height: 100px; object-fit: cover; margin-top: 8px; border-radius: 6px; }
.progress-bar { margin-top: 8px; }
</style> 