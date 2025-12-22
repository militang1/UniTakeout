import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Layout from '../layouts/Layout.vue'
import Dashboard from '../views/Dashboard.vue'
import ShopManage from '../views/ShopManage.vue'
import ProductManage from '../views/ProductManage.vue'
import CategoryManage from '../views/CategoryManage.vue'
import OrderManage from '../views/OrderManage.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard
      },
      {
        path: 'shop',
        name: 'ShopManage',
        component: ShopManage
      },
      {
        path: 'products',
        name: 'ProductManage',
        component: ProductManage
      },
      {
        path: 'categories',
        name: 'CategoryManage',
        component: CategoryManage
      },
      {
        path: 'orders',
        name: 'OrderManage',
        component: OrderManage
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('admin_token')
  if (to.path === '/login') {
    if (token) {
      next('/')
    } else {
      next()
    }
  } else {
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router





