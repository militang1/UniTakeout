# 商家端 API 接口文档

## 基础信息

- **Base URL**: `http://localhost:3000/api/admin`
- **API版本**: v1
- **数据格式**: JSON
- **字符编码**: UTF-8

## 认证方式

所有接口需要在请求头中携带认证token：

```
Authorization: Bearer {token}
```

商家登录使用与用户端相同的认证方式（手机号+验证码），登录后获取token用于后续接口调用。

---

## 接口列表

### 1. 商家认证

#### 1.1 商家登录
- **URL**: `/auth/login`
- **Method**: `POST`
- **说明**: 商家登录，支持手机号+验证码登录

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
      "nickname": "商家名称",
      "phone": "13800138000",
      "avatar": "https://example.com/avatar.jpg",
      "address": "店铺地址"
    }
  }
}
```

#### 1.2 发送验证码
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

### 2. 店铺管理

#### 2.1 获取店铺信息
- **URL**: `/shop`
- **Method**: `GET`
- **认证**: 需要

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "name": "校园食堂",
    "description": "营养丰富，价格实惠",
    "image": "https://example.com/shop1.jpg",
    "rating": 4.8,
    "monthlySales": 1200,
    "deliveryTime": 20,
    "distance": 0.5,
    "businessHours": "08:00-20:00",
    "tags": ["实惠", "营养"],
    "createTime": "2024-01-15 10:00:00",
    "updateTime": "2024-01-15 10:00:00"
  }
}
```

#### 2.2 更新店铺信息
- **URL**: `/shop`
- **Method**: `PUT`
- **认证**: 需要

**请求参数**:
```json
{
  "name": "新店铺名称",
  "description": "新店铺描述",
  "image": "https://example.com/new-image.jpg",
  "deliveryTime": 25,
  "distance": 0.8,
  "businessHours": "09:00-21:00"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "name": "新店铺名称",
    "description": "新店铺描述",
    "image": "https://example.com/new-image.jpg",
    "deliveryTime": 25,
    "distance": 0.8,
    "businessHours": "09:00-21:00"
  }
}
```

---

### 3. 商品管理

#### 3.1 获取商品列表
- **URL**: `/products`
- **Method**: `GET`
- **认证**: 需要

**请求参数** (Query):
- `categoryId`: 分类ID（可选）
- `page`: 页码，默认1
- `pageSize`: 每页数量，默认10

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 1,
        "shopId": 1,
        "categoryId": 2,
        "categoryName": "主食",
        "name": "宫保鸡丁",
        "description": "鸡肉鲜嫩，花生酥脆",
        "price": 18.00,
        "image": "https://example.com/product1.jpg",
        "sales": 500,
        "rating": 4.7
      }
    ],
    "total": 10,
    "page": 1,
    "pageSize": 10
  }
}
```

#### 3.2 获取商品详情
- **URL**: `/products/{id}`
- **Method**: `GET`
- **认证**: 需要

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "shopId": 1,
    "categoryId": 2,
    "categoryName": "主食",
    "name": "宫保鸡丁",
    "description": "鸡肉鲜嫩，花生酥脆",
    "price": 18.00,
    "image": "https://example.com/product1.jpg",
    "sales": 500,
    "rating": 4.7
  }
}
```

#### 3.3 创建商品
- **URL**: `/products`
- **Method**: `POST`
- **认证**: 需要

**请求参数**:
```json
{
  "categoryId": 2,
  "name": "新商品",
  "description": "商品描述",
  "price": 20.00,
  "image": "https://example.com/product.jpg"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 10,
    "shopId": 1,
    "categoryId": 2,
    "name": "新商品",
    "description": "商品描述",
    "price": 20.00,
    "image": "https://example.com/product.jpg",
    "sales": 0,
    "rating": 0.0
  }
}
```

#### 3.4 更新商品
- **URL**: `/products/{id}`
- **Method**: `PUT`
- **认证**: 需要

**请求参数**:
```json
{
  "categoryId": 2,
  "name": "更新后的商品名称",
  "description": "更新后的描述",
  "price": 22.00,
  "image": "https://example.com/new-product.jpg"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 10,
    "name": "更新后的商品名称",
    "description": "更新后的描述",
    "price": 22.00,
    "image": "https://example.com/new-product.jpg"
  }
}
```

#### 3.5 删除商品
- **URL**: `/products/{id}`
- **Method**: `DELETE`
- **认证**: 需要

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功"
}
```

---

### 4. 分类管理

#### 4.1 获取分类列表
- **URL**: `/categories`
- **Method**: `GET`
- **认证**: 需要

**响应示例**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "shopId": 1,
      "name": "主食",
      "sortOrder": 1,
      "createTime": "2024-01-15 10:00:00"
    },
    {
      "id": 2,
      "shopId": 1,
      "name": "汤类",
      "sortOrder": 2,
      "createTime": "2024-01-15 10:00:00"
    }
  ]
}
```

#### 4.2 获取分类详情
- **URL**: `/categories/{id}`
- **Method**: `GET`
- **认证**: 需要

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "shopId": 1,
    "name": "主食",
    "sortOrder": 1,
    "createTime": "2024-01-15 10:00:00"
  }
}
```

#### 4.3 创建分类
- **URL**: `/categories`
- **Method**: `POST`
- **认证**: 需要

**请求参数**:
```json
{
  "name": "新分类",
  "sortOrder": 3
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 3,
    "shopId": 1,
    "name": "新分类",
    "sortOrder": 3,
    "createTime": "2024-01-15 10:00:00"
  }
}
```

#### 4.4 更新分类
- **URL**: `/categories/{id}`
- **Method**: `PUT`
- **认证**: 需要

**请求参数**:
```json
{
  "name": "更新后的分类名称",
  "sortOrder": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 3,
    "name": "更新后的分类名称",
    "sortOrder": 1
  }
}
```

#### 4.5 删除分类
- **URL**: `/categories/{id}`
- **Method**: `DELETE`
- **认证**: 需要

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功"
}
```

---

### 5. 订单管理

#### 5.1 获取订单列表
- **URL**: `/orders`
- **Method**: `GET`
- **认证**: 需要

**请求参数** (Query):
- `status`: 订单状态，可选值：`pending`(待处理), `processing`(进行中), `completed`(已完成), `cancelled`(已取消)
- `page`: 页码，默认1
- `pageSize`: 每页数量，默认10

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 1001,
        "orderNo": "202401150001",
        "userId": 1,
        "shopId": 1,
        "shopName": "校园食堂",
        "status": "pending",
        "contactName": "张三",
        "contactPhone": "13800138000",
        "address": "xx校区 3号宿舍楼 201",
        "payMethod": "wechat",
        "goodsAmount": 23.00,
        "deliveryFee": 3.00,
        "totalAmount": 26.00,
        "remark": "不要辣",
        "items": [
          {
            "id": 1,
            "productId": 1,
            "name": "宫保鸡丁",
            "quantity": 1,
            "price": 18.00
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

#### 5.2 获取订单详情
- **URL**: `/orders/{id}`
- **Method**: `GET`
- **认证**: 需要

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "id": 1001,
    "orderNo": "202401150001",
    "userId": 1,
    "shopId": 1,
    "shopName": "校园食堂",
    "status": "pending",
    "contactName": "张三",
    "contactPhone": "13800138000",
    "address": "xx校区 3号宿舍楼 201",
    "payMethod": "wechat",
    "goodsAmount": 23.00,
    "deliveryFee": 3.00,
    "totalAmount": 26.00,
    "remark": "不要辣",
    "items": [
      {
        "id": 1,
        "productId": 1,
        "name": "宫保鸡丁",
        "quantity": 1,
        "price": 18.00
      }
    ],
    "createTime": "2024-01-15 12:30:00",
    "payTime": null,
    "deliveryTime": null
  }
}
```

#### 5.3 更新订单状态
- **URL**: `/orders/{id}/status`
- **Method**: `PUT`
- **认证**: 需要
- **说明**: 商家可以更新订单状态为 `processing`(接单) 或 `completed`(完成)

**请求参数**:
```json
{
  "status": "processing"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "订单状态更新成功"
}
```

**订单状态说明**:
- `pending`: 待处理（待接单）
- `processing`: 进行中（已接单/配送中）
- `completed`: 已完成
- `cancelled`: 已取消

---

### 6. 文件上传

#### 6.1 上传图片
- **URL**: `/upload/image`
- **Method**: `POST`
- **认证**: 需要
- **Content-Type**: `multipart/form-data`
- **说明**: 上传图片到阿里云OSS，返回图片URL

**请求参数** (FormData):
- `file`: 图片文件（必填）

**文件要求**:
- 支持格式：jpg, jpeg, png, gif, bmp, webp
- 文件大小：不超过5MB

**响应示例**:
```json
{
  "code": 200,
  "message": "上传成功",
  "data": "https://bucket-name.oss-cn-hangzhou.aliyuncs.com/images/2024/01/15/abc123def456.jpg"
}
```

**错误响应示例**:
```json
{
  "code": 400,
  "message": "文件不能为空",
  "data": null
}
```

**使用说明**:
1. 前端使用 `multipart/form-data` 格式上传文件
2. 上传成功后，将返回的URL保存到商品或店铺的image字段
3. 图片会按日期自动分类存储：`images/yyyy/MM/dd/文件名`
4. 文件名使用UUID生成，确保唯一性

**前端示例** (JavaScript):
```javascript
const formData = new FormData();
formData.append('file', file);

fetch('/api/admin/upload/image', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer ' + token
  },
  body: formData
})
.then(response => response.json())
.then(data => {
  if (data.code === 200) {
    console.log('图片URL:', data.data);
    // 使用返回的URL更新商品或店铺信息
  }
});
```

**前端示例** (Vue 3):
```vue
<template>
  <input type="file" @change="handleFileChange" accept="image/*" />
</template>

<script setup>
import { ref } from 'vue';

const handleFileChange = async (event) => {
  const file = event.target.files[0];
  if (!file) return;
  
  const formData = new FormData();
  formData.append('file', file);
  
  try {
    const response = await fetch('/api/admin/upload/image', {
      method: 'POST',
      headers: {
        'Authorization': 'Bearer ' + token
      },
      body: formData
    });
    
    const result = await response.json();
    if (result.code === 200) {
      console.log('图片URL:', result.data);
      // 使用返回的URL
    }
  } catch (error) {
    console.error('上传失败:', error);
  }
};
</script>
```

---


### 7. 数据统计

#### 7.1 获取统计数据
- **URL**: `/statistics`
- **Method**: `GET`
- **认证**: 需要

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "totalOrders": 100,
    "todayOrders": 5,
    "totalRevenue": 5000.00,
    "todayRevenue": 250.00,
    "totalProducts": 20,
    "pendingOrders": 3
  }
}
```

**字段说明**:
- `totalOrders`: 总订单数
- `todayOrders`: 今日订单数
- `totalRevenue`: 总营业额
- `todayRevenue`: 今日营业额
- `totalProducts`: 商品总数
- `pendingOrders`: 待处理订单数

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
6. 商家只能操作自己店铺的数据，系统会自动验证权限
7. 订单状态更新：只能从 `pending` 更新为 `processing`，或从 `processing` 更新为 `completed`

