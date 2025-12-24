<template>
    <div class="address-manage">
        <div class="page-header">
            <h2>地址管理</h2>
            <p class="hint">管理您的默认收货地址（会同步到用户信息）</p>
        </div>

        <div class="card">
            <div class="form-group">
                <label>昵称</label>
                <input v-model="form.nickname" type="text" placeholder="请输入昵称" />
            </div>

            <div class="form-group">
                <label>手机号（只读）</label>
                <input v-model="form.phone" type="tel" readonly />
            </div>

            <div class="form-group">
                <label>收货地址</label>
                <textarea v-model="form.address" rows="3" placeholder="请输入详细地址，如：xx校区 3号宿舍楼201"></textarea>
            </div>

            <div class="actions">
                <button class="btn btn-outline" @click="goBack">取消</button>
                <button class="btn btn-primary" @click="save">保存</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { userApi } from '../utils/request'

const router = useRouter()
const userStore = useUserStore()

const form = ref({
    nickname: '',
    phone: '',
    address: ''
})

onMounted(async () => {
    // 优先使用 store 中的信息，如无则尝试拉取
    if (userStore.userInfo && userStore.userInfo.nickname !== undefined) {
        form.value.nickname = userStore.userInfo.nickname || ''
        form.value.phone = userStore.userInfo.phone || ''
        form.value.address = userStore.userInfo.address || ''
    } else {
        try {
            const res = await userApi.getUserInfo()
            if (res.code === 200 && res.data) {
                form.value.nickname = res.data.nickname || ''
                form.value.phone = res.data.phone || ''
                form.value.address = res.data.address || ''
            }
        } catch (err) {
            console.error('获取用户信息失败', err)
        }
    }
})

function goBack() {
    router.back()
}

async function save() {
    if (!form.value.address || !form.value.address.trim()) {
        alert('请输入收货地址')
        return
    }

    try {
        const payload = {
            nickname: form.value.nickname,
            address: form.value.address
        }

        const res = await userApi.updateUserInfo(payload)
        if (res.code === 200 && res.data) {
            // 更新 store
            userStore.updateUserInfo(res.data)
            alert('保存成功')
            router.back()
        } else {
            alert(res.message || '保存失败')
        }
    } catch (err) {
        console.error('更新用户信息失败', err)
        alert(err.message || '保存失败，请稍后重试')
    }
}
</script>

<style scoped>
.address-manage {
    padding: 16px;
}

.page-header h2 {
    margin: 0 0 6px 0;
}

.hint {
    margin: 0 0 12px 0;
    color: #666;
    font-size: 13px;
}

.card {
    background: #fff;
    padding: 16px;
    border-radius: 8px;
}

.form-group {
    margin-bottom: 12px;
}

.form-group label {
    display: block;
    margin-bottom: 8px;
    font-weight: 500;
}

.form-group input,
.form-group textarea {
    width: 100%;
    padding: 10px;
    border: 1px solid var(--border-color);
    border-radius: 8px;
    font-size: 14px;
}

.actions {
    display: flex;
    gap: 12px;
    margin-top: 8px;
}
</style>
