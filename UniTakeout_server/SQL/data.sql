-- 校园外卖系统初始化数据

-- 插入用户数据
INSERT INTO `user` (phone, nickname, avatar, address) VALUES
('13800138000', '用户1234', 'https://example.com/avatar.jpg', '3号宿舍楼201'),
('13800138001', '小明', 'https://example.com/avatar2.jpg', '5号宿舍楼302'),
('13800138002', '小红', 'https://example.com/avatar3.jpg', '2号宿舍楼101');

-- 插入店铺数据
INSERT INTO `shop` (name, description, image, rating, monthly_sales, delivery_time, distance, business_hours) VALUES
('校园食堂', '营养丰富，价格实惠，学生首选', 'https://example.com/shop1.jpg', 4.8, 1200, 20, 0.5, '08:00-20:00'),
('麻辣香锅店', '正宗川味，麻辣鲜香', 'https://example.com/shop2.jpg', 4.7, 800, 25, 0.8, '10:00-22:00'),
('奶茶店', '新鲜现做，多种口味', 'https://example.com/shop3.jpg', 4.9, 1500, 15, 0.3, '09:00-21:00'),
('烧烤摊', '夜宵首选，美味烧烤', 'https://example.com/shop4.jpg', 4.6, 600, 30, 1.2, '17:00-24:00');

-- 插入店铺标签
INSERT INTO `shop_tag` (shop_id, tag) VALUES
(1, '实惠'),
(1, '营养'),
(2, '麻辣'),
(2, '川味'),
(3, '新鲜'),
(3, '多种口味'),
(4, '夜宵'),
(4, '烧烤');

-- 插入商品分类
INSERT INTO `category` (shop_id, name, sort_order) VALUES
(1, '主食', 1),
(1, '汤类', 2),
(1, '小菜', 3),
(2, '香锅', 1),
(2, '配菜', 2),
(3, '奶茶', 1),
(3, '果茶', 2),
(4, '烤串', 1),
(4, '烤蔬菜', 2);

-- 插入商品数据
INSERT INTO `product` (shop_id, category_id, name, description, price, image, sales, rating) VALUES
-- 校园食堂商品
(1, 1, '宫保鸡丁', '鸡肉鲜嫩，花生酥脆', 18.00, 'https://example.com/product1.jpg', 500, 4.7),
(1, 1, '鱼香肉丝', '酸甜可口，下饭神器', 16.00, 'https://example.com/product2.jpg', 450, 4.6),
(1, 1, '米饭', '香糯可口', 2.50, 'https://example.com/product3.jpg', 1200, 4.8),
(1, 2, '紫菜蛋花汤', '清淡鲜美', 5.00, 'https://example.com/product4.jpg', 300, 4.5),
(1, 3, '凉拌黄瓜', '清爽开胃', 6.00, 'https://example.com/product5.jpg', 200, 4.4),
-- 麻辣香锅店商品
(2, 4, '麻辣香锅', '自选配菜，麻辣鲜香', 35.00, 'https://example.com/product6.jpg', 400, 4.8),
(2, 4, '微辣香锅', '适合不太能吃辣的朋友', 35.00, 'https://example.com/product7.jpg', 350, 4.7),
(2, 5, '金针菇', '配菜', 8.00, 'https://example.com/product8.jpg', 250, 4.6),
(2, 5, '豆皮', '配菜', 6.00, 'https://example.com/product9.jpg', 200, 4.5),
-- 奶茶店商品
(3, 6, '珍珠奶茶', '经典口味，Q弹珍珠', 12.00, 'https://example.com/product10.jpg', 800, 4.9),
(3, 6, '红豆奶茶', '香甜红豆，浓郁奶茶', 13.00, 'https://example.com/product11.jpg', 600, 4.8),
(3, 7, '柠檬蜂蜜茶', '清新柠檬，天然蜂蜜', 10.00, 'https://example.com/product12.jpg', 500, 4.7),
(3, 7, '百香果茶', '酸甜百香果，清爽解腻', 11.00, 'https://example.com/product13.jpg', 450, 4.8),
-- 烧烤摊商品
(4, 8, '羊肉串', '新鲜羊肉，炭火烤制', 5.00, 'https://example.com/product14.jpg', 300, 4.6),
(4, 8, '牛肉串', '精选牛肉，香嫩可口', 6.00, 'https://example.com/product15.jpg', 280, 4.7),
(4, 9, '烤茄子', '蒜香烤茄子', 8.00, 'https://example.com/product16.jpg', 200, 4.5),
(4, 9, '烤韭菜', '经典烤韭菜', 6.00, 'https://example.com/product17.jpg', 180, 4.4);

-- 插入委托数据
INSERT INTO `delegation` (type, user_id, user_name, title, description, shop_id, shop_name, reward, status) VALUES
('request', 2, '小明', '求带校园食堂的宫保鸡丁', '今天太忙了，求好心人帮忙带一份宫保鸡丁', 1, '校园食堂', 5.00, 'pending'),
('request', 3, '小红', '求带奶茶店的珍珠奶茶', '想喝奶茶但没时间去买，求帮忙', 3, '奶茶店', 3.00, 'pending'),
('offer', 1, '用户1234', '可带校园食堂的饭菜', '今天要去食堂，可以帮忙带餐', 1, '校园食堂', 0.00, 'pending');

