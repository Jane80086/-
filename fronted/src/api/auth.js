import request from '@/utils/request'
export const login = (data) => request.post('/api/login', data)
export const register = (data) => request.post('/api/register', data)
export const logout = () => request.post('/api/auth/logout') 