import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref({
    id: null,
    nickname: '',
    avatar: '',
    phone: '',
    address: ''
  })

  const isLoggedIn = ref(false)

  function login(user) {
    userInfo.value = { ...user }
    isLoggedIn.value = true
  }

  function logout() {
    userInfo.value = {
      id: null,
      nickname: '',
      avatar: '',
      phone: '',
      address: ''
    }
    isLoggedIn.value = false
  }

  function updateUserInfo(info) {
    userInfo.value = { ...userInfo.value, ...info }
  }

  return {
    userInfo,
    isLoggedIn,
    login,
    logout,
    updateUserInfo
  }
})

