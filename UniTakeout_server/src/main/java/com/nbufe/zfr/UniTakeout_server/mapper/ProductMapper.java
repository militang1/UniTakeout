package com.nbufe.zfr.UniTakeout_server.mapper;

import com.nbufe.zfr.UniTakeout_server.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ProductMapper {
    Product selectById(Long id);
    List<Product> selectByShopId(Long shopId, Long categoryId);
    List<Product> selectAll();
    List<Product> searchByKeyword(String keyword, Long shopId, Integer offset, Integer limit);
    Long countByKeyword(String keyword, Long shopId);
    void insert(Product product);
    void update(Product product);
    void delete(Long id);
}

