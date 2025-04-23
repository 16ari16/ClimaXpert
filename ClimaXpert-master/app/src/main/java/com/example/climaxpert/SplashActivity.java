package com.example.climaxpert;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.cert.Certificate;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding_swipes();
        get_weather();
    }


    public void get_weather() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://free.easychat.work/") // Базовый URL
                .addConverterFactory(GsonConverterFactory.create()) // Используем Gson для конвертации JSON
                .build();

        // Создаем экземпляр интерфейса API
        ChatService chatService = retrofit.create(ChatService.class);

        // Создаем данные запроса
        List<Message> messages = List.of(new Message("user", "Привет!"));
        ChatRequest chatRequest = new ChatRequest("gpt-3.5-turbo", "0.7", "0", messages);

        // Выполняем запрос
        Call<ChatResponse> call = chatService.sendMessage(chatRequest);
        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                TextView texter = findViewById(R.id.textView);
                texter.setText(Integer.toString(response.code()));


                TextView texter2 = findViewById(R.id.textView2);

            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {

            }
        });
    }

    private void binding_swipes() {
        findViewById(R.id.main).setOnTouchListener(
            new OnSwipeTouchListener(this) {
                @Override
                public void onSwipeTop() {
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                }
        });
    }
}