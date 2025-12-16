package com.nbufe.zfr.UniTakeout_server.controller;

import com.nbufe.zfr.UniTakeout_server.common.BaseContext;
import com.nbufe.zfr.UniTakeout_server.common.Result;
import com.nbufe.zfr.UniTakeout_server.dto.UserUpdateDTO;
import com.nbufe.zfr.UniTakeout_server.service.UserService;
import com.nbufe.zfr.UniTakeout_server.vo.UserInfoVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/info")
    public Result<UserInfoVO> getInfo() {
        try {
            Long userId = BaseContext.getCurrentUserId();
            UserInfoVO vo = userService.getInfo(userId);
            return Result.success(vo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/info")
    public Result<UserInfoVO> updateInfo(@RequestBody UserUpdateDTO dto) {
        try {
            Long userId = BaseContext.getCurrentUserId();
            UserInfoVO vo = userService.updateInfo(userId, dto);
            return Result.success("更新成功", vo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

