package com.example.climaxpert.AiApi;

import okhttp3.*;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Collections;

public class OpenAIApiClient {
    private static final String API_URL = "https://free.easychat.work/api/openai/v1/chat/completions";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public void getChatResponse(String userMessage, final Callback callback) {
        // 1. Подготовка тела запроса
        OpenAIRequest.Message message = new OpenAIRequest.Message("user", userMessage);
        OpenAIRequest requestBody = new OpenAIRequest(
                "gpt-3.5-turbo",
                0.7,
                0,
                Collections.singletonList(message)
        );

        String json = gson.toJson(requestBody);
        RequestBody body = RequestBody.create(json, JSON);

        // 2. Создание запроса
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        // 3. Асинхронное выполнение
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure(new IOException("Unexpected code " + response));
                    return;
                }

                String responseData = response.body().string();
                OpenAIResponse apiResponse = gson.fromJson(responseData, OpenAIResponse.class);
                callback.onSuccess(apiResponse);
            }
        });
    }

    public interface Callback {
        void onSuccess(OpenAIResponse response);
        void onFailure(Exception e);
    }
}