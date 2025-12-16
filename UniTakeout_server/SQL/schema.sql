-- 校园外卖系统数据库表结构

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

