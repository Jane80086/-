import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            redirect: '/user/news-list'
        },
        // 普通用户路由 (User Routes)
        {
            path: '/user',
            name: 'UserLayout', // 建议为布局/父路由也使用清晰的名称
            children: [
                {
                    path: 'news-list',
                    // 名称已修正，确保唯一 (Name corrected for uniqueness)
                    name: 'UserNewsList',
                    component: () => import('@/views/user/NewsList.vue'),
                    meta: { title: '行业动态' } // Industry News
                },
                {
                    path: 'news-detail/:id',
                    // 名称已修正，确保唯一 (Name corrected for uniqueness)
                    name: 'UserNewsDetail',
                    component: () => import('@/views/user/NewsDetail.vue'),
                    meta: { title: '动态详情' } // News Details
                }
            ]
        },
        // 企业用户路由 (Enterprise User Routes)
        {
            path: '/enterprise',
            name: 'EnterpriseLayout', // 建议为布局/父路由也使用清晰的名称
            children: [
                {
                    path: 'news-publish',
                    // 名称已修正，确保唯一 (Name corrected for uniqueness)
                    name: 'EnterpriseNewsPublish',
                    component: () => import('@/views/enterprise/NewsPublish.vue'),
                    meta: { title: '发布动态' } // Publish News
                },
                {
                    path: 'my-news',
                    // 名称已修正，确保唯一 (Name corrected for uniqueness)
                    name: 'EnterpriseMyNews',
                    component: () => import('@/views/enterprise/MyNews.vue'),
                    meta: { title: '我的动态' } // My News
                },
                {
                    path: 'news-edit/:id',
                    // 名称已修正，确保唯一 (Name corrected for uniqueness)
                    name: 'EnterpriseNewsEdit',
                    component: () => import('@/views/enterprise/NewsEdit.vue'),
                    meta: { title: '编辑动态' } // Edit News
                },
                {
                    path: 'news-list',
                    // 名称已修正，确保唯一 (Name corrected for uniqueness)
                    name: 'EnterpriseNewsList',
                    component: () => import('@/views/enterprise/NewsList.vue'),
                    meta: { title: '行业动态' } // Industry News
                },
                {
                    path: 'news-detail/:id',
                    // 名称已修正，确保唯一 (Name corrected for uniqueness)
                    name: 'EnterpriseNewsDetail',
                    component: () => import('@/views/enterprise/NewsDetail.vue'),
                    meta: { title: '动态详情' } // News Details
                }
            ]
        },
        // 管理员路由 (Admin Routes)
        {
            path: '/admin',
            name: 'AdminLayout', // 建议为布局/父路由也使用清晰的名称
            children: [
                {
                    path: 'news-audit',
                    // 名称已修正，确保唯一 (Name corrected for uniqueness)
                    name: 'AdminNewsAudit',
                    component: () => import('@/views/admin/NewsAudit.vue'),
                    meta: { title: '动态审核' } // News Audit
                },
                {
                    path: 'news-manage',
                    // 名称已修正，确保唯一 (Name corrected for uniqueness)
                    name: 'AdminNewsManage',
                    component: () => import('@/views/admin/NewsManage.vue'),
                    meta: { title: '动态管理' } // News Management
                },
                {
                    path: 'statistics',
                    // 名称已修正，确保唯一 (Name corrected for uniqueness)
                    name: 'AdminStatistics',
                    component: () => import('@/views/admin/NewsStatistics.vue'),
                    meta: { title: '数据统计' } // Data Statistics
                },
                {
                    path: 'news-publish',
                    // 名称已修正，确保唯一 (Name corrected for uniqueness)
                    name: 'AdminNewsPublish',
                    component: () => import('@/views/admin/NewsPublish.vue'),
                    meta: { title: '发布动态' } // Publish News
                },
                {
                    path: 'news-detail/:id',
                    // 名称已修正，确保唯一 (Name corrected for uniqueness)
                    name: 'AdminNewsDetail',
                    component: () => import('@/views/admin/NewsDetail.vue'),
                    meta: { title: '动态详情' } // News Details
                },
                {
                    path: 'news-edit/:id',
                    // 名称已修正，确保唯一 (Name corrected for uniqueness)
                    name: 'AdminNewsEdit',
                    component: () => import('@/views/admin/NewsEdit.vue'),
                    meta: { title: '编辑动态' } // Edit News
                }
            ]
        }
    ]
})

export default router
