import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import ShopList from '../views/ShopList.vue'
import ShopDetail from '../views/ShopDetail.vue'
import Order from '../views/Order.vue'
import OrderConfirm from '../views/OrderConfirm.vue'
import DelegationSquare from '../views/DelegationSquare.vue'
import AIAgent from '../views/AIAgent.vue'
import Profile from '../views/Profile.vue'
import Search from '../views/Search.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/shops',
    name: 'ShopList',
    component: ShopList
  },
  {
    path: '/shop/:id',
    name: 'ShopDetail',
    component: ShopDetail
  },
  {
    path: '/order',
    name: 'Order',
    component: Order
  },
  {
    path: '/order/confirm',
    name: 'OrderConfirm',
    component: OrderConfirm
  },
  {
    path: '/delegation',
    name: 'DelegationSquare',
    component: DelegationSquare
  },
  {
    path: '/ai-agent',
    name: 'AIAgent',
    component: AIAgent
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile
  },
  {
    path: '/search',
    name: 'Search',
    component: Search
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router

