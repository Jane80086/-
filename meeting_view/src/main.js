import { createApp } from 'vue';
import App from './App.vue'; 
import router from './router';
import pinia from './stores';
import 'element-plus/dist/index.css';
import 'element-plus/theme-chalk/dark/css-vars.css';

const app = createApp(App);

// 使用插件
app.use(router);
app.use(pinia);

app.mount('#app'); 