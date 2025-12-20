package com.nbufe.zfr.UniTakeout_server.controller.admin;

import com.nbufe.zfr.UniTakeout_server.common.Result;
import com.nbufe.zfr.UniTakeout_server.dto.LoginDTO;
import com.nbufe.zfr.UniTakeout_server.service.AuthService;
import com.nbufe.zfr.UniTakeout_server.vo.LoginVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {
    private final AuthService authService;

    public AdminAuthController(AuthService authService) {
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

