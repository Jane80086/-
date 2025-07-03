import request from './index'
export const login = (data) => request.post('/api/auth/login', data)
export const register = (data) => request.post('/api/auth/register', data) 