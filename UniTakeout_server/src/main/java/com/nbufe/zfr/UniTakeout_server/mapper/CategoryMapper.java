package com.nbufe.zfr.UniTakeout_server.mapper;

import com.nbufe.zfr.UniTakeout_server.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> selectByShopId(Long shopId);
    Category selectById(Long id);
    void insert(Category category);
    void update(Category category);
    void delete(Long id);
}

