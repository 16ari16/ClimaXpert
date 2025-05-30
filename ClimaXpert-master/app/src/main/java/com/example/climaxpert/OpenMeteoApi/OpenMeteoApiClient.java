package com.example.climaxpert.OpenMeteoApi;
import okhttp3.*;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OpenMeteoApiClient {
    private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast";
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public interface WeatherCallback {
        void onSuccess(List<HourlyWeather> hourlyData);
        void onFailure(Exception e);
    }

    public void getHourlyWeather(double latitude, double longitude, WeatherCallback callback) {
        HttpUrl url = HttpUrl.parse(BASE_URL).newBuilder()
                .addQueryParameter("latitude", String.valueOf(latitude))
                .addQueryParameter("longitude", String.valueOf(longitude))
                .addQueryParameter("hourly", "temperature_2m,rain,weather_code")
                .addQueryParameter("timezone", "Europe/Moscow")
                .addQueryParameter("past_days", "1")
                .addQueryParameter("forecast_days", "2")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure(new IOException("Ошибка API: " + response.code()));
                    return;
                }

                String responseData = response.body().string();
                OpenMeteoResponse apiResponse = gson.fromJson(responseData, OpenMeteoResponse.class);

                List<HourlyWeather> hourlyData = new ArrayList<>();
                List<String> times = apiResponse.getHourly().getTimes();
                List<Double> temps = apiResponse.getHourly().getTemperatures();
                List<Double> rains = apiResponse.getHourly().getRain();
                List<Integer> weather_codes = apiResponse.getHourly().getWeatherCode();

                for (int i = 0; i < times.size(); i++) {
                    hourlyData.add(new HourlyWeather(
                            times.get(i),
                            temps.get(i),
                            rains.get(i),
                            weather_codes.get(i)
                    ));
                }

                callback.onSuccess(hourlyData);
            }
        });
    }

    public static class HourlyWeather {
        private final String time;
        private final double temperature;
        private final double rain;
        private final int weather_code;

        public HourlyWeather(String time, double temperature, double rain, int weather_code) {
            this.time = time;
            this.temperature = temperature;
            this.rain = rain;
            this.weather_code = weather_code;
        }

        public String getTime() { return time; }
        public double getTemperature() { return temperature; }
        public double getRain() { return rain; }
        public int getWeatherCode() { return weather_code; }
    }
}