import api from '../api'

export const authApi = {
  login: (data) => api.post('/auth/login', data),
  sendCode: (data) => api.post('/auth/send-code', data)
}

export const shopApi = {
  getShopInfo: () => api.get('/shop'),
  updateShopInfo: (data) => api.put('/shop', data)
}

export const productApi = {
  getProductList: (params) => api.get('/products', { params }),
  getProductDetail: (id) => api.get(`/products/${id}`),
  createProduct: (data) => api.post('/products', data),
  updateProduct: (id, data) => api.put(`/products/${id}`, data),
  deleteProduct: (id) => api.delete(`/products/${id}`)
}

export const categoryApi = {
  getCategoryList: () => api.get('/categories'),
  getCategoryDetail: (id) => api.get(`/categories/${id}`),
  createCategory: (data) => api.post('/categories', data),
  updateCategory: (id, data) => api.put(`/categories/${id}`, data),
  deleteCategory: (id) => api.delete(`/categories/${id}`)
}

export const orderApi = {
  getOrderList: (params) => api.get('/orders', { params }),
  getOrderDetail: (id) => api.get(`/orders/${id}`),
  updateOrderStatus: (id, data) => api.put(`/orders/${id}/status`, data)
}

export const statisticsApi = {
  getStatistics: () => api.get('/statistics')
}

export const uploadApi = {
  // file: File
  uploadImage: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/upload/image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}




