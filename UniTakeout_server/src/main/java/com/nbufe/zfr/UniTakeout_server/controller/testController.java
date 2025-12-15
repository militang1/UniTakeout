package com.nbufe.zfr.UniTakeout_server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class testController {
    @GetMapping("/home")
    public String home(){
        return "校园外卖启动成功！";
    }
}
