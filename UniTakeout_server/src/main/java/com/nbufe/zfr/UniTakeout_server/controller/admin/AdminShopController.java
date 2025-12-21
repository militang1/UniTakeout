package com.nbufe.zfr.UniTakeout_server.controller.admin;

import com.nbufe.zfr.UniTakeout_server.common.BaseContext;
import com.nbufe.zfr.UniTakeout_server.common.Result;
import com.nbufe.zfr.UniTakeout_server.dto.ShopUpdateDTO;
import com.nbufe.zfr.UniTakeout_server.service.AdminService;
import com.nbufe.zfr.UniTakeout_server.vo.AdminShopVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/shop")
public class AdminShopController {
    private final AdminService adminService;

    public AdminShopController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public Result<AdminShopVO> getShop() {
        try {
            Long ownerId = BaseContext.getCurrentUserId();
            AdminShopVO shop = adminService.getShop(ownerId);
            return Result.success(shop);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping
    public Result<AdminShopVO> updateShop(@RequestBody ShopUpdateDTO dto) {
        try {
            Long ownerId = BaseContext.getCurrentUserId();
            AdminShopVO shop = adminService.updateShop(ownerId, dto);
            return Result.success("更新成功", shop);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}







