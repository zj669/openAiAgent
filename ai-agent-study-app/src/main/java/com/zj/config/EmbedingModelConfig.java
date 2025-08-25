package com.zj.config;

import io.micrometer.observation.ObservationRegistry;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Component
public class EmbedingModelConfig {
    @Value("${spring.ai.ollama.embedding.model}")
    private String embeddingModel;
    @Value("${spring.ai.ollama.base-url}")
    private String baseUrl;


    @Bean
    public OllamaEmbeddingModel ollamaEmbeddingModel() {
        return new OllamaEmbeddingModel(OllamaApi.builder().baseUrl(baseUrl).build()
                , OllamaOptions.builder().model(embeddingModel).build()
                , ObservationRegistry.create()
        , ModelManagementOptions.builder().timeout(Duration.ofSeconds(5)).maxRetries(3).build()
        );
    }
    @Bean
    public PgVectorStore pgVectorStore(JdbcTemplate jdbcTemplate) {
        reactor.netty.http.client.HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(3000))       // 响应超时（整个请求）
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 50000) // 连接超时（5秒）
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(300))    // 读取超时（20秒）
                        .addHandlerLast(new WriteTimeoutHandler(300))); // 写入超时（20秒）

        Builder client = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient));
        OllamaApi build2 = OllamaApi.builder().baseUrl(baseUrl).webClientBuilder(client).build();
        OllamaOptions build = OllamaOptions.builder().model(embeddingModel).build();
        ObservationRegistry observationRegistry = ObservationRegistry.create();
        ModelManagementOptions build1 = ModelManagementOptions.builder().timeout(Duration.ofSeconds(5)).maxRetries(3).build();
        OllamaEmbeddingModel ollamaEmbeddingModel = new OllamaEmbeddingModel(build2, build, observationRegistry, build1);
        return PgVectorStore.builder(jdbcTemplate, ollamaEmbeddingModel).build();
    }
}
