package com.nbufe.zfr.UniTakeout_server.controller.admin;

import com.nbufe.zfr.UniTakeout_server.common.BaseContext;
import com.nbufe.zfr.UniTakeout_server.common.Result;
import com.nbufe.zfr.UniTakeout_server.dto.ProductCreateDTO;
import com.nbufe.zfr.UniTakeout_server.dto.ProductUpdateDTO;
import com.nbufe.zfr.UniTakeout_server.entity.Product;
import com.nbufe.zfr.UniTakeout_server.entity.Shop;
import com.nbufe.zfr.UniTakeout_server.mapper.ShopMapper;
import com.nbufe.zfr.UniTakeout_server.service.AdminService;
import com.nbufe.zfr.UniTakeout_server.vo.PageResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {
    private final AdminService adminService;
    private final ShopMapper shopMapper;

    public AdminProductController(AdminService adminService, ShopMapper shopMapper) {
        this.adminService = adminService;
        this.shopMapper = shopMapper;
    }

    @GetMapping
    public Result<PageResult<Product>> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            Long ownerId = BaseContext.getCurrentUserId();
            Shop shop = shopMapper.selectByOwnerId(ownerId);
            if (shop == null) {
                return Result.error("店铺不存在");
            }
            PageResult<Product> result = adminService.getProducts(shop.getId(), categoryId, page, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Product> getProduct(@PathVariable Long id) {
        try {
            Long ownerId = BaseContext.getCurrentUserId();
            Shop shop = shopMapper.selectByOwnerId(ownerId);
            if (shop == null) {
                return Result.error("店铺不存在");
            }
            Product product = adminService.getProduct(id, shop.getId());
            return Result.success(product);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping
    public Result<Product> createProduct(@RequestBody ProductCreateDTO dto) {
        try {
            Long ownerId = BaseContext.getCurrentUserId();
            Shop shop = shopMapper.selectByOwnerId(ownerId);
            if (shop == null) {
                return Result.error("店铺不存在");
            }
            Product product = adminService.createProduct(shop.getId(), dto);
            return Result.success("创建成功", product);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Product> updateProduct(@PathVariable Long id, @RequestBody ProductUpdateDTO dto) {
        try {
            Long ownerId = BaseContext.getCurrentUserId();
            Shop shop = shopMapper.selectByOwnerId(ownerId);
            if (shop == null) {
                return Result.error("店铺不存在");
            }
            Product product = adminService.updateProduct(id, shop.getId(), dto);
            return Result.success("更新成功", product);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        try {
            Long ownerId = BaseContext.getCurrentUserId();
            Shop shop = shopMapper.selectByOwnerId(ownerId);
            if (shop == null) {
                return Result.error("店铺不存在");
            }
            adminService.deleteProduct(id, shop.getId());
            Result<Void> result = Result.success();
            result.setMessage("删除成功");
            return result;
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}






