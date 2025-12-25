package com.nbufe.zfr.UniTakeout_server;

import com.nbufe.zfr.UniTakeout_server.config.AIConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@MapperScan("com.nbufe.zfr.UniTakeout_server.mapper")
public class UniTakeoutServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(UniTakeoutServerApplication.class, args);
	}

}
