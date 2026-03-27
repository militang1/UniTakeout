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

    <div class="chart-grid">
      <div class="chart-card">
        <div class="chart-header">
          <div>
            <h3>关键指标对比</h3>
            <p>展示今日订单、待处理订单和商品总数</p>
          </div>
        </div>
        <div ref="summaryChartRef" class="chart-container"></div>
      </div>
      <div class="chart-card">
        <div class="chart-header">
          <div>
            <h3>订单状态分布</h3>
            <p>最近订单的状态比例</p>
          </div>
        </div>
        <div ref="statusChartRef" class="chart-container"></div>
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
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { statisticsApi, orderApi } from '../utils/request'

const statistics = ref({})
const recentOrders = ref([])
const loading = ref(false)
const summaryChartRef = ref(null)
const statusChartRef = ref(null)

let summaryChart = null
let statusChart = null

onMounted(() => {
  nextTick(initCharts)
  loadStatistics()
  loadRecentOrders()
  window.addEventListener('resize', resizeCharts)
})

onUnmounted(() => {
  window.removeEventListener('resize', resizeCharts)
  summaryChart?.dispose()
  statusChart?.dispose()
})

async function loadStatistics() {
  try {
    loading.value = true
    const response = await statisticsApi.getStatistics()
    if (response.code === 200 && response.data) {
      statistics.value = response.data
      updateSummaryChart()
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
      updateStatusChart()
    }
  } catch (error) {
    console.error('加载最近订单失败:', error)
  }
}

function initCharts() {
  if (summaryChartRef.value && !summaryChart) {
    summaryChart = echarts.init(summaryChartRef.value)
  }
  if (statusChartRef.value && !statusChart) {
    statusChart = echarts.init(statusChartRef.value)
  }
  updateSummaryChart()
  updateStatusChart()
}

function resizeCharts() {
  summaryChart?.resize()
  statusChart?.resize()
}

function updateSummaryChart() {
  if (!summaryChartRef.value) return
  if (!summaryChart) {
    summaryChart = echarts.init(summaryChartRef.value)
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    xAxis: {
      type: 'category',
      data: ['今日订单', '待处理订单', '商品总数'],
      axisLine: { lineStyle: { color: '#ccc' } },
      axisTick: { alignWithLabel: true }
    },
    yAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: '#ccc' } },
      splitLine: { lineStyle: { type: 'dashed', color: '#eee' } }
    },
    series: [
      {
        name: '数量',
        type: 'bar',
        barWidth: '40%',
        data: [
          statistics.value.todayOrders || 0,
          statistics.value.pendingOrders || 0,
          statistics.value.totalProducts || 0
        ],
        itemStyle: {
          color: '#5470C6'
        },
        label: {
          show: true,
          position: 'top'
        }
      }
    ]
  }

  summaryChart.setOption(option, { notMerge: true })
}

function updateStatusChart() {
  if (!statusChartRef.value) return
  if (!statusChart) {
    statusChart = echarts.init(statusChartRef.value)
  }

  const statusCounts = recentOrders.value.reduce((acc, order) => {
    const name = getStatusText(order.status)
    acc[name] = (acc[name] || 0) + 1
    return acc
  }, {})

  const data = Object.entries(statusCounts).map(([name, value]) => ({ name, value }))
  const chartData = data.length ? data : [{ name: '暂无订单', value: 1 }]

  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      bottom: 10,
      textStyle: { color: '#666' }
    },
    series: [
      {
        name: '订单状态',
        type: 'pie',
        radius: ['45%', '70%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          position: 'outside',
          formatter: '{b}: {c} ({d}%)'
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.2)'
          }
        },
        data: chartData
      }
    ]
  }

  statusChart.setOption(option, { notMerge: true })
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

.chart-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.chart-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: var(--shadow-light);
}

.chart-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 12px;
}

.chart-header h3 {
  margin: 0;
  font-size: 16px;
  color: var(--text-primary);
}

.chart-header p {
  margin: 4px 0 0;
  color: var(--text-secondary);
  font-size: 12px;
}

.chart-container {
  width: 100%;
  min-height: 320px;
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
