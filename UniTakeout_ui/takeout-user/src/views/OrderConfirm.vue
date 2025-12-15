<template>
  <div class="order-confirm">
    <div class="header">
      <h2>确认订单</h2>
      <p class="subtitle">请确认收货信息和订单内容</p>
    </div>

    <div class="page-content">
      <!-- 收货信息 -->
      <section class="card section-card">
        <div class="section-title">收货信息</div>
        <div class="form-row">
          <label>收货人</label>
          <input v-model="form.name" type="text" placeholder="请输入姓名" />
        </div>
        <div class="form-row">
          <label>手机号</label>
          <input v-model="form.phone" type="tel" placeholder="请输入手机号" />
        </div>
        <div class="form-row">
          <label>配送地址</label>
          <textarea
            v-model="form.address"
            rows="2"
            placeholder="如：xx校区 xx宿舍楼 xx室"
          ></textarea>
        </div>
      </section>

      <!-- 订单信息 -->
      <section class="card section-card">
        <div class="section-title">订单信息</div>
        <div class="shop-row">
          <span class="shop-name">{{ shopName }}</span>
        </div>
        <div class="order-items">
          <div v-for="item in cartStore.items" :key="item.id" class="order-item">
            <div class="item-main">
              <span class="item-name">{{ item.name }}</span>
              <span class="item-quantity">x{{ item.quantity }}</span>
            </div>
            <span class="item-price">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
          </div>
        </div>
        <div class="order-summary">
          <div class="row">
            <span>商品小计</span>
            <span>¥{{ cartStore.totalPrice.toFixed(2) }}</span>
          </div>
          <div class="row">
            <span>配送费</span>
            <span>¥{{ deliveryFee.toFixed(2) }}</span>
          </div>
          <div class="row total">
            <span>合计</span>
            <span class="price">¥{{ totalAmount.toFixed(2) }}</span>
          </div>
        </div>
      </section>

      <!-- 支付方式 -->
      <section class="card section-card">
        <div class="section-title">支付方式</div>
        <div class="pay-methods">
          <div
            v-for="method in payMethods"
            :key="method.value"
            class="pay-item"
            :class="{ active: form.payMethod === method.value }"
            @click="form.payMethod = method.value"
          >
            <span class="icon">{{ method.icon }}</span>
            <span class="label">{{ method.label }}</span>
          </div>
        </div>
      </section>
    </div>

    <!-- 底部提交栏：固定在 TabBar 之上 -->
    <div class="bottom-bar">
      <div class="amount">
        <span class="label">应付金额</span>
        <span class="value">¥{{ totalAmount.toFixed(2) }}</span>
      </div>
      <button class="btn btn-primary submit-btn" @click="submitOrder">提交订单</button>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'
import { useUserStore } from '../stores/user'

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

// TODO: 可以根据路由参数或接口获取真实店铺名称
const shopName = '校园店铺'

const form = reactive({
  name: userStore.userInfo.nickname || '',
  phone: userStore.userInfo.phone || '',
  address: userStore.userInfo.address || '',
  payMethod: 'wechat'
})

const payMethods = [
  { value: 'wechat', label: '微信支付', icon: '💚' },
  { value: 'alipay', label: '支付宝', icon: '💙' },
  { value: 'cash', label: '货到付款', icon: '💵' }
]

const deliveryFee = computed(() => {
  return cartStore.totalPrice > 30 ? 0 : 3
})

const totalAmount = computed(() => {
  return cartStore.totalPrice + deliveryFee.value
})

function validate() {
  if (!form.name.trim()) {
    alert('请输入收货人姓名')
    return false
  }
  if (!form.phone.trim()) {
    alert('请输入联系电话')
    return false
  }
  if (!form.address.trim()) {
    alert('请输入配送地址')
    return false
  }
  if (!cartStore.totalCount) {
    alert('购物车为空，请先选择商品')
    return false
  }
  return true
}

function submitOrder() {
  if (!validate()) return

  // 这里可以调用后端创建订单接口，目前先模拟
  alert('订单提交成功！')

  // 清空购物车
  cartStore.clearCart()

  // 跳转到订单列表页
  router.replace('/order')
}
</script>

<style scoped>
.order-confirm {
  min-height: 100vh;
  background: var(--bg-light);
  padding-bottom: 100px; /* 为底部栏预留空间，避免被遮挡 */
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

.subtitle {
  margin-top: 4px;
  font-size: 13px;
  color: var(--text-secondary);
}

.page-content {
  padding: 12px 12px 0;
}

.card {
  background: var(--bg-primary);
  border-radius: 12px;
  padding: 12px 16px;
  margin-bottom: 12px;
  box-shadow: var(--shadow-light);
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 8px;
}

.form-row {
  display: flex;
  flex-direction: column;
  margin-bottom: 10px;
}

.form-row label {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 4px;
}

.form-row input,
.form-row textarea {
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 8px 10px;
  font-size: 14px;
  font-family: inherit;
}

.shop-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.shop-name {
  font-size: 15px;
  font-weight: 600;
}

.order-items {
  border-top: 1px solid var(--border-color);
  border-bottom: 1px solid var(--border-color);
  padding: 8px 0;
  margin-top: 4px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
  font-size: 14px;
}

.item-main {
  display: flex;
  align-items: center;
}

.item-name {
  margin-right: 8px;
}

.item-quantity {
  color: var(--text-light);
}

.item-price {
  min-width: 70px;
  text-align: right;
}

.order-summary {
  padding-top: 8px;
}

.order-summary .row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
  font-size: 14px;
}

.order-summary .row.total {
  margin-top: 4px;
  font-weight: 600;
}

.order-summary .price {
  color: var(--primary-color);
  font-size: 16px;
}

.pay-methods {
  display: flex;
  gap: 12px;
}

.pay-item {
  flex: 1;
  background: var(--bg-secondary);
  border-radius: 10px;
  padding: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
  border: 1px solid transparent;
  cursor: pointer;
  font-size: 14px;
}

.pay-item.active {
  background: #e6f9f0;
  border-color: var(--primary-color);
}

.pay-item .icon {
  font-size: 18px;
}

.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 60px; /* 明确在 TabBar 上方，避免被覆盖 */
  height: 56px;
  background: white;
  border-top: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  z-index: 1001;
}

.amount .label {
  font-size: 13px;
  color: var(--text-secondary);
}

.amount .value {
  margin-left: 4px;
  font-size: 18px;
  font-weight: 600;
  color: var(--primary-color);
}

.submit-btn {
  padding: 10px 24px;
  border-radius: 20px;
  font-size: 15px;
}
</style>
