<template>
  <div class="delegation-square">
    <div class="header">
      <h2>委托广场</h2>
      <button class="btn btn-primary" @click="showPublishModal = true">发布委托</button>
    </div>

    <div class="tabs">
      <div
        v-for="tab in tabs"
        :key="tab.value"
        class="tab"
        :class="{ active: activeTab === tab.value }"
        @click="activeTab = tab.value"
      >
        {{ tab.label }}
      </div>
    </div>

    <div class="delegation-list">
      <div
        v-for="delegation in filteredDelegations"
        :key="delegation.id"
        class="delegation-card"
        @click="viewDelegation(delegation)"
      >
        <div class="card-header">
          <div class="user-info">
            <div class="avatar">{{ delegation.userName.charAt(0) }}</div>
            <div>
              <div class="user-name">{{ delegation.userName }}</div>
              <div class="delegation-time">{{ delegation.createTime }}</div>
            </div>
          </div>
          <span class="delegation-type" :class="delegation.type">
            {{ delegation.type === 'request' ? '求带' : '可带' }}
          </span>
        </div>
        <div class="card-content">
          <h4>{{ delegation.title }}</h4>
          <p class="delegation-desc">{{ delegation.description }}</p>
          <div class="delegation-info">
            <span class="shop-name">📍 {{ delegation.shopName }}</span>
            <span class="reward">💰 酬金 ¥{{ delegation.reward }}</span>
          </div>
        </div>
        <div v-if="delegation.type === 'offer'" class="card-footer">
          <div class="offer-items">
            <span v-for="item in delegation.items" :key="item" class="offer-item">{{ item }}</span>
          </div>
        </div>
        <div v-if="delegation.status === 'pending'" class="card-actions">
          <button
            v-if="delegation.type === 'request'"
            class="btn btn-primary"
            @click.stop="acceptDelegation(delegation.id)"
          >
            接受委托
          </button>
          <button
            v-if="delegation.type === 'offer'"
            class="btn btn-primary"
            @click.stop="contactDelegation(delegation.id)"
          >
            联系TA
          </button>
        </div>
      </div>

      <div v-if="filteredDelegations.length === 0" class="empty">
        <p>暂无委托信息</p>
      </div>
    </div>

    <!-- 发布委托弹窗 -->
    <div v-if="showPublishModal" class="modal" @click="showPublishModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>发布委托</h3>
          <span @click="showPublishModal = false">✕</span>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>委托类型</label>
            <div class="type-selector">
              <button
                class="type-btn"
                :class="{ active: publishForm.type === 'request' }"
                @click="publishForm.type = 'request'"
              >
                求带
              </button>
              <button
                class="type-btn"
                :class="{ active: publishForm.type === 'offer' }"
                @click="publishForm.type = 'offer'"
              >
                可带
              </button>
            </div>
          </div>
          <div class="form-group">
            <label>标题</label>
            <input v-model="publishForm.title" type="text" placeholder="请输入标题" />
          </div>
          <div class="form-group">
            <label>店铺名称</label>
            <input v-model="publishForm.shopName" type="text" placeholder="请输入店铺名称" />
          </div>
          <div class="form-group">
            <label>描述</label>
            <textarea
              v-model="publishForm.description"
              placeholder="请描述您的需求或可带物品"
              rows="4"
            ></textarea>
          </div>
          <div class="form-group">
            <label>酬金（元）</label>
            <input v-model.number="publishForm.reward" type="number" placeholder="0" />
          </div>
          <div v-if="publishForm.type === 'offer'" class="form-group">
            <label>可带物品（用逗号分隔）</label>
            <input
              v-model="publishForm.items"
              type="text"
              placeholder="例如：奶茶,炸鸡,汉堡"
            />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-outline" @click="showPublishModal = false">取消</button>
          <button class="btn btn-primary" @click="publishDelegation">发布</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const activeTab = ref('all')
const showPublishModal = ref(false)

const tabs = [
  { label: '全部', value: 'all' },
  { label: '求带', value: 'request' },
  { label: '可带', value: 'offer' }
]

const publishForm = ref({
  type: 'request',
  title: '',
  shopName: '',
  description: '',
  reward: 0,
  items: ''
})

const delegations = ref([
  {
    id: 1,
    type: 'request',
    userName: '小明',
    title: '求带校园食堂的宫保鸡丁',
    description: '今天太忙了，求好心人帮忙带一份宫保鸡丁，送到3号宿舍楼',
    shopName: '校园食堂',
    reward: 5,
    createTime: '2小时前',
    status: 'pending'
  },
  {
    id: 2,
    type: 'offer',
    userName: '小红',
    title: '可带咖啡时光的饮品',
    description: '我要去咖啡时光，可以帮忙带饮品，需要的联系我',
    shopName: '咖啡时光',
    reward: 3,
    createTime: '1小时前',
    status: 'pending',
    items: ['拿铁', '美式', '卡布奇诺']
  },
  {
    id: 3,
    type: 'request',
    userName: '小李',
    title: '求带麻辣香锅',
    description: '想吃麻辣香锅，但是不想出门，求带',
    shopName: '麻辣香锅',
    reward: 8,
    createTime: '30分钟前',
    status: 'pending'
  }
])

const filteredDelegations = computed(() => {
  if (activeTab.value === 'all') {
    return delegations.value
  }
  return delegations.value.filter(d => d.type === activeTab.value)
})

function viewDelegation(delegation) {
  // 查看委托详情
  console.log('查看委托:', delegation)
}

function acceptDelegation(id) {
  const delegation = delegations.value.find(d => d.id === id)
  if (delegation) {
    delegation.status = 'accepted'
    alert('已接受委托')
  }
}

function contactDelegation(id) {
  alert('联系功能开发中')
}

function publishDelegation() {
  if (!publishForm.value.title || !publishForm.value.shopName) {
    alert('请填写完整信息')
    return
  }

  const newDelegation = {
    id: delegations.value.length + 1,
    type: publishForm.value.type,
    userName: '我',
    title: publishForm.value.title,
    description: publishForm.value.description,
    shopName: publishForm.value.shopName,
    reward: publishForm.value.reward,
    createTime: '刚刚',
    status: 'pending',
    items: publishForm.value.items ? publishForm.value.items.split(',') : []
  }

  delegations.value.unshift(newDelegation)
  showPublishModal.value = false
  publishForm.value = {
    type: 'request',
    title: '',
    shopName: '',
    description: '',
    reward: 0,
    items: ''
  }
}
</script>

<style scoped>
.delegation-square {
  min-height: 100vh;
  background: var(--bg-light);
}

.header {
  background: white;
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid var(--border-color);
}

.header h2 {
  font-size: 20px;
  font-weight: 600;
}

.header .btn {
  padding: 8px 16px;
  font-size: 14px;
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

.delegation-list {
  padding: 12px 16px;
}

.delegation-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  cursor: pointer;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--primary-color);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
}

.delegation-time {
  font-size: 12px;
  color: var(--text-light);
}

.delegation-type {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
}

.delegation-type.request {
  background: #fff3cd;
  color: #856404;
}

.delegation-type.offer {
  background: #d1ecf1;
  color: #0c5460;
}

.card-content h4 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
}

.delegation-desc {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 12px;
  line-height: 1.5;
}

.delegation-info {
  display: flex;
  gap: 16px;
  font-size: 14px;
}

.shop-name {
  color: var(--text-secondary);
}

.reward {
  color: var(--secondary-color);
  font-weight: 600;
}

.card-footer {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--border-color);
}

.offer-items {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.offer-item {
  padding: 4px 12px;
  background: var(--bg-light);
  border-radius: 12px;
  font-size: 12px;
  color: var(--text-secondary);
}

.card-actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}

.card-actions .btn {
  padding: 8px 16px;
  font-size: 14px;
}

.empty {
  text-align: center;
  padding: 60px 0;
  color: var(--text-light);
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1001;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-content {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 500px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
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
  padding: 16px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 14px;
  font-family: inherit;
}

.type-selector {
  display: flex;
  gap: 12px;
}

.type-btn {
  flex: 1;
  padding: 10px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background: white;
  cursor: pointer;
  transition: all 0.3s;
}

.type-btn.active {
  background: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.modal-footer {
  display: flex;
  gap: 12px;
  padding: 16px;
  border-top: 1px solid var(--border-color);
}

.modal-footer .btn {
  flex: 1;
}
</style>

