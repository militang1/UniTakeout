import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:3000/api',
  timeout: 20000,
  headers: {
    'Content-Type': 'application/json'
  }
})

api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

api.interceptors.response.use(
  response => {
    // 根据API文档，后端返回格式为 { code, message, data }
    if (response.data.code === 200) {
      return response.data
    } else {
      // 业务错误
      return Promise.reject(new Error(response.data.message || '请求失败'))
    }
  },
  error => {
    // 网络错误或HTTP错误
    const message = error.response?.data?.message || error.message || '网络错误，请稍后重试'
    return Promise.reject(new Error(message))
  }
)

export default api

