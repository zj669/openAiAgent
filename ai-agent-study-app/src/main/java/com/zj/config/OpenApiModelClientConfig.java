package com.zj.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.DefaultChatClientBuilder;
import org.springframework.ai.chat.client.observation.ChatClientObservationConvention;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Component;

@Component
public class OpenApiModelClientConfig {
    @Bean("deepseekOpenAi")
    public OpenAiChatModel deepseekChatModel(WebClient.Builder webClientBuilder) {
        System.out.println("开始初始化model，webClientBuilder是" + webClientBuilder.toString());
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl("https://api.deepseek.com")
                .apiKey("sk-913c967554c44c3ea3182be9cffb74b8")
                .webClientBuilder(webClientBuilder)
                .build();

        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("deepseek-chat")
                        .build())
                .build();

        return chatModel;
    }
    @Bean("claudeOpenAi")
    public OpenAiChatModel claudeChatModel(WebClient.Builder webClientBuilder) {
        System.out.println("开始初始化model，webClientBuilder是" + webClientBuilder.toString());
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl("https://globalai.vip")
                .apiKey("sk-igZGYZBslPs4UxqU1r8BuprgCFzD388sk36Mwnp95jxsfgKp")
                .webClientBuilder( webClientBuilder)
                .build();
        OpenAiChatModel openAiChatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("claude-3-7-sonnet")
                        .build())
                .build();
        return openAiChatModel;
    }
    // gemini-2.5-flash-lite-thinking
    @Bean("geminiOpenAi")
    public OpenAiChatModel geminiChatModel(WebClient.Builder webClientBuilder) {
        System.out.println("开始初始化model，webClientBuilder是" + webClientBuilder.toString());
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl("https://globalai.vip")
                .apiKey("sk-M5krIRHhrlTXuR409xwhJilDom8o3Cu6lf4x3HKvvDwBUR7l")
                .webClientBuilder( webClientBuilder)
                .build();
        OpenAiChatModel openAiChatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("gemini-2.5-flash-lite-preview-06-17")
                        .build())
                .build();
        return openAiChatModel;
    }
    @Bean("deepseekChatClientBuilder")
    public ChatClient.Builder deepseekChatClientBuilder(@Qualifier("deepseekOpenAi") OpenAiChatModel deepseekChatModel) {

        return new DefaultChatClientBuilder(deepseekChatModel, ObservationRegistry.NOOP, (ChatClientObservationConvention) null);
    }

    @Bean("claudeChatClientBuilder")
    public ChatClient.Builder chatClientBuilder(@Qualifier("claudeOpenAi") OpenAiChatModel claudeChatModel) {
        return new DefaultChatClientBuilder(claudeChatModel, ObservationRegistry.NOOP, (ChatClientObservationConvention) null);
    }

    @Bean("geminiChatClientBuilder")
    public ChatClient.Builder geminiChatClientBuilder(@Qualifier("geminiOpenAi") OpenAiChatModel claudeChatModel) {
        return new DefaultChatClientBuilder(claudeChatModel, ObservationRegistry.NOOP, (ChatClientObservationConvention) null);
    }

}
