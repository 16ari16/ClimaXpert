package com.example.climaxpert;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.CircularArray;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.climaxpert.AiApi.OpenAIApiClient;
import com.example.climaxpert.AiApi.OpenAIResponse;
import com.example.climaxpert.NetworkUtils.NetworkUtils;
import com.example.climaxpert.WeatherApi.WeatherApiClient;
import java.util.Date;

public class SplashActivity extends AppCompatActivity {
    private SharedPreferences sh;
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        ((TextView)findViewById(R.id.load_status)).setText("Загрузка приложения...");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ((TextView)findViewById(R.id.load_status)).setText("Обработка...");
        binding_swipes();

        this.sh = getApplicationContext().getSharedPreferences(
                "Weather", Context.MODE_PRIVATE
        );


        this.city = sh.getString("city", "Н.о.");
        if (!NetworkUtils.checkInternetConnectionWithToast(this)) {
            return;
        }

        if (city == "Н.о.") {
            Intent intent = new Intent(this, SearchCityActivity.class);
            startActivity(intent);
            overridePendingTransition(
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_top
            );

            return;
        } else {
            getWeather(city);
        }
    }

    public void getWeather(String city) {
        ((TextView)findViewById(R.id.load_status)).setText("Получение погоды...");
        WeatherApiClient weatherClient = new WeatherApiClient();

        WeatherApiClient client = new WeatherApiClient();

        client.getWeatherData(city, new WeatherApiClient.WeatherCallback() {
            @Override
            public void onSuccess(WeatherApiClient.WeatherData data) {
                String weatherInfo = String.format(
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

                SharedPreferences.Editor editor = sh.edit();

                editor.putString("Temperature", String.valueOf(data.getTemperature()));
                editor.putString("Humidity", String.valueOf(data.getHumidity()));
                editor.putString("WindSpeed", String.valueOf(data.getWindSpeed()));
                editor.putString("WindDeg", String.valueOf(data.getWindDeg()));
                editor.putString("Rain", String.valueOf(data.getRainVolume()));
                editor.putString("WeatherMain", data.getWeatherMain());
                editor.putString("WeatherDescription", data.getWeatherDescription());

                editor.commit();

                Log.e("[Splash Log]", weatherInfo);
                getAIAnswer(
                        "У меня есть данные о погоде в " + city + ". " +
                        "Посоветуй, как мне лучше одеться, основываясь на следующих данных: " +
                        weatherInfo + "\n\n\n" +
                        "В ответе не используй никаких специальных символов, кроме . или ,\n\n" +
                        "Не нужно добавлять никакое форматирование текста. Ответ должен быть полностью на русском." +
                        "Свой ответ начни со слов:" +
                        "Жителям города *название города на русском языке* сегодня рекомендую надеть...."
//                        "У меня есть данные о погоде в городе " + city + ". " +
//                                "Используя эти данные, сформируй ответ, в следующем формате: \n" +
//                                "Сегодня я рекомендую надеть: [" +
//                                "нужно ли надевать головной убор и если да, то какой , " +
//                                "что надеть в качестве верхней одежды, " +
//                                "что надеть в качестве нижней одежды," +
//                                "какую обувь стоит надеть]\n" +
//                                "[Зонт пригодится/Зонт не нужен]\n" +
//                                "Комфортное время нахождения на улице: [Сколько времени можно провести на улице," +
//                                "в указанной тобой одежде, не замерзнув и не перегревшись]\n" +
//                                "Не добавляй в ответ ничего лишнего. Пиши только ту информацию, которая" +
//                                " указана мною. В ответе не должно быть не русских букв," +
//                                "слов 'Понятно', 'Ясно', 'Ладно' и других междометий \n\n" +
//                                "Данные о погоде: " + weatherInfo
                );
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("WEATHER_ERROR", "Ошибка запроса", e);
            }
        });
    }

    public void getAIAnswer(String request) {
        ((TextView)findViewById(R.id.load_status)).setText("Спрашиваем нейросеть...");
        OpenAIApiClient client = new OpenAIApiClient();
        client.getChatResponse(
            request,
            new OpenAIApiClient.Callback() {
                @Override
                public void onSuccess(OpenAIResponse response) {
                    String assistantReply = response.getAssistantReply();
                    if (assistantReply != null) {
                        Log.e("[Splash Log]", "Ответ на запрос: " + assistantReply);

                        SharedPreferences.Editor editor = sh.edit();
                        editor.putString("HomeAssistant", assistantReply);
                        editor.putLong("HomeAssistantTime", new Date().getTime());
                        editor.commit();

                    } else {
                        Log.e("[Splash Log]", "Нет ответа от сервера");
                        if (sh.getString("HomeAssistant", "Н.о.") == "Н.о.") {
                            SharedPreferences.Editor editor = sh.edit();
                            editor.putString("HomeAssistant", "Не удалось получить данные от сервера!");
                            editor.putString("HomeAssistantTime", new Date().toString());
                            editor.commit();
                        }
                    }

                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                }

                @Override
                public void onFailure(Exception e) {
                    if (sh.getString("HomeAssistant", "Н.о.") == "Н.о.") {
                        SharedPreferences.Editor editor = sh.edit();
                        editor.putString("HomeAssistant", "Ошибка запроса!");
                        editor.putString("HomeAssistantTime", new Date().toString());
                        editor.commit();
                    }

                    Log.e("[Splash Log]", "Ошибка запроса", e);
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                }
            }
        );
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