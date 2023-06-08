package com.ming.chatgptdemo.client;

import com.ming.chatgptdemo.ChatGptConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;

import static com.ming.chatgptdemo.constant.HttpConstant.PROXY_PORT;
import static com.ming.chatgptdemo.constant.HttpConstant.PROXY_URL;

@Configuration
public class ChatGptWebClient {

    @Autowired
    private ChatGptConfig chatGptConfig;

    @Bean
    public WebClient webClient() {
        HttpClient httpClient;
        // 判断是否进行了代理
        if (PROXY_URL.equals(chatGptConfig.proxyUrl) || PROXY_PORT.equals(chatGptConfig.proxyPort)){
            httpClient = HttpClient.create()
                    .secure()
                    .tcpConfiguration(tcpClient ->
                            tcpClient.proxy(proxy ->
                                    proxy.type(ProxyProvider.Proxy.HTTP)
                                            .host(chatGptConfig.proxyUrl)
                                            .port(chatGptConfig.proxyPort)
                            )
                    );
        }else {
            httpClient = HttpClient.create();
        }

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

}
