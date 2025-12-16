package com.nbufe.zfr.UniTakeout_server.vo;

import lombok.Data;

@Data
public class LoginVO {
    private String token;
    private UserInfoVO userInfo;
}

