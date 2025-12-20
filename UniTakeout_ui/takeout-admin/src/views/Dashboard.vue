<template>
  <div class="dashboard">
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">📦</div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.totalOrders || 0 }}</div>
          <div class="stat-label">总订单数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📊</div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.todayOrders || 0 }}</div>
          <div class="stat-label">今日订单</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">💰</div>
        <div class="stat-content">
          <div class="stat-value">¥{{ (statistics.totalRevenue || 0).toFixed(2) }}</div>
          <div class="stat-label">总营业额</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">💵</div>
        <div class="stat-content">
          <div class="stat-value">¥{{ (statistics.todayRevenue || 0).toFixed(2) }}</div>
          <div class="stat-label">今日营业额</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🍔</div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.totalProducts || 0 }}</div>
          <div class="stat-label">商品总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">⏳</div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.pendingOrders || 0 }}</div>
          <div class="stat-label">待处理订单</div>
        </div>
      </div>
    </div>

    <div class="recent-orders">
      <h3>最近订单</h3>
      <div class="card">
        <table class="table">
          <thead>
            <tr>
              <th>订单号</th>
              <th>客户</th>
              <th>金额</th>
              <th>状态</th>
              <th>下单时间</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in recentOrders" :key="order.id">
              <td>{{ order.orderNo }}</td>
              <td>{{ order.contactName }}</td>
              <td>¥{{ order.totalAmount }}</td>
              <td>
                <span class="status-badge" :class="order.status">{{ getStatusText(order.status) }}</span>
              </td>
              <td>{{ order.createTime }}</td>
            </tr>
            <tr v-if="recentOrders.length === 0">
              <td colspan="5" class="empty">暂无订单</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { statisticsApi, orderApi } from '../utils/request'

const statistics = ref({})
const recentOrders = ref([])
const loading = ref(false)

onMounted(() => {
  loadStatistics()
  loadRecentOrders()
})

async function loadStatistics() {
  try {
    loading.value = true
    const response = await statisticsApi.getStatistics()
    if (response.code === 200 && response.data) {
      statistics.value = response.data
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    alert(error.message || '加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

async function loadRecentOrders() {
  try {
    const response = await orderApi.getOrderList({ page: 1, pageSize: 5 })
    if (response.code === 200 && response.data) {
      recentOrders.value = response.data.list || []
    }
  } catch (error) {
    console.error('加载最近订单失败:', error)
  }
}

function getStatusText(status) {
  const statusMap = {
    pending: '待处理',
    processing: '进行中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return statusMap[status] || status
}
</script>

<style scoped>
.dashboard {
  max-width: 1400px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  box-shadow: var(--shadow-light);
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow);
}

.stat-icon {
  font-size: 40px;
  margin-right: 16px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.recent-orders {
  margin-top: 24px;
}

.recent-orders h3 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
  color: var(--text-primary);
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
}

.status-badge.pending {
  background: #fff3cd;
  color: #856404;
}

.status-badge.processing {
  background: #d1ecf1;
  color: #0c5460;
}

.status-badge.completed {
  background: #d4edda;
  color: #155724;
}

.status-badge.cancelled {
  background: #f8d7da;
  color: #721c24;
}
</style>


