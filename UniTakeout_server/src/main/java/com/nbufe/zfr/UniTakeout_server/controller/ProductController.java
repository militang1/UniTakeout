package com.nbufe.zfr.UniTakeout_server.controller;

import com.nbufe.zfr.UniTakeout_server.common.Result;
import com.nbufe.zfr.UniTakeout_server.entity.Product;
import com.nbufe.zfr.UniTakeout_server.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products/{id}")
    public Result<Product> getById(@PathVariable Long id) {
        try {
            Product product = productService.getById(id);
            return Result.success(product);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/shops/{shopId}/products")
    public Result<List<Product>> getByShopId(
            @PathVariable Long shopId,
            @RequestParam(required = false) Long categoryId) {
        try {
            List<Product> products = productService.getByShopId(shopId, categoryId);
            return Result.success(products);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

