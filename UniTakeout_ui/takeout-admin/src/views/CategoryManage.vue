<template>
  <div class="category-manage">
    <div class="page-header">
      <h3>分类管理</h3>
      <button class="btn btn-primary" @click="showAddModal = true">添加分类</button>
    </div>

    <div class="card">
      <table class="table">
        <thead>
          <tr>
            <th>分类名称</th>
            <th>排序</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="category in categories" :key="category.id">
            <td>{{ category.name }}</td>
            <td>{{ category.sortOrder || 0 }}</td>
            <td>{{ category.createTime || '-' }}</td>
            <td>
              <button class="btn btn-outline btn-sm" @click="editCategory(category)">编辑</button>
              <button class="btn btn-danger btn-sm" @click="deleteCategory(category.id)">删除</button>
            </td>
          </tr>
          <tr v-if="categories.length === 0">
            <td colspan="4" class="empty">暂无分类</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 添加/编辑分类弹窗 -->
    <div v-if="showAddModal || showEditModal" class="modal" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ showEditModal ? '编辑分类' : '添加分类' }}</h3>
          <span @click="closeModal">✕</span>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>分类名称</label>
            <input v-model="categoryForm.name" type="text" placeholder="请输入分类名称" required />
          </div>
          <div class="form-group">
            <label>排序</label>
            <input v-model.number="categoryForm.sortOrder" type="number" placeholder="0" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-outline" @click="closeModal">取消</button>
          <button class="btn btn-primary" @click="saveCategory" :disabled="saving">
            {{ saving ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { categoryApi } from '../utils/request'

const categories = ref([])
const showAddModal = ref(false)
const showEditModal = ref(false)
const saving = ref(false)
const editingCategoryId = ref(null)

const categoryForm = ref({
  name: '',
  sortOrder: 0
})

onMounted(() => {
  loadCategories()
})

async function loadCategories() {
  try {
    const response = await categoryApi.getCategoryList()
    if (response.code === 200 && response.data) {
      categories.value = response.data
    }
  } catch (error) {
    console.error('加载分类列表失败:', error)
    alert(error.message || '加载失败，请稍后重试')
  }
}

function editCategory(category) {
  editingCategoryId.value = category.id
  categoryForm.value = {
    name: category.name,
    sortOrder: category.sortOrder || 0
  }
  showEditModal.value = true
}

function closeModal() {
  showAddModal.value = false
  showEditModal.value = false
  editingCategoryId.value = null
  categoryForm.value = {
    name: '',
    sortOrder: 0
  }
}

async function saveCategory() {
  if (!categoryForm.value.name) {
    alert('请输入分类名称')
    return
  }

  try {
    saving.value = true
    let response
    if (showEditModal.value && editingCategoryId.value) {
      response = await categoryApi.updateCategory(editingCategoryId.value, categoryForm.value)
    } else {
      response = await categoryApi.createCategory(categoryForm.value)
    }

    if (response.code === 200) {
      alert(showEditModal.value ? '更新成功' : '创建成功')
      closeModal()
      loadCategories()
    } else {
      alert(response.message || '操作失败')
    }
  } catch (error) {
    console.error('保存分类失败:', error)
    alert(error.message || '保存失败，请稍后重试')
  } finally {
    saving.value = false
  }
}

async function deleteCategory(id) {
  if (!confirm('确定要删除这个分类吗？删除后该分类下的商品将无法分类。')) {
    return
  }

  try {
    const response = await categoryApi.deleteCategory(id)
    if (response.code === 200) {
      alert('删除成功')
      loadCategories()
    } else {
      alert(response.message || '删除失败')
    }
  } catch (error) {
    console.error('删除分类失败:', error)
    alert(error.message || '删除失败，请稍后重试')
  }
}
</script>

<style scoped>
.category-manage {
  max-width: 1000px;
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
  max-width: 500px;
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
</style>




