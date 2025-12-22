package com.nbufe.zfr.UniTakeout_server.controller.admin;

import com.nbufe.zfr.UniTakeout_server.common.BaseContext;
import com.nbufe.zfr.UniTakeout_server.common.Result;
import com.nbufe.zfr.UniTakeout_server.dto.OrderStatusUpdateDTO;
import com.nbufe.zfr.UniTakeout_server.entity.Order;
import com.nbufe.zfr.UniTakeout_server.entity.Shop;
import com.nbufe.zfr.UniTakeout_server.mapper.ShopMapper;
import com.nbufe.zfr.UniTakeout_server.service.AdminService;
import com.nbufe.zfr.UniTakeout_server.vo.PageResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {
    private final AdminService adminService;
    private final ShopMapper shopMapper;

    public AdminOrderController(AdminService adminService, ShopMapper shopMapper) {
        this.adminService = adminService;
        this.shopMapper = shopMapper;
    }

    @GetMapping
    public Result<PageResult<Order>> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            Long ownerId = BaseContext.getCurrentUserId();
            Shop shop = shopMapper.selectByOwnerId(ownerId);
            if (shop == null) {
                return Result.error("店铺不存在");
            }
            PageResult<Order> result = adminService.getOrders(shop.getId(), status, page, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Order> getOrder(@PathVariable Long id) {
        try {
            Long ownerId = BaseContext.getCurrentUserId();
            Shop shop = shopMapper.selectByOwnerId(ownerId);
            if (shop == null) {
                return Result.error("店铺不存在");
            }
            Order order = adminService.getOrder(id, shop.getId());
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateOrderStatus(@PathVariable Long id, @RequestBody OrderStatusUpdateDTO dto) {
        try {
            Long ownerId = BaseContext.getCurrentUserId();
            Shop shop = shopMapper.selectByOwnerId(ownerId);
            if (shop == null) {
                return Result.error("店铺不存在");
            }
            adminService.updateOrderStatus(id, shop.getId(), dto.getStatus());
            Result<Void> result = Result.success();
            result.setMessage("订单状态更新成功");
            return result;
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}








