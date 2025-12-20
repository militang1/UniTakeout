package com.nbufe.zfr.UniTakeout_server.controller.user;

import com.nbufe.zfr.UniTakeout_server.common.BaseContext;
import com.nbufe.zfr.UniTakeout_server.common.Result;
import com.nbufe.zfr.UniTakeout_server.dto.DelegationCreateDTO;
import com.nbufe.zfr.UniTakeout_server.entity.Delegation;
import com.nbufe.zfr.UniTakeout_server.service.DelegationService;
import com.nbufe.zfr.UniTakeout_server.vo.PageResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delegations")
public class DelegationController {
    private final DelegationService delegationService;

    public DelegationController(DelegationService delegationService) {
        this.delegationService = delegationService;
    }

    @GetMapping
    public Result<PageResult<Delegation>> getList(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            PageResult<Delegation> result = delegationService.getList(type, status, page, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping
    public Result<Delegation> create(@RequestBody DelegationCreateDTO dto) {
        try {
            Long userId = BaseContext.getCurrentUserId();
            Delegation delegation = delegationService.create(userId, dto);
            return Result.success("委托发布成功", delegation);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Delegation> getById(@PathVariable Long id) {
        try {
            Delegation delegation = delegationService.getById(id);
            return Result.success(delegation);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/accept")
    public Result<Void> accept(@PathVariable Long id) {
        try {
            Long userId = BaseContext.getCurrentUserId();
            delegationService.accept(id, userId);
            Result<Void> result = Result.success();
            result.setMessage("已接受委托");
            return result;
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/complete")
    public Result<Void> complete(@PathVariable Long id) {
        try {
            delegationService.complete(id);
            Result<Void> result = Result.success();
            result.setMessage("委托已完成");
            return result;
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

