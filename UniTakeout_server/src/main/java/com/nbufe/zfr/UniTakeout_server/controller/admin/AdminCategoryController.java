package com.nbufe.zfr.UniTakeout_server.controller.admin;

import com.nbufe.zfr.UniTakeout_server.common.BaseContext;
import com.nbufe.zfr.UniTakeout_server.common.Result;
import com.nbufe.zfr.UniTakeout_server.dto.CategoryCreateDTO;
import com.nbufe.zfr.UniTakeout_server.dto.CategoryUpdateDTO;
import com.nbufe.zfr.UniTakeout_server.entity.Category;
import com.nbufe.zfr.UniTakeout_server.entity.Shop;
import com.nbufe.zfr.UniTakeout_server.mapper.ShopMapper;
import com.nbufe.zfr.UniTakeout_server.service.AdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {
    private final AdminService adminService;
    private final ShopMapper shopMapper;

    public AdminCategoryController(AdminService adminService, ShopMapper shopMapper) {
        this.adminService = adminService;
        this.shopMapper = shopMapper;
    }

    @GetMapping
    public Result<List<Category>> getCategories() {
        try {
            Long ownerId = BaseContext.getCurrentUserId();
            Shop shop = shopMapper.selectByOwnerId(ownerId);
            if (shop == null) {
                return Result.error("店铺不存在");
            }
            List<Category> categories = adminService.getCategories(shop.getId());
            return Result.success(categories);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Category> getCategory(@PathVariable Long id) {
        try {
            Long ownerId = BaseContext.getCurrentUserId();
            Shop shop = shopMapper.selectByOwnerId(ownerId);
            if (shop == null) {
                return Result.error("店铺不存在");
            }
            Category category = adminService.getCategory(id, shop.getId());
            return Result.success(category);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping
    public Result<Category> createCategory(@RequestBody CategoryCreateDTO dto) {
        try {
            Long ownerId = BaseContext.getCurrentUserId();
            Shop shop = shopMapper.selectByOwnerId(ownerId);
            if (shop == null) {
                return Result.error("店铺不存在");
            }
            Category category = adminService.createCategory(shop.getId(), dto);
            return Result.success("创建成功", category);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Category> updateCategory(@PathVariable Long id, @RequestBody CategoryUpdateDTO dto) {
        try {
            Long ownerId = BaseContext.getCurrentUserId();
            Shop shop = shopMapper.selectByOwnerId(ownerId);
            if (shop == null) {
                return Result.error("店铺不存在");
            }
            Category category = adminService.updateCategory(id, shop.getId(), dto);
            return Result.success("更新成功", category);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        try {
            Long ownerId = BaseContext.getCurrentUserId();
            Shop shop = shopMapper.selectByOwnerId(ownerId);
            if (shop == null) {
                return Result.error("店铺不存在");
            }
            adminService.deleteCategory(id, shop.getId());
            Result<Void> result = Result.success();
            result.setMessage("删除成功");
            return result;
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}







