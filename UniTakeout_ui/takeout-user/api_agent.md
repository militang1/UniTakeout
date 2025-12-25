# AI Agent 接口文档

## POST /api/ai/agent-suggest ✅
- 描述：使用 AI agent 分析用户的自然语言指令并返回一个固定格式的 JSON 建议（**不直接创建订单**）。前端应展示 `reply`（给用户的自然语言回复）和 `order`（可直接用于下单的 `OrderCreateDTO` 结构），等待用户确认后再调用下单接口。

- 请求体（application/json）：
```json
{
  "query": "我想要两份麻辣烫，预算 40 元，地址是 XX 宿舍",
  "userId": 123,
  "address": "XX 宿舍 2-101"
}
```

- 返回示例（200）：
```json
{
  "code": 1,
  "message": "成功",
  "data": {
    "reply": "我为您挑选了两份麻辣烫，预计总价 36 元，包含配送费 3 元。请确认是否下单。",
    "order": {
      "shopId": 10,
      "shopName": "麻辣烫小吃",
      "contactName": "用户",
      "contactPhone": "13800138000",
      "address": "XX 宿舍 2-101",
      "payMethod": "wechat",
      "items": [
        {"productId": 1001, "name": "麻辣烫（小份）", "quantity": 2, "price": 16}
      ],
      "goodsAmount": 32,
      "deliveryFee": 3,
      "totalAmount": 35,
      "remark": "请辣一点"
    }
  }
}
```

- 说明：
  - 后端会将当前商品列表（简化字段）作为上下文提供给 AI，并严格要求 AI 只返回一个纯 JSON 对象。
  - 后端会对 AI 返回的 `order.items` 做基本校验及金额重算（goodsAmount, deliveryFee, totalAmount），以保证一致性。
  - 若用户确认下单，前端可直接将返回的 `order` 对象作为请求体 `POST /api/orders` 创建订单（该接口会使用当前登录用户作为下单人）。

---

## 现有相关接口
- POST /api/ai/recommend：商品推荐（保留）
- POST /api/ai/auto-order：使用后端简单策略直接创建订单（保留）
- POST /api/ai/chat：文本对话（保留）
- POST /api/orders：创建订单（用于用户确认后下单，接受 `OrderCreateDTO`）

---

> 💡 Tips: 当前实现为简易 POC，未处理安全鉴权、速率限制、AI 返回注入与模糊解析等生产级问题。若未来上线，请务必添加鉴权、输出验证和审计日志。
