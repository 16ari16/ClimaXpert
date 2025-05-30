package com.example.climaxpert.OpenMeteoApi;
import okhttp3.*;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OpenMeteoApiDailyClient {
    private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast";
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public interface WeatherCallback {
        void onSuccess(List<DailyWeather> dailyWeather);
        void onFailure(Exception e);
    }

    public void getDailyWeather(double latitude, double longitude, WeatherCallback callback) {
        HttpUrl url = HttpUrl.parse(BASE_URL).newBuilder()
                .addQueryParameter("latitude", String.valueOf(latitude))
                .addQueryParameter("longitude", String.valueOf(longitude))
                .addQueryParameter("daily", "weather_code,temperature_2m_max,temperature_2m_min")
                .addQueryParameter("timezone", "Europe/Moscow")
                .addQueryParameter("past_days", "1")
                .addQueryParameter("forecast_days", "7")
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
                OpenMeteoDailyResponse apiResponse = gson.fromJson(responseData, OpenMeteoDailyResponse.class);

                List<DailyWeather> dailyWeatherList = new ArrayList<>();
                List<String> dates = apiResponse.getDaily().getTime();
                List<Integer> codes = apiResponse.getDaily().getWeatherCodes();
                List<Double> maxTemps = apiResponse.getDaily().getTempMax();
                List<Double> minTemps = apiResponse.getDaily().getTempMin();

                for (int i = 0; i < dates.size(); i++) {
                    dailyWeatherList.add(new DailyWeather(
                            dates.get(i),
                            codes.get(i),
                            maxTemps.get(i),
                            minTemps.get(i)
                    ));
                }

                callback.onSuccess(dailyWeatherList);
            }
        });
    }

    public static class DailyWeather {
        private final String date;
        private final int weatherCode;
        private final double maxTemp;
        private final double minTemp;

        public DailyWeather(String date, int weatherCode, double maxTemp, double minTemp) {
            this.date = date;
            this.weatherCode = weatherCode;
            this.maxTemp = maxTemp;
            this.minTemp = minTemp;
        }

        // Геттеры
        public String getDate() { return date; }
        public int getWeatherCode() { return weatherCode; }
        public double getMaxTemp() { return maxTemp; }
        public double getMinTemp() { return minTemp; }
    }
}