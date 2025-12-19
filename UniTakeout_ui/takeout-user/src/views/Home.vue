<template>
  <div class="home">
    <div class="header">
      <div class="search-bar" @click="$router.push('/shops')">
        <span class="search-icon">🔍</span>
        <span class="search-placeholder">搜索店铺、商品</span>
      </div>
    </div>

    <div class="banner">
      <div class="banner-content">
        <h2>校园外卖</h2>
        <p>便捷点餐，快速送达</p>
      </div>
    </div>

    <div class="container">
      <div class="quick-actions">
        <div class="action-item" @click="$router.push('/shops')">
          <div class="action-icon">🍔</div>
          <div class="action-label">点餐</div>
        </div>
        <div class="action-item" @click="$router.push('/delegation')">
          <div class="action-icon">📋</div>
          <div class="action-label">委托广场</div>
        </div>
        <div class="action-item" @click="$router.push('/ai-agent')">
          <div class="action-icon">🤖</div>
          <div class="action-label">AI推荐</div>
        </div>
        <div class="action-item" @click="$router.push('/order')">
          <div class="action-icon">📦</div>
          <div class="action-label">我的订单</div>
        </div>
      </div>

      <div class="section">
        <div class="section-header">
          <h3>热门店铺</h3>
          <span class="more" @click="$router.push('/shops')">更多 ></span>
        </div>
        <div class="shop-list">
          <div
            v-for="shop in hotShops"
            :key="shop.id"
            class="shop-card"
            @click="$router.push(`/shop/${shop.id}`)"
          >
            <div class="shop-image">
              <img :src="shop.image" :alt="shop.name" />
            </div>
            <div class="shop-info">
              <h4>{{ shop.name }}</h4>
              <p class="shop-desc">{{ shop.description }}</p>
              <div class="shop-meta">
                <span class="rating">⭐ {{ shop.rating }}</span>
                <span class="sales">月售{{ shop.monthlySales }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { shopApi } from '../utils/request'

const hotShops = ref([])
const loading = ref(false)

onMounted(async () => {
  await loadHotShops()
})

async function loadHotShops() {
  try {
    loading.value = true
    const response = await shopApi.getShopList({ page: 1, pageSize: 3, sort: 'sales' })
    if (response.code === 200 && response.data) {
      hotShops.value = response.data.list || []
    }
  } catch (error) {
    console.error('加载热门店铺失败:', error)
    // 可以显示错误提示
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: var(--bg-light);
}

.header {
  background: var(--primary-color);
  padding: 16px;
}

.search-bar {
  background: white;
  border-radius: 24px;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  cursor: pointer;
}

.search-icon {
  margin-right: 8px;
  font-size: 18px;
}

.search-placeholder {
  color: var(--text-light);
  font-size: 14px;
}

.banner {
  background: linear-gradient(135deg, var(--primary-color) 0%, #06ad56 100%);
  padding: 32px 16px;
  color: white;
  text-align: center;
}

.banner-content h2 {
  font-size: 28px;
  margin-bottom: 8px;
}

.banner-content p {
  font-size: 14px;
  opacity: 0.9;
}

.quick-actions {
  display: flex;
  justify-content: space-around;
  padding: 24px 0;
  background: white;
  margin: 12px 0;
  border-radius: 12px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}

.action-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.action-label {
  font-size: 14px;
  color: var(--text-primary);
}

.section {
  margin-top: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding: 0 4px;
}

.section-header h3 {
  font-size: 18px;
  font-weight: 600;
}

.more {
  font-size: 14px;
  color: var(--text-secondary);
  cursor: pointer;
}

.shop-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.shop-card {
  background: white;
  border-radius: 12px;
  padding: 12px;
  display: flex;
  cursor: pointer;
  transition: transform 0.2s;
}

.shop-card:active {
  transform: scale(0.98);
}

.shop-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  margin-right: 12px;
  flex-shrink: 0;
}

.shop-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.shop-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.shop-info h4 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 4px;
}

.shop-desc {
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.shop-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: var(--text-light);
}

.rating {
  color: #ff9500;
}
</style>

