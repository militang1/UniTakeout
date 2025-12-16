package com.nbufe.zfr.UniTakeout_server.service;

import com.nbufe.zfr.UniTakeout_server.dto.DelegationCreateDTO;
import com.nbufe.zfr.UniTakeout_server.entity.Delegation;
import com.nbufe.zfr.UniTakeout_server.vo.PageResult;

public interface DelegationService {
    PageResult<Delegation> getList(String type, String status, Integer page, Integer pageSize);
    Delegation create(Long userId, DelegationCreateDTO dto);
    Delegation getById(Long id);
    void accept(Long id, Long userId);
    void complete(Long id);
}

