<template>
  <div class="delegation-square">
    <div class="header">
      <h2>委托广场</h2>
      <button class="btn btn-primary" @click="showPublishModal = true">发布委托</button>
    </div>

    <div class="tabs">
      <div v-for="tab in tabs" :key="tab.value" class="tab" :class="{ active: activeTab === tab.value }"
        @click="activeTab = tab.value">
        {{ tab.label }}
      </div>
    </div>

    <div class="delegation-list">
      <div v-if="loading" class="loading">加载中...</div>
      <div v-for="delegation in filteredDelegations" :key="delegation.id" class="delegation-card"
        @click="viewDelegation(delegation)">
        <div class="card-header">
          <div class="user-info">
            <div class="avatar">{{ (delegation.userName || 'U').charAt(0) }}</div>
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
          <button v-if="delegation.type === 'request'" class="btn btn-primary"
            @click.stop="acceptDelegation(delegation.id)">
            接受委托
          </button>
          <button v-if="delegation.type === 'offer'" class="btn btn-primary"
            @click.stop="contactDelegation(delegation.id)">
            联系TA
          </button>
        </div>
      </div>

      <div v-if="!loading && filteredDelegations.length === 0" class="empty">
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
              <button class="type-btn" :class="{ active: publishForm.type === 'request' }"
                @click="publishForm.type = 'request'">
                求带
              </button>
              <button class="type-btn" :class="{ active: publishForm.type === 'offer' }"
                @click="publishForm.type = 'offer'">
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
            <textarea v-model="publishForm.description" placeholder="请描述您的需求或可带物品" rows="4"></textarea>
          </div>
          <div class="form-group">
            <label>酬金（元）</label>
            <input v-model.number="publishForm.reward" type="number" placeholder="0" />
          </div>
          <div v-if="publishForm.type === 'offer'" class="form-group">
            <label>可带物品（用逗号分隔）</label>
            <input v-model="publishForm.items" type="text" placeholder="例如：奶茶,炸鸡,汉堡" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-outline" @click="showPublishModal = false">取消</button>
          <button class="btn btn-primary" @click="publishDelegation" :disabled="submitting">
            {{ submitting ? '发布中...' : '发布' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { delegationApi } from '../utils/request'

const activeTab = ref('all')
const showPublishModal = ref(false)
const loading = ref(false)
const submitting = ref(false)

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

const delegations = ref([])

const filteredDelegations = computed(() => {
  if (activeTab.value === 'all') {
    return delegations.value
  }
  return delegations.value.filter(d => d.type === activeTab.value)
})

onMounted(() => {
  loadDelegations()
})

watch(activeTab, () => {
  // 后端也支持 type 过滤，这里优先走后端过滤减少数据量
  loadDelegations()
})

async function loadDelegations() {
  try {
    loading.value = true
    const params = {}
    if (activeTab.value !== 'all') {
      params.type = activeTab.value
    }
    // 默认拉取进行中的委托（如需全部可去掉此行）
    // params.status = 'pending'
    const response = await delegationApi.getDelegationList(params)
    if (response.code === 200 && response.data) {
      delegations.value = response.data.list || response.data || []
    }
  } catch (error) {
    console.error('加载委托列表失败:', error)
    alert(error.message || '加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

async function viewDelegation(delegation) {
  // 可按需补“详情弹窗”，此处保持轻量
  console.log('查看委托:', delegation)
}

async function acceptDelegation(id) {
  try {
    const response = await delegationApi.acceptDelegation(id)
    if (response.code === 200) {
      alert('已接受委托')
      await loadDelegations()
    } else {
      alert(response.message || '接受失败')
    }
  } catch (error) {
    console.error('接受委托失败:', error)
    alert(error.message || '接受失败，请稍后重试')
  }
}

function contactDelegation(id) {
  alert('联系功能开发中')
}

async function publishDelegation() {
  if (submitting.value) return

  if (!publishForm.value.title?.trim() || !publishForm.value.shopName?.trim()) {
    alert('请填写标题和店铺名称')
    return
  }

  try {
    submitting.value = true
    const items = publishForm.value.items
      ? publishForm.value.items
        .split(',')
        .map(s => s.trim())
        .filter(Boolean)
      : []

    const payload = {
      type: publishForm.value.type,
      title: publishForm.value.title.trim(),
      description: (publishForm.value.description || '').trim(),
      shopName: publishForm.value.shopName.trim(),
      reward: Number(publishForm.value.reward || 0),
      items
    }

    const response = await delegationApi.createDelegation(payload)
    if (response.code === 200) {
      alert('委托发布成功')
      showPublishModal.value = false
      publishForm.value = {
        type: 'request',
        title: '',
        shopName: '',
        description: '',
        reward: 0,
        items: ''
      }
      await loadDelegations()
    } else {
      alert(response.message || '发布失败')
    }
  } catch (error) {
    console.error('发布委托失败:', error)
    alert(error.message || '发布失败，请稍后重试')
  } finally {
    submitting.value = false
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

.loading {
  text-align: center;
  padding: 40px 0;
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
