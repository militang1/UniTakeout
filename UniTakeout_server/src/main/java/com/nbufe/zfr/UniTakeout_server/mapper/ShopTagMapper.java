package com.nbufe.zfr.UniTakeout_server.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ShopTagMapper {
    List<String> selectTagsByShopId(Long shopId);
}

