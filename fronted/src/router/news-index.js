// 新闻相关路由配置
export const newsRoutes = [
    // 普通用户新闻路由
        {
        path: '/user/news-list',
                    name: 'UserNewsList',
                    component: () => import('@/views/user/NewsList.vue'),
        meta: { title: '行业动态' }
                },
                {
        path: '/user/news-detail/:id',
                    name: 'UserNewsDetail',
                    component: () => import('@/views/user/NewsDetail.vue'),
        meta: { title: '动态详情' }
        },
    // 企业用户新闻路由
        {
        path: '/enterprise/news-publish',
                    name: 'EnterpriseNewsPublish',
                    component: () => import('@/views/enterprise/NewsPublish.vue'),
        meta: { title: '发布动态' }
                },
                {
        path: '/enterprise/my-news',
                    name: 'EnterpriseMyNews',
                    component: () => import('@/views/enterprise/MyNews.vue'),
        meta: { title: '我的动态' }
                },
                {
        path: '/enterprise/news-edit/:id',
                    name: 'EnterpriseNewsEdit',
                    component: () => import('@/views/enterprise/NewsEdit.vue'),
        meta: { title: '编辑动态' }
                },
                {
        path: '/enterprise/news-list',
                    name: 'EnterpriseNewsList',
                    component: () => import('@/views/enterprise/NewsList.vue'),
        meta: { title: '行业动态' }
                },
                {
        path: '/enterprise/news-detail/:id',
                    name: 'EnterpriseNewsDetail',
                    component: () => import('@/views/enterprise/NewsDetail.vue'),
        meta: { title: '动态详情' }
        },
    // 管理员新闻路由
        {
        path: '/admin/news-audit',
                    name: 'AdminNewsAudit',
                    component: () => import('@/views/admin/NewsAudit.vue'),
        meta: { title: '动态审核' }
                },
                {
        path: '/admin/news-manage',
                    name: 'AdminNewsManage',
        component: () => import('@/views/admin/NewsManagement.vue'),
        meta: { title: '动态管理' }
                },
                {
        path: '/admin/news-statistics',
        name: 'AdminNewsStatistics',
                    component: () => import('@/views/admin/NewsStatistics.vue'),
        meta: { title: '数据统计' }
                },
                {
        path: '/admin/news-publish',
                    name: 'AdminNewsPublish',
                    component: () => import('@/views/admin/NewsPublish.vue'),
        meta: { title: '发布动态' }
                },
                {
        path: '/admin/news-detail/:id',
                    name: 'AdminNewsDetail',
                    component: () => import('@/views/admin/NewsDetail.vue'),
        meta: { title: '动态详情' }
                },
                {
        path: '/admin/news-edit/:id',
                    name: 'AdminNewsEdit',
                    component: () => import('@/views/admin/NewsEdit.vue'),
        meta: { title: '编辑动态' }
        }
    ]
