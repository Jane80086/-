import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
 
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/city': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false 
      }
    }
  }
});
