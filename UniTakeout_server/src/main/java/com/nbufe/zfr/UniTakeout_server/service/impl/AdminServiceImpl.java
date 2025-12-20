package com.nbufe.zfr.UniTakeout_server.service.impl;

import com.nbufe.zfr.UniTakeout_server.common.Constants;
import com.nbufe.zfr.UniTakeout_server.dto.*;
import com.nbufe.zfr.UniTakeout_server.entity.Category;
import com.nbufe.zfr.UniTakeout_server.entity.Order;
import com.nbufe.zfr.UniTakeout_server.entity.Product;
import com.nbufe.zfr.UniTakeout_server.entity.Shop;
import com.nbufe.zfr.UniTakeout_server.mapper.*;
import com.nbufe.zfr.UniTakeout_server.service.AdminService;
import com.nbufe.zfr.UniTakeout_server.vo.AdminShopVO;
import com.nbufe.zfr.UniTakeout_server.vo.PageResult;
import com.nbufe.zfr.UniTakeout_server.vo.StatisticsVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    private final ShopMapper shopMapper;
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ShopTagMapper shopTagMapper;

    public AdminServiceImpl(ShopMapper shopMapper, ProductMapper productMapper,
                           CategoryMapper categoryMapper, OrderMapper orderMapper,
                           OrderItemMapper orderItemMapper, ShopTagMapper shopTagMapper) {
        this.shopMapper = shopMapper;
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.shopTagMapper = shopTagMapper;
    }

    @Override
    public AdminShopVO getShop(Long ownerId) {
        Shop shop = shopMapper.selectByOwnerId(ownerId);
        if (shop == null) {
            throw new RuntimeException("店铺不存在");
        }
        AdminShopVO vo = new AdminShopVO();
        BeanUtils.copyProperties(shop, vo);
        vo.setTags(shopTagMapper.selectTagsByShopId(shop.getId()));
        return vo;
    }

    @Override
    @Transactional
    public AdminShopVO updateShop(Long ownerId, ShopUpdateDTO dto) {
        Shop shop = shopMapper.selectByOwnerId(ownerId);
        if (shop == null) {
            throw new RuntimeException("店铺不存在");
        }
        
        if (dto.getName() != null) shop.setName(dto.getName());
        if (dto.getDescription() != null) shop.setDescription(dto.getDescription());
        if (dto.getImage() != null) shop.setImage(dto.getImage());
        if (dto.getDeliveryTime() != null) shop.setDeliveryTime(dto.getDeliveryTime());
        if (dto.getDistance() != null) shop.setDistance(dto.getDistance());
        if (dto.getBusinessHours() != null) shop.setBusinessHours(dto.getBusinessHours());
        
        shopMapper.update(shop);
        
        AdminShopVO vo = new AdminShopVO();
        BeanUtils.copyProperties(shop, vo);
        vo.setTags(shopTagMapper.selectTagsByShopId(shop.getId()));
        return vo;
    }

    @Override
    public PageResult<Product> getProducts(Long shopId, Long categoryId, Integer page, Integer pageSize) {
        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        if (pageSize > 100) pageSize = 100;
        
        List<Product> list = productMapper.selectByShopId(shopId, categoryId);
        // 简单分页处理
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, list.size());
        List<Product> pagedList = list.subList(Math.max(0, start), end);
        
        PageResult<Product> result = new PageResult<>();
        result.setList(pagedList);
        result.setTotal((long) list.size());
        result.setPage(page);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public Product getProduct(Long id, Long shopId) {
        Product product = productMapper.selectById(id);
        if (product == null || !product.getShopId().equals(shopId)) {
            throw new RuntimeException("商品不存在");
        }
        return product;
    }

    @Override
    @Transactional
    public Product createProduct(Long shopId, ProductCreateDTO dto) {
        Product product = new Product();
        product.setShopId(shopId);
        product.setCategoryId(dto.getCategoryId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setImage(dto.getImage());
        product.setSales(0);
        product.setRating(BigDecimal.ZERO);
        
        productMapper.insert(product);
        return product;
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, Long shopId, ProductUpdateDTO dto) {
        Product product = productMapper.selectById(id);
        if (product == null || !product.getShopId().equals(shopId)) {
            throw new RuntimeException("商品不存在");
        }
        
        if (dto.getCategoryId() != null) product.setCategoryId(dto.getCategoryId());
        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
        if (dto.getPrice() != null) product.setPrice(dto.getPrice());
        if (dto.getImage() != null) product.setImage(dto.getImage());
        
        productMapper.update(product);
        return productMapper.selectById(id);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id, Long shopId) {
        Product product = productMapper.selectById(id);
        if (product == null || !product.getShopId().equals(shopId)) {
            throw new RuntimeException("商品不存在");
        }
        productMapper.delete(id);
    }

    @Override
    public List<Category> getCategories(Long shopId) {
        return categoryMapper.selectByShopId(shopId);
    }

    @Override
    public Category getCategory(Long id, Long shopId) {
        Category category = categoryMapper.selectById(id);
        if (category == null || !category.getShopId().equals(shopId)) {
            throw new RuntimeException("分类不存在");
        }
        return category;
    }

    @Override
    @Transactional
    public Category createCategory(Long shopId, CategoryCreateDTO dto) {
        Category category = new Category();
        category.setShopId(shopId);
        category.setName(dto.getName());
        category.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        
        categoryMapper.insert(category);
        return category;
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, Long shopId, CategoryUpdateDTO dto) {
        Category category = categoryMapper.selectById(id);
        if (category == null || !category.getShopId().equals(shopId)) {
            throw new RuntimeException("分类不存在");
        }
        
        if (dto.getName() != null) category.setName(dto.getName());
        if (dto.getSortOrder() != null) category.setSortOrder(dto.getSortOrder());
        
        categoryMapper.update(category);
        return categoryMapper.selectById(id);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id, Long shopId) {
        Category category = categoryMapper.selectById(id);
        if (category == null || !category.getShopId().equals(shopId)) {
            throw new RuntimeException("分类不存在");
        }
        categoryMapper.delete(id);
    }

    @Override
    public PageResult<Order> getOrders(Long shopId, String status, Integer page, Integer pageSize) {
        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        if (pageSize > 100) pageSize = 100;
        
        Integer offset = (page - 1) * pageSize;
        List<Order> list = orderMapper.selectByShopId(shopId, status, offset, pageSize);
        
        for (Order order : list) {
            order.setItems(orderItemMapper.selectByOrderId(order.getId()));
        }
        
        Long total = orderMapper.countByShopId(shopId, status);
        
        PageResult<Order> result = new PageResult<>();
        result.setList(list);
        result.setTotal(total);
        result.setPage(page);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public Order getOrder(Long id, Long shopId) {
        Order order = orderMapper.selectById(id);
        if (order == null || !order.getShopId().equals(shopId)) {
            throw new RuntimeException("订单不存在");
        }
        order.setItems(orderItemMapper.selectByOrderId(id));
        return order;
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long id, Long shopId, String status) {
        Order order = orderMapper.selectById(id);
        if (order == null || !order.getShopId().equals(shopId)) {
            throw new RuntimeException("订单不存在");
        }
        
        if (Constants.ORDER_STATUS_PROCESSING.equals(status)) {
            order.setStatus(status);
        } else if (Constants.ORDER_STATUS_COMPLETED.equals(status)) {
            order.setStatus(status);
            order.setDeliveryTime(LocalDateTime.now());
        } else {
            throw new RuntimeException("不支持的状态更新");
        }
        
        orderMapper.update(order);
    }

    @Override
    public StatisticsVO getStatistics(Long shopId) {
        StatisticsVO stats = new StatisticsVO();
        
        // 总订单数
        Long totalOrders = orderMapper.countByShopId(shopId, null);
        stats.setTotalOrders(totalOrders);
        
        // 今日订单数（简化处理，实际应该按日期查询）
        stats.setTodayOrders(0L);
        
        // 总营业额
        List<Order> allOrders = orderMapper.selectByShopId(shopId, Constants.ORDER_STATUS_COMPLETED, 0, 10000);
        BigDecimal totalRevenue = allOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalRevenue(totalRevenue);
        
        // 今日营业额（简化处理）
        stats.setTodayRevenue(BigDecimal.ZERO);
        
        // 商品总数
        List<Product> products = productMapper.selectByShopId(shopId, null);
        stats.setTotalProducts((long) products.size());
        
        // 待处理订单数
        Long pendingOrders = orderMapper.countByShopId(shopId, Constants.ORDER_STATUS_PENDING);
        stats.setPendingOrders(pendingOrders != null ? pendingOrders.intValue() : 0);
        
        return stats;
    }
}

