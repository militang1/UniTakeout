<template>
  <div class="flex flex-col h-screen bg-[#F8F9FA] font-sans text-[#1A1A1A]">
    <!-- Header -->
    <header class="bg-gradient-to-r from-[#FF6B35] to-[#FF8C42] text-white p-4 shadow-md z-10">
      <div class="max-w-3xl mx-auto flex items-center justify-between">
        <div class="flex items-center gap-3">
          <div class="bg-white/20 p-2 rounded-xl backdrop-blur-sm">
            <UtensilsCrossed :size="24" />
          </div>
          <div>
            <h1 class="text-xl font-bold tracking-tight">校园外卖 AI 助手</h1>
            <p class="text-xs opacity-90 flex items-center gap-1">
              <Sparkles :size="10" /> 智能推荐 · 极速下单
            </p>
          </div>
        </div>
        <div class="flex items-center gap-2 text-sm bg-white/10 px-3 py-1.5 rounded-full backdrop-blur-sm">
          <MapPin :size="14" />
          <span>{{ selectedAddress }}</span>
        </div>
      </div>
    </header>

    <!-- Chat Area -->
    <main class="flex-1 overflow-hidden relative">
      <div ref="scrollRef" class="h-full overflow-y-auto p-4 space-y-6 pb-32">
        <div class="max-w-3xl mx-auto space-y-6">
          <div
            v-for="msg in messages"
            :key="msg.id"
            class="flex gap-3"
            :class="msg.role === 'user' ? 'flex-row-reverse' : 'flex-row'"
          >
            <div 
              class="w-10 h-10 rounded-2xl flex items-center justify-center shrink-0 shadow-sm"
              :class="msg.role === 'user' ? 'bg-[#FF6B35] text-white' : 'bg-white text-[#FF6B35]'"
            >
              <User v-if="msg.role === 'user'" :size="20" />
              <Bot v-else :size="20" />
            </div>
            
            <div 
              class="max-w-[85%] flex flex-col"
              :class="msg.role === 'user' ? 'items-end' : 'items-start'"
            >
              <div 
                class="p-4 rounded-2xl shadow-sm"
                :class="msg.role === 'user' 
                  ? 'bg-[#FF6B35] text-white rounded-tr-none' 
                  : 'bg-white text-[#1A1A1A] rounded-tl-none border border-[#E0E0E0]'"
              >
                <div class="prose prose-sm max-w-none prose-p:leading-relaxed">
                  <VueMarkdown :source="msg.content" />
                  <span v-if="msg.isStreaming" class="inline-block w-1.5 h-4 bg-current animate-pulse ml-1 align-middle"></span>
                </div>
              </div>

              <!-- Recommendations -->
              <div v-if="msg.recommendations && msg.recommendations.length > 0" class="grid grid-cols-1 sm:grid-cols-2 gap-3 mt-3 w-full">
                <div
                  v-for="item in msg.recommendations"
                  :key="item.id"
                  class="bg-white p-3 rounded-2xl border border-[#E0E0E0] shadow-sm flex gap-3 group cursor-pointer hover:scale-[1.02] transition-transform"
                >
                  <img 
                    :src="item.image" 
                    :alt="item.name"
                    class="w-16 h-16 rounded-xl object-cover shrink-0"
                    referrerPolicy="no-referrer"
                  />
                  <div class="flex-1 min-w-0">
                    <h4 class="font-bold text-sm truncate">{{ item.name }}</h4>
                    <p class="text-xs text-[#666] truncate">{{ item.shopName }}</p>
                    <div class="flex items-center justify-between mt-1">
                      <span class="text-[#FF6B35] font-bold text-sm">¥{{ item.price }}</span>
                      <div class="flex items-center gap-0.5 text-[#FFB800]">
                        <Star :size="10" fill="currentColor" />
                        <span class="text-[10px] font-medium text-[#666]">{{ item.rating }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Order Preview -->
              <div v-if="msg.orderPreview" class="bg-white p-4 rounded-2xl border-2 border-dashed border-[#FF6B35]/30 bg-[#FFF9F6] space-y-3 mt-3 w-full">
                <div class="flex items-center gap-2 text-[#FF6B35] font-bold text-sm">
                  <ShoppingBag :size="16" />
                  <span>订单预览</span>
                </div>
                <div class="space-y-1">
                  <div v-for="(item, idx) in msg.orderPreview.items" :key="idx" class="flex justify-between text-sm">
                    <span class="text-[#4A4A4A]">{{ item.name }} x{{ item.quantity }}</span>
                    <span class="font-medium">¥{{ item.price * item.quantity }}</span>
                  </div>
                </div>
                <div class="pt-2 border-t border-[#FF6B35]/10 flex justify-between items-center">
                  <span class="text-xs text-[#666]">合计</span>
                  <span class="text-lg font-bold text-[#FF6B35]">¥{{ msg.orderPreview.total }}</span>
                </div>
                <button 
                  @click="handlePlaceOrder(msg.orderPreview)"
                  class="w-full py-2.5 bg-[#FF6B35] text-white rounded-xl font-bold text-sm shadow-lg shadow-[#FF6B35]/20 hover:bg-[#E85A24] transition-colors flex items-center justify-center gap-2"
                >
                  立即下单 <ChevronRight :size="16" />
                </button>
              </div>
            </div>
          </div>

          <!-- Loading Indicator -->
          <div v-if="isLoading && (!messages.length || !messages[messages.length - 1].isStreaming)" class="flex gap-3">
            <div class="w-10 h-10 rounded-2xl bg-white flex items-center justify-center shrink-0 border border-[#E0E0E0]">
              <Bot :size="20" class="text-[#FF6B35] animate-pulse" />
            </div>
            <div class="bg-white p-4 rounded-2xl rounded-tl-none border border-[#E0E0E0] shadow-sm">
              <div class="flex gap-1">
                <span class="w-2 h-2 bg-[#E0E0E0] rounded-full animate-bounce" style="animation-delay: 0ms"></span>
                <span class="w-2 h-2 bg-[#E0E0E0] rounded-full animate-bounce" style="animation-delay: 150ms"></span>
                <span class="w-2 h-2 bg-[#E0E0E0] rounded-full animate-bounce" style="animation-delay: 300ms"></span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Bottom Input Area -->
      <div class="absolute bottom-0 left-0 right-0 p-4 bg-gradient-to-t from-[#F8F9FA] via-[#F8F9FA] to-transparent">
        <div class="max-w-3xl mx-auto space-y-3">
          <!-- Quick Questions -->
          <div class="flex gap-2 overflow-x-auto pb-2 no-scrollbar">
            <button
              v-for="q in quickQuestions"
              :key="q"
              @click="handleQuickQuestion(q)"
              class="whitespace-nowrap px-4 py-1.5 bg-white border border-[#E0E0E0] rounded-full text-xs font-medium text-[#666] hover:border-[#FF6B35] hover:text-[#FF6B35] transition-colors shadow-sm"
            >
              {{ q }}
            </button>
          </div>

          <!-- Input Box -->
          <div class="relative flex items-center gap-2">
            <div class="flex-1 relative">
              <input
                v-model="input"
                type="text"
                @keypress.enter="handleSend()"
                placeholder="告诉我想吃什么..."
                class="w-full bg-white border border-[#E0E0E0] rounded-2xl px-5 py-4 pr-12 text-sm focus:outline-none focus:border-[#FF6B35] focus:ring-2 focus:ring-[#FF6B35]/10 shadow-lg transition-all"
              />
              <button
                @click="handleSend()"
                :disabled="!input.trim() || isLoading"
                class="absolute right-2 top-1/2 -translate-y-1/2 p-2 rounded-xl transition-all"
                :class="input.trim() && !isLoading 
                  ? 'bg-[#FF6B35] text-white shadow-md' 
                  : 'bg-[#F0F0F0] text-[#A0A0A0]'"
              >
                <Send :size="20" />
              </button>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- Order Confirmation Modal -->
    <Transition
      enter-active-class="transition duration-300 ease-out"
      enter-from-class="opacity-0 scale-95"
      enter-to-class="opacity-100 scale-100"
      leave-active-class="transition duration-200 ease-in"
      leave-from-class="opacity-100 scale-100"
      leave-to-class="opacity-0 scale-95"
    >
      <div v-if="showOrderModal && currentOrder" class="fixed inset-0 z-50 flex items-center justify-center p-4">
        <div @click="showOrderModal = false" class="absolute inset-0 bg-black/40 backdrop-blur-sm"></div>
        <div class="relative bg-white w-full max-w-md rounded-3xl shadow-2xl overflow-hidden transform transition-all">
          <div class="p-6 space-y-6">
            <div class="flex justify-between items-center">
              <h2 class="text-xl font-bold">确认订单</h2>
              <button @click="showOrderModal = false" class="p-1 hover:bg-gray-100 rounded-full">
                <X :size="24" />
              </button>
            </div>

            <div class="space-y-4">
              <div class="bg-[#F8F9FA] p-4 rounded-2xl space-y-3">
                <div class="flex items-start gap-3">
                  <MapPin class="text-[#FF6B35] mt-1" :size="18" />
                  <div>
                    <p class="text-xs text-[#666]">配送地址</p>
                    <p class="font-bold text-sm">{{ selectedAddress }}</p>
                  </div>
                </div>
                <div class="flex items-start gap-3">
                  <Clock class="text-[#FF6B35] mt-1" :size="18" />
                  <div>
                    <p class="text-xs text-[#666]">预计送达</p>
                    <p class="font-bold text-sm">30-45 分钟</p>
                  </div>
                </div>
              </div>

              <div class="space-y-3">
                <p class="text-xs font-bold text-[#666] uppercase tracking-wider">订单详情</p>
                <div class="space-y-2">
                  <div v-for="(item, idx) in currentOrder.items" :key="idx" class="flex justify-between items-center">
                    <span class="text-sm">{{ item.name }} <span class="text-[#999]">x{{ item.quantity }}</span></span>
                    <span class="font-bold text-sm">¥{{ item.price * item.quantity }}</span>
                  </div>
                </div>
              </div>

              <div class="pt-4 border-t border-gray-100 flex justify-between items-center">
                <span class="font-bold">实付款</span>
                <span class="text-2xl font-black text-[#FF6B35]">¥{{ currentOrder.total }}</span>
              </div>
            </div>

            <button 
              @click="confirmOrder"
              class="w-full py-4 bg-[#FF6B35] text-white rounded-2xl font-bold text-lg shadow-xl shadow-[#FF6B35]/30 hover:bg-[#E85A24] transition-all active:scale-95"
            >
              确认支付
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue';
import { 
  Send, 
  User, 
  Bot, 
  ShoppingBag, 
  Star, 
  ChevronRight, 
  X, 
  MapPin, 
  Clock,
  UtensilsCrossed,
  Sparkles
} from 'lucide-vue-next';
import VueMarkdown from 'vue-markdown-render';
import { MOCK_FOOD_ITEMS } from './constants';
import { chatWithAI } from './services/geminiService';

const messages = ref([
  {
    id: '1',
    role: 'assistant',
    content: '你好！我是你的校园外卖助手。今天想吃点什么？我可以为你推荐食堂的特色美食。',
  }
]);
const input = ref('');
const isLoading = ref(false);
const showOrderModal = ref(false);
const currentOrder = ref(null);
const selectedAddress = ref('宿舍楼 A 座 402');
const scrollRef = ref(null);

const quickQuestions = ['推荐点辣的', '我想吃快餐', '有什么特价菜？', '帮我点一份红烧肉'];

const scrollToBottom = async () => {
  await nextTick();
  if (scrollRef.value) {
    scrollRef.value.scrollTop = scrollRef.value.scrollHeight;
  }
};

watch(messages, scrollToBottom, { deep: true });

const handleSend = async (text = input.value) => {
  if (!text.trim() || isLoading.value) return;

  const userMessage = {
    id: Date.now().toString(),
    role: 'user',
    content: text,
  };

  messages.value.push(userMessage);
  input.value = '';
  isLoading.value = true;

  const assistantMessageId = (Date.now() + 1).toString();
  const assistantMessage = {
    id: assistantMessageId,
    role: 'assistant',
    content: '',
    isStreaming: true,
  };

  messages.value.push(assistantMessage);

  try {
    const stream = await chatWithAI(text);
    let fullContent = '';
    
    for await (const chunk of stream) {
      const chunkText = chunk.text || '';
      fullContent += chunkText;
      
      const msgIndex = messages.value.findIndex(m => m.id === assistantMessageId);
      if (msgIndex !== -1) {
        messages.value[msgIndex].content = fullContent;
      }
    }

    const foundRecs = MOCK_FOOD_ITEMS.filter(item => 
      fullContent.toLowerCase().includes(item.name.toLowerCase())
    );

    const msgIndex = messages.value.findIndex(m => m.id === assistantMessageId);
    if (msgIndex !== -1) {
      messages.value[msgIndex].isStreaming = false;
      if (foundRecs.length > 0) {
        messages.value[msgIndex].recommendations = foundRecs;
      }
      if (text.includes('下单') || text.includes('买') || text.includes('要')) {
        messages.value[msgIndex].orderPreview = {
          items: foundRecs.map(r => ({ id: r.id, name: r.name, price: r.price, quantity: 1 })),
          total: foundRecs.reduce((sum, r) => sum + r.price, 0)
        };
      }
    }

  } catch (error) {
    console.error('Chat error:', error);
    const msgIndex = messages.value.findIndex(m => m.id === assistantMessageId);
    if (msgIndex !== -1) {
      messages.value[msgIndex].content = '抱歉，我遇到了一些问题，请稍后再试。';
      messages.value[msgIndex].isStreaming = false;
    }
  } finally {
    isLoading.value = false;
  }
};

const handleQuickQuestion = (q) => {
  handleSend(q);
};

const handlePlaceOrder = (order) => {
  currentOrder.value = order;
  showOrderModal.value = true;
};

const confirmOrder = () => {
  alert('订单已提交！正在为您配送...');
  showOrderModal.value = false;
  currentOrder.value = null;
};
</script>

<style scoped>
.no-scrollbar::-webkit-scrollbar {
  display: none;
}
.no-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
