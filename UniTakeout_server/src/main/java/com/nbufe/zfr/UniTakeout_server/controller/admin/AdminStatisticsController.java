package com.nbufe.zfr.UniTakeout_server.controller.admin;

import com.nbufe.zfr.UniTakeout_server.common.BaseContext;
import com.nbufe.zfr.UniTakeout_server.common.Result;
import com.nbufe.zfr.UniTakeout_server.entity.Shop;
import com.nbufe.zfr.UniTakeout_server.mapper.ShopMapper;
import com.nbufe.zfr.UniTakeout_server.service.AdminService;
import com.nbufe.zfr.UniTakeout_server.vo.StatisticsVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/statistics")
public class AdminStatisticsController {
    private final AdminService adminService;
    private final ShopMapper shopMapper;

    public AdminStatisticsController(AdminService adminService, ShopMapper shopMapper) {
        this.adminService = adminService;
        this.shopMapper = shopMapper;
    }

    @GetMapping
    public Result<StatisticsVO> getStatistics() {
        try {
            Long ownerId = BaseContext.getCurrentUserId();
            Shop shop = shopMapper.selectByOwnerId(ownerId);
            if (shop == null) {
                return Result.error("店铺不存在");
            }
            StatisticsVO statistics = adminService.getStatistics(shop.getId());
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}








