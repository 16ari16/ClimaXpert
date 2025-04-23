package com.example.climaxpert;

import com.google.gson.annotations.SerializedName;

class Choice {
    @SerializedName("index")
    private int index;
    @SerializedName("message")
    private Message message;
    @SerializedName("finish_reason")
    private String finishReason;

    public Choice(int index, Message message, String finishReason) {
        this.index = index;
        this.message = message;
        this.finishReason = finishReason;
    }

    public int getIndex() { return index; }
    public Message getMessage() { return message; }
    public String getFinishReason() { return finishReason; }
}