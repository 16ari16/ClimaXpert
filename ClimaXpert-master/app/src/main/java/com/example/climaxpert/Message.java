package com.example.climaxpert;
import com.google.gson.annotations.SerializedName;

public class Message {
    @SerializedName("role")
    private String role;
    @SerializedName("content")
    private String content;

    // Конструктор
    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    // Геттеры (необходимы для Gson)
    public String getRole() { return role; }
    public String getContent() { return content; }
}