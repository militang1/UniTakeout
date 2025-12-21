package com.nbufe.zfr.UniTakeout_server.service;

import com.nbufe.zfr.UniTakeout_server.entity.Product;
import com.nbufe.zfr.UniTakeout_server.entity.Shop;
import com.nbufe.zfr.UniTakeout_server.vo.PageResult;
import com.nbufe.zfr.UniTakeout_server.vo.SearchResultVO;

public interface SearchService {
    SearchResultVO searchAll(String keyword, Integer page, Integer pageSize);
    PageResult<Shop> searchShops(String keyword, String sort, Integer page, Integer pageSize);
    PageResult<Product> searchProducts(String keyword, Long shopId, Integer page, Integer pageSize);
}









