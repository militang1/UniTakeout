package com.nbufe.zfr.UniTakeout_server.service;

import com.nbufe.zfr.UniTakeout_server.dto.UserUpdateDTO;
import com.nbufe.zfr.UniTakeout_server.vo.UserInfoVO;

public interface UserService {
    UserInfoVO getInfo(Long userId);
    UserInfoVO updateInfo(Long userId, UserUpdateDTO dto);
}

