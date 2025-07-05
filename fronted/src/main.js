import { createApp } from 'vue'
import App from './App.vue'
import pinia from './store'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import router from './router'
import { useUserStore } from '@/store/user'

const app = createApp(App)
app.use(pinia)
app.use(ElementPlus)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
app.use(router)
// 初始化用户信息，防止刷新丢失角色
useUserStore().initUser()
app.mount('#app') 