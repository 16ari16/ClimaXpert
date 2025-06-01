package com.example.climaxpert;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.climaxpert.OpenMeteoApi.DateConverter;
import com.example.climaxpert.OpenMeteoApi.OpenMeteoApiClient;
import com.example.climaxpert.OpenMeteoApi.OpenMeteoWeatherCodes;

import java.util.Date;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    LayoutInflater weatherInflater;
    LinearLayout hourlyContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);

        weatherInflater = getLayoutInflater();
        hourlyContainer = findViewById(R.id.hourly_weather_container);

        SharedPreferences sh = getSharedPreferences(
                "Weather", MODE_PRIVATE
        );
        String city = sh.getString("city", "Н.о.");
        String country = sh.getString("country", "Н.о.");
        ((TextView)findViewById(R.id.detail_full_adress)).setText(city + ", " + country);

        binding_clicks();
        this.fillWeatherInfo();
        this.fillHourlyInfo();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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

    private void fillWeatherInfo() {
        SharedPreferences sh = getSharedPreferences(
                "Weather", Context.MODE_PRIVATE
        );
        String temp = sh.getString("Temperature", "Н.о.");
        String wind = sh.getString("WindSpeed", "Н.о.");
        String deg = sh.getString("WindDeg", "Н.о.");
        String humidity = sh.getString("Humidity", "Н.о.");
        String weather_desc = sh.getString("WeatherDescription", "Н.о.");

        ((TextView) findViewById(R.id.detailTempTextView)).setText(temp + "°C");
        ((TextView) findViewById(R.id.detailHumidityTextView)).setText(humidity + "%");
        ((TextView) findViewById(R.id.detailWindTextView)).setText(wind + "м/с");
        ((TextView) findViewById(R.id.detailDegTextView)).setText(deg + "°");
        ((TextView) findViewById(R.id.detail_air_state)).setText(
                weather_desc.substring(0, 1)
                        .toUpperCase()
                        .concat(weather_desc.substring(1))
        );

        ImageView wind_deg_image = findViewById(R.id.detailDegImage);
        double deg_db = Double.parseDouble(deg);
        String image_name = "down";

        if (deg_db > 337.5 || deg_db <= 22.5) image_name = "up";
        else if (deg_db > 22.5 && deg_db <= 67.5) image_name = "up_right";
        else if (deg_db > 67.5 && deg_db <= 112.5) image_name = "right";
        else if (deg_db > 112.5 && deg_db <= 157.5) image_name = "down_right";
        else if (deg_db > 157.5 && deg_db <= 202.5) image_name = "down";
        else if (deg_db > 202.5 && deg_db <= 247.5) image_name = "down_left";
        else if (deg_db > 247.5 && deg_db <= 292.5) image_name = "left";
        else if (deg_db > 292.5 && deg_db <= 337.5) image_name = "up_left";
        image_name += "_blue";
        int imageID = this
                .getResources()
                .getIdentifier(
                        image_name,
                        "drawable",
                        getApplicationContext().getPackageName()
                );
        wind_deg_image.setImageResource(imageID);

    }

    private void binding_clicks() {
        findViewById(R.id.back_arrow).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DetailActivity.this, HomeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                }
        );
    }
}