<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
// 修正：引入 adminEditNews 以匹配管理员权限
import { getNewsDetail, adminEditNews } from '@/api/news'
import { ElMessage } from 'element-plus'

const route = useRoute()
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
const submitLoading = ref(false)
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

const loadNewsDetail = async () => {
  loading.value = true
  try {
    const newsId = route.params.id
    const data = await getNewsDetail(newsId)
    newsForm.value = {
      title: data.title,
      image: data.image || '',
      content: data.content,
      summary: data.summary,
      author: data.author
    }
  } catch (error) {
    ElMessage.error('加载动态详情失败')
    router.back()
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    submitLoading.value = true
    const newsId = route.params.id
    // 修正：调用 adminEditNews 接口
    await adminEditNews(newsId, newsForm.value)
    ElMessage.success('编辑成功')
    // 修正：跳转到管理员动态管理页面
    router.push('/admin/news-manage')
  } catch (error) {
    if (error && error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('编辑失败')
    }
  } finally {
    submitLoading.value = false
  }
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  loadNewsDetail()
})
</script>
<template>
  <div class="news-edit" v-loading="loading">
    <el-card>
      <template #header>
        <div class="header-actions">
          <el-button @click="goBack" :icon="'ArrowLeft'">返回</el-button>
          <span>编辑动态</span>
        </div>
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
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            保存修改
          </el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>
<style scoped>
.news-edit {
  padding: 20px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>