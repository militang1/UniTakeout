package com.nbufe.zfr.UniTakeout_server.service;

import com.nbufe.zfr.UniTakeout_server.entity.Product;
import java.util.List;

public interface ProductService {
    Product getById(Long id);
    List<Product> getByShopId(Long shopId, Long categoryId);
}

