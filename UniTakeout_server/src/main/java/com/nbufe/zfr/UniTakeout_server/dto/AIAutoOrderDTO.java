package com.nbufe.zfr.UniTakeout_server.dto;

import lombok.Data;

@Data
public class AIAutoOrderDTO {
    private String query;
    private Long userId;
    private String address;
}

