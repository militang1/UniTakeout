package com.nbufe.zfr.UniTakeout_server.service;

import com.nbufe.zfr.UniTakeout_server.dto.LoginDTO;
import com.nbufe.zfr.UniTakeout_server.dto.RegisterDTO;
import com.nbufe.zfr.UniTakeout_server.vo.LoginVO;

public interface AuthService {
    LoginVO login(LoginDTO dto);
    LoginVO register(RegisterDTO dto);
    String sendCode(String phone);
}

