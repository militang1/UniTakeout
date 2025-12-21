<template>
  <div class="product-manage">
    <div class="page-header">
      <h3>商品管理</h3>
      <button class="btn btn-primary" @click="showAddModal = true">添加商品</button>
    </div>

    <div class="card">
      <div class="filter-bar">
        <select v-model="selectedCategory" @change="loadProducts" class="category-select">
          <option value="">全部分类</option>
          <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
        </select>
      </div>

      <table class="table">
        <thead>
          <tr>
            <th>商品名称</th>
            <th>分类</th>
            <th>价格</th>
            <th>销量</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in products" :key="product.id">
            <td>
              <div class="product-info">
                <img v-if="product.image" :src="product.image" :alt="product.name" class="product-thumb" />
                <span>{{ product.name }}</span>
              </div>
            </td>
            <td>{{ product.categoryName || '-' }}</td>
            <td>¥{{ product.price }}</td>
            <td>{{ product.sales || 0 }}</td>
            <td>
              <button class="btn btn-outline btn-sm" @click="editProduct(product)">编辑</button>
              <button class="btn btn-danger btn-sm" @click="deleteProduct(product.id)">删除</button>
            </td>
          </tr>
          <tr v-if="products.length === 0">
            <td colspan="5" class="empty">暂无商品</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 添加/编辑商品弹窗 -->
    <div v-if="showAddModal || showEditModal" class="modal" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ showEditModal ? '编辑商品' : '添加商品' }}</h3>
          <span @click="closeModal">✕</span>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>商品名称</label>
            <input v-model="productForm.name" type="text" placeholder="请输入商品名称" required />
          </div>
          <div class="form-group">
            <label>分类</label>
            <select v-model.number="productForm.categoryId" required>
              <option value="">请选择分类</option>
              <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
            </select>
          </div>
          <div class="form-group">
            <label>价格（元）</label>
            <input v-model.number="productForm.price" type="number" step="0.01" placeholder="0.00" required />
          </div>
          <div class="form-group">
            <label>商品描述</label>
            <textarea v-model="productForm.description" placeholder="请输入商品描述" rows="3"></textarea>
          </div>
          <div class="form-group">
            <label>商品图片</label>
            <div class="image-upload">
              <input ref="productFileInput" type="file" accept="image/*" @change="handleProductImageChange"
                style="display:none" />

              <div class="image-preview clickable" @click="triggerProductFileSelect"
                :title="productForm.image ? '点击更换图片' : '点击上传图片'">
                <img v-if="productForm.image" :src="productForm.image" alt="商品图片" class="preview" />

                <div v-else class="placeholder">
                  <div class="placeholder-text">点击上传</div>
                </div>

                <div class="upload-overlay" v-if="productUploading">上传中...</div>
              </div>

              <input v-model="productForm.image" type="url" placeholder="https://example.com/image.jpg"
                class="url-input" />
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-outline" @click="closeModal">取消</button>
          <button class="btn btn-primary" @click="saveProduct" :disabled="saving || productUploading">
            {{ saving ? '保存中...' : (productUploading ? '上传中...' : '保存') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { productApi, categoryApi, uploadApi } from '../utils/request'

const products = ref([])
const categories = ref([])
const selectedCategory = ref('')
const showAddModal = ref(false)
const showEditModal = ref(false)
const saving = ref(false)
const editingProductId = ref(null)
const productUploading = ref(false)
const productFileInput = ref(null)

function triggerProductFileSelect() {
  if (productFileInput.value) productFileInput.value.click()
}

const productForm = ref({
  name: '',
  categoryId: null,
  price: 0,
  description: '',
  image: ''
})

onMounted(() => {
  loadCategories()
  loadProducts()
})

async function loadCategories() {
  try {
    const response = await categoryApi.getCategoryList()
    if (response.code === 200 && response.data) {
      categories.value = response.data
    }
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

async function loadProducts() {
  try {
    const params = {}
    if (selectedCategory.value) {
      params.categoryId = selectedCategory.value
    }
    const response = await productApi.getProductList(params)
    if (response.code === 200 && response.data) {
      products.value = response.data.list || response.data || []
    }
  } catch (error) {
    console.error('加载商品列表失败:', error)
    alert(error.message || '加载失败，请稍后重试')
  }
}

function editProduct(product) {
  editingProductId.value = product.id
  productForm.value = {
    name: product.name,
    categoryId: product.categoryId,
    price: product.price,
    description: product.description || '',
    image: product.image || ''
  }
  showEditModal.value = true
}

function closeModal() {
  showAddModal.value = false
  showEditModal.value = false
  editingProductId.value = null
  productForm.value = {
    name: '',
    categoryId: null,
    price: 0,
    description: '',
    image: ''
  }
}

async function saveProduct() {
  if (!productForm.value.name || !productForm.value.categoryId || !productForm.value.price) {
    alert('请填写完整信息')
    return
  }

  try {
    saving.value = true
    let response
    if (showEditModal.value && editingProductId.value) {
      response = await productApi.updateProduct(editingProductId.value, productForm.value)
    } else {
      response = await productApi.createProduct(productForm.value)
    }

    if (response.code === 200) {
      alert(showEditModal.value ? '更新成功' : '创建成功')
      closeModal()
      loadProducts()
    } else {
      alert(response.message || '操作失败')
    }
  } catch (error) {
    console.error('保存商品失败:', error)
    alert(error.message || '保存失败，请稍后重试')
  } finally {
    saving.value = false
  }
}

async function handleProductImageChange(event) {
  const file = event.target.files && event.target.files[0]
  if (!file) return
  if (file.size > 5 * 1024 * 1024) {
    alert('文件大小不能超过 5MB')
    event.target.value = ''
    return
  }

  productUploading.value = true
  try {
    const response = await uploadApi.uploadImage(file)
    if (response.code === 200) {
      productForm.value.image = response.data
    } else {
      alert(response.message || '上传失败')
    }
  } catch (err) {
    console.error('上传商品图片失败:', err)
    alert(err.message || '上传失败，请稍后重试')
  } finally {
    productUploading.value = false
    event.target.value = ''
  }
}

async function deleteProduct(id) {
  if (!confirm('确定要删除这个商品吗？')) {
    return
  }

  try {
    const response = await productApi.deleteProduct(id)
    if (response.code === 200) {
      alert('删除成功')
      loadProducts()
    } else {
      alert(response.message || '删除失败')
    }
  } catch (error) {
    console.error('删除商品失败:', error)
    alert(error.message || '删除失败，请稍后重试')
  }
}
</script>

<style scoped>
.product-manage {
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
  margin-bottom: 16px;
}

.category-select {
  padding: 8px 12px;
  border: 1px solid var(--border-color);
  border-radius: 6px;
  font-size: 14px;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-thumb {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  object-fit: cover;
}

.btn-sm {
  padding: 6px 12px;
  font-size: 12px;
  margin-right: 8px;
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
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
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

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px;
  border-top: 1px solid var(--border-color);
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
