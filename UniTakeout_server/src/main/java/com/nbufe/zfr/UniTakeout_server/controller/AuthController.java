package com.nbufe.zfr.UniTakeout_server.controller;

import com.nbufe.zfr.UniTakeout_server.common.Result;
import com.nbufe.zfr.UniTakeout_server.dto.LoginDTO;
import com.nbufe.zfr.UniTakeout_server.dto.RegisterDTO;
import com.nbufe.zfr.UniTakeout_server.service.AuthService;
import com.nbufe.zfr.UniTakeout_server.vo.LoginVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        try {
            LoginVO vo = authService.login(dto);
            return Result.success("登录成功", vo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<LoginVO> register(@RequestBody RegisterDTO dto) {
        try {
            LoginVO vo = authService.register(dto);
            return Result.success("注册成功", vo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/send-code")
    public Result<Void> sendCode(@RequestBody LoginDTO dto) {
        try {
            authService.sendCode(dto.getPhone());
            Result<Void> result = Result.success();
            result.setMessage("验证码已发送");
            return result;
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

