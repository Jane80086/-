import request from '@/utils/request'

// 登录接口
export const login = (data) => request.post('/login', data)

// 分离的注册接口
export const adminRegister = (data) => request.post('/auth/admin/register', data)
export const userRegister = (data) => request.post('/auth/user/register', data)
export const enterpriseRegister = (data) => request.post('/auth/enterprise/register', data)

// 兼容旧接口（保留一段时间）
export const register = (data) => {
  // 根据userType自动选择对应的注册接口
  const userType = data.userType?.toLowerCase()
  if (userType === 'admin') {
    return adminRegister(data)
  } else if (userType === 'enterprise') {
    return enterpriseRegister(data)
  } else {
    return userRegister(data)
  }
}

export const logout = () => request.post('/auth/logout') 