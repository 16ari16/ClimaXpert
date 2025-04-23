package com.example.climaxpert;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

interface ChatService {
    @POST("api/openai/v1/chat/completions")
    Call<ChatResponse> sendMessage(@Body ChatRequest request);
}