<template>
  <div class="order-manage">
    <div class="page-header">
      <h3>订单管理</h3>
      <div class="filter-bar">
        <select v-model="selectedStatus" @change="loadOrders" class="status-select">
          <option value="">全部状态</option>
          <option value="pending">待处理</option>
          <option value="processing">进行中</option>
          <option value="completed">已完成</option>
          <option value="cancelled">已取消</option>
        </select>
      </div>
    </div>

    <div class="card">
      <table class="table">
        <thead>
          <tr>
            <th>订单号</th>
            <th>客户信息</th>
            <th>商品</th>
            <th>金额</th>
            <th>状态</th>
            <th>下单时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.id">
            <td>{{ order.orderNo }}</td>
            <td>
              <div class="customer-info">
                <div>{{ order.contactName }}</div>
                <div class="phone">{{ order.contactPhone }}</div>
                <div class="address">{{ order.address }}</div>
              </div>
            </td>
            <td>
              <div class="order-items">
                <div v-for="item in order.items" :key="item.id" class="order-item">
                  {{ item.name }} x{{ item.quantity }}
                </div>
              </div>
            </td>
            <td>¥{{ order.totalAmount }}</td>
            <td>
              <span class="status-badge" :class="order.status">{{ getStatusText(order.status) }}</span>
            </td>
            <td>{{ order.createTime }}</td>
            <td>
              <button v-if="order.status === 'pending'" class="btn btn-primary btn-sm"
                @click="updateOrderStatus(order.id, 'processing')">
                接单
              </button>
              <button v-if="order.status === 'processing'" class="btn btn-primary btn-sm"
                @click="updateOrderStatus(order.id, 'completed')">
                完成
              </button>
              <button class="btn btn-outline btn-sm" @click="viewOrderDetail(order.id)">详情</button>
            </td>
          </tr>
          <tr v-if="orders.length === 0">
            <td colspan="7" class="empty">暂无订单</td>
          </tr>
        </tbody>
      </table>

      <!-- 分页控件 -->
      <div class="pagination-controls" v-if="total > pageSize">
        <div class="page-info">显示 {{ Math.min((page - 1) * pageSize + 1, total) }} - {{ Math.min(page * pageSize, total) }} / {{ total }} 条</div>
        <div class="page-actions">
          <button class="btn btn-outline btn-sm" :disabled="page <= 1" @click="prevPage">上一页</button>
          <span class="page-number">第 {{ page }} / {{ totalPages }} 页</span>
          <button class="btn btn-outline btn-sm" :disabled="page >= totalPages" @click="nextPage">下一页</button>
        </div>
      </div>
    </div>

    <!-- 订单详情弹窗 -->
    <div v-if="showDetailModal && currentOrder" class="modal" @click="showDetailModal = false">
      <div class="modal-content modal-large" @click.stop>
        <div class="modal-header">
          <h3>订单详情</h3>
          <span @click="showDetailModal = false">✕</span>
        </div>
        <div class="modal-body">
          <div class="detail-section">
            <h4>订单信息</h4>
            <div class="detail-row">
              <span class="label">订单号：</span>
              <span>{{ currentOrder.orderNo }}</span>
            </div>
            <div class="detail-row">
              <span class="label">状态：</span>
              <span class="status-badge" :class="currentOrder.status">
                {{ getStatusText(currentOrder.status) }}
              </span>
            </div>
            <div class="detail-row">
              <span class="label">下单时间：</span>
              <span>{{ currentOrder.createTime }}</span>
            </div>
          </div>

          <div class="detail-section">
            <h4>客户信息</h4>
            <div class="detail-row">
              <span class="label">姓名：</span>
              <span>{{ currentOrder.contactName }}</span>
            </div>
            <div class="detail-row">
              <span class="label">电话：</span>
              <span>{{ currentOrder.contactPhone }}</span>
            </div>
            <div class="detail-row">
              <span class="label">地址：</span>
              <span>{{ currentOrder.address }}</span>
            </div>
          </div>

          <div class="detail-section">
            <h4>商品明细</h4>
            <table class="table">
              <thead>
                <tr>
                  <th>商品名称</th>
                  <th>数量</th>
                  <th>单价</th>
                  <th>小计</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in currentOrder.items" :key="item.id">
                  <td>{{ item.name }}</td>
                  <td>{{ item.quantity }}</td>
                  <td>¥{{ item.price }}</td>
                  <td>¥{{ (item.price * item.quantity).toFixed(2) }}</td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="detail-section">
            <h4>金额信息</h4>
            <div class="detail-row">
              <span class="label">商品小计：</span>
              <span>¥{{ currentOrder.goodsAmount }}</span>
            </div>
            <div class="detail-row">
              <span class="label">配送费：</span>
              <span>¥{{ currentOrder.deliveryFee }}</span>
            </div>
            <div class="detail-row total">
              <span class="label">订单总额：</span>
              <span class="amount">¥{{ currentOrder.totalAmount }}</span>
            </div>
          </div>

          <div v-if="currentOrder.remark" class="detail-section">
            <h4>备注</h4>
            <p>{{ currentOrder.remark }}</p>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-outline" @click="showDetailModal = false">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, watch } from 'vue'
import { orderApi, shopApi } from '../utils/request'

const orders = ref([])
const selectedStatus = ref('')
const showDetailModal = ref(false)
const currentOrder = ref(null)

// 分页状态
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))

// 监听筛选变化，重置页码
watch(selectedStatus, () => {
  page.value = 1
  loadOrders()
})

// WebSocket state
let ws = null
let reconnectTimer = null
let reconnectAttempts = 0
const maxReconnectAttempts = 6

// 通知音频
let notifyAudio = null
function playNotificationSound() {
  try {
    if (!notifyAudio) {
      // /preview.mp3 在 public 目录下，会被当作根路径资源提供
      notifyAudio = new Audio('/preview.mp3')
      notifyAudio.preload = 'auto'
    }
    // 避免叠加播放，重置到 0 后播放
    try { notifyAudio.pause() } catch (e) { }
    try { notifyAudio.currentTime = 0 } catch (e) { }
    const p = notifyAudio.play()
    if (p && typeof p.then === 'function') {
      p.catch((err) => console.warn('播放提示音被阻止或失败：', err))
    }
  } catch (err) {
    console.error('播放提示音异常：', err)
  }
}

onMounted(async () => {
  await loadOrders()
  initWebSocket()
})

onUnmounted(() => {
  if (ws) {
    try { ws.close() } catch (e) { console.error('关闭 ws 失败', e) }
    ws = null
  }
  if (reconnectTimer) {
    clearTimeout(reconnectTimer)
    reconnectTimer = null
  }
})

function buildWsUrl(sid) {
  const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws'
  const host = 'localhost:3000'
  return `${protocol}://${host}/ws/${sid}`
}

async function initWebSocket() {
  // 获取店铺信息以确定 sid；若失败则退回通用 sid
  let sid = 'admin'
  try {
    const res = await shopApi.getShopInfo()
    if (res && res.code === 200 && res.data && res.data.id) {
      sid = res.data.id
    }
  } catch (e) {
    console.warn('获取店铺信息失败，使用默认 sid', e)
  }

  connectWs(sid)
}

function connectWs(sid) {
  if (ws) {
    try { ws.close() } catch (e) { console.error(e) }
    ws = null
  }

  const url = buildWsUrl(sid)
  try {
    ws = new WebSocket(url)
  } catch (e) {
    console.error('创建 WebSocket 失败', e)
    scheduleReconnect(sid)
    return
  }

  ws.onopen = () => {
    console.log('WebSocket 已连接', url)
    reconnectAttempts = 0
  }

  ws.onmessage = (event) => {
    const raw = event.data

    // 如果服务端直接发送简短文本通知（后端示例），先处理明确的文本提示
    if (typeof raw === 'string' && raw.includes('您有新的订单，请及时接单')) {
      // 重新拉取订单并播放提示音，等待1s后再执行，避免由于数据库延迟导致新订单未显示
      setTimeout(async () => {
        await loadOrders()  // 等待 loadOrders 异步完成
        playNotificationSound()
      }, 1000)
      return
    }

    // 解析消息，支持多种服务端格式：{ type: 'new_order', data: {...} } 或 { order: {...} }
    try {
      const msg = JSON.parse(raw)

      // 优先使用明确的类型
      if (msg.type === 'new_order' || msg.event === 'order_created') {
        if (msg.data || msg.order) {
          const newOrder = msg.data || msg.order
          // 如果当前是第一页，插入并保持不超过 pageSize；否则刷新列表
          if (page.value === 1) {
            orders.value.unshift(newOrder)
            if (orders.value.length > pageSize.value) orders.value.splice(pageSize.value)
          } else {
            loadOrders()
          }
        } else {
          // 没有携带完整订单时重新拉取列表以保证数据一致
          loadOrders()
        }
        // 也播放提示音以提醒管理员
        playNotificationSound()
      } else if (msg.type === 'order_update' || msg.event === 'order_updated') {
        // 简单策略：刷新列表
        loadOrders()
      } else if (msg.order) {
        if (page.value === 1) {
          orders.value.unshift(msg.order)
          if (orders.value.length > pageSize.value) orders.value.splice(pageSize.value)
        } else {
          loadOrders()
        }
        playNotificationSound()
      } else {
        // 兼容：如果消息看起来像是 orderId
        if (msg.orderId) {
          loadOrders()
        } else {
          console.log('未识别的 ws 消息：', msg)
        }
      }
    } catch (err) {
      // 仍然可能是纯文本或无法解析的数据
      console.warn('无法解析 WebSocket 消息，可能是纯文本:', raw)
    }
  }

  ws.onclose = (ev) => {
    console.warn('WebSocket 连接已关闭', ev)
    scheduleReconnect(sid)
  }

  ws.onerror = (err) => {
    console.error('WebSocket 错误', err)
    // onerror 后通常会触发 onclose
  }
}

function scheduleReconnect(sid) {
  if (reconnectAttempts >= maxReconnectAttempts) {
    console.warn('达到最大重连次数，不再重连')
    return
  }
  reconnectAttempts++
  const timeout = Math.min(30000, 1000 * Math.pow(2, reconnectAttempts))
  console.log(`第 ${reconnectAttempts} 次重连将在 ${timeout}ms 后尝试`)
  reconnectTimer = setTimeout(() => connectWs(sid), timeout)
}

async function loadOrders() {
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value
    }
    if (selectedStatus.value) {
      params.status = selectedStatus.value
    }
    const response = await orderApi.getOrderList(params)
    if (response.code === 200 && response.data) {
      orders.value = response.data.list || []
      // 兼容后端返回分页信息
      total.value = response.data.total || response.data.total || (response.data.list ? response.data.list.length : 0)
      // 如果后端返回 page/pageSize，保持一致
      if (response.data.page) page.value = response.data.page
      if (response.data.pageSize) pageSize.value = response.data.pageSize
    }
  } catch (error) {
    console.error('加载订单列表失败:', error)
    alert(error.message || '加载失败，请稍后重试')
  }
}

async function viewOrderDetail(id) {
  try {
    const response = await orderApi.getOrderDetail(id)
    if (response.code === 200 && response.data) {
      currentOrder.value = response.data
      showDetailModal.value = true
    }
  } catch (error) {
    console.error('加载订单详情失败:', error)
    alert(error.message || '加载失败，请稍后重试')
  }
}

function prevPage() {
  if (page.value > 1) {
    page.value--
    loadOrders()
  }
}

function nextPage() {
  if (page.value < totalPages.value) {
    page.value++
    loadOrders()
  }
}

async function updateOrderStatus(id, status) {
  const statusText = status === 'processing' ? '接单' : '完成'
  if (!confirm(`确定要${statusText}这个订单吗？`)) {
    return
  }

  try {
    const response = await orderApi.updateOrderStatus(id, { status })
    if (response.code === 200) {
      alert(`${statusText}成功`)
      loadOrders()
    } else {
      alert(response.message || `${statusText}失败`)
    }
  } catch (error) {
    console.error('更新订单状态失败:', error)
    alert(error.message || `${statusText}失败，请稍后重试`)
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
.order-manage {
  max-width: 1400px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
}

.filter-bar {
  display: flex;
  gap: 12px;
}

.status-select {
  padding: 8px 12px;
  border: 1px solid var(--border-color);
  border-radius: 6px;
  font-size: 14px;
}

.customer-info {
  font-size: 13px;
}

.customer-info .phone {
  color: var(--text-secondary);
  margin: 4px 0;
}

.customer-info .address {
  color: var(--text-light);
  font-size: 12px;
}

.order-items {
  font-size: 13px;
}

.order-item {
  margin-bottom: 4px;
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

.btn-sm {
  padding: 6px 12px;
  font-size: 12px;
  margin-right: 8px;
}

/* 分页控件 - 保持简洁与现有样式一致 */
.pagination-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
}
.pagination-controls .page-info {
  color: var(--text-secondary);
  font-size: 13px;
}
.pagination-controls .page-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}
.pagination-controls .page-number {
  color: var(--text-secondary);
  font-size: 13px;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-large {
  max-width: 1000px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
}

.modal-header h3 {
  font-size: 18px;
  font-weight: 600;
}

.modal-header span {
  font-size: 24px;
  color: var(--text-light);
  cursor: pointer;
}

.modal-body {
  padding: 20px;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section h4 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
  color: var(--text-primary);
}

.detail-row {
  display: flex;
  padding: 8px 0;
  border-bottom: 1px solid var(--border-color);
}

.detail-row.total {
  border-bottom: 2px solid var(--primary-color);
  font-weight: 600;
  margin-top: 8px;
}

.detail-row .label {
  width: 120px;
  color: var(--text-secondary);
}

.detail-row .amount {
  color: var(--primary-color);
  font-size: 18px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px;
  border-top: 1px solid var(--border-color);
}
</style>
