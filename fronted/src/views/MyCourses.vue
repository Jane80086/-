<template>
  <div class="my-courses-page">
    <h2>我的课程</h2>
    <el-tabs v-model="tab">
      <el-tab-pane label="我发布的" name="published">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="course in published" :key="course.id">
            <CourseCard :course="course" />
            <div style="margin-top:8px;">
              <el-button size="small" @click="edit(course.id)">编辑</el-button>
              <el-button size="small" type="danger" @click="remove(course.id)">删除</el-button>
            </div>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane label="我收藏的" name="collected">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="course in collected" :key="course.id">
            <CourseCard :course="course" />
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane label="我点赞的" name="liked">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="course in liked" :key="course.id">
            <CourseCard :course="course" />
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import CourseCard from '../components/CourseCard.vue'

const tab = ref('published')
const published = ref([])
const collected = ref([])
const liked = ref([])
const router = useRouter()

const fetchMyCourses = async () => {
  const res = await axios.get('/api/course/my')
  published.value = res.data.data.published || []
  collected.value = res.data.data.collected || []
  liked.value = res.data.data.liked || []
}
const edit = (id) => router.push(`/course/${id}/edit`)
const remove = async (id) => {
  await axios.delete(`/api/course/${id}`)
  fetchMyCourses()
}
onMounted(fetchMyCourses)
</script>

<style scoped>
.my-courses-page { padding: 32px; background: #f5f5f5; }
</style> 