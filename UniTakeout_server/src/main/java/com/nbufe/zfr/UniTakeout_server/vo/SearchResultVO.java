package com.nbufe.zfr.UniTakeout_server.vo;

import com.nbufe.zfr.UniTakeout_server.entity.Product;
import com.nbufe.zfr.UniTakeout_server.entity.Shop;
import lombok.Data;
import java.util.List;

@Data
public class SearchResultVO {
    private ShopResult shops;
    private ProductResult products;
    
    @Data
    public static class ShopResult {
        private List<Shop> list;
        private Long total;
    }
    
    @Data
    public static class ProductResult {
        private List<Product> list;
        private Long total;
    }
}

