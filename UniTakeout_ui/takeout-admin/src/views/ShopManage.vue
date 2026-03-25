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
          <label>店铺图片</label>
          <div class="image-upload">
            <input ref="shopFileInput" type="file" accept="image/*" @change="handleShopImageChange"
              style="display:none" />

            <div class="image-preview clickable" @click="triggerShopFileSelect"
              :title="shopForm.image ? '点击更换图片' : '点击上传图片'">
              <img v-if="shopForm.image" :src="shopForm.image" alt="店铺图片" class="preview" />

              <div v-else class="placeholder">
                <div class="placeholder-icon">📷</div>
                <div class="placeholder-text">点击上传</div>
              </div>

              <div class="upload-overlay" v-if="shopUploading">上传中...</div>
            </div>

            <input v-model="shopForm.image" type="url" placeholder="https://example.com/image.jpg" class="url-input" />
          </div>
        </div>
        <div class="form-actions">
          <button type="submit" class="btn btn-primary" :disabled="saving || shopUploading">
            {{ saving ? '保存中...' : (shopUploading ? '上传中...' : '保存') }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { shopApi, uploadApi } from '../utils/request'

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
const shopUploading = ref(false)
const shopFileInput = ref(null)

function triggerShopFileSelect() {
  if (shopFileInput.value) shopFileInput.value.click()
}

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

async function handleShopImageChange(event) {
  const file = event.target.files && event.target.files[0]
  if (!file) return
  // 限制 5MB
  if (file.size > 5 * 1024 * 1024) {
    alert('文件大小不能超过 5MB')
    event.target.value = ''
    return
  }

  shopUploading.value = true
  try {
    const response = await uploadApi.uploadImage(file)
    if (response.code === 200) {
      shopForm.value.image = response.data
      // 可选：提示
      // alert('上传成功')
    } else {
      alert(response.message || '上传失败')
    }
  } catch (err) {
    console.error('上传店铺图片失败:', err)
    alert(err.message || '上传失败，请稍后重试')
  } finally {
    shopUploading.value = false
    event.target.value = ''
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

.image-upload input[type="file"] {
  display: none;
}

.image-upload {
  display: flex;
  gap: 12px;
  align-items: center;
}

.image-preview {
  width: 160px;
  height: 110px;
  border-radius: 10px;
  overflow: hidden;
  background: var(--bg-soft, #f8f9fa);
  border: 1px dashed var(--border-color);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  cursor: pointer;
  transition: transform .12s ease, box-shadow .12s ease;
}

.image-preview:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
}

.image-preview .preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.placeholder {
  text-align: center;
  color: var(--text-muted);
}

.placeholder-icon {
  font-size: 28px;
}

.placeholder-text {
  margin-top: 6px;
  font-size: 13px;
}

.upload-overlay {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.6);
  font-size: 14px;
  color: var(--text-primary);
}

.url-input {
  flex: 1;
  padding: 8px 10px;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  font-size: 13px;
}
</style>



