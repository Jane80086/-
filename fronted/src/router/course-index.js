// course-index.js
// 统一管理 course 相关的所有路由跳转
import { useRouter } from 'vue-router'

export function useCourseRouter() {
  const router = useRouter()

  // 跳转到课程列表
  const goCourseList = () => router.push('/user/course')

  // 跳转到课程详情
  const goCourseDetail = (courseId) => router.push(`/user/course/${courseId}`)

  // 跳转到课程播放页
  const goCoursePlay = (courseId) => router.push(`/user/course/${courseId}/play`)

  // 跳转到我的课程
  const goMyCourses = () => router.push('/user/my-courses')

  // 跳转到课程编辑页
  const goCourseEdit = (courseId) => router.push(`/user/course/${courseId}/edit`)

  // 跳转到课程创建页
  const goCourseCreate = () => router.push('/user/course/create')

  return {
    goCourseList,
    goCourseDetail,
    goCoursePlay,
    goMyCourses,
    goCourseEdit,
    goCourseCreate
  }
} 