<template>
  <div class="order">
    <div class="header">
      <h2>我的订单</h2>
    </div>

    <div class="tabs">
      <div
        v-for="tab in orderTabs"
        :key="tab.value"
        class="tab"
        :class="{ active: activeTab === tab.value }"
        @click="activeTab = tab.value"
      >
        {{ tab.label }}
      </div>
    </div>

    <div class="order-list">
      <div v-if="loading" class="loading">加载中...</div>
      <div
        v-for="order in filteredOrders"
        :key="order.id"
        class="order-card"
        @click="$router.push(`/order/${order.id}`)"
      >
        <div class="order-header">
          <span class="shop-name">{{ order.shopName }}</span>
          <span class="order-status" :class="order.status">{{ getStatusText(order.status) }}</span>
        </div>
        <div class="order-items">
          <div v-for="item in order.items" :key="item.id" class="order-item">
            <span class="item-name">{{ item.name }}</span>
            <span class="item-quantity">x{{ item.quantity }}</span>
            <span class="item-price">¥{{ item.price }}</span>
          </div>
        </div>
        <div class="order-footer">
          <span class="order-time">{{ order.createTime }}</span>
          <span class="order-total">合计：¥{{ order.totalAmount || order.total }}</span>
        </div>
        <div v-if="order.status === 'pending'" class="order-actions">
          <button class="btn btn-outline" @click.stop="cancelOrder(order.id)">取消订单</button>
        </div>
      </div>

      <div v-if="filteredOrders.length === 0" class="empty">
        <p>暂无订单</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { orderApi } from '../utils/request'

const activeTab = ref('all')
const orders = ref([])
const loading = ref(false)

const orderTabs = [
  { label: '全部', value: 'all' },
  { label: '待支付', value: 'pending' },
  { label: '进行中', value: 'processing' },
  { label: '已完成', value: 'completed' }
]

onMounted(() => {
  loadOrders()
})

watch(activeTab, () => {
  loadOrders()
})

async function loadOrders() {
  try {
    loading.value = true
    const params = {}
    if (activeTab.value !== 'all') {
      params.status = activeTab.value
    }
    const response = await orderApi.getOrderList(params)
    if (response.code === 200 && response.data) {
      orders.value = response.data.list || []
    }
  } catch (error) {
    console.error('加载订单列表失败:', error)
    alert(error.message || '加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const filteredOrders = computed(() => {
  return orders.value
})

function getStatusText(status) {
  const statusMap = {
    pending: '待支付',
    processing: '进行中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return statusMap[status] || status
}

async function cancelOrder(orderId) {
  if (!confirm('确定要取消这个订单吗？')) {
    return
  }
  
  try {
    const response = await orderApi.cancelOrder(orderId)
    if (response.code === 200) {
      alert('订单已取消')
      // 重新加载订单列表
      await loadOrders()
    } else {
      alert(response.message || '取消失败')
    }
  } catch (error) {
    console.error('取消订单失败:', error)
    alert(error.message || '取消失败，请稍后重试')
  }
}
</script>

<style scoped>
.order {
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

.tabs {
  background: white;
  display: flex;
  border-bottom: 1px solid var(--border-color);
}

.tab {
  flex: 1;
  padding: 12px;
  text-align: center;
  font-size: 14px;
  color: var(--text-secondary);
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.tab.active {
  color: var(--primary-color);
  border-bottom-color: var(--primary-color);
}

.order-list {
  padding: 12px 16px;
}

.order-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  cursor: pointer;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-color);
}

.shop-name {
  font-size: 16px;
  font-weight: 600;
}

.order-status {
  font-size: 14px;
  padding: 4px 12px;
  border-radius: 12px;
}

.order-status.pending {
  background: #fff3cd;
  color: #856404;
}

.order-status.processing {
  background: #d1ecf1;
  color: #0c5460;
}

.order-status.completed {
  background: #d4edda;
  color: #155724;
}

.order-items {
  margin-bottom: 12px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.item-name {
  flex: 1;
}

.item-quantity {
  margin: 0 12px;
  color: var(--text-light);
}

.item-price {
  color: var(--text-secondary);
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid var(--border-color);
}

.order-time {
  font-size: 12px;
  color: var(--text-light);
}

.order-total {
  font-size: 16px;
  font-weight: 600;
  color: var(--primary-color);
}

.order-actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}

.empty {
  text-align: center;
  padding: 60px 0;
  color: var(--text-light);
}

.loading {
  text-align: center;
  padding: 40px 0;
  color: var(--text-light);
}
</style>

