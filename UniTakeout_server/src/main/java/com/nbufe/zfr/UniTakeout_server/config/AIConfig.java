package com.nbufe.zfr.UniTakeout_server.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "unitakeout.deepseek")
@Data
public class AIConfig {
    private String apiKey;
    private String baseUrl;
    private String model;
}
