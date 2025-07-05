import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            redirect: '/user/news-list'
        },
        // 普通用户路由
        {
            path: '/user',
            name: 'User',
            children: [
                {
                    path: 'news-list',
                    name: 'NewsList',
                    component: () => import('@/views/user/NewsList.vue'),
                    meta: { title: '行业动态' }
                },
                {
                    path: 'news-detail/:id',
                    name: 'NewsDetail',
                    component: () => import('@/views/user/NewsDetail.vue'),
                    meta: { title: '动态详情' }
                }
            ]
        },
        // 企业用户路由
        {
            path: '/enterprise',
            name: 'Enterprise',
            children: [
                {
                    path: 'news-publish',
                    name: 'NewsPublish',
                    component: () => import('@/views/enterprise/NewsPublish.vue'),
                    meta: { title: '发布动态' }
                },
                {
                    path: 'my-news',
                    name: 'MyNews',
                    component: () => import('@/views/enterprise/MyNews.vue'),
                    meta: { title: '我的动态' }
                },
                {
                    path: 'news-edit/:id',
                    name: 'NewsEdit',
                    component: () => import('@/views/enterprise/NewsEdit.vue'),
                    meta: { title: '编辑动态' }
                },
                {
                    path: 'news-list',
                    name: 'NewsList',
                    component: () => import('@/views/enterprise/NewsList.vue'),
                    meta: { title: '行业动态' }
                },
                {
                    path: 'news-detail/:id',
                    name: 'NewsDetail',
                    component: () => import('@/views/enterprise/NewsDetail.vue'),
                    meta: { title: '动态详情' }
                }
            ]
        },
        // 管理员路由
        {
            path: '/admin',
            name: 'Admin',
            children: [
                {
                    path: 'news-audit',
                    name: 'NewsAudit',
                    component: () => import('@/views/admin/NewsAudit.vue'),
                    meta: { title: '动态审核' }
                },
                {
                    path: 'news-manage',
                    name: 'NewsManage',
                    component: () => import('@/views/admin/NewsManage.vue'),
                    meta: { title: '动态管理' }
                },
                {
                    path: 'statistics',
                    name: 'Statistics',
                    component: () => import('@/views/admin/NewsStatistics.vue'),
                    meta: { title: '数据统计' }
                },
                {
                    path: 'news-publish',
                    name: 'AdminNewsPublish',
                    component: () => import('@/views/admin/NewsPublish.vue'),
                    meta: { title: '发布动态' }
                },
                {
                    path: 'news-detail/:id',
                    name: 'NewsDetail',
                    component: () => import('@/views/admin/NewsDetail.vue'),
                    meta: { title: '动态详情' }
                },
                {
                    path: 'news-edit/:id',
                    name: 'NewsEdit',
                    component: () => import('@/views/admin/NewsEdit.vue'),
                    meta: { title: '编辑动态' }
                }
            ]
        }
    ]
})

export default router