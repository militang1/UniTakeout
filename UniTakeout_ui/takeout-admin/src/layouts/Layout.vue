<template>
  <div class="layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <h2>商家后台</h2>
      </div>
      <nav class="sidebar-nav">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ active: $route.path === item.path }"
        >
          <span class="nav-icon">{{ item.icon }}</span>
          <span class="nav-label">{{ item.label }}</span>
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <div class="user-info">
          <div class="user-avatar">{{ userStore.userInfo.nickname?.charAt(0) || 'U' }}</div>
          <div class="user-details">
            <div class="user-name">{{ userStore.userInfo.nickname || '商家' }}</div>
            <div class="user-phone">{{ userStore.userInfo.phone || '' }}</div>
          </div>
        </div>
        <button class="btn btn-outline logout-btn" @click="handleLogout">退出登录</button>
      </div>
    </aside>
    <main class="main-content">
      <header class="topbar">
        <h3 class="page-title">{{ currentPageTitle }}</h3>
      </header>
      <div class="content-wrapper">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const menuItems = [
  { path: '/dashboard', label: '数据统计', icon: '📊' },
  { path: '/shop', label: '店铺管理', icon: '🏪' },
  { path: '/products', label: '商品管理', icon: '🍔' },
  { path: '/categories', label: '分类管理', icon: '📁' },
  { path: '/orders', label: '订单管理', icon: '📦' }
]

const pageTitleMap = {
  '/dashboard': '数据统计',
  '/shop': '店铺管理',
  '/products': '商品管理',
  '/categories': '分类管理',
  '/orders': '订单管理'
}

const currentPageTitle = computed(() => {
  return pageTitleMap[route.path] || '商家后台'
})

function handleLogout() {
  if (confirm('确定要退出登录吗？')) {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: var(--sidebar-width);
  background: white;
  border-right: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
}

.sidebar-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: var(--primary-color);
}

.sidebar-nav {
  flex: 1;
  padding: 20px 0;
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  color: var(--text-secondary);
  text-decoration: none;
  transition: all 0.3s;
  cursor: pointer;
}

.nav-item:hover {
  background: var(--bg-light);
  color: var(--primary-color);
}

.nav-item.active {
  background: rgba(7, 193, 96, 0.1);
  color: var(--primary-color);
  border-right: 3px solid var(--primary-color);
}

.nav-icon {
  font-size: 18px;
  margin-right: 12px;
  width: 24px;
  text-align: center;
}

.nav-label {
  font-size: 14px;
  font-weight: 500;
}

.sidebar-footer {
  padding: 20px;
  border-top: 1px solid var(--border-color);
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--primary-color);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  margin-right: 12px;
}

.user-details {
  flex: 1;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
}

.user-phone {
  font-size: 12px;
  color: var(--text-light);
}

.logout-btn {
  width: 100%;
  padding: 8px;
  font-size: 14px;
}

.main-content {
  flex: 1;
  margin-left: var(--sidebar-width);
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.topbar {
  background: white;
  padding: 20px 24px;
  border-bottom: 1px solid var(--border-color);
  position: sticky;
  top: 0;
  z-index: 10;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
}

.content-wrapper {
  flex: 1;
  padding: 24px;
  background: var(--bg-light);
}
</style>




