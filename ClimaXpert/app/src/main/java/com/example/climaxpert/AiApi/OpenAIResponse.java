package com.example.climaxpert.AiApi;
import java.util.List;

public class OpenAIResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;
    private String system_fingerprint;

    // Геттеры
    public String getAssistantReply() {
        if (choices != null && !choices.isEmpty()) {
            return choices.get(0).message.content;
        }
        return null;
    }

    // Вложенные классы
    public static class Choice {
        private int index;
        private Message message;
        private String finish_reason;
    }

    public static class Message {
        private String role;
        private String content;
    }

    public static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;
    }
}