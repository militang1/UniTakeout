package com.nbufe.zfr.UniTakeout_server.service;

import com.nbufe.zfr.UniTakeout_server.entity.Shop;
import com.nbufe.zfr.UniTakeout_server.vo.PageResult;

public interface ShopService {
    PageResult<Shop> getList(String keyword, String sort, Integer page, Integer pageSize);
    Shop getById(Long id);
}

