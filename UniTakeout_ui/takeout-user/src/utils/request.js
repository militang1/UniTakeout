import api from '../api'

export const shopApi = {
  getShopList: (params) => api.get('/shops', { params }),
  getShopDetail: (id) => api.get(`/shops/${id}`),
  getShopProducts: (shopId) => api.get(`/shops/${shopId}/products`)
}

export const productApi = {
  getProductDetail: (id) => api.get(`/products/${id}`)
}

export const orderApi = {
  createOrder: (data) => api.post('/orders', data),
  getOrderList: (params) => api.get('/orders', { params }),
  getOrderDetail: (id) => api.get(`/orders/${id}`),
  cancelOrder: (id) => api.post(`/orders/${id}/cancel`)
}

export const delegationApi = {
  getDelegationList: (params) => api.get('/delegations', { params }),
  createDelegation: (data) => api.post('/delegations', data),
  getDelegationDetail: (id) => api.get(`/delegations/${id}`),
  acceptDelegation: (id) => api.post(`/delegations/${id}/accept`),
  completeDelegation: (id) => api.post(`/delegations/${id}/complete`)
}

export const aiApi = {
  getRecommendations: (data) => api.post('/ai/recommend', data),
  autoOrder: (data) => api.post('/ai/auto-order', data)
}

export const userApi = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
  sendCode: (data) => api.post('/auth/send-code', data),
  getUserInfo: () => api.get('/user/info'),
  updateUserInfo: (data) => api.put('/user/info', data)
}

export const searchApi = {
  searchShops: (params) => api.get('/search/shops', { params }),
  searchProducts: (params) => api.get('/search/products', { params }),
  searchAll: (params) => api.get('/search', { params })
}

