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
      '/api/file': {
        target: 'http://localhost:8081',
        changeOrigin: true
      },
      '/api/course': {
        target: 'http://localhost:8081',
        changeOrigin: true
      },
      '/course': {
        target: 'http://localhost:8081/api',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/course/, '/course')
      },
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api/, '/api')
      }
    }
  }
}) 