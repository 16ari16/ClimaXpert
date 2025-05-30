package com.example.climaxpert.AiApi;
import java.util.List;

public class OpenAIRequest {
    private String model;
    private double temperature;
    private double presence_penalty;
    private List<Message> messages;

    // Конструктор
    public OpenAIRequest(String model, double temperature, double presence_penalty, List<Message> messages) {
        this.model = model;
        this.temperature = temperature;
        this.presence_penalty = presence_penalty;
        this.messages = messages;
    }

    // Вложенный класс для сообщений
    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}