<template>
  <div class="shop-manage">
    <div class="card">
      <h3>店铺信息</h3>
      <form @submit.prevent="handleSubmit" class="shop-form">
        <div class="form-row">
          <div class="form-group">
            <label>店铺名称</label>
            <input v-model="shopForm.name" type="text" placeholder="请输入店铺名称" required />
          </div>
          <div class="form-group">
            <label>配送时间（分钟）</label>
            <input v-model.number="shopForm.deliveryTime" type="number" placeholder="20" required />
          </div>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>营业时间</label>
            <input v-model="shopForm.businessHours" type="text" placeholder="08:00-20:00" required />
          </div>
          <div class="form-group">
            <label>距离（公里）</label>
            <input v-model.number="shopForm.distance" type="number" step="0.1" placeholder="0.5" />
          </div>
        </div>
        <div class="form-group">
          <label>店铺描述</label>
          <textarea v-model="shopForm.description" placeholder="请输入店铺描述" rows="3"></textarea>
        </div>
        <div class="form-group">
          <label>店铺图片URL</label>
          <input v-model="shopForm.image" type="url" placeholder="https://example.com/image.jpg" />
        </div>
        <div class="form-actions">
          <button type="submit" class="btn btn-primary" :disabled="saving">
            {{ saving ? '保存中...' : '保存' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { shopApi } from '../utils/request'

const shopForm = ref({
  name: '',
  description: '',
  image: '',
  deliveryTime: 20,
  distance: 0.5,
  businessHours: '08:00-20:00'
})

const saving = ref(false)
const loading = ref(false)

onMounted(() => {
  loadShopInfo()
})

async function loadShopInfo() {
  try {
    loading.value = true
    const response = await shopApi.getShopInfo()
    if (response.code === 200 && response.data) {
      shopForm.value = {
        name: response.data.name || '',
        description: response.data.description || '',
        image: response.data.image || '',
        deliveryTime: response.data.deliveryTime || 20,
        distance: response.data.distance || 0.5,
        businessHours: response.data.businessHours || '08:00-20:00'
      }
    }
  } catch (error) {
    console.error('加载店铺信息失败:', error)
    alert(error.message || '加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  try {
    saving.value = true
    const response = await shopApi.updateShopInfo(shopForm.value)
    if (response.code === 200) {
      alert('保存成功')
    } else {
      alert(response.message || '保存失败')
    }
  } catch (error) {
    console.error('保存店铺信息失败:', error)
    alert(error.message || '保存失败，请稍后重试')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.shop-manage {
  max-width: 1000px;
}

.shop-manage h3 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
  color: var(--text-primary);
}

.shop-form {
  margin-top: 20px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 16px;
}

.form-actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

.form-actions .btn {
  min-width: 120px;
}
</style>




