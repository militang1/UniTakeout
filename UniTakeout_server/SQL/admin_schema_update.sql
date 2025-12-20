-- 商家端数据库结构更新

-- 为店铺表添加商家ID字段（如果还没有的话）
ALTER TABLE `shop` 
ADD COLUMN IF NOT EXISTS `owner_id` BIGINT COMMENT '商家ID（关联user表）' AFTER `id`,
ADD INDEX IF NOT EXISTS `idx_owner_id` (`owner_id`);

-- 商家表（可选：如果需要独立的商家表，可以使用这个）
-- 这里我们使用user表作为商家表，通过role字段区分
-- 如果需要，可以在user表添加role字段：ALTER TABLE `user` ADD COLUMN `role` VARCHAR(20) DEFAULT 'user' COMMENT '角色：user/shop_admin';

