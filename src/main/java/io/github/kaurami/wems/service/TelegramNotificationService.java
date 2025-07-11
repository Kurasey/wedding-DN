package io.github.kaurami.wems.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.kaurami.wems.config.TelegramConfig;
import io.github.kaurami.wems.model.ActionSource;
import io.github.kaurami.wems.model.Family;
import io.github.kaurami.wems.model.Guest;
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
import java.util.stream.Collectors;

@Service
public class TelegramNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(TelegramNotificationService.class);
    private final TelegramConfig telegramConfig;
    private final RestTemplate restTemplate;

    public TelegramNotificationService(TelegramConfig telegramConfig) {
        this.telegramConfig = telegramConfig;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Уведомление при первоначальном заполнении формы RSVP.
     */
    @Async
    public void sendRsvpNotification(Family family, List<Guest> guests) {
        String guestNames = guests.stream()
                .map(Guest::getName)
                .map(name -> "- " + name)
                .collect(Collectors.joining("\n"));

        String message = String.format(
                "🎉 <b>Новое подтверждение (RSVP)!</b> 🎉\n\n" +
                        "<b>Семья:</b> %s\n" +
                        "<b>Количество гостей:</b> %d\n" +
                        "<b>Имена:</b>\n%s",
                escapeHtml(family.getName()),
                guests.size(),
                escapeHtml(guestNames)
        );
        sendMessage(message);
    }

    @Async
    public void sendGuestAddedNotification(Family family, Guest newGuest, ActionSource source) {
        String message = String.format(
                "➕ <b>Добавлен новый гость</b> ➕\n\n" +
                        "<b>Семья:</b> %s\n" +
                        "<b>Имя гостя:</b> %s\n" +
                        "<b>Статус:</b> %s\n" +
                        "<b>Источник:</b> %s",
                escapeHtml(family.getName()),
                escapeHtml(newGuest.getName()),
                newGuest.isWillAttend() ? "Придет ✅" : "Не придет ❌",
                source.getDisplayName()
        );
        sendMessage(message);
    }

    @Async
    public void sendGuestRemovedNotification(Family family, String guestName, ActionSource source) {
        String message = String.format(
                "➖ <b>Гость удален</b> ➖\n\n" +
                        "<b>Семья:</b> %s\n" +
                        "<b>Имя гостя:</b> %s\n" +
                        "<b>Источник:</b> %s",
                escapeHtml(family.getName()),
                escapeHtml(guestName),
                source.getDisplayName()
        );
        sendMessage(message);
    }

    @Async
    public void sendGuestStatusChangedNotification(Family family, Guest guest, ActionSource source) {
        String newStatus = guest.isWillAttend() ? "Придет ✅" : "Не придет ❌";
        String message = String.format(
                "🔄 <b>Изменен статус гостя</b> 🔄\n\n" +
                        "<b>Семья:</b> %s\n" +
                        "<b>Имя гостя:</b> %s\n" +
                        "<b>Новый статус:</b> %s\n" +
                        "<b>Источник:</b> %s",
                escapeHtml(family.getName()),
                escapeHtml(guest.getName()),
                newStatus,
                source.getDisplayName()
        );
        sendMessage(message);
    }


    private void sendMessage(String message) {
        String botToken = telegramConfig.getBotToken();
        List<String> chatIds = getRecipientChatIds();

        if (botToken == null || botToken.isBlank() || chatIds.isEmpty()) {
            logger.warn("Telegram bot token or chat IDs are not configured. Skipping notification.");
            return;
        }

        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        for (String chatId : chatIds) {
            try {
                TelegramMessageRequest requestPayload = new TelegramMessageRequest(chatId, message);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<TelegramMessageRequest> entity = new HttpEntity<>(requestPayload, headers);

                restTemplate.postForObject(url, entity, String.class);
                logger.info("Successfully sent Telegram notification to chat ID {}", chatId);
            } catch (Exception e) {
                logger.error("Failed to send Telegram notification to chat ID {}: {}", chatId, e.getMessage());
            }
        }
    }

    private List<String> getRecipientChatIds() {
        List<String> chatIds = new ArrayList<>();
        if (isValid(telegramConfig.getGroomChatId())) {
            chatIds.add(telegramConfig.getGroomChatId());
        }
        if (isValid(telegramConfig.getBrideChatId())) {
            chatIds.add(telegramConfig.getBrideChatId());
        }
        return chatIds;
    }

    private boolean isValid(String chatId) {
        return chatId != null && !chatId.isBlank();
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
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