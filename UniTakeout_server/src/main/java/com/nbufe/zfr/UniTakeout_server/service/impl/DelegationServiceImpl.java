package com.nbufe.zfr.UniTakeout_server.service.impl;

import com.nbufe.zfr.UniTakeout_server.common.Constants;
import com.nbufe.zfr.UniTakeout_server.dto.DelegationCreateDTO;
import com.nbufe.zfr.UniTakeout_server.entity.Delegation;
import com.nbufe.zfr.UniTakeout_server.entity.User;
import com.nbufe.zfr.UniTakeout_server.mapper.DelegationMapper;
import com.nbufe.zfr.UniTakeout_server.mapper.UserMapper;
import com.nbufe.zfr.UniTakeout_server.service.DelegationService;
import com.nbufe.zfr.UniTakeout_server.vo.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DelegationServiceImpl implements DelegationService {
    private final DelegationMapper delegationMapper;
    private final UserMapper userMapper;

    public DelegationServiceImpl(DelegationMapper delegationMapper, UserMapper userMapper) {
        this.delegationMapper = delegationMapper;
        this.userMapper = userMapper;
    }

    @Override
    public PageResult<Delegation> getList(String type, String status, Integer page, Integer pageSize) {
        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        if (pageSize > 100) pageSize = 100;
        
        Integer offset = (page - 1) * pageSize;
        List<Delegation> list = delegationMapper.selectList(type, status, offset, pageSize);
        
        Long total = delegationMapper.count(type, status);
        
        PageResult<Delegation> result = new PageResult<>();
        result.setList(list);
        result.setTotal(total);
        result.setPage(page);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    @Transactional
    public Delegation create(Long userId, DelegationCreateDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        Delegation delegation = new Delegation();
        delegation.setType(dto.getType());
        delegation.setUserId(userId);
        delegation.setUserName(user.getNickname());
        delegation.setTitle(dto.getTitle());
        delegation.setDescription(dto.getDescription());
        delegation.setShopId(dto.getShopId());
        delegation.setShopName(dto.getShopName());
        delegation.setReward(dto.getReward());
        delegation.setStatus(Constants.DELEGATION_STATUS_PENDING);
        delegation.setItems(dto.getItems());
        
        delegationMapper.insert(delegation);
        return delegation;
    }

    @Override
    public Delegation getById(Long id) {
        return delegationMapper.selectById(id);
    }

    @Override
    @Transactional
    public void accept(Long id, Long userId) {
        Delegation delegation = delegationMapper.selectById(id);
        if (delegation == null) {
            throw new RuntimeException("委托不存在");
        }
        if (!Constants.DELEGATION_TYPE_REQUEST.equals(delegation.getType())) {
            throw new RuntimeException("只能接受求带类型的委托");
        }
        if (!Constants.DELEGATION_STATUS_PENDING.equals(delegation.getStatus())) {
            throw new RuntimeException("委托状态不允许接受");
        }
        
        delegation.setStatus(Constants.DELEGATION_STATUS_ACCEPTED);
        delegation.setAcceptedBy(userId);
        delegationMapper.update(delegation);
    }

    @Override
    @Transactional
    public void complete(Long id) {
        Delegation delegation = delegationMapper.selectById(id);
        if (delegation == null) {
            throw new RuntimeException("委托不存在");
        }
        delegation.setStatus(Constants.DELEGATION_STATUS_COMPLETED);
        delegationMapper.update(delegation);
    }
}

