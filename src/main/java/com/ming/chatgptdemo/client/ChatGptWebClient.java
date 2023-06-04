package com.ming.chatgptdemo.client;

import com.ming.chatgptdemo.ChatGptConfig;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.util.concurrent.TimeUnit;

@Configuration
public class ChatGptWebClient {

    @Autowired
    private ChatGptConfig chatGptConfig;

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .proxy(proxy ->
                    proxy.type(ProxyProvider.Proxy.HTTP)
                            .host(chatGptConfig.proxyUrl)
                            .port(chatGptConfig.proxyPort)
                );

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

}
