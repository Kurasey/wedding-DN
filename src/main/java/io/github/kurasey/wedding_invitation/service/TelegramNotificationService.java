package io.github.kurasey.wedding_invitation.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.kurasey.wedding_invitation.config.TelegramConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class TelegramNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(TelegramNotificationService.class);
    private final TelegramConfig telegramConfig;
    private final RestTemplate restTemplate;

    public TelegramNotificationService(TelegramConfig telegramConfig) {
        this.telegramConfig = telegramConfig;
        this.restTemplate = new RestTemplate();
    }

    @Async
    public void sendRsvpNotification(String familyName, int guestCount, String guestNames) {
        String botToken = telegramConfig.getBotToken();

        // 1. Собираем список получателей
        List<String> chatIds = new ArrayList<>();
        if (isValid(telegramConfig.getGroomChatId())) {
            chatIds.add(telegramConfig.getGroomChatId());
        }
        if (isValid(telegramConfig.getBrideChatId())) {
            chatIds.add(telegramConfig.getBrideChatId());
        }

        // 2. Проверяем, есть ли кому отправлять и есть ли токен
        if (botToken == null || botToken.isBlank() || chatIds.isEmpty()) {
            logger.warn("Telegram bot token or chat IDs are not configured. Skipping notification.");
            return;
        }

        // 3. Формируем сообщение один раз
        String message = String.format(
                "🎉 <b>Новое подтверждение на свадьбу!</b> 🎉\n\n" +
                        "<b>Семья:</b> %s\n" +
                        "<b>Количество гостей:</b> %d\n" +
                        "<b>Имена:</b>\n%s",
                escapeHtml(familyName),
                guestCount,
                escapeHtml(guestNames)
        );

        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        // 4. Отправляем сообщение каждому получателю в цикле
        for (String chatId : chatIds) {
            try {
                TelegramMessageRequest requestPayload = new TelegramMessageRequest(chatId, message);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<TelegramMessageRequest> entity = new HttpEntity<>(requestPayload, headers);

                restTemplate.postForObject(url, entity, String.class);
                logger.info("Successfully sent Telegram notification to chat ID {}", chatId);

            } catch (Exception e) {
                // Если ошибка для одного получателя, логируем ее и продолжаем, чтобы другие получили
                logger.error("Failed to send Telegram notification to chat ID {}: {}", chatId, e.getMessage());
            }
        }
    }

    private boolean isValid(String chatId) {
        return chatId != null && !chatId.isBlank();
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&")
                .replace("<", "<")
                .replace(">", ">");
    }

    private static class TelegramMessageRequest {
        @JsonProperty("chat_id")
        private final String chatId;

        private final String text;

        @JsonProperty("parse_mode")
        private final String parseMode = "HTML";

        public TelegramMessageRequest(String chatId, String text) {
            this.chatId = chatId;
            this.text = text;
        }

        public String getChatId() { return chatId; }
        public String getText() { return text; }
        public String getParseMode() { return parseMode; }
    }
}