package com.nbufe.zfr.UniTakeout_server.service.impl;

import com.nbufe.zfr.UniTakeout_server.common.Constants;
import com.nbufe.zfr.UniTakeout_server.dto.LoginDTO;
import com.nbufe.zfr.UniTakeout_server.dto.RegisterDTO;
import com.nbufe.zfr.UniTakeout_server.entity.User;
import com.nbufe.zfr.UniTakeout_server.entity.VerificationCode;
import com.nbufe.zfr.UniTakeout_server.mapper.UserMapper;
import com.nbufe.zfr.UniTakeout_server.mapper.VerificationCodeMapper;
import com.nbufe.zfr.UniTakeout_server.service.AuthService;
import com.nbufe.zfr.UniTakeout_server.utils.CodeUtil;
import com.nbufe.zfr.UniTakeout_server.utils.JwtUtil;
import com.nbufe.zfr.UniTakeout_server.vo.LoginVO;
import com.nbufe.zfr.UniTakeout_server.vo.UserInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    private final VerificationCodeMapper codeMapper;

    public AuthServiceImpl(UserMapper userMapper, VerificationCodeMapper codeMapper) {
        this.userMapper = userMapper;
        this.codeMapper = codeMapper;
    }

    @Override
    @Transactional
    public LoginVO login(LoginDTO dto) {
        VerificationCode code = codeMapper.selectLatestByPhone(dto.getPhone());
        if (code == null || !code.getCode().equals(dto.getCode())) {
            throw new RuntimeException("验证码错误");
        }
        
        long minutes = ChronoUnit.MINUTES.between(code.getCreateTime(), LocalDateTime.now());
        if (minutes > Constants.CODE_EXPIRATION) {
            throw new RuntimeException("验证码已过期");
        }
        
        User user = userMapper.selectByPhone(dto.getPhone());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        String token = JwtUtil.generateToken(user.getId());
        UserInfoVO userInfo = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfo);
        
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserInfo(userInfo);
        return vo;
    }

    @Override
    @Transactional
    public LoginVO register(RegisterDTO dto) {
        VerificationCode code = codeMapper.selectLatestByPhone(dto.getPhone());
        if (code == null || !code.getCode().equals(dto.getCode())) {
            throw new RuntimeException("验证码错误");
        }
        
        long minutes = ChronoUnit.MINUTES.between(code.getCreateTime(), LocalDateTime.now());
        if (minutes > Constants.CODE_EXPIRATION) {
            throw new RuntimeException("验证码已过期");
        }
        
        User existUser = userMapper.selectByPhone(dto.getPhone());
        if (existUser != null) {
            throw new RuntimeException("手机号已注册");
        }
        
        User user = new User();
        user.setPhone(dto.getPhone());
        user.setNickname(dto.getNickname());
        userMapper.insert(user);
        
        String token = JwtUtil.generateToken(user.getId());
        UserInfoVO userInfo = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfo);
        
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserInfo(userInfo);
        return vo;
    }

    @Override
    public String sendCode(String phone) {
        VerificationCode latestCode = codeMapper.selectLatestByPhone(phone);
        if (latestCode != null) {
            long seconds = ChronoUnit.SECONDS.between(latestCode.getCreateTime(), LocalDateTime.now());
            if (seconds < Constants.CODE_SEND_INTERVAL) {
                throw new RuntimeException("发送过于频繁，请稍后再试");
            }
        }
        
        String code = CodeUtil.generateCode();
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setPhone(phone);
        verificationCode.setCode(code);
        codeMapper.insert(verificationCode);
        
        // 这里应该调用短信服务发送验证码，暂时只打印日志
        System.out.println("发送验证码到 " + phone + ": " + code);
        return code;
    }
}

