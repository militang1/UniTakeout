package com.nbufe.zfr.UniTakeout_server.controller;

import com.nbufe.zfr.UniTakeout_server.common.Result;
import com.nbufe.zfr.UniTakeout_server.entity.Product;
import com.nbufe.zfr.UniTakeout_server.entity.Shop;
import com.nbufe.zfr.UniTakeout_server.service.SearchService;
import com.nbufe.zfr.UniTakeout_server.vo.PageResult;
import com.nbufe.zfr.UniTakeout_server.vo.SearchResultVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public Result<SearchResultVO> searchAll(
            @RequestParam String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return Result.error(400, "搜索关键词不能为空");
            }
            SearchResultVO result = searchService.searchAll(keyword.trim(), page, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/shops")
    public Result<PageResult<Shop>> searchShops(
            @RequestParam String keyword,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return Result.error(400, "搜索关键词不能为空");
            }
            PageResult<Shop> result = searchService.searchShops(keyword.trim(), sort, page, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/products")
    public Result<PageResult<Product>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return Result.error(400, "搜索关键词不能为空");
            }
            PageResult<Product> result = searchService.searchProducts(keyword.trim(), shopId, page, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}


