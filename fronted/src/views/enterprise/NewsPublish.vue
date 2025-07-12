<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { publishNews, refineContent } from '@/api/news'
import { ElMessage } from 'element-plus'
// 确保 Element Plus 图标已全局注册或在此处按需导入，例如：
// import { MagicStick } from '@element-plus/icons-vue'

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
const refineLoadingContent = ref(false) // 内容润色加载状态
// const refineLoadingSummary = ref(false) // 移除：简介润色加载状态

// --- 新增：预览功能相关的状态 ---
const dialogVisible = ref(false) // 控制预览弹窗的显示
const previewContent = ref('') // 存储待预览的润色内容
const currentRefineField = ref('') // 记录当前正在润色的字段 ('content' 或 'summary')
// --- 新增结束 ---

const rules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 1, max: 100, message: '标题长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入内容', trigger: 'blur' }
  ],
  summary: [
    { required: true, message: '请输入简介', trigger: 'blur' },
    { max: 200, message: '简介不能超过200个字符', trigger: 'blur' }
  ],
  author: [
    { required: true, message: '请输入作者', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true

    await publishNews(newsForm.value)
    ElMessage.success('发布成功，等待审核')
    router.push('/enterprise/news/my')
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('发布失败，请重试')
    }
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  formRef.value.resetFields()
}

/**
 * 处理内容润色请求
 * @param {string} fieldName - 要润色的字段名 ('content')
 */
const handleRefine = async (fieldName) => {
  const originalText = newsForm.value[fieldName]

  if (!originalText || originalText.trim() === '') {
    ElMessage.warning('内容不能为空，无法进行润色。')
    return
  }

  if (fieldName === 'content') {
    refineLoadingContent.value = true
  }

  try {
    // 1. 发起请求并等待完整响应
    const response = await refineContent(originalText)

    console.log('润色 API 响应:', response); // 打印整个响应对象

    // 2. 核心修改：检查响应的顶层结构
    // 根据你提供的后端返回，它是一个 Result 对象
    if (response.code === '0' && response.data) {
      // 3. 获取真正的润色数据
      const refineResponse = response.data;
      if (refineResponse.success) {
        previewContent.value = refineResponse.refinedContent;
        currentRefineField.value = fieldName;
        dialogVisible.value = true;
        ElMessage.success('润色成功，请预览！');
      } else {
        ElMessage.error(refineResponse.message || '润色失败，请稍后再试。');
      }
    } else {
      // 如果外层响应不成功
      ElMessage.error(response.msg || '润色请求失败：未收到有效数据');
    }
  } catch (error) {
    ElMessage.error(`润色请求异常: ${error.message || '网络或服务器错误'}`);
  } finally {
    if (fieldName === 'content') {
      refineLoadingContent.value = false;
    }
  }
}

// --- 新增：处理预览弹窗中的确认替换操作 ---
const confirmReplace = () => {
  if (currentRefineField.value) {
    newsForm.value[currentRefineField.value] = previewContent.value; // 确认后替换
    ElMessage.success('内容已替换！');
  }
  dialogVisible.value = false; // 关闭弹窗
  previewContent.value = ''; // 清空预览内容
  currentRefineField.value = ''; // 清空记录的字段
}

// --- 新增：处理预览弹窗中的取消操作 ---
const cancelReplace = () => {
  dialogVisible.value = false; // 关闭弹窗
  previewContent.value = ''; // 清空预览内容
  currentRefineField.value = ''; // 清空记录的字段
  ElMessage.info('已取消替换。');
}
// --- 新增结束 ---

</script>

<template>
  <div class="news-publish">
    <el-card>
      <template #header>
        <span>发布动态</span>
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
          <!-- 移除：简介润色按钮 for 简介 -->
          <!-- <el-button
            type="info"
            size="small"
            @click="handleRefine('summary')"
            :loading="refineLoadingSummary"
            style="margin-top: 10px;"
          >
            <el-icon><MagicStick /></el-icon> 智能润色简介
          </el-button> -->
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <el-input
              v-model="newsForm.content"
              type="textarea"
              :rows="10"
              placeholder="请输入动态内容"
          />
          <!-- 润色按钮 for 内容 -->
          <el-button
              type="info"
              size="small"
              @click="handleRefine('content')"
              :loading="refineLoadingContent"
              style="margin-top: 10px;"
          >
            <el-icon><MagicStick /></el-icon> 智能润色内容
          </el-button>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">
            发布动态
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 新增：润色内容预览弹窗 -->
    <el-dialog
        v-model="dialogVisible"
        title="润色内容预览"
        width="60%"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      <div class="dialog-content">
        <p>以下是智能润色后的内容，请确认是否替换：</p>
        <el-input
            v-model="previewContent"
            type="textarea"
            :rows="15"
            readonly
            class="preview-textarea"
        ></el-input>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelReplace">取消</el-button>
          <el-button type="primary" @click="confirmReplace">
            确认替换
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.news-publish {
  padding: 20px;
}
.dialog-content {
  padding: 10px 0;
}
.preview-textarea {
  margin-top: 10px;
}
</style>
