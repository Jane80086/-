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
      '/api/course': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api\/course/, '/api/course')
      },
      '/api/user': {
        target: 'http://localhost:8084',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api\/user/, '/api/user')
      },
      '/api/auth': {
        target: 'http://localhost:8084',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api\/auth/, '/api/auth')
      },
      '/api/login': {
        target: 'http://localhost:8084',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api\/login/, '/api/login')
      },
      '/api/register': {
        target: 'http://localhost:8084',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api\/register/, '/api/register')
      },
      '/api/industry': {
        target: 'http://localhost:8083',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api\/industry/, '/api/industry')
      },
      // 其它 /api 路径默认走 8081
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api/, '/api')
      }
    }
  }
}) 
