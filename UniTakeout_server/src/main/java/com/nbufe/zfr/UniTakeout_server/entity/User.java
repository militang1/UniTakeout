package com.nbufe.zfr.UniTakeout_server.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String phone;
    private String nickname;
    private String avatar;
    private String address;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

