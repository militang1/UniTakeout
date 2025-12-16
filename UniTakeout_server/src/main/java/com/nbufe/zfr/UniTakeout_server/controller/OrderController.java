package com.nbufe.zfr.UniTakeout_server.controller;

import com.nbufe.zfr.UniTakeout_server.common.BaseContext;
import com.nbufe.zfr.UniTakeout_server.common.Result;
import com.nbufe.zfr.UniTakeout_server.dto.OrderCreateDTO;
import com.nbufe.zfr.UniTakeout_server.entity.Order;
import com.nbufe.zfr.UniTakeout_server.service.OrderService;
import com.nbufe.zfr.UniTakeout_server.vo.PageResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Result<Order> create(@RequestBody OrderCreateDTO dto) {
        try {
            Long userId = BaseContext.getCurrentUserId();
            Order order = orderService.create(userId, dto);
            return Result.success("订单创建成功", order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping
    public Result<PageResult<Order>> getList(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            Long userId = BaseContext.getCurrentUserId();
            PageResult<Order> result = orderService.getList(userId, status, page, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Order> getById(@PathVariable Long id) {
        try {
            Order order = orderService.getById(id);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        try {
            String reason = body != null ? body.get("reason") : null;
            orderService.cancel(id, reason);
            Result<Void> result = Result.success();
            result.setMessage("订单已取消");
            return result;
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

