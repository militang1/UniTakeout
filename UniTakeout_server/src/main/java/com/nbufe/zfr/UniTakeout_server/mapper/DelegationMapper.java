package com.nbufe.zfr.UniTakeout_server.mapper;

import com.nbufe.zfr.UniTakeout_server.entity.Delegation;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface DelegationMapper {
    Delegation selectById(Long id);
    List<Delegation> selectList(String type, String status, Integer offset, Integer limit);
    Long count(String type, String status);
    void insert(Delegation delegation);
    void update(Delegation delegation);
}

