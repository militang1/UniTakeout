package com.nbufe.zfr.UniTakeout_server.mapper;

import com.nbufe.zfr.UniTakeout_server.entity.Shop;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ShopMapper {
    Shop selectById(Long id);
    List<Shop> selectList(String keyword, String sort, Integer offset, Integer limit);
    Long count(String keyword);
}

