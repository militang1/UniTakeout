package com.nbufe.zfr.UniTakeout_server.service.impl;

import com.nbufe.zfr.UniTakeout_server.entity.Product;
import com.nbufe.zfr.UniTakeout_server.entity.Shop;
import com.nbufe.zfr.UniTakeout_server.mapper.ProductMapper;
import com.nbufe.zfr.UniTakeout_server.mapper.ShopMapper;
import com.nbufe.zfr.UniTakeout_server.mapper.ShopTagMapper;
import com.nbufe.zfr.UniTakeout_server.service.SearchService;
import com.nbufe.zfr.UniTakeout_server.vo.PageResult;
import com.nbufe.zfr.UniTakeout_server.vo.SearchResultVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {
    private final ShopMapper shopMapper;
    private final ProductMapper productMapper;
    private final ShopTagMapper shopTagMapper;

    public SearchServiceImpl(ShopMapper shopMapper, ProductMapper productMapper, ShopTagMapper shopTagMapper) {
        this.shopMapper = shopMapper;
        this.productMapper = productMapper;
        this.shopTagMapper = shopTagMapper;
    }

    @Override
    public SearchResultVO searchAll(String keyword, Integer page, Integer pageSize) {
        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        if (pageSize > 100) pageSize = 100;
        
        Integer offset = (page - 1) * pageSize;
        
        // 搜索店铺
        List<Shop> shops = shopMapper.searchByKeyword(keyword, null, offset, pageSize);
        for (Shop shop : shops) {
            shop.setTags(shopTagMapper.selectTagsByShopId(shop.getId()));
        }
        Long shopTotal = shopMapper.countByKeyword(keyword);
        
        // 搜索商品
        List<Product> products = productMapper.searchByKeyword(keyword, null, offset, pageSize);
        Long productTotal = productMapper.countByKeyword(keyword, null);
        
        SearchResultVO result = new SearchResultVO();
        
        SearchResultVO.ShopResult shopResult = new SearchResultVO.ShopResult();
        shopResult.setList(shops);
        shopResult.setTotal(shopTotal);
        result.setShops(shopResult);
        
        SearchResultVO.ProductResult productResult = new SearchResultVO.ProductResult();
        productResult.setList(products);
        productResult.setTotal(productTotal);
        result.setProducts(productResult);
        
        return result;
    }

    @Override
    public PageResult<Shop> searchShops(String keyword, String sort, Integer page, Integer pageSize) {
        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        if (pageSize > 100) pageSize = 100;
        
        Integer offset = (page - 1) * pageSize;
        List<Shop> list = shopMapper.searchByKeyword(keyword, sort, offset, pageSize);
        
        for (Shop shop : list) {
            shop.setTags(shopTagMapper.selectTagsByShopId(shop.getId()));
        }
        
        Long total = shopMapper.countByKeyword(keyword);
        
        PageResult<Shop> result = new PageResult<>();
        result.setList(list);
        result.setTotal(total);
        result.setPage(page);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public PageResult<Product> searchProducts(String keyword, Long shopId, Integer page, Integer pageSize) {
        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        if (pageSize > 100) pageSize = 100;
        
        Integer offset = (page - 1) * pageSize;
        List<Product> list = productMapper.searchByKeyword(keyword, shopId, offset, pageSize);
        
        Long total = productMapper.countByKeyword(keyword, shopId);
        
        PageResult<Product> result = new PageResult<>();
        result.setList(list);
        result.setTotal(total);
        result.setPage(page);
        result.setPageSize(pageSize);
        return result;
    }
}

