package com.zj.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;

public class TestChatClient implements ChatClient {
    @Override
    public ChatClientRequestSpec prompt() {
        return null;
    }

    @Override
    public ChatClientRequestSpec prompt(String content) {
        return null;
    }

    @Override
    public ChatClientRequestSpec prompt(Prompt prompt) {
        return null;
    }

    @Override
    public Builder mutate() {
        return null;
    }
}
