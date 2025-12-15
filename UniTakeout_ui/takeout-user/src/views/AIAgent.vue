<template>
  <div class="ai-agent">
    <div class="header">
      <h2>AI智能推荐</h2>
      <p class="subtitle">让AI帮你选择美食，一键下单</p>
    </div>

    <div class="container">
      <div class="chat-container">
        <div class="chat-messages" ref="chatContainer">
          <div
            v-for="(message, index) in messages"
            :key="index"
            class="message"
            :class="message.type"
          >
            <div v-if="message.type === 'ai'" class="avatar">🤖</div>
            <div class="message-content">
              <div class="message-text">{{ message.text }}</div>
              <div v-if="message.recommendations" class="recommendations">
                <div
                  v-for="rec in message.recommendations"
                  :key="rec.id"
                  class="recommendation-card"
                  @click="selectRecommendation(rec)"
                >
                  <div class="rec-image">
                    <img :src="rec.image" :alt="rec.name" />
                  </div>
                  <div class="rec-info">
                    <h4>{{ rec.name }}</h4>
                    <p class="rec-shop">{{ rec.shopName }}</p>
                    <div class="rec-footer">
                      <span class="rec-price">¥{{ rec.price }}</span>
                      <span class="rec-rating">⭐ {{ rec.rating }}</span>
                    </div>
                  </div>
                </div>
              </div>
              <div v-if="message.order" class="order-summary">
                <h4>推荐订单</h4>
                <div v-for="item in message.order.items" :key="item.id" class="order-item">
                  <span>{{ item.name }} x{{ item.quantity }}</span>
                  <span>¥{{ item.price * item.quantity }}</span>
                </div>
                <div class="order-total">
                  <span>总计：¥{{ message.order.total }}</span>
                </div>
                <button class="btn btn-primary" @click="confirmOrder(message.order)">
                  确认下单
                </button>
              </div>
            </div>
          </div>
        </div>

        <div class="chat-input">
          <input
            v-model="inputText"
            type="text"
            placeholder="告诉AI你的需求，例如：我想吃辣的，预算30元"
            @keyup.enter="sendMessage"
          />
          <button class="send-btn" @click="sendMessage">发送</button>
        </div>
      </div>

      <div class="quick-questions">
        <h3>快速提问</h3>
        <div class="question-tags">
          <span
            v-for="question in quickQuestions"
            :key="question"
            class="question-tag"
            @click="inputText = question; sendMessage()"
          >
            {{ question }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { useCartStore } from '../stores/cart'

const cartStore = useCartStore()
const chatContainer = ref(null)
const inputText = ref('')

const quickQuestions = [
  '推荐今天的午餐',
  '我想吃辣的',
  '预算30元以内',
  '帮我选个套餐',
  '有什么新品推荐'
]

const messages = ref([
  {
    type: 'ai',
    text: '你好！我是AI美食助手，可以帮你推荐美食并自动下单。告诉我你的需求吧！',
    recommendations: null,
    order: null
  }
])

function scrollToBottom() {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
  })
}

function sendMessage() {
  if (!inputText.value.trim()) return

  // 添加用户消息
  messages.value.push({
    type: 'user',
    text: inputText.value
  })

  const userInput = inputText.value
  inputText.value = ''
  scrollToBottom()

  // 模拟AI回复
  setTimeout(() => {
    let aiResponse = {
      type: 'ai',
      text: '',
      recommendations: null,
      order: null
    }

    if (userInput.includes('推荐') || userInput.includes('推荐')) {
      aiResponse.text = '根据你的需求，我为你推荐以下美食：'
      aiResponse.recommendations = [
        {
          id: 1,
          name: '麻辣香锅',
          shopName: '麻辣香锅店',
          price: 35,
          rating: 4.8,
          image: 'https://via.placeholder.com/100'
        },
        {
          id: 2,
          name: '宫保鸡丁',
          shopName: '校园食堂',
          price: 18,
          rating: 4.7,
          image: 'https://via.placeholder.com/100'
        },
        {
          id: 3,
          name: '水煮鱼',
          shopName: '川味餐厅',
          price: 42,
          rating: 4.9,
          image: 'https://via.placeholder.com/100'
        }
      ]
    } else if (userInput.includes('下单') || userInput.includes('点餐')) {
      aiResponse.text = '我为你推荐以下组合，点击确认即可下单：'
      aiResponse.order = {
        shopName: '校园食堂',
        items: [
          { id: 1, name: '宫保鸡丁', quantity: 1, price: 18 },
          { id: 2, name: '米饭', quantity: 2, price: 2 },
          { id: 3, name: '可乐', quantity: 1, price: 5 }
        ],
        total: 27
      }
    } else {
      aiResponse.text = '我理解你的需求。让我为你推荐一些美食：'
      aiResponse.recommendations = [
        {
          id: 1,
          name: '推荐套餐A',
          shopName: '校园食堂',
          price: 25,
          rating: 4.8,
          image: 'https://via.placeholder.com/100'
        }
      ]
    }

    messages.value.push(aiResponse)
    scrollToBottom()
  }, 1000)
}

function selectRecommendation(rec) {
  messages.value.push({
    type: 'user',
    text: `我想点${rec.name}`
  })

  setTimeout(() => {
    messages.value.push({
      type: 'ai',
      text: `好的，已为你加入购物车。${rec.name} ¥${rec.price}`,
      recommendations: null,
      order: null
    })
    cartStore.addItem({
      id: rec.id,
      name: rec.name,
      price: rec.price
    })
    scrollToBottom()
  }, 500)
}

function confirmOrder(order) {
  // 将订单添加到购物车
  order.items.forEach(item => {
    cartStore.addItem({
      id: item.id,
      name: item.name,
      price: item.price,
      quantity: item.quantity
    })
  })

  messages.value.push({
    type: 'user',
    text: '确认下单'
  })

  setTimeout(() => {
    messages.value.push({
      type: 'ai',
      text: '订单已创建！请前往购物车查看并完成支付。',
      recommendations: null,
      order: null
    })
    scrollToBottom()
  }, 500)
}
</script>

<style scoped>
.ai-agent {
  min-height: 100vh;
  background: var(--bg-light);
}

.header {
  background: linear-gradient(135deg, var(--primary-color) 0%, #06ad56 100%);
  padding: 24px 16px;
  color: white;
  text-align: center;
}

.header h2 {
  font-size: 24px;
  margin-bottom: 8px;
}

.subtitle {
  font-size: 14px;
  opacity: 0.9;
}

.chat-container {
  background: white;
  border-radius: 12px;
  margin: 16px;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 300px);
  max-height: 600px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.message {
  display: flex;
  margin-bottom: 16px;
  gap: 12px;
}

.message.user {
  flex-direction: row-reverse;
}

.message.user .message-content {
  background: var(--primary-color);
  color: white;
}

.message.ai .avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.message-content {
  flex: 1;
  background: var(--bg-secondary);
  border-radius: 12px;
  padding: 12px;
}

.message-text {
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 8px;
}

.recommendations {
  margin-top: 12px;
}

.recommendation-card {
  background: white;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 8px;
  display: flex;
  cursor: pointer;
  transition: transform 0.2s;
}

.recommendation-card:active {
  transform: scale(0.98);
}

.rec-image {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  overflow: hidden;
  margin-right: 12px;
  flex-shrink: 0;
}

.rec-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.rec-info {
  flex: 1;
}

.rec-info h4 {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
}

.rec-shop {
  font-size: 12px;
  color: var(--text-light);
  margin-bottom: 8px;
}

.rec-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.rec-price {
  font-size: 16px;
  font-weight: 600;
  color: var(--primary-color);
}

.rec-rating {
  font-size: 12px;
  color: #ff9500;
}

.order-summary {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.order-summary h4 {
  font-size: 16px;
  margin-bottom: 8px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.order-total {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
}

.chat-input {
  display: flex;
  padding: 12px;
  border-top: 1px solid var(--border-color);
  gap: 8px;
}

.chat-input input {
  flex: 1;
  padding: 10px;
  border: 1px solid var(--border-color);
  border-radius: 20px;
  font-size: 14px;
}

.send-btn {
  padding: 10px 20px;
  background: var(--primary-color);
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
}

.quick-questions {
  margin: 16px;
  background: white;
  border-radius: 12px;
  padding: 16px;
}

.quick-questions h3 {
  font-size: 16px;
  margin-bottom: 12px;
}

.question-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.question-tag {
  padding: 8px 16px;
  background: var(--bg-light);
  border-radius: 16px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.question-tag:active {
  background: var(--primary-color);
  color: white;
}
</style>

