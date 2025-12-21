package com.nbufe.zfr.UniTakeout_server.config;

import com.nbufe.zfr.UniTakeout_server.utils.AliOssUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AliOssConfig {
    
    @Value("${unitakeout.alioss.endpoint}")
    private String endpoint;
    
    @Value("${unitakeout.alioss.access-key-id}")
    private String accessKeyId;
    
    @Value("${unitakeout.alioss.access-key-secret}")
    private String accessKeySecret;
    
    @Value("${unitakeout.alioss.bucket-name}")
    private String bucketName;
    
    @Bean
    public AliOssUtil aliOssUtil() {
        return new AliOssUtil(endpoint, accessKeyId, accessKeySecret, bucketName);
    }
}



