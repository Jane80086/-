import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      // 管理端审核接口，必须优先
      '/api/admin': {
        target: 'http://localhost:8081',
        changeOrigin: true
      },
      // 课程模块
      '/api/course': {
        target: 'http://localhost:8081',
        changeOrigin: true
      },
      // AI问答接口
      '/api/ai-questions': {
        target: 'http://localhost:8090',
        changeOrigin: true
      },
      // 会议主模块（包括文件操作）
      '/api/meeting': {
        target: 'http://localhost:8082',
        changeOrigin: true
      },
      // 会议模块的文件接口
      '/api/file': {
        target: 'http://localhost:8081',
        changeOrigin: true
      },
      // 搜索相关接口
      '/api/search': {
        target: 'http://localhost:8081',
        changeOrigin: true
      },
      // 登录、个人中心
      '/api/user': {
        target: 'http://localhost:8084',
        changeOrigin: true
      },
      // 认证相关接口
      '/api/auth': {
        target: 'http://localhost:8084',
        changeOrigin: true
      },
      // 登录接口
      '/api/login': {
        target: 'http://localhost:8084',
        changeOrigin: true
      },
      // AI智能问答接口
      '/ai': {
        target: 'http://localhost:8090',
        changeOrigin: true
      },
      // 新闻模块
      '/api/news': {
        target: 'http://localhost:8083',
        changeOrigin: true
      },
      '/api/enterprise/news': {
        target: 'http://localhost:8083',
        changeOrigin: true
      },
      '/api/admin/news': {
        target: 'http://localhost:8083',
        changeOrigin: true
      },
      // 通用 /api 兜底，必须放最后
      '/api': {
        target: 'http://localhost:8084',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api/, '/api')
      }
    }
  }
}) 
