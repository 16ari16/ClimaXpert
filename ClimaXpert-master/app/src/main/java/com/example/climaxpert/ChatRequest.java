package com.example.climaxpert;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class ChatRequest {
    @SerializedName("model")
    private String model;
    @SerializedName("temperature")
    private String temperature;
    @SerializedName("presence_penalty")
    private String presencePenalty;
    @SerializedName("messages")
    private List<Message> messages;

    // Конструктор
    public ChatRequest(String model, String temperature, String presencePenalty, List<Message> messages) {
        this.model = model;
        this.temperature = temperature;
        this.presencePenalty = presencePenalty;
        this.messages = messages;
    }

    // Геттеры (необходимы для Gson)
    public String getModel() { return model; }
    public String getTemperature() { return temperature; }
    public String getPresencePenalty() { return presencePenalty; }
    public List<Message> getMessages() { return messages; }
}