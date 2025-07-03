<template>
  <div class="placeholder-page">
    <div class="page-header">
      <h1>{{ pageTitle }}</h1>
      <p>{{ pageDescription }}</p>
    </div>

    <el-card>
      <template #header>
        <span>{{ cardTitle }}</span>
      </template>
      
      <div class="placeholder-content">
        <el-empty :description="emptyDescription">
          <el-button type="primary" @click="handleAction">
            {{ actionText }}
          </el-button>
        </el-empty>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()

// 根据路由名称生成页面内容
const pageConfig = computed(() => {
  const configs = {
    'EnterpriseTraining': {
      title: '培训计划',
      description: '管理企业培训计划和学习路径',
      cardTitle: '培训计划列表',
      emptyDescription: '暂无培训计划',
      actionText: '创建培训计划'
    },
    'EnterpriseCourses': {
      title: '企业课程',
      description: '管理企业专属课程',
      cardTitle: '企业课程列表',
      emptyDescription: '暂无企业课程',
      actionText: '创建课程'
    },
    'EnterpriseCreateCourse': {
      title: '创建课程',
      description: '为企业创建专属课程',
      cardTitle: '课程信息',
      emptyDescription: '课程创建功能开发中',
      actionText: '返回课程列表'
    },
    'EnterpriseAnalytics': {
      title: '学习分析',
      description: '企业学习数据分析',
      cardTitle: '分析报告',
      emptyDescription: '数据分析功能开发中',
      actionText: '生成报告'
    },
    'EnterpriseReports': {
      title: '学习报告',
      description: '查看企业学习报告',
      cardTitle: '报告列表',
      emptyDescription: '暂无学习报告',
      actionText: '生成报告'
    },
    'EnterprisePerformance': {
      title: '绩效分析',
      description: '员工学习绩效分析',
      cardTitle: '绩效数据',
      emptyDescription: '绩效分析功能开发中',
      actionText: '查看详情'
    },
    'EnterpriseAIInsights': {
      title: 'AI洞察',
      description: 'AI驱动的学习洞察',
      cardTitle: '智能洞察',
      emptyDescription: 'AI分析功能开发中',
      actionText: '刷新洞察'
    },
    'EnterpriseSettings': {
      title: '企业配置',
      description: '企业账户配置管理',
      cardTitle: '配置选项',
      emptyDescription: '配置功能开发中',
      actionText: '保存配置'
    },
    'EnterpriseBilling': {
      title: '账单管理',
      description: '企业账户账单管理',
      cardTitle: '账单信息',
      emptyDescription: '账单功能开发中',
      actionText: '查看账单'
    },
    'EnterpriseSupport': {
      title: '企业支持',
      description: '企业客户支持服务',
      cardTitle: '支持服务',
      emptyDescription: '支持功能开发中',
      actionText: '联系支持'
    },
    'AdminUserAnalytics': {
      title: '用户分析',
      description: '系统用户数据分析',
      cardTitle: '分析数据',
      emptyDescription: '用户分析功能开发中',
      actionText: '生成报告'
    },
    'AdminPermissions': {
      title: '权限管理',
      description: '系统权限配置管理',
      cardTitle: '权限列表',
      emptyDescription: '权限管理功能开发中',
      actionText: '配置权限'
    },
    'AdminCourseAnalytics': {
      title: '课程分析',
      description: '课程数据分析',
      cardTitle: '课程统计',
      emptyDescription: '课程分析功能开发中',
      actionText: '查看统计'
    },
    'AdminCategories': {
      title: '分类管理',
      description: '课程分类管理',
      cardTitle: '分类列表',
      emptyDescription: '分类管理功能开发中',
      actionText: '添加分类'
    },
    'AdminSystemHealth': {
      title: '系统健康',
      description: '系统运行状态监控',
      cardTitle: '系统状态',
      emptyDescription: '系统监控功能开发中',
      actionText: '刷新状态'
    },
    'AdminPerformance': {
      title: '性能监控',
      description: '系统性能监控',
      cardTitle: '性能指标',
      emptyDescription: '性能监控功能开发中',
      actionText: '查看详情'
    },
    'AdminLogs': {
      title: '系统日志',
      description: '系统运行日志查看',
      cardTitle: '日志列表',
      emptyDescription: '日志功能开发中',
      actionText: '查看日志'
    },
    'AdminSettings': {
      title: '系统设置',
      description: '系统配置管理',
      cardTitle: '系统配置',
      emptyDescription: '系统设置功能开发中',
      actionText: '保存设置'
    },
    'AdminAIConfig': {
      title: 'AI配置',
      description: 'AI服务配置管理',
      cardTitle: 'AI配置',
      emptyDescription: 'AI配置功能开发中',
      actionText: '保存配置'
    },
    'AdminBackup': {
      title: '数据备份',
      description: '系统数据备份管理',
      cardTitle: '备份管理',
      emptyDescription: '备份功能开发中',
      actionText: '创建备份'
    },
    'CourseSearch': {
      title: '课程搜索',
      description: '搜索和筛选课程',
      cardTitle: '搜索结果',
      emptyDescription: '搜索功能开发中',
      actionText: '重新搜索'
    },
    'Personalized': {
      title: '个性化课程',
      description: '基于个人偏好的课程推荐',
      cardTitle: '个性化推荐',
      emptyDescription: '个性化功能开发中',
      actionText: '刷新推荐'
    },
    'Trending': {
      title: '热门趋势',
      description: '当前热门和趋势课程',
      cardTitle: '趋势课程',
      emptyDescription: '趋势分析功能开发中',
      actionText: '查看热门'
    }
  }
  
  return configs[route.name] || {
    title: '页面开发中',
    description: '该页面正在开发中',
    cardTitle: '开发状态',
    emptyDescription: '功能开发中，敬请期待',
    actionText: '返回首页'
  }
})

const pageTitle = computed(() => pageConfig.value.title)
const pageDescription = computed(() => pageConfig.value.description)
const cardTitle = computed(() => pageConfig.value.cardTitle)
const emptyDescription = computed(() => pageConfig.value.emptyDescription)
const actionText = computed(() => pageConfig.value.actionText)

const handleAction = () => {
  ElMessage.info(`${actionText.value}功能开发中`)
}
</script>

<style scoped>
.placeholder-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  text-align: center;
}

.page-header h1 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 28px;
}

.page-header p {
  margin: 0;
  color: #666;
  font-size: 16px;
}

.placeholder-content {
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style> 