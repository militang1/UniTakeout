package com.nbufe.zfr.UniTakeout_server.mapper;

import com.nbufe.zfr.UniTakeout_server.entity.VerificationCode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VerificationCodeMapper {
    VerificationCode selectLatestByPhone(String phone);
    void insert(VerificationCode code);
    void deleteByPhone(String phone);
}

