# 校园外卖系统 API 接口文档

## 基础信息

- **Base URL**: `http://localhost:3000/api`
- **API版本**: v1
- **数据格式**: JSON
- **字符编码**: UTF-8

## 认证方式

大部分接口需要在请求头中携带认证token：

```
Authorization: Bearer {token}
```

## 接口列表

### 1. 用户认证

#### 1.1 用户登录
- **URL**: `/auth/login`
- **Method**: `POST`
- **说明**: 用户登录，支持手机号+验证码登录

**请求参数**:
```json
{
  "phone": "13800138000",
  "code": "123456"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 1,
      "nickname": "用户1234",
      "phone": "13800138000",
      "avatar": "https://example.com/avatar.jpg",
      "address": "3号宿舍楼201"
    }
  }
}
```

#### 1.2 用户注册
- **URL**: `/auth/register`
- **Method**: `POST`
- **说明**: 新用户注册

**请求参数**:
```json
{
  "phone": "13800138000",
  "code": "123456",
  "nickname": "用户昵称"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 1,
      "nickname": "用户昵称",
      "phone": "13800138000"
    }
  }
}
```

#### 1.3 发送验证码
- **URL**: `/auth/send-code`
- **Method**: `POST`
- **说明**: 发送手机验证码

**请求参数**:
```json
{
  "phone": "13800138000"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "验证码已发送"
}
```

---

### 2. 用户信息

#### 2.1 获取用户信息
- **URL**: `/user/info`
- **Method**: `GET`
- **认证**: 需要

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "nickname": "用户1234",
    "phone": "13800138000",
    "avatar": "https://example.com/avatar.jpg",
    "address": "3号宿舍楼201"
  }
}
```

#### 2.2 更新用户信息
- **URL**: `/user/info`
- **Method**: `PUT`
- **认证**: 需要

**请求参数**:
```json
{
  "nickname": "新昵称",
  "avatar": "https://example.com/new-avatar.jpg",
  "address": "新地址"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "nickname": "新昵称",
    "avatar": "https://example.com/new-avatar.jpg",
    "address": "新地址"
  }
}
```

---

### 3. 店铺相关

#### 3.1 获取店铺列表
- **URL**: `/shops`
- **Method**: `GET`
- **说明**: 获取店铺列表，支持筛选和排序

**请求参数** (Query):
- `page`: 页码，默认1
- `pageSize`: 每页数量，默认10
- `sort`: 排序方式，可选值：`sales`(销量), `rating`(评分), `distance`(距离)
- `keyword`: 搜索关键词

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 1,
        "name": "校园食堂",
        "description": "营养丰富，价格实惠",
        "image": "https://example.com/shop1.jpg",
        "rating": 4.8,
        "monthlySales": 1200,
        "deliveryTime": 20,
        "distance": 0.5,
        "tags": ["实惠", "营养"]
      }
    ],
    "total": 10,
    "page": 1,
    "pageSize": 10
  }
}
```

#### 3.2 获取店铺详情
- **URL**: `/shops/:id`
- **Method**: `GET`
- **说明**: 获取指定店铺的详细信息

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "name": "校园食堂",
    "description": "营养丰富，价格实惠，学生首选",
    "image": "https://example.com/shop1.jpg",
    "rating": 4.8,
    "monthlySales": 1200,
    "deliveryTime": 20,
    "distance": 0.5,
    "tags": ["实惠", "营养"],
    "businessHours": "08:00-20:00"
  }
}
```

#### 3.3 获取店铺商品列表
- **URL**: `/shops/:shopId/products`
- **Method**: `GET`
- **说明**: 获取指定店铺的商品列表

**请求参数** (Query):
- `categoryId`: 分类ID（可选）

**响应示例**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "name": "宫保鸡丁",
      "description": "鸡肉鲜嫩，花生酥脆",
      "price": 18,
      "image": "https://example.com/product1.jpg",
      "categoryId": 2,
      "categoryName": "主食",
      "sales": 500
    }
  ]
}
```

---

### 4. 商品相关

#### 4.1 获取商品详情
- **URL**: `/products/:id`
- **Method**: `GET`
- **说明**: 获取指定商品的详细信息

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "name": "宫保鸡丁",
    "description": "鸡肉鲜嫩，花生酥脆",
    "price": 18,
    "image": "https://example.com/product1.jpg",
    "categoryId": 2,
    "categoryName": "主食",
    "sales": 500,
    "rating": 4.7,
    "shopId": 1,
    "shopName": "校园食堂"
  }
}
```

---

### 5. 订单相关

> 本章节与前端 `OrderConfirm.vue` / `Order.vue` 页面字段保持一致，包含收货人姓名、电话、地址、支付方式、金额等信息。

#### 5.1 创建订单（提交订单）
- **URL**: `/orders`
- **Method**: `POST`
- **认证**: 需要
- **说明**: 在确认订单页提交订单，后端生成订单记录

**请求参数**:
```json
{
  "shopId": 1,
  "shopName": "校园食堂",
  "contactName": "张三",
  "contactPhone": "13800138000",
  "address": "xx校区 3号宿舍楼 201",
  "payMethod": "wechat",
  "items": [
    {
      "productId": 1,
      "name": "宫保鸡丁",
      "quantity": 1,
      "price": 18
    },
    {
      "productId": 2,
      "name": "米饭",
      "quantity": 2,
      "price": 2.5
    }
  ],
  "goodsAmount": 23,      // 商品小计
  "deliveryFee": 3,       // 配送费（满减后）
  "totalAmount": 26,      // 订单总金额 = goodsAmount + deliveryFee
  "remark": "不要辣，多加饭"
}
```

字段说明：
- **contactName**: 收货人姓名（前端表单 `form.name`）
- **contactPhone**: 收货人手机号（前端表单 `form.phone`）
- **address**: 配送地址（前端表单 `form.address`）
- **payMethod**: 支付方式，枚举：
  - `wechat`：微信支付
  - `alipay`：支付宝
  - `cash`：货到付款
- **goodsAmount**: 商品总价（前端 `cartStore.totalPrice`）
- **deliveryFee**: 配送费，当前规则示例：商品金额大于 30 元免配送费，否则 3 元（与前端一致）
- **totalAmount**: 订单应付金额

**响应示例**:
```json
{
  "code": 200,
  "message": "订单创建成功",
  "data": {
    "id": 1001,
    "orderNo": "202401150001",
    "shopId": 1,
    "shopName": "校园食堂",
    "status": "pending",            // 见下方状态说明
    "contactName": "张三",
    "contactPhone": "13800138000",
    "address": "xx校区 3号宿舍楼 201",
    "payMethod": "wechat",
    "goodsAmount": 23,
    "deliveryFee": 3,
    "totalAmount": 26,
    "items": [
      {
        "id": 1,
        "productId": 1,
        "name": "宫保鸡丁",
        "quantity": 1,
        "price": 18
      },
      {
        "id": 2,
        "productId": 2,
        "name": "米饭",
        "quantity": 2,
        "price": 2.5
      }
    ],
    "remark": "不要辣，多加饭",
    "createTime": "2024-01-15 12:30:00",
    "payTime": null,
    "deliveryTime": null
  }
}
```

**订单状态（status）枚举建议**：
- `pending`：待支付/待接单
- `processing`：进行中（已接单/配送中）
- `completed`：已完成
- `cancelled`：已取消

---

#### 5.2 获取订单列表
- **URL**: `/orders`
- **Method**: `GET`
- **认证**: 需要
- **说明**: 获取当前登录用户的订单列表，用于订单列表页 `Order.vue`

**请求参数** (Query):
- `page`: 页码，默认 1
- `pageSize`: 每页数量，默认 10
- `status`: 订单状态，可选值：
  - `pending`
  - `processing`
  - `completed`
  - `cancelled`

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 1001,
        "orderNo": "202401150001",
        "shopId": 1,
        "shopName": "校园食堂",
        "status": "pending",
        "goodsAmount": 23,
        "deliveryFee": 3,
        "totalAmount": 26,
        "items": [
          {
            "id": 1,
            "name": "宫保鸡丁",
            "quantity": 1,
            "price": 18
          },
          {
            "id": 2,
            "name": "米饭",
            "quantity": 2,
            "price": 2.5
          }
        ],
        "createTime": "2024-01-15 12:30:00"
      }
    ],
    "total": 5,
    "page": 1,
    "pageSize": 10
  }
}
```

---

#### 5.3 获取订单详情
- **URL**: `/orders/:id`
- **Method**: `GET`
- **认证**: 需要
- **说明**: 获取指定订单的详细信息，供订单详情页使用（如后续新增）

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "id": 1001,
    "orderNo": "202401150001",
    "shopId": 1,
    "shopName": "校园食堂",
    "status": "pending",
    "contactName": "张三",
    "contactPhone": "13800138000",
    "address": "xx校区 3号宿舍楼 201",
    "payMethod": "wechat",
    "goodsAmount": 23,
    "deliveryFee": 3,
    "totalAmount": 26,
    "items": [
      {
        "id": 1,
        "productId": 1,
        "name": "宫保鸡丁",
        "quantity": 1,
        "price": 18
      },
      {
        "id": 2,
        "productId": 2,
        "name": "米饭",
        "quantity": 2,
        "price": 2.5
      }
    ],
    "remark": "不要辣，多加饭",
    "createTime": "2024-01-15 12:30:00",
    "payTime": null,
    "deliveryTime": null
  }
}
```

---

#### 5.4 取消订单
- **URL**: `/orders/:id/cancel`
- **Method**: `POST`
- **认证**: 需要
- **说明**: 取消指定订单（通常仅待支付/待接单状态可取消）

**请求参数**（可选）:
```json
{
  "reason": "临时有事，不需要了"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "订单已取消"
}
```

---

### 6. 委托广场

#### 6.1 获取委托列表
- **URL**: `/delegations`
- **Method**: `GET`
- **说明**: 获取委托广场的委托列表

**请求参数** (Query):
- `page`: 页码，默认1
- `pageSize`: 每页数量，默认10
- `type`: 委托类型，可选值：`request`(求带), `offer`(可带)
- `status`: 状态，可选值：`pending`(进行中), `accepted`(已接受), `completed`(已完成)

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 1,
        "type": "request",
        "userId": 1,
        "userName": "小明",
        "title": "求带校园食堂的宫保鸡丁",
        "description": "今天太忙了，求好心人帮忙带一份宫保鸡丁",
        "shopId": 1,
        "shopName": "校园食堂",
        "reward": 5,
        "status": "pending",
        "createTime": "2024-01-15 10:30:00"
      }
    ],
    "total": 10,
    "page": 1,
    "pageSize": 10
  }
}
```

#### 6.2 创建委托
- **URL**: `/delegations`
- **Method**: `POST`
- **认证**: 需要
- **说明**: 发布新的委托

**请求参数**:
```json
{
  "type": "request",
  "title": "求带校园食堂的宫保鸡丁",
  "description": "今天太忙了，求好心人帮忙带一份宫保鸡丁",
  "shopId": 1,
  "shopName": "校园食堂",
  "reward": 5,
  "items": []
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "委托发布成功",
  "data": {
    "id": 1,
    "type": "request",
    "title": "求带校园食堂的宫保鸡丁",
    "status": "pending",
    "createTime": "2024-01-15 10:30:00"
  }
}
```

#### 6.3 获取委托详情
- **URL**: `/delegations/:id`
- **Method**: `GET`

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "type": "request",
    "userId": 1,
    "userName": "小明",
    "title": "求带校园食堂的宫保鸡丁",
    "description": "今天太忙了，求好心人帮忙带一份宫保鸡丁",
    "shopId": 1,
    "shopName": "校园食堂",
    "reward": 5,
    "status": "pending",
    "items": [],
    "createTime": "2024-01-15 10:30:00"
  }
}
```

#### 6.4 接受委托
- **URL**: `/delegations/:id/accept`
- **Method**: `POST`
- **认证**: 需要
- **说明**: 接受委托（仅限type为request的委托）

**响应示例**:
```json
{
  "code": 200,
  "message": "已接受委托"
}
```

#### 6.5 完成委托
- **URL**: `/delegations/:id/complete`
- **Method**: `POST`
- **认证**: 需要
- **说明**: 标记委托为已完成

**响应示例**:
```json
{
  "code": 200,
  "message": "委托已完成"
}
```

---

### 7. AI智能推荐

#### 7.1 获取AI推荐
- **URL**: `/ai/recommend`
- **Method**: `POST`
- **认证**: 需要（可选）
- **说明**: 根据用户需求获取AI推荐的商品

**请求参数**:
```json
{
  "query": "我想吃辣的，预算30元",
  "userId": 1,
  "preferences": {
    "spicy": true,
    "budget": 30
  }
}
```

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "recommendations": [
      {
        "id": 1,
        "name": "麻辣香锅",
        "shopName": "麻辣香锅店",
        "price": 35,
        "rating": 4.8,
        "image": "https://example.com/product1.jpg",
        "reason": "符合您的口味偏好，价格适中"
      }
    ],
    "message": "根据您的需求，我为您推荐以下美食"
  }
}
```

#### 7.2 AI自动下单
- **URL**: `/ai/auto-order`
- **Method**: `POST`
- **认证**: 需要
- **说明**: AI根据用户需求自动生成订单

**请求参数**:
```json
{
  "query": "帮我选个午餐套餐，预算30元",
  "userId": 1,
  "address": "3号宿舍楼201"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "订单已创建",
  "data": {
    "orderId": 1001,
    "orderNo": "202401150001",
    "items": [
      {
        "id": 1,
        "name": "宫保鸡丁",
        "quantity": 1,
        "price": 18
      },
      {
        "id": 2,
        "name": "米饭",
        "quantity": 2,
        "price": 2
      }
    ],
    "total": 22,
    "reason": "根据您的预算和偏好，为您推荐了营养均衡的套餐"
  }
}
```

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权，需要登录 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 通用响应格式

所有接口统一使用以下响应格式：

**成功响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

**错误响应**:
```json
{
  "code": 400,
  "message": "错误信息",
  "data": null
}
```

## 注意事项

1. 所有时间字段统一使用 ISO 8601 格式：`YYYY-MM-DD HH:mm:ss`
2. 所有金额字段单位为元，保留两位小数
3. 图片URL使用HTTPS协议
4. 分页参数：page从1开始，pageSize默认10，最大100
5. 接口需要认证的，必须在请求头中携带有效的token
6. 验证码有效期5分钟，每个手机号每分钟最多发送1次

