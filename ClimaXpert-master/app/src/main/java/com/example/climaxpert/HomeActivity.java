package com.example.climaxpert;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.climaxpert.AiApi.OpenAIApiClient;
import com.example.climaxpert.AiApi.OpenAIResponse;
import com.example.climaxpert.OpenMeteoApi.DateConverter;
import com.example.climaxpert.OpenMeteoApi.OpenMeteoApiClient;
import com.example.climaxpert.OpenMeteoApi.OpenMeteoApiDailyClient;
import com.example.climaxpert.OpenMeteoApi.OpenMeteoWeatherCodes;
import com.example.climaxpert.WeatherApi.WeatherApiClient;
import com.example.climaxpert.WeatherApi.WeatherResponse;
import com.google.android.material.shadow.ShadowRenderer;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.Inflater;

public class HomeActivity extends AppCompatActivity {
    LayoutInflater weatherInflater;
    LinearLayout hourlyContainer;
    LinearLayout dailyContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sh = getSharedPreferences(
                "Weather", MODE_PRIVATE
        );
        String city = sh.getString("city", "Н.о.");
        String country = sh.getString("country", "Н.о.");
        ((TextView)findViewById(R.id.home_full_adress)).setText(city + ", " + country);
        ((TextView)findViewById(R.id.textView5)).setText(city + ", " + country);

        this.weatherInflater = getLayoutInflater();
        this.hourlyContainer = findViewById(R.id.hourly_weather);
        this.dailyContainer = findViewById(R.id.daily_weather);

        this.hourlyContainer.removeAllViews();
        this.dailyContainer.removeAllViews();

        binding_swipes();
        binding_clicks();

        fillWeatherInfo();
        fillHourlyInfo();
        fillDailyInfo();
    }

    private void add_daily_weather(String date, String temp, String image) {
        View weather_card = weatherInflater.inflate(R.layout.daily_weather_element, dailyContainer, false);

        ImageView wImage = weather_card.findViewById(R.id.daily_image);
        TextView wTemp = weather_card.findViewById(R.id.daily_temperature);
        TextView wDate = weather_card.findViewById(R.id.daily_date);

        wTemp.setText(temp);
        wDate.setText(date);

        int imageID = this
                .getResources()
                .getIdentifier(
                        image,
                        "drawable",
                        getApplicationContext().getPackageName()
                );
        wImage.setImageResource(imageID);
        Log.e("[Home Log]", image);
        this.dailyContainer.addView(weather_card);
    }

    private void add_hourly_weather(String time, String temp, String image) {
        View weather_card = weatherInflater.inflate(R.layout.hourly_weather_element, hourlyContainer, false);

        ImageView wImage = weather_card.findViewById(R.id.hourly_image);
        TextView wTemp = weather_card.findViewById(R.id.hourly_temperature);
        TextView wTime = weather_card.findViewById(R.id.hourly_time);

        wTemp.setText(temp);
        wTime.setText(time);

        int imageID = this
                .getResources()
                .getIdentifier(
                        image,
                        "drawable",
                        getApplicationContext().getPackageName()
                );
        wImage.setImageResource(imageID);

        this.hourlyContainer.addView(weather_card);
    }

    private void fillWeatherInfo() {
        SharedPreferences sh = getSharedPreferences(
                "Weather", Context.MODE_PRIVATE
        );
        String homeAssistant = sh.getString("HomeAssistant", "Не удалось получить данные");
        String temp = sh.getString("Temperature", "Н.о.");
        String rainPercentage = sh.getString("Rain", "Н.о.");
        String wind = sh.getString("WindSpeed", "Н.о.");
        String deg = sh.getString("WindDeg", "Н.о.");
        String humidity = sh.getString("Humidity", "Н.о.");
        String weather_desc = sh.getString("WeatherDescription", "Н.о.");
        String weather_main = sh.getString("WeatherMain", "Н.о.");
        long assistant_time = sh.getLong("HomeAssistantTime", 0);

        ((TextView) findViewById(R.id.home_assistant_text)).setText(homeAssistant);
        ((TextView) findViewById(R.id.tempTextView)).setText(temp + "°C");
        ((TextView) findViewById(R.id.humidityTextView)).setText(humidity + "%");
        ((TextView) findViewById(R.id.windTextView)).setText(wind + "м/с");
        ((TextView) findViewById(R.id.degTextView)).setText(deg + "°");
        ((TextView) findViewById(R.id.rain_percent)).setText("Покрытие дождём: " + rainPercentage + "%");
        ((TextView) findViewById(R.id.air_state)).setText(
                weather_desc.substring(0, 1)
                        .toUpperCase()
                        .concat(weather_desc.substring(1))
        );

        java.util.Date time = new java.util.Date(new Date().getTime() - assistant_time);
        String left_time = "";

        Log.e("[Home Log]", time.toString());
        Log.e("[Home Log]", String.valueOf(new Date().getTime()));
        Log.e("[Home Log]", String.valueOf(assistant_time));
        if (time.getHours() != 0) left_time += time.getHours() + "час. ";
        if (time.getMinutes() != 0) left_time += time.getMinutes() + "мин. ";
        if (time.getSeconds() != 0) left_time += time.getSeconds() + "сек. ";
        left_time += "назад.";

        if (time.getHours() == 0 && time.getMinutes() == 0 && time.getSeconds() == 0) {
            left_time = "Только что";
        }

        ((TextView) findViewById(R.id.home_assistant_time)).setText(left_time);

        ImageView wind_deg_image = findViewById(R.id.degImage);
        double deg_db = Double.parseDouble(deg);
        String image_name = "down";

        if (deg_db > 337.5 && deg_db <= 22.5) image_name = "up";
        if (deg_db > 22.5 && deg_db <= 67.5) image_name = "up_right";
        if (deg_db > 67.5 && deg_db <= 112.5) image_name = "right";
        if (deg_db > 112.5 && deg_db <= 157.5) image_name = "down_right";
        if (deg_db > 157.5 && deg_db <= 202.5) image_name = "down";
        if (deg_db > 202.5 && deg_db <= 247.5) image_name = "down_left";
        if (deg_db > 247.5 && deg_db <= 292.5) image_name = "left";
        if (deg_db > 292.5 && deg_db <= 337.5) image_name = "up_left";
        int imageID = this
                .getResources()
                .getIdentifier(
                        image_name,
                        "drawable",
                        getApplicationContext().getPackageName()
                );
        wind_deg_image.setImageResource(imageID);

    }

    private void fillHourlyInfo() {
        OpenMeteoApiClient client = new OpenMeteoApiClient();

        SharedPreferences sh = getSharedPreferences(
                "Weather", Context.MODE_PRIVATE
        );
        Float latitude = sh.getFloat("latitude", 55.745035f);
        Float longitude = sh.getFloat("longitude", 49.286137f);

        Log.e("[Home Log]", "longitude: " + longitude);
        Log.e("[Home Log]", "latitude: " + latitude);

        client.getHourlyWeather(latitude, longitude,
            new OpenMeteoApiClient.WeatherCallback() {
                @Override
                public void onSuccess(List<OpenMeteoApiClient.HourlyWeather> hourlyData) {
                    int index = new Date().getHours() + 24;
                    List<OpenMeteoApiClient.HourlyWeather> weathers =
                            hourlyData.subList(index - 5, index + 8);

                    for (int i = 0; i < weathers.size(); i++) {
                        OpenMeteoApiClient.HourlyWeather w = weathers.get(i);

                        String time = String.format("%02d:%02d",
                                DateConverter.convertStringToDate(w.getTime()).getHours(),
                                DateConverter.convertStringToDate(w.getTime()).getMinutes()
                        );
                        String temp = w.getTemperature() + "°C";
                        String rain = w.getRain() + "%";
                        String image = OpenMeteoWeatherCodes.getCodeImage(w.getWeatherCode());

                        runOnUiThread(() -> {
                            add_hourly_weather(time, temp, image);
                        });
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    for (int i = 0; i < 5; i++) {
                        add_hourly_weather("Н.о.", "Н.о.", "Н.о.");
                    }
                }
            }
        );
    }

    private void fillDailyInfo() {
        OpenMeteoApiDailyClient client = new OpenMeteoApiDailyClient();
        client.getDailyWeather(55.745035, 49.286317,
                new OpenMeteoApiDailyClient.WeatherCallback() {
                    @Override
                    public void onSuccess(List<OpenMeteoApiDailyClient.DailyWeather> dailyWeather) {
                        for (int i = 0; i < dailyWeather.size(); i++) {
                            OpenMeteoApiDailyClient.DailyWeather w = dailyWeather.get(i);

                            String sDate = w.getDate();
                            String temp = String.format("%.1f°/%.1f°",
                                w.getMaxTemp(),
                                w.getMinTemp()
                            );
                            String image = OpenMeteoWeatherCodes.getCodeImage(w.getWeatherCode());

                            runOnUiThread(() -> {
                                add_daily_weather(sDate, temp, image);
                            });
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        for (int i = 0; i < 7; i++) {
                            add_daily_weather("Н.о.", "Н.о.", "clear_sky");
                        }
                    }
                }
        );
    }

    private void binding_clicks() {
        findViewById(R.id.detail_widget).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        );

        findViewById(R.id.update_btn).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeActivity.this, SplashActivity.class);
                        startActivity(intent);
                        overridePendingTransition(
                                R.anim.slide_in_top,
                                R.anim.slide_out_bottom
                        );
                    }
                }
        );

        findViewById(R.id.home_full_adress).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeActivity.this, SearchCityActivity.class);
                        startActivity(intent);
                        overridePendingTransition(
                                R.anim.slide_in_left,
                                R.anim.slide_out_right
                        );
                    }
                }
        );

        findViewById(R.id.home_searcher_btn).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeActivity.this, SearchCityActivity.class);
                        startActivity(intent);
                        overridePendingTransition(
                                R.anim.slide_in_left,
                                R.anim.slide_out_right
                        );
                    }
                }
        );

        findViewById(R.id.assistant_activity).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeActivity.this, AssistantActivity.class);
                        startActivity(intent);
                        overridePendingTransition(
                                R.anim.slide_in_bottom,
                                R.anim.slide_out_top
                        );
                    }
                }
        );
    }

    private void binding_swipes() {
        findViewById(R.id.main).setOnTouchListener(
                new OnSwipeTouchListener(this) {
                    @Override
                    public void onSwipeTop() {
                        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                    }
                }
        );
    }
}
