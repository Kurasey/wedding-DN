package io.github.kaurami.wems.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "telegram")
public class TelegramConfig {
    private String botToken;
    private String groomChatId;
    private String brideChatId;

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public String getGroomChatId() {
        return groomChatId;
    }

    public void setGroomChatId(String groomChatId) {
        this.groomChatId = groomChatId;
    }

    public String getBrideChatId() {
        return brideChatId;
    }

    public void setBrideChatId(String brideChatId) {
        this.brideChatId = brideChatId;
    }
}