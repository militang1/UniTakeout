package com.nbufe.zfr.UniTakeout_server.service.impl;

import com.nbufe.zfr.UniTakeout_server.entity.Shop;
import com.nbufe.zfr.UniTakeout_server.mapper.ShopMapper;
import com.nbufe.zfr.UniTakeout_server.mapper.ShopTagMapper;
import com.nbufe.zfr.UniTakeout_server.service.ShopService;
import com.nbufe.zfr.UniTakeout_server.vo.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {
    private final ShopMapper shopMapper;
    private final ShopTagMapper shopTagMapper;

    public ShopServiceImpl(ShopMapper shopMapper, ShopTagMapper shopTagMapper) {
        this.shopMapper = shopMapper;
        this.shopTagMapper = shopTagMapper;
    }

    @Override
    public PageResult<Shop> getList(String keyword, String sort, Integer page, Integer pageSize) {
        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        if (pageSize > 100) pageSize = 100;
        
        Integer offset = (page - 1) * pageSize;
        List<Shop> list = shopMapper.selectList(keyword, sort, offset, pageSize);
        
        for (Shop shop : list) {
            shop.setTags(shopTagMapper.selectTagsByShopId(shop.getId()));
        }
        
        Long total = shopMapper.count(keyword);
        
        PageResult<Shop> result = new PageResult<>();
        result.setList(list);
        result.setTotal(total);
        result.setPage(page);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public Shop getById(Long id) {
        Shop shop = shopMapper.selectById(id);
        if (shop != null) {
            shop.setTags(shopTagMapper.selectTagsByShopId(id));
        }
        return shop;
    }
}

