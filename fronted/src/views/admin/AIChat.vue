<template>
  <div
    class="ai-chat-modal"
    v-if="visible"
    :style="{ top: modalTop + 'px', right: modalRight + 'px' }"
    ref="modalRef"
  >
    <div class="ai-chat-header" @mousedown="startDrag">
      <span>AI助手</span>
      <button class="close-btn" @click="$emit('close')">×</button>
    </div>
    <div class="ai-chat-body">
      <div v-for="(msg, idx) in messages" :key="idx" :class="['msg', msg.role]">
        <span v-if="msg.role==='user'">🧑: </span>
        <span v-else>🤖: </span>
        <span>{{ msg.content }}</span>
      </div>
      <div v-if="loading" class="msg ai">🤖: 正在思考...</div>
    </div>
    <div class="ai-chat-footer">
      <input v-model="input" @keyup.enter="send" placeholder="请输入你的问题..." :disabled="loading" />
      <button @click="send" :disabled="loading || !input">发送</button>
    </div>
  </div>
</template>

<script>
import { askAI } from '@/api/index.js';
import { ref, onMounted, onBeforeUnmount } from 'vue';
export default {
  name: 'AIChat',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  setup(props, { emit }) {
    const input = ref('');
    const messages = ref([]);
    const loading = ref(false);
    // 拖拽相关
    const modalTop = ref(window.innerHeight - 500); // 初始bottom: 100px, height: 400px
    const modalRight = ref(40);
    const dragging = ref(false);
    const dragStart = ref({ x: 0, y: 0, top: 0, right: 0 });
    const modalRef = ref(null);

    const send = async () => {
      if (!input.value.trim()) return;
      const question = input.value.trim();
      messages.value.push({ role: 'user', content: question });
      input.value = '';
      loading.value = true;
      try {
        const res = await askAI(question);
        messages.value.push({ role: 'ai', content: res.answer || 'AI无回复' });
      } catch (e) {
        messages.value.push({ role: 'ai', content: 'AI服务暂时不可用' });
      }
      loading.value = false;
      setTimeout(() => {
        const body = modalRef.value?.querySelector('.ai-chat-body');
        if (body) body.scrollTop = body.scrollHeight;
      }, 0);
    };

    // 拖拽事件
    const startDrag = (e) => {
      dragging.value = true;
      dragStart.value = {
        x: e.clientX,
        y: e.clientY,
        top: modalTop.value,
        right: modalRight.value
      };
      document.addEventListener('mousemove', onDrag);
      document.addEventListener('mouseup', stopDrag);
    };
    const onDrag = (e) => {
      if (!dragging.value) return;
      const deltaX = e.clientX - dragStart.value.x;
      const deltaY = e.clientY - dragStart.value.y;
      // 只允许在窗口内拖动
      let newTop = dragStart.value.top + deltaY;
      let newRight = dragStart.value.right - deltaX;
      // 限制范围
      const minTop = 0;
      const maxTop = window.innerHeight - 400; // modal高度
      const minRight = 0;
      const maxRight = window.innerWidth - 340; // modal宽度
      modalTop.value = Math.min(Math.max(newTop, minTop), maxTop);
      modalRight.value = Math.min(Math.max(newRight, minRight), maxRight);
    };
    const stopDrag = () => {
      dragging.value = false;
      document.removeEventListener('mousemove', onDrag);
      document.removeEventListener('mouseup', stopDrag);
    };
    onMounted(() => {
      // 重置位置
      modalTop.value = window.innerHeight - 500;
      modalRight.value = 40;
    });
    onBeforeUnmount(() => {
      document.removeEventListener('mousemove', onDrag);
      document.removeEventListener('mouseup', stopDrag);
    });

    return {
      input,
      messages,
      loading,
      send,
      modalTop,
      modalRight,
      startDrag,
      modalRef
    };
  }
};
</script>

<style scoped>
.ai-chat-modal {
  position: fixed;
  width: 340px;
  height: 400px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 16px rgba(0,0,0,0.18);
  z-index: 9999;
  display: flex;
  flex-direction: column;
  /* top/right 由js动态控制 */
}
.ai-chat-header {
  padding: 12px 16px;
  border-bottom: 1px solid #eee;
  font-weight: bold;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: move;
  user-select: none;
}
.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
}
.ai-chat-body {
  flex: 1;
  padding: 12px 16px;
  overflow-y: auto;
  max-height: 320px;
}
.msg {
  margin-bottom: 10px;
  word-break: break-all;
}
.msg.user {
  text-align: right;
  color: #409eff;
}
.msg.ai {
  text-align: left;
  color: #67c23a;
}
.ai-chat-footer {
  padding: 10px 16px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 8px;
}
.ai-chat-footer input {
  flex: 1;
  padding: 6px 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.ai-chat-footer button {
  padding: 6px 16px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.ai-chat-footer button:disabled {
  background: #ccc;
  cursor: not-allowed;
}
</style> 