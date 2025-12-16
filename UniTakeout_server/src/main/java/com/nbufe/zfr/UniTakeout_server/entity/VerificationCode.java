package com.nbufe.zfr.UniTakeout_server.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VerificationCode {
    private Long id;
    private String phone;
    private String code;
    private LocalDateTime createTime;
}

