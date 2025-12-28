# UniTakeout — 校园周边外卖系统

## 简介 ✅
本项目是一个面向校园场景的外卖系统（前后端分离），包含：用户端（Vue 3 + Vite）、管理员端（简易后台）、服务端（Spring Boot + MyBatis）和数据库脚本。主要特色有短信验证码登录（JWT 驱动）、AI Agent 推荐与自动组单、委托广场（帮取/代买）和实时商家通知（WebSocket）。

---

## 技术栈与亮点 🔧
- 后端：Spring Boot、MyBatis（Mapper XML）、Lombok、Jakarta WebSocket、RestTemplate
- 前端：Vue 3 + Vite、Pinia（状态管理）、Axios（请求封装）
- 数据库：MySQL（表结构见 `SQL/schema.sql`）
- 认证：短信验证码 + JWT（`JwtUtil` / `JwtInterceptor`），`Authorization: Bearer <token>`
- AI：与外部大模型对接（`AIService` / `AIConfig`），`/api/ai/agent-suggest` 返回可直接下单的建议 JSON（前端需二次确认并调用下单接口）
- 实时：WebSocket（`/ws/{sid}`）用于向商家推送新订单通知
- 其他：短信网关通过 AppCode 调用（在配置中设置 `unitakeout.sms.*`）

---

## 功能亮点 ⭐
- 手机号 + 验证码登录 / 注册（无密码，验证码过期与发送频率控制）
- 选店、浏览商品、购物车、下单、订单管理、订单取消
- 委托广场：发布委托（request/offer）、接受、完成
- AI 智能助手：自然语言建议、商品推荐、一键加入购物车或确认下单
- 管理后台：商品/分类/店铺/订单管理（位于 `takeout-admin`）

---

## 登录架构与流程 🔐
1. 前端调用 `/api/auth/send-code`（`AuthController.sendCode`）传入手机号。
2. 后端生成验证码并写入 `verification_code` 表（`VerificationCodeMapper`）。同时尝试调用短信网关发送验证码（配置于 `application.yaml`）。
3. 前端提交 `/api/auth/login` 或 `/api/auth/register`（`AuthController`）。
4. 后端在 `AuthServiceImpl` 验证验证码有效性（是否存在、是否匹配、是否过期），然后：
   - 若登录：检查用户是否存在，生成 JWT（`JwtUtil.generateToken(userId)`），返回 `LoginVO`（包含 token 与 `userInfo`）。
   - 若注册：插入新用户并生成 token 返回。
5. 客户端后续请求将 `Authorization: Bearer <token>` 放在请求头中，后端 `JwtInterceptor` 验证 Token，验证通过后将 userId 设置到 `BaseContext` 中，供业务逻辑取用。

注意：验证码发送限制与过期时间由 `Constants` 中的相关常量控制。

---

## 数据库架构与表（概要） 🗄️
主要表见 `SQL/schema.sql`，核心表与关系：

- `user`（用户）
- `verification_code`（短信验证码）
- `shop`（店铺）
- `category`（商品分类，关联 `shop_id`）
- `product`（商品，关联 `shop_id`, `category_id`）
- `order`（订单，关联 `user_id`, `shop_id`）
- `order_item`（订单项，关联 `order_id`, `product_id`）
- `delegation`（委托：request/offer，含 `accepted_by` 字段）
- `shop_tag`（店铺标签）

关系简述：
- 一个 `shop` 有多 `category`、多 `product`。
- 一个 `user` 可有多 `order`；`order` 包含多个 `order_item`，每个 `order_item` 指向 `product`。
- `delegation` 记录委托与接单状态，带有 `user_id` 与 `accepted_by` 字段表示发布者与接受者。

---

## Java 实体与主要类关系（简述） 📦
- `User` ←→ 数据库 `user`
- `VerificationCode` ←→ `verification_code`
- `Shop`, `Category`, `Product` ←→ 商家数据
- `Order` 包含 `OrderItem`（非 DB 字段在实体中有集合映射）
- `Delegation`（委托，支持 request/offer）
- `AIService` / `AIServiceImpl` 负责与 LLM 的对接与建议生成（`agentSuggest`、`autoOrder`、`recommend`）

Mapper XML 位于：`src/main/resources/mapper/*.xml`（MyBatis SQL 映射）。

---

## AI Agent 工作流程（关键点） 🤖➡️🛒
1. 前端（`AIAgent.vue`）将自然语言请求发送到 `/api/ai/agent-suggest`，payload 包含 `query`、`userId`、`address` 等。
2. 后端 `AIServiceImpl.agentSuggest`：
   - 收集产品简化列表，构造 `system prompt`（强制要求返回单个 JSON，且格式固定：`reply` 与 `order`）。
   - 使用配置的模型（`AIConfig`，通过 `RestTemplate` 调用模型的 Chat Completions API）发送请求。
   - 解析返回文本中嵌入的 JSON，转换为 `OrderCreateDTO`，并对金额、配送费等做计算/校验。
   - 返回 `{ reply, order }` 给前端（不自动创建订单）。
3. 前端展示 `reply` 与 `order`（如商品列表、推荐项），用户确认后调用 `/api/orders` 创建订单（`OrderService.create`），服务端会插入 `order` 与 `order_item` 并通过 `WebSocketServer.sendToAllClient` 通知商家。

安全提示：AI 返回结果基于外部模型，后端对返回的 `order` 做了基本校验，仍建议增强白名单校验（检查 productId 是否存在、价格核验）以防异常数据。 

---

## 开发与运行（快速指南） ▶️
- 后端（Java）: 进入 `UniTakeout_server`，使用 `./mvnw spring-boot:run` 或 `mvn spring-boot:run` 启动（Windows 下使用 `mvnw.cmd`）。
- 前端（用户端/管理员端）: 进入对应目录（`takeout-user` / `takeout-admin`），执行 `npm install` 后 `npm run dev`（或使用 yarn）。
- 数据库: 导入 `SQL/schema.sql` 并根据 `application.yaml` 配置连接信息。
- 配置项: `application.yaml` 中设置短信、AI 接口 Key 与 WebSocket 配置等。

---

## 文档与接口参考 📚
- 后端接口说明：`UniTakeout_server/api.md`, `api_agent.md`, `api_admin.md` 等
- 前端说明：`UniTakeout_ui/takeout-user/README.md`, `takeout-admin/api_admin.md`

---

如果需要，我可以：
- 将 README 稍作精简或补充更多调用示例（示例请求/返回）；
- 生成一页“部署指南”（包括 Docker/CICD 的建议）；
- 或为关键流程补充时序图或 ER 图。

✅ 已完成：项目阅读与 README 草稿准备。下一步：是否需要我将 README 进一步精简或添加示例与部署步骤？


补充：

-- 校园外卖系统数据库表结构schema.sql

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `phone` VARCHAR(11) NOT NULL UNIQUE COMMENT '手机号',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `address` VARCHAR(255) COMMENT '地址',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 验证码表（临时存储）
CREATE TABLE IF NOT EXISTS `verification_code` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `phone` VARCHAR(11) NOT NULL COMMENT '手机号',
    `code` VARCHAR(6) NOT NULL COMMENT '验证码',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='验证码表';

-- 店铺表
CREATE TABLE IF NOT EXISTS `shop` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '店铺ID',
    `name` VARCHAR(100) NOT NULL COMMENT '店铺名称',
    `description` VARCHAR(500) COMMENT '店铺描述',
    `image` VARCHAR(255) COMMENT '店铺图片',
    `rating` DECIMAL(3,1) DEFAULT 0.0 COMMENT '评分',
    `monthly_sales` INT DEFAULT 0 COMMENT '月销量',
    `delivery_time` INT DEFAULT 20 COMMENT '配送时间（分钟）',
    `distance` DECIMAL(5,2) DEFAULT 0.0 COMMENT '距离（公里）',
    `business_hours` VARCHAR(50) COMMENT '营业时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺表';

-- 商品分类表
CREATE TABLE IF NOT EXISTS `category` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    `shop_id` BIGINT NOT NULL COMMENT '店铺ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_shop_id` (`shop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品表
CREATE TABLE IF NOT EXISTS `product` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    `shop_id` BIGINT NOT NULL COMMENT '店铺ID',
    `category_id` BIGINT COMMENT '分类ID',
    `name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `description` VARCHAR(500) COMMENT '商品描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `image` VARCHAR(255) COMMENT '商品图片',
    `sales` INT DEFAULT 0 COMMENT '销量',
    `rating` DECIMAL(3,1) DEFAULT 0.0 COMMENT '评分',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_shop_id` (`shop_id`),
    INDEX `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 订单表
CREATE TABLE IF NOT EXISTS `order` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `shop_id` BIGINT NOT NULL COMMENT '店铺ID',
    `shop_name` VARCHAR(100) COMMENT '店铺名称',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT '订单状态：pending/processing/completed/cancelled',
    `contact_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    `contact_phone` VARCHAR(11) NOT NULL COMMENT '收货人电话',
    `address` VARCHAR(255) NOT NULL COMMENT '配送地址',
    `pay_method` VARCHAR(20) NOT NULL COMMENT '支付方式：wechat/alipay/cash',
    `goods_amount` DECIMAL(10,2) NOT NULL COMMENT '商品金额',
    `delivery_fee` DECIMAL(10,2) DEFAULT 0.0 COMMENT '配送费',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    `remark` VARCHAR(500) COMMENT '备注',
    `pay_time` DATETIME COMMENT '支付时间',
    `delivery_time` DATETIME COMMENT '配送时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_shop_id` (`shop_id`),
    INDEX `idx_order_no` (`order_no`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单项表
CREATE TABLE IF NOT EXISTS `order_item` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单项ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `quantity` INT NOT NULL COMMENT '数量',
    `price` DECIMAL(10,2) NOT NULL COMMENT '单价',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_order_id` (`order_id`),
    INDEX `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

-- 委托表
CREATE TABLE IF NOT EXISTS `delegation` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '委托ID',
    `type` VARCHAR(20) NOT NULL COMMENT '委托类型：request/offer',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `user_name` VARCHAR(50) COMMENT '用户名',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `description` VARCHAR(1000) COMMENT '描述',
    `shop_id` BIGINT COMMENT '店铺ID',
    `shop_name` VARCHAR(100) COMMENT '店铺名称',
    `reward` DECIMAL(10,2) DEFAULT 0.0 COMMENT '报酬',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending/accepted/completed',
    `accepted_by` BIGINT COMMENT '接受者ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_shop_id` (`shop_id`),
    INDEX `idx_type` (`type`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='委托表';

-- 店铺标签关联表（用于存储店铺的tags）
CREATE TABLE IF NOT EXISTS `shop_tag` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `shop_id` BIGINT NOT NULL COMMENT '店铺ID',
    `tag` VARCHAR(20) NOT NULL COMMENT '标签',
    INDEX `idx_shop_id` (`shop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺标签表';
