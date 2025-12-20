# 搜索功能 API 接口文档

## 基础信息

- **Base URL**: `http://localhost:3000/api`
- **API版本**: v1
- **数据格式**: JSON
- **字符编码**: UTF-8

## 认证方式

搜索接口通常不需要认证，但可根据业务需求决定是否需要登录。

---

## 接口列表

### 1. 综合搜索

#### 1.1 搜索全部（店铺+商品）
- **URL**: `/search`
- **Method**: `GET`
- **认证**: 不需要
- **说明**: 同时搜索店铺和商品，返回综合结果

**请求参数** (Query):
- `keyword`: 搜索关键词（必填）
- `page`: 页码，默认1
- `pageSize`: 每页数量，默认10

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "shops": {
      "list": [
        {
          "id": 1,
          "name": "校园食堂",
          "description": "营养丰富，价格实惠",
          "image": "https://example.com/shop1.jpg",
          "rating": 4.8,
          "monthlySales": 1200,
          "deliveryTime": 20,
          "distance": 0.5
        }
      ],
      "total": 5
    },
    "products": {
      "list": [
        {
          "id": 1,
          "name": "宫保鸡丁",
          "description": "鸡肉鲜嫩，花生酥脆",
          "price": 18,
          "image": "https://example.com/product1.jpg",
          "shopId": 1,
          "shopName": "校园食堂"
        }
      ],
      "total": 10
    }
  }
}
```

---

### 2. 店铺搜索

#### 2.1 搜索店铺
- **URL**: `/search/shops`
- **Method**: `GET`
- **认证**: 不需要
- **说明**: 根据关键词搜索店铺

**请求参数** (Query):
- `keyword`: 搜索关键词（必填），支持店铺名称、描述等字段搜索
- `page`: 页码，默认1
- `pageSize`: 每页数量，默认10
- `sort`: 排序方式，可选值：`sales`(销量), `rating`(评分), `distance`(距离)

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 1,
        "name": "校园食堂",
        "description": "营养丰富，价格实惠，学生首选",
        "image": "https://example.com/shop1.jpg",
        "rating": 4.8,
        "monthlySales": 1200,
        "deliveryTime": 20,
        "distance": 0.5,
        "tags": ["实惠", "营养"]
      }
    ],
    "total": 5,
    "page": 1,
    "pageSize": 10
  }
}
```

**字段说明**:
- `name`: 店铺名称（会高亮匹配的关键词）
- `description`: 店铺描述
- `rating`: 评分（0-5）
- `monthlySales`: 月销量
- `deliveryTime`: 预计配送时间（分钟）
- `distance`: 距离（公里）

---

### 3. 商品搜索

#### 3.1 搜索商品
- **URL**: `/search/products`
- **Method**: `GET`
- **认证**: 不需要
- **说明**: 根据关键词搜索商品

**请求参数** (Query):
- `keyword`: 搜索关键词（必填），支持商品名称、描述等字段搜索
- `page`: 页码，默认1
- `pageSize`: 每页数量，默认10
- `shopId`: 店铺ID（可选），限定在指定店铺内搜索

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 1,
        "name": "宫保鸡丁",
        "description": "鸡肉鲜嫩，花生酥脆",
        "price": 18,
        "image": "https://example.com/product1.jpg",
        "shopId": 1,
        "shopName": "校园食堂",
        "categoryId": 2,
        "categoryName": "主食",
        "sales": 500
      }
    ],
    "total": 10,
    "page": 1,
    "pageSize": 10
  }
}
```

**字段说明**:
- `name`: 商品名称（会高亮匹配的关键词）
- `description`: 商品描述
- `price`: 商品价格（元）
- `shopId`: 所属店铺ID
- `shopName`: 所属店铺名称
- `categoryId`: 商品分类ID
- `categoryName`: 商品分类名称
- `sales`: 销量

---

## 搜索建议

### 热门搜索词
前端可以维护一个热门搜索词列表，用于展示在搜索页：

```javascript
const hotSearches = [
  '麻辣香锅',
  '咖啡',
  '奶茶',
  '炸鸡',
  '日式料理',
  '火锅'
]
```

### 搜索历史
前端可以在 localStorage 中保存用户的搜索历史：

```javascript
// 保存搜索历史
localStorage.setItem('searchHistory', JSON.stringify(['麻辣香锅', '咖啡', '奶茶']))

// 读取搜索历史
const history = JSON.parse(localStorage.getItem('searchHistory') || '[]')
```

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误（如缺少keyword） |
| 500 | 服务器内部错误 |

## 通用响应格式

所有接口统一使用以下响应格式：

**成功响应**:
```json
{
  "code": 200,
  "message": "搜索成功",
  "data": {}
}
```

**错误响应**:
```json
{
  "code": 400,
  "message": "搜索关键词不能为空",
  "data": null
}
```

## 注意事项

1. 搜索关键词支持模糊匹配，不区分大小写
2. 搜索结果按相关度排序，相关度高的排在前面
3. 关键词高亮：前端可以根据返回的数据，使用 `<mark>` 标签高亮匹配的关键词
4. 分页参数：page从1开始，pageSize默认10，最大100
5. 如果搜索结果为空，返回空数组 `[]`，而不是 `null`
6. 搜索接口建议做防抖处理，避免频繁请求

## 前端实现建议

### 1. 防抖处理
```javascript
import { debounce } from 'lodash-es'

const debouncedSearch = debounce(performSearch, 300)
```

### 2. 关键词高亮
```javascript
function highlightKeyword(text, keyword) {
  if (!keyword || !text) return text
  const regex = new RegExp(`(${keyword})`, 'gi')
  return text.replace(regex, '<mark>$1</mark>')
}
```

### 3. 搜索历史管理
- 最多保存10条搜索历史
- 重复搜索词移到最前面
- 支持清空和删除单条历史

