<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { adminPublishNews } from '@/api/news' // 假设这里有一个新的 adminPublishNews API
import { ElMessage } from 'element-plus'

const router = useRouter()

const newsForm = ref({
  title: '',
  image: '',
  content: '',
  summary: '',
  author: ''
})

const formRef = ref()
const loading = ref(false)

const rules = {
  title: [
    {required: true, message: '请输入标题', trigger: 'blur'},
    {min: 1, max: 100, message: '标题长度在 1 到 100 个字符', trigger: 'blur'}
  ],
  content: [
    {required: true, message: '请输入内容', trigger: 'blur'}
  ],
  summary: [
    {required: true, message: '请输入简介', trigger: 'blur'},
    {max: 200, message: '简介不能超过200个字符', trigger: 'blur'}
  ],
  author: [
    {required: true, message: '请输入作者', trigger: 'blur'}
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true

    // 调用管理员发布动态的API，假设这个API会直接将动态设置为已发布状态
    await adminPublishNews(newsForm.value)
    ElMessage.success('动态发布成功！') // 提示发布成功，无需审核
    router.push('/admin/news/') // 发布后跳转到动态管理页面
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('发布失败，请检查网络或表单内容')
    }
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  formRef.value.resetFields()
}
</script>

<template>
  <div class="news-publish">
    <el-card>
      <template #header>
        <span>发布动态 (管理员)</span>
      </template>

      <el-form
          ref="formRef"
          :model="newsForm"
          :rules="rules"
          label-width="100px"
          size="large"
      >
        <el-form-item label="标题" prop="title">
          <el-input
              v-model="newsForm.title"
              placeholder="请输入动态标题"
              maxlength="100"
              show-word-limit
          />
        </el-form-item>

        <el-form-item label="作者" prop="author">
          <el-input
              v-model="newsForm.author"
              placeholder="请输入作者姓名"
              maxlength="50"
          />
        </el-form-item>

        <el-form-item label="图片链接" prop="image">
          <el-input
              v-model="newsForm.image"
              placeholder="请输入图片链接地址"
          />
          <div v-if="newsForm.image" style="margin-top: 10px;">
            <el-image
                :src="newsForm.image"
                style="width: 200px; height: 150px;"
                fit="cover"
                :preview-src-list="[newsForm.image]"
            />
          </div>
        </el-form-item>

        <el-form-item label="简介" prop="summary">
          <el-input
              v-model="newsForm.summary"
              type="textarea"
              :rows="3"
              placeholder="请输入动态简介"
              maxlength="200"
              show-word-limit
          />
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <el-input
              v-model="newsForm.content"
              type="textarea"
              :rows="10"
              placeholder="请输入动态内容"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">
            发布动态
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.news-publish {
  padding: 20px;
}
</style>
