export const roles = {
  admin: {
    name: '超级管理员',
    permissions: [
      'user:manage', 'role:manage', 'news:publish', 'news:edit', 'news:delete', 'news:audit', 'news:list',
      'course:add', 'course:edit', 'course:delete', 'course:submit', 'course:audit', 'course:list', 'course:recommend',
      'meeting:create', 'meeting:edit', 'meeting:delete', 'meeting:audit', 'meeting:list',
      'dashboard:view'
    ]
  },
  enterprise: {
    name: '企业用户',
    permissions: [
      'news:publish', 'news:edit', 'news:delete', 'news:list',
      'course:add', 'course:edit', 'course:delete', 'course:submit', 'course:list',
      'meeting:create', 'meeting:edit', 'meeting:delete', 'meeting:list'
    ]
  },
  user: {
    name: '普通用户',
    permissions: [
      'news:list', 'news:detail',
      'course:list', 'course:detail', 'course:play', 'course:qna',
      'meeting:list', 'meeting:detail',
      'profile:view', 'profile:edit', 'profile:change-password', 'profile:bind-third'
    ]
  }
} 