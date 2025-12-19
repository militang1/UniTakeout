import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useCartStore = defineStore('cart', () => {
  const items = ref([])
  const shopId = ref(null)
  const shopName = ref('')

  const totalCount = computed(() => {
    return items.value.reduce((sum, item) => sum + item.quantity, 0)
  })

  const totalPrice = computed(() => {
    return items.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
  })

  function addItem(product, currentShopId = null, currentShopName = '') {
    // 如果购物车已有商品但店铺不同，清空购物车
    if (shopId.value && currentShopId && shopId.value !== currentShopId) {
      if (confirm('购物车中有其他店铺的商品，是否清空后添加？')) {
        clearCart()
      } else {
        return
      }
    }
    
    // 设置店铺信息
    if (currentShopId) {
      shopId.value = currentShopId
      shopName.value = currentShopName
    }
    
    const existingItem = items.value.find(item => item.id === product.id)
    if (existingItem) {
      existingItem.quantity += 1
    } else {
      items.value.push({
        ...product,
        quantity: 1
      })
    }
  }

  function removeItem(productId) {
    const index = items.value.findIndex(item => item.id === productId)
    if (index > -1) {
      items.value.splice(index, 1)
    }
  }

  function updateQuantity(productId, quantity) {
    const item = items.value.find(item => item.id === productId)
    if (item) {
      if (quantity <= 0) {
        removeItem(productId)
      } else {
        item.quantity = quantity
      }
    }
  }

  function clearCart() {
    items.value = []
    shopId.value = null
    shopName.value = ''
  }

  return {
    items,
    shopId,
    shopName,
    totalCount,
    totalPrice,
    addItem,
    removeItem,
    updateQuantity,
    clearCart
  }
})

