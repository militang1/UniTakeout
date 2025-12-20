package com.nbufe.zfr.UniTakeout_server.service;

import com.nbufe.zfr.UniTakeout_server.dto.*;
import com.nbufe.zfr.UniTakeout_server.entity.Category;
import com.nbufe.zfr.UniTakeout_server.entity.Order;
import com.nbufe.zfr.UniTakeout_server.entity.Product;
import com.nbufe.zfr.UniTakeout_server.vo.AdminShopVO;
import com.nbufe.zfr.UniTakeout_server.vo.PageResult;
import com.nbufe.zfr.UniTakeout_server.vo.StatisticsVO;

public interface AdminService {
    // 店铺管理
    AdminShopVO getShop(Long ownerId);
    AdminShopVO updateShop(Long ownerId, ShopUpdateDTO dto);
    
    // 商品管理
    PageResult<Product> getProducts(Long shopId, Long categoryId, Integer page, Integer pageSize);
    Product getProduct(Long id, Long shopId);
    Product createProduct(Long shopId, ProductCreateDTO dto);
    Product updateProduct(Long id, Long shopId, ProductUpdateDTO dto);
    void deleteProduct(Long id, Long shopId);
    
    // 分类管理
    java.util.List<Category> getCategories(Long shopId);
    Category getCategory(Long id, Long shopId);
    Category createCategory(Long shopId, CategoryCreateDTO dto);
    Category updateCategory(Long id, Long shopId, CategoryUpdateDTO dto);
    void deleteCategory(Long id, Long shopId);
    
    // 订单管理
    PageResult<Order> getOrders(Long shopId, String status, Integer page, Integer pageSize);
    Order getOrder(Long id, Long shopId);
    void updateOrderStatus(Long id, Long shopId, String status);
    
    // 数据统计
    StatisticsVO getStatistics(Long shopId);
}

