<template>
  <div class="tab-bar">
    <router-link
      v-for="tab in tabs"
      :key="tab.path"
      :to="tab.path"
      class="tab-item"
      :class="{ active: $route.path === tab.path }"
    >
      <span class="tab-icon">{{ tab.icon }}</span>
      <span class="tab-label">{{ tab.label }}</span>
    </router-link>
  </div>
</template>

<script setup>
const tabs = [
  { path: '/', label: '首页', icon: '🏠' },
  { path: '/shops', label: '点餐', icon: '🍔' },
  { path: '/delegation', label: '委托', icon: '📋' },
  { path: '/ai-agent', label: 'AI助手', icon: '🤖' },
  { path: '/profile', label: '我的', icon: '👤' }
]
</script>

<style scoped>
.tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  background: white;
  border-top: 1px solid var(--border-color);
  padding: 8px 0;
  z-index: 1000;
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  color: var(--text-secondary);
  transition: color 0.3s;
}

.tab-item.active {
  color: var(--primary-color);
}

.tab-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.tab-label {
  font-size: 12px;
}
</style>

