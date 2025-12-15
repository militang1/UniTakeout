<template>
  <div class="shop-detail">
    <div class="shop-header">
      <div class="shop-banner">
        <img :src="shop.image" :alt="shop.name" />
      </div>
      <div class="shop-info">
        <h2>{{ shop.name }}</h2>
        <p class="shop-desc">{{ shop.description }}</p>
        <div class="shop-meta">
          <span class="rating">⭐ {{ shop.rating }}</span>
          <span class="sales">月售{{ shop.monthlySales }}</span>
          <span class="time">{{ shop.deliveryTime }}分钟</span>
        </div>
      </div>
    </div>

    <div class="category-tabs">
      <div
        v-for="category in categories"
        :key="category.id"
        class="category-tab"
        :class="{ active: activeCategory === category.id }"
        @click="activeCategory = category.id"
      >
        {{ category.name }}
      </div>
    </div>

    <div class="product-list">
      <div
        v-for="product in filteredProducts"
        :key="product.id"
        class="product-card"
      >
        <div class="product-image">
          <img :src="product.image" :alt="product.name" />
        </div>
        <div class="product-info">
          <h4>{{ product.name }}</h4>
          <p class="product-desc">{{ product.description }}</p>
          <div class="product-footer">
            <span class="price">¥{{ product.price }}</span>
            <button class="add-btn" @click="addToCart(product)">+</button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="cartStore.totalCount > 0" class="cart-bar">
      <div class="cart-info" @click="showCart = true">
        <span class="cart-count">{{ cartStore.totalCount }}</span>
        <span class="cart-price">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
      </div>
      <button class="checkout-btn" @click="checkout">去结算</button>
    </div>

    <div v-if="showCart" class="cart-modal" @click="showCart = false">
      <div class="cart-content" @click.stop>
        <div class="cart-header">
          <h3>购物车</h3>
          <span @click="showCart = false">关闭</span>
        </div>
        <div class="cart-items">
          <div v-for="item in cartStore.items" :key="item.id" class="cart-item">
            <div class="item-info">
              <h5>{{ item.name }}</h5>
              <span class="item-price">¥{{ item.price }}</span>
            </div>
            <div class="item-controls">
              <button @click="cartStore.updateQuantity(item.id, item.quantity - 1)">-</button>
              <span>{{ item.quantity }}</span>
              <button @click="cartStore.updateQuantity(item.id, item.quantity + 1)">+</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const showCart = ref(false)
const activeCategory = ref(1)

const shop = ref({
  id: 1,
  name: '校园食堂',
  description: '营养丰富，价格实惠，学生首选',
  image: 'https://via.placeholder.com/400x200',
  rating: 4.8,
  monthlySales: 1200,
  deliveryTime: 20
})

const categories = ref([
  { id: 1, name: '全部' },
  { id: 2, name: '主食' },
  { id: 3, name: '小食' },
  { id: 4, name: '饮品' }
])

const products = ref([
  {
    id: 1,
    name: '宫保鸡丁',
    description: '鸡肉鲜嫩，花生酥脆',
    price: 18,
    image: 'https://via.placeholder.com/100',
    categoryId: 2
  },
  {
    id: 2,
    name: '麻婆豆腐',
    description: '麻辣鲜香，下饭神器',
    price: 15,
    image: 'https://via.placeholder.com/100',
    categoryId: 2
  },
  {
    id: 3,
    name: '炸鸡块',
    description: '外酥里嫩，香脆可口',
    price: 12,
    image: 'https://via.placeholder.com/100',
    categoryId: 3
  },
  {
    id: 4,
    name: '可乐',
    description: '冰镇可乐，清爽解腻',
    price: 5,
    image: 'https://via.placeholder.com/100',
    categoryId: 4
  }
])

const filteredProducts = computed(() => {
  if (activeCategory.value === 1) {
    return products.value
  }
  return products.value.filter(p => p.categoryId === activeCategory.value)
})

function addToCart(product) {
  cartStore.addItem(product)
}

function checkout() {
  showCart.value = false
  router.push('/order')
}
</script>

<style scoped>
.shop-detail {
  min-height: 100vh;
  padding-bottom: 80px;
}

.shop-header {
  background: white;
}

.shop-banner {
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.shop-banner img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.shop-info {
  padding: 16px;
}

.shop-info h2 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
}

.shop-desc {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 12px;
}

.shop-meta {
  display: flex;
  gap: 16px;
  font-size: 14px;
}

.rating {
  color: #ff9500;
}

.category-tabs {
  background: white;
  padding: 12px 16px;
  display: flex;
  gap: 12px;
  overflow-x: auto;
  border-bottom: 1px solid var(--border-color);
}

.category-tab {
  padding: 6px 16px;
  border-radius: 16px;
  font-size: 14px;
  white-space: nowrap;
  cursor: pointer;
  background: var(--bg-secondary);
  color: var(--text-secondary);
  transition: all 0.3s;
}

.category-tab.active {
  background: var(--primary-color);
  color: white;
}

.product-list {
  padding: 12px 16px;
}

.product-card {
  background: white;
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 12px;
  display: flex;
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  margin-right: 12px;
  flex-shrink: 0;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-info h4 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 4px;
}

.product-desc {
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  font-size: 18px;
  font-weight: 600;
  color: var(--primary-color);
}

.add-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: var(--primary-color);
  color: white;
  font-size: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cart-bar {
  position: fixed;
  bottom: 60px;
  left: 0;
  right: 0;
  background: white;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid var(--border-color);
  z-index: 999;
}

.cart-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.cart-count {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: var(--primary-color);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

.cart-price {
  font-size: 18px;
  font-weight: 600;
}

.checkout-btn {
  padding: 10px 24px;
  background: var(--primary-color);
  color: white;
  border: none;
  border-radius: 20px;
  font-size: 16px;
  cursor: pointer;
}

.cart-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1001;
  display: flex;
  align-items: flex-end;
}

.cart-content {
  background: white;
  width: 100%;
  max-height: 60vh;
  border-radius: 16px 16px 0 0;
  padding: 16px;
  overflow-y: auto;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-color);
}

.cart-header h3 {
  font-size: 18px;
  font-weight: 600;
}

.cart-header span {
  color: var(--text-secondary);
  cursor: pointer;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid var(--border-color);
}

.item-info {
  flex: 1;
}

.item-info h5 {
  font-size: 16px;
  margin-bottom: 4px;
}

.item-price {
  font-size: 14px;
  color: var(--primary-color);
}

.item-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.item-controls button {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: 1px solid var(--border-color);
  background: white;
  cursor: pointer;
}

.item-controls span {
  min-width: 24px;
  text-align: center;
}
</style>

