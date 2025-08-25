package com.zj.config;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;
import java.time.Duration;
import io.netty.channel.ChannelOption;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Component;

@Component
public class WebClientConfig {
    @Bean
    public WebClient.Builder webClientBuilder() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMinutes(5))  // 设置响应超时时间为5秒
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000);  // 设置连接超时时间为3秒

        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        return WebClient.builder().clientConnector(connector);
    }
}
