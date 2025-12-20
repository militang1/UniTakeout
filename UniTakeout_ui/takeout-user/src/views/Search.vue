<template>
  <div class="search">
    <div class="header">
      <div class="search-input-wrapper">
        <input v-model="keyword" type="text" class="search-input" placeholder="搜索店铺、商品" @input="handleSearch"
          @keyup.enter="performSearch" />
        <span class="search-icon">🔍</span>
      </div>
      <span class="cancel-btn" @click="$router.back()">取消</span>
    </div>

    <div v-if="!keyword && !hasSearched" class="search-content">
      <div class="hot-searches">
        <h3>热门搜索</h3>
        <div class="hot-tags">
          <span v-for="tag in hotSearches" :key="tag" class="hot-tag" @click="keyword = tag; performSearch()">
            {{ tag }}
          </span>
        </div>
      </div>
      <div class="search-history" v-if="searchHistory.length > 0">
        <div class="history-header">
          <h3>搜索历史</h3>
          <span class="clear-btn" @click="clearHistory">清空</span>
        </div>
        <div class="history-list">
          <div v-for="(item, index) in searchHistory" :key="index" class="history-item"
            @click="keyword = item; performSearch()">
            <span class="history-icon">🕐</span>
            <span class="history-text">{{ item }}</span>
            <span class="delete-icon" @click.stop="deleteHistory(index)">✕</span>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="search-results">
      <div class="result-tabs">
        <div v-for="tab in resultTabs" :key="tab.value" class="tab" :class="{ active: activeTab === tab.value }"
          @click="activeTab = tab.value; performSearch()">
          {{ tab.label }}
        </div>
      </div>

      <div v-if="loading" class="loading">搜索中...</div>
      <div v-else-if="keyword && hasSearched">
        <!-- 店铺搜索结果 -->
        <div v-if="activeTab === 'all' || activeTab === 'shop'" class="result-section">
          <h4 class="section-title">店铺 ({{ shopResults.length }})</h4>
          <div v-for="shop in shopResults" :key="shop.id" class="result-item shop-item"
            @click="$router.push(`/shop/${shop.id}`)">
            <div class="shop-image">
              <img :src="shop.image" :alt="shop.name" />
            </div>
            <div class="shop-info">
              <h5>{{ highlightKeyword(shop.name) }}</h5>
              <p class="shop-desc">{{ shop.description }}</p>
              <div class="shop-meta">
                <span class="rating">⭐ {{ shop.rating }}</span>
                <span class="sales">月售{{ shop.monthlySales }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 商品搜索结果 -->
        <div v-if="activeTab === 'all' || activeTab === 'product'" class="result-section">
          <h4 class="section-title">商品 ({{ productResults.length }})</h4>
          <div v-for="product in productResults" :key="product.id" class="result-item product-item"
            @click="goToProduct(product)">
            <div class="product-image">
              <img :src="product.image" :alt="product.name" />
            </div>
            <div class="product-info">
              <h5>{{ highlightKeyword(product.name) }}</h5>
              <p class="product-desc">{{ product.description }}</p>
              <div class="product-meta">
                <span class="price">¥{{ product.price }}</span>
                <span class="shop-name">{{ product.shopName }}</span>
              </div>
            </div>
          </div>
        </div>

        <div v-if="!loading && shopResults.length === 0 && productResults.length === 0" class="empty">
          <p>未找到相关结果</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { searchApi } from '../utils/request'

const route = useRoute()
const router = useRouter()

const keyword = ref(route.query.keyword || '')
const activeTab = ref('all')
const loading = ref(false)
const hasSearched = ref(false)

const shopResults = ref([])
const productResults = ref([])

const hotSearches = ref(['麻辣香锅', '烧烤', '奶茶', '炸鸡', '日式料理', '火锅'])

const resultTabs = [
  { label: '全部', value: 'all' },
  { label: '店铺', value: 'shop' },
  { label: '商品', value: 'product' }
]

// 搜索历史
const searchHistory = ref([])

onMounted(() => {
  loadSearchHistory()
  if (keyword.value) {
    performSearch()
  }
})

function loadSearchHistory() {
  const history = localStorage.getItem('searchHistory')
  if (history) {
    searchHistory.value = JSON.parse(history)
  }
}

function saveSearchHistory(keyword) {
  if (!keyword.trim()) return

  const history = searchHistory.value.filter(item => item !== keyword)
  history.unshift(keyword)
  if (history.length > 10) {
    history.pop()
  }
  searchHistory.value = history
  localStorage.setItem('searchHistory', JSON.stringify(history))
}

function clearHistory() {
  searchHistory.value = []
  localStorage.removeItem('searchHistory')
}

function deleteHistory(index) {
  searchHistory.value.splice(index, 1)
  localStorage.setItem('searchHistory', JSON.stringify(searchHistory.value))
}

function handleSearch() {
  // 实时搜索可以在这里实现防抖
}

async function performSearch() {
  const searchKeyword = keyword.value.trim()
  if (!searchKeyword) {
    hasSearched.value = false
    return
  }

  try {
    loading.value = true
    hasSearched.value = true

    // 保存搜索历史
    saveSearchHistory(searchKeyword)

    // 根据当前tab决定搜索内容
    const promises = []

    if (activeTab.value === 'all' || activeTab.value === 'shop') {
      promises.push(
        searchApi.searchShops({ keyword: searchKeyword }).catch(err => {
          console.error('搜索店铺失败:', err)
          return { code: 200, data: { list: [] } }
        })
      )
    } else {
      promises.push(Promise.resolve({ code: 200, data: { list: [] } }))
    }

    if (activeTab.value === 'all' || activeTab.value === 'product') {
      promises.push(
        searchApi.searchProducts({ keyword: searchKeyword }).catch(err => {
          console.error('搜索商品失败:', err)
          return { code: 200, data: { list: [] } }
        })
      )
    } else {
      promises.push(Promise.resolve({ code: 200, data: { list: [] } }))
    }

    const [shopResponse, productResponse] = await Promise.all(promises)

    if (shopResponse.code === 200 && shopResponse.data) {
      shopResults.value = shopResponse.data.list || shopResponse.data || []
    }

    if (productResponse.code === 200 && productResponse.data) {
      productResults.value = productResponse.data.list || productResponse.data || []
    }
  } catch (error) {
    console.error('搜索失败:', error)
    alert(error.message || '搜索失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

function highlightKeyword(text) {
  if (!keyword.value || !text) return text
  const regex = new RegExp(`(${keyword.value})`, 'gi')
  return text.replace(regex, '$1')
}

function goToProduct(product) {
  if (product.shopId) {
    router.push(`/shop/${product.shopId}`)
  }
}
</script>

<style scoped>
.search {
  min-height: 100vh;
  background: var(--bg-light);
}

.header {
  background: white;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid var(--border-color);
}

.search-input-wrapper {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
}

.search-input {
  flex: 1;
  padding: 10px 40px 10px 16px;
  border: 1px solid var(--border-color);
  border-radius: 20px;
  font-size: 14px;
  background: var(--bg-secondary);
}

.search-icon {
  position: absolute;
  right: 12px;
  font-size: 16px;
  pointer-events: none;
}

.cancel-btn {
  font-size: 14px;
  color: var(--text-secondary);
  cursor: pointer;
}

.search-content {
  padding: 16px;
}

.hot-searches {
  margin-bottom: 24px;
}

.hot-searches h3 {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 12px;
}

.hot-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.hot-tag {
  padding: 6px 16px;
  background: white;
  border-radius: 16px;
  font-size: 14px;
  cursor: pointer;
  border: 1px solid var(--border-color);
}

.search-history {
  margin-top: 24px;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.history-header h3 {
  font-size: 14px;
  color: var(--text-secondary);
}

.clear-btn {
  font-size: 12px;
  color: var(--text-light);
  cursor: pointer;
}

.history-list {
  background: white;
  border-radius: 12px;
  overflow: hidden;
}

.history-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid var(--border-color);
  cursor: pointer;
}

.history-item:last-child {
  border-bottom: none;
}

.history-icon {
  margin-right: 12px;
  font-size: 14px;
}

.history-text {
  flex: 1;
  font-size: 14px;
}

.delete-icon {
  font-size: 16px;
  color: var(--text-light);
  cursor: pointer;
}

.search-results {
  padding: 12px 16px;
}

.result-tabs {
  display: flex;
  background: white;
  border-radius: 12px;
  padding: 4px;
  margin-bottom: 12px;
}

.tab {
  flex: 1;
  padding: 8px;
  text-align: center;
  font-size: 14px;
  color: var(--text-secondary);
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s;
}

.tab.active {
  background: var(--primary-color);
  color: white;
}

.loading {
  text-align: center;
  padding: 40px 0;
  color: var(--text-light);
}

.result-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
  padding: 0 4px;
}

.result-item {
  background: white;
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 12px;
  display: flex;
  cursor: pointer;
  transition: transform 0.2s;
}

.result-item:active {
  transform: scale(0.98);
}

.shop-item .shop-image,
.product-item .product-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  margin-right: 12px;
  flex-shrink: 0;
}

.shop-item .shop-image img,
.product-item .product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.shop-info,
.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.shop-info h5,
.product-info h5 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 4px;
}

.shop-info h5 mark,
.product-info h5 mark {
  background: #fff3cd;
  color: var(--primary-color);
  padding: 0 2px;
}

.shop-desc,
.product-desc {
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

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  font-size: 18px;
  font-weight: 600;
  color: var(--primary-color);
}

.shop-name {
  font-size: 12px;
  color: var(--text-light);
}

.empty {
  text-align: center;
  padding: 60px 0;
  color: var(--text-light);
}
</style>

