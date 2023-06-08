package com.ming.chatgptdemo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@ComponentScan("com.ming")
@Configuration
@ConfigurationProperties(prefix = "chatgpt.api")
public class ChatGptConfig {

    /**
     * 代理url
     */
    public String proxyUrl;

    /**
     * 代理端口
     */
    public Integer proxyPort;

    /**
     * 私钥
     */
    public String apiKey;

    /**
     * 影响因子
     */
    public Double temperature = 0.9;

    /**
     * 最大上下文对话的数量
     */
    public Integer chatLargestContext = 10;

}
