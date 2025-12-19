<template>
  <div class="shop-list">
    <div class="header">
      <h2>店铺列表</h2>
    </div>

    <div class="filter-bar">
      <div
        v-for="filter in filters"
        :key="filter.value"
        class="filter-item"
        :class="{ active: activeFilter === filter.value }"
        @click="activeFilter = filter.value"
      >
        {{ filter.label }}
      </div>
    </div>

    <div class="container">
      <div v-if="loading" class="loading">加载中...</div>
      <div
        v-for="shop in shops"
        :key="shop.id"
        class="shop-card"
        @click="$router.push(`/shop/${shop.id}`)"
      >
        <div class="shop-image">
          <img :src="shop.image" :alt="shop.name" />
        </div>
        <div class="shop-info">
          <div class="shop-header">
            <h3>{{ shop.name }}</h3>
            <span class="delivery-time">{{ shop.deliveryTime }}分钟</span>
          </div>
          <p class="shop-desc">{{ shop.description }}</p>
          <div class="shop-meta">
            <span class="rating">⭐ {{ shop.rating }}</span>
            <span class="sales">月售{{ shop.monthlySales }}</span>
            <span class="distance">{{ shop.distance }}km</span>
          </div>
          <div class="shop-tags">
            <span v-for="tag in shop.tags" :key="tag" class="tag">{{ tag }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { shopApi } from '../utils/request'

const activeFilter = ref('all')
const shops = ref([])
const loading = ref(false)

const filters = [
  { label: '全部', value: 'all' },
  { label: '销量', value: 'sales' },
  { label: '评分', value: 'rating' },
  { label: '距离', value: 'distance' }
]

onMounted(() => {
  loadShops()
})

watch(activeFilter, () => {
  loadShops()
})

async function loadShops() {
  try {
    loading.value = true
    const params = {}
    if (activeFilter.value !== 'all') {
      params.sort = activeFilter.value
    }
    const response = await shopApi.getShopList(params)
    if (response.code === 200 && response.data) {
      shops.value = response.data.list || []
    }
  } catch (error) {
    console.error('加载店铺列表失败:', error)
    alert(error.message || '加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.shop-list {
  min-height: 100vh;
  background: var(--bg-light);
}

.header {
  background: white;
  padding: 16px;
  border-bottom: 1px solid var(--border-color);
}

.header h2 {
  font-size: 20px;
  font-weight: 600;
}

.filter-bar {
  background: white;
  padding: 12px 16px;
  display: flex;
  gap: 12px;
  overflow-x: auto;
  border-bottom: 1px solid var(--border-color);
}

.filter-item {
  padding: 6px 16px;
  border-radius: 16px;
  font-size: 14px;
  white-space: nowrap;
  cursor: pointer;
  background: var(--bg-secondary);
  color: var(--text-secondary);
  transition: all 0.3s;
}

.filter-item.active {
  background: var(--primary-color);
  color: white;
}

.shop-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  display: flex;
  cursor: pointer;
  transition: transform 0.2s;
}

.shop-card:active {
  transform: scale(0.98);
}

.shop-image {
  width: 100px;
  height: 100px;
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
}

.shop-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.shop-header h3 {
  font-size: 18px;
  font-weight: 600;
}

.delivery-time {
  font-size: 12px;
  color: var(--text-light);
}

.shop-desc {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.shop-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: var(--text-light);
  margin-bottom: 8px;
}

.rating {
  color: #ff9500;
}

.shop-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag {
  padding: 2px 8px;
  background: var(--bg-light);
  color: var(--text-secondary);
  border-radius: 4px;
  font-size: 12px;
}

.loading {
  text-align: center;
  padding: 40px 0;
  color: var(--text-light);
}

.empty {
  text-align: center;
  padding: 60px 0;
  color: var(--text-light);
}
</style>

