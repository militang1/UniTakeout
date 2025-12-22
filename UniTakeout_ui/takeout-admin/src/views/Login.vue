<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h1>商家管理后台</h1>
        <p>UniTakeout 校园外卖系统</p>
      </div>
      <div class="login-form">
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
        <button class="btn btn-primary login-btn" @click="handleLogin" :disabled="logging">
          {{ logging ? '登录中...' : '登录' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { authApi } from '../utils/request'

const router = useRouter()
const userStore = useUserStore()

const loginForm = ref({
  phone: '',
  code: ''
})

const sendingCode = ref(false)
const logging = ref(false)

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
    const response = await authApi.sendCode({ phone: loginForm.value.phone })
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
    logging.value = true
    const response = await authApi.login({
      phone: loginForm.value.phone,
      code: loginForm.value.code
    })

    if (response.code === 200 && response.data) {
      if (response.data.token) {
        localStorage.setItem('admin_token', response.data.token)
      }
      if (response.data.userInfo) {
        userStore.login(response.data.userInfo)
      }
      router.push('/')
    } else {
      alert(response.message || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error)
    alert(error.message || '登录失败，请稍后重试')
  } finally {
    logging.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--primary-color) 0%, #06ad56 100%);
}

.login-box {
  width: 400px;
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-header h1 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
  color: var(--text-primary);
}

.login-header p {
  font-size: 14px;
  color: var(--text-secondary);
}

.login-form {
  margin-top: 32px;
}

.code-input {
  display: flex;
  gap: 12px;
}

.code-input input {
  flex: 1;
}

.code-input .btn {
  white-space: nowrap;
  padding: 10px 16px;
}

.login-btn {
  width: 100%;
  padding: 12px;
  margin-top: 8px;
  font-size: 16px;
}
</style>





