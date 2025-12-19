<template>
  <div class="profile">
    <div class="profile-header">
      <div class="avatar-section">
        <div class="avatar">{{ userInfo.nickname.charAt(0) || 'U' }}</div>
        <div class="user-info">
          <h3>{{ userInfo.nickname || '未登录' }}</h3>
          <p class="user-phone">{{ userInfo.phone || '点击登录' }}</p>
        </div>
      </div>
      <button v-if="!isLoggedIn" class="btn btn-primary" @click="showLogin = true">
        登录
      </button>
    </div>

    <div class="menu-list">
      <div class="menu-section">
        <div class="menu-item" @click="$router.push('/order')">
          <span class="menu-icon">📦</span>
          <span class="menu-label">我的订单</span>
          <span class="menu-arrow">></span>
        </div>
        <div class="menu-item" @click="$router.push('/delegation')">
          <span class="menu-icon">📋</span>
          <span class="menu-label">我的委托</span>
          <span class="menu-arrow">></span>
        </div>
      </div>

      <div class="menu-section">
        <div class="menu-item" @click="editAddress">
          <span class="menu-icon">📍</span>
          <span class="menu-label">收货地址</span>
          <span class="menu-arrow">></span>
        </div>
        <div class="menu-item" @click="showSettings">
          <span class="menu-icon">⚙️</span>
          <span class="menu-label">设置</span>
          <span class="menu-arrow">></span>
        </div>
      </div>

      <div class="menu-section">
        <div class="menu-item" @click="showAbout">
          <span class="menu-icon">ℹ️</span>
          <span class="menu-label">关于我们</span>
          <span class="menu-arrow">></span>
        </div>
      </div>

      <div v-if="isLoggedIn" class="menu-section">
        <div class="menu-item logout" @click="handleLogout">
          <span class="menu-label">退出登录</span>
        </div>
      </div>
    </div>

    <!-- 登录弹窗 -->
    <div v-if="showLogin" class="modal" @click="showLogin = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>登录</h3>
          <span @click="showLogin = false">✕</span>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>手机号</label>
            <input v-model="loginForm.phone" type="tel" placeholder="请输入手机号" />
          </div>
          <div class="form-group">
            <label>验证码</label>
            <div class="code-input">
              <input v-model="loginForm.code" type="text" placeholder="请输入验证码" />
              <button class="btn btn-outline" @click="sendCode" :disabled="sendingCode">
                {{ sendingCode ? '发送中...' : '发送验证码' }}
              </button>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-outline" @click="showLogin = false">取消</button>
          <button class="btn btn-primary" @click="handleLogin">登录</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import { userApi } from '../utils/request'

const userStore = useUserStore()
const showLogin = ref(false)
const sendingCode = ref(false)

const userInfo = ref({
  nickname: userStore.userInfo.nickname || '用户',
  phone: userStore.userInfo.phone || ''
})

const isLoggedIn = ref(userStore.isLoggedIn)

const loginForm = ref({
  phone: '',
  code: ''
})

onMounted(async () => {
  // 如果已登录，获取用户信息
  if (userStore.isLoggedIn) {
    await loadUserInfo()
  }
})

async function loadUserInfo() {
  try {
    const response = await userApi.getUserInfo()
    if (response.code === 200 && response.data) {
      userStore.updateUserInfo(response.data)
      userInfo.value = {
        nickname: response.data.nickname || '用户',
        phone: response.data.phone || ''
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    // 如果token失效，清除登录状态
    if (error.message.includes('401') || error.message.includes('未授权')) {
      userStore.logout()
      isLoggedIn.value = false
    }
  }
}

function editAddress() {
  alert('地址管理功能开发中')
}

function showSettings() {
  alert('设置功能开发中')
}

function showAbout() {
  alert('校园外卖系统 v1.0\n\n一个便捷的校园外卖点餐平台')
}

async function sendCode() {
  if (!loginForm.value.phone) {
    alert('请输入手机号')
    return
  }
  if (!/^1[3-9]\d{9}$/.test(loginForm.value.phone)) {
    alert('请输入正确的手机号')
    return
  }
  
  try {
    sendingCode.value = true
    const response = await userApi.sendCode({ phone: loginForm.value.phone })
    if (response.code === 200) {
      alert('验证码已发送')
    } else {
      alert(response.message || '发送失败')
    }
  } catch (error) {
    console.error('发送验证码失败:', error)
    alert(error.message || '发送失败，请稍后重试')
  } finally {
    sendingCode.value = false
  }
}

async function handleLogin() {
  if (!loginForm.value.phone || !loginForm.value.code) {
    alert('请填写完整信息')
    return
  }

  try {
    const response = await userApi.login({
      phone: loginForm.value.phone,
      code: loginForm.value.code
    })
    
    if (response.code === 200 && response.data) {
      // 保存token
      if (response.data.token) {
        localStorage.setItem('token', response.data.token)
      }
      
      // 更新用户信息
      if (response.data.userInfo) {
        userStore.login(response.data.userInfo)
        userInfo.value = {
          nickname: response.data.userInfo.nickname || '用户',
          phone: response.data.userInfo.phone || ''
        }
        isLoggedIn.value = true
        showLogin.value = false
        loginForm.value = { phone: '', code: '' }
      }
    } else {
      alert(response.message || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error)
    alert(error.message || '登录失败，请稍后重试')
  }
}

function handleLogout() {
  if (confirm('确定要退出登录吗？')) {
    localStorage.removeItem('token')
    userStore.logout()
    userInfo.value = {
      nickname: '用户',
      phone: ''
    }
    isLoggedIn.value = false
  }
}
</script>

<style scoped>
.profile {
  min-height: 100vh;
  background: var(--bg-light);
}

.profile-header {
  background: linear-gradient(135deg, var(--primary-color) 0%, #06ad56 100%);
  padding: 32px 16px;
  color: white;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 600;
}

.user-info h3 {
  font-size: 20px;
  margin-bottom: 4px;
}

.user-phone {
  font-size: 14px;
  opacity: 0.9;
}

.profile-header .btn {
  margin-top: 16px;
}

.menu-list {
  margin-top: 12px;
}

.menu-section {
  background: white;
  margin-bottom: 12px;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid var(--border-color);
  cursor: pointer;
  transition: background 0.2s;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:active {
  background: var(--bg-light);
}

.menu-item.logout {
  justify-content: center;
  color: var(--secondary-color);
}

.menu-icon {
  font-size: 20px;
  margin-right: 12px;
  width: 24px;
  text-align: center;
}

.menu-label {
  flex: 1;
  font-size: 16px;
}

.menu-arrow {
  color: var(--text-light);
  font-size: 14px;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1001;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-content {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 400px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid var(--border-color);
}

.modal-header h3 {
  font-size: 18px;
  font-weight: 600;
}

.modal-header span {
  font-size: 24px;
  color: var(--text-light);
  cursor: pointer;
}

.modal-body {
  padding: 16px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 14px;
}

.code-input {
  display: flex;
  gap: 8px;
}

.code-input input {
  flex: 1;
}

.code-input .btn {
  white-space: nowrap;
  padding: 10px 16px;
}

.modal-footer {
  display: flex;
  gap: 12px;
  padding: 16px;
  border-top: 1px solid var(--border-color);
}

.modal-footer .btn {
  flex: 1;
}
</style>

