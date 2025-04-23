package com.example.climaxpert;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class ChatResponse {
    @SerializedName("id")
    private String id;
    @SerializedName("object")
    private String object;
    @SerializedName("created")
    private long created;
    @SerializedName("model")
    private String model;
    @SerializedName("choices")
    private List<Choice> choices;


    public ChatResponse(String id, String object, long created, String model, List<Choice> choices) {
        this.id = id;
        this.object = object;
        this.created = created;
        this.model = model;
        this.choices = choices;
    }

    public String getId() {
        return id;
    }

    public String getObject() {
        return object;
    }

    public long getCreated() {
        return created;
    }

    public String getModel() {
        return model;
    }

    public List<Choice> getChoices() { return choices; }

}