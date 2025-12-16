# 数据库初始化说明

## 使用步骤

1. 创建数据库：
```sql
CREATE DATABASE campus_takeout CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行表结构SQL：
```bash
mysql -u root -p campus_takeout < schema.sql
```

3. 执行初始化数据SQL（可选）：
```bash
mysql -u root -p campus_takeout < data.sql
```

## 数据库配置

在 `application.yaml` 中配置数据库连接信息：
- 数据库名：`campus_takeout`
- 用户名：`root`
- 密码：`123456`（请根据实际情况修改）

## 表结构说明

- `user`: 用户表
- `verification_code`: 验证码表（临时存储）
- `shop`: 店铺表
- `category`: 商品分类表
- `product`: 商品表
- `order`: 订单表
- `order_item`: 订单项表
- `delegation`: 委托表
- `shop_tag`: 店铺标签表
