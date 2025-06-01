package com.example.climaxpert;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.climaxpert.AiApi.OpenAIApiClient;
import com.example.climaxpert.AiApi.OpenAIResponse;
import com.example.climaxpert.WeatherApi.WeatherApiClient;

import java.util.Date;

public class AssistantActivity extends AppCompatActivity {
    LayoutInflater inflater;
    LinearLayout container;
    String weatherInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_assistant);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inflater = getLayoutInflater();
        container = findViewById(R.id.messages_container);

        SharedPreferences sh = getSharedPreferences(
                "Weather", MODE_PRIVATE
        );
        String city = sh.getString("city", "Kazan");
        getWeather(city);
        container.removeAllViews();

        findViewById(R.id.send_message).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String user_text = send_message();

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() { add_gpt_message(user_text, weatherInfo); }
                        };

                        Thread t = new Thread(runnable);
                        t.start();

                        Log.e("[Assistant Log]", String.valueOf(container.getBottom()));
                    }
                }
        );

        findViewById(R.id.back_arrow).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AssistantActivity.this, HomeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(
                                R.anim.slide_in_top,
                                R.anim.slide_out_bottom
                        );
                    }
                }
        );
    }

    public String send_message() {
        String user_text = ((EditText)findViewById(R.id.message_input)).getText().toString();
        ((EditText)findViewById(R.id.message_input)).setText("");
        View user_message = inflater.inflate(R.layout.user_message, container, false);

        ((TextView)user_message.findViewById(R.id.message_text)).setText(user_text);
        container.addView(user_message);

        return user_text;
    }

    public void add_gpt_message(String user_text, String weatherInfo) {
        String request =  "Ты - исскуственный интеллект, призванный для помощи в выборе одежды, вещей, " +
                        "планировании дня, основываясь на погоде. Твоя задача - ответить на поставленный " +
                        "вопрос от клиента : " + user_text + ". Длина ответа: не более 150 слов\n\n" +
                        "Погодные условия в городе: " + weatherInfo;

        OpenAIApiClient client = new OpenAIApiClient();
        client.getChatResponse(
            request,
            new OpenAIApiClient.Callback() {
                @Override
                public void onSuccess(OpenAIResponse response) {
                    String assistantReply = response.getAssistantReply();
                    if (assistantReply != null) {
                        Log.e("[Assistant Log]", "Ответ на запрос: " + assistantReply);
                        runOnUiThread(() -> {
                            View gpt_message = inflater.inflate(R.layout.gpt_message, container, false);
                            ((TextView)gpt_message.findViewById(R.id.message_text)).setText(assistantReply);
                            container.addView(gpt_message);
                            ((ScrollView)findViewById(R.id.scroller)).smoothScrollTo(0, container.getBottom());
                        });

                    } else {
                        Log.e("[Assistant Log]", "Нет ответа от сервера");
                        runOnUiThread(() -> {
                            View gpt_message = inflater.inflate(R.layout.gpt_message, container, false);
                            ((TextView)gpt_message.findViewById(R.id.message_text)).setText(
                                    "Ошибка соединения с сервером! Повторите запрос"
                            );
                            container.addView(gpt_message);
                            ((ScrollView)findViewById(R.id.scroller)).smoothScrollTo(0, container.getBottom());
                        });
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("[Assistant Log]", "Нет ответа от сервера");
                    runOnUiThread(() -> {
                        View gpt_message = inflater.inflate(R.layout.gpt_message, container, false);
                        ((TextView)gpt_message.findViewById(R.id.message_text)).setText(
                                "Ошибка соединения с сервером! Повторите запрос"
                        );
                        container.addView(gpt_message);
                        ((ScrollView)findViewById(R.id.scroller)).smoothScrollTo(0, container.getBottom());
                    });
                }
            }
        );
    }

    public void getWeather(String city) {
        WeatherApiClient weatherClient = new WeatherApiClient();
        WeatherApiClient client = new WeatherApiClient();

        client.getWeatherData(city, new WeatherApiClient.WeatherCallback() {
            @Override
            public void onSuccess(WeatherApiClient.WeatherData data) {
                AssistantActivity.this.weatherInfo = String.format(
                        "Температура: %.1f°C\n" +
                                "Влажность: %d%%\n" +
                                "Скорость ветра: %.1f м/с\n" +
                                "Направление ветра: %.1f °\n" +
                                "Покрытие дождем: %.1f мм\n" +
                                "Погода: %s\n" +
                                "Описание: %s",
                        data.getTemperature(),
                        data.getHumidity(),
                        data.getWindSpeed(),
                        data.getWindDeg(),
                        data.getRainVolume(),
                        data.getWeatherMain(),
                        data.getWeatherDescription()
                );
            }

            @Override
            public void onFailure(Exception e) {}
        });
    }
}