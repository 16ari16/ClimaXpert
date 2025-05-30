package com.example.climaxpert.WeatherApi;
import okhttp3.*;
import com.google.gson.Gson;
import java.io.IOException;


import okhttp3.*;
import com.google.gson.Gson;
import java.io.IOException;

public class WeatherApiClient {
    private static final String API_KEY = "63085ab9b973a44fa9e3768349bcfb44";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public interface WeatherCallback {
        void onSuccess(WeatherData data);
        void onFailure(Exception e);
    }

    public void getWeatherData(String cityName, WeatherCallback callback) {
        HttpUrl url = HttpUrl.parse(BASE_URL).newBuilder()
                .addQueryParameter("q", cityName)
                .addQueryParameter("appid", API_KEY)
                .addQueryParameter("units", "metric")  // для температуры в °C
                .addQueryParameter("lang", "ru")       // для русских описаний
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
                WeatherResponse apiResponse = gson.fromJson(responseData, WeatherResponse.class);

                // Преобразуем ответ API в удобный формат
                WeatherData weatherData = new WeatherData(
                        apiResponse.getMain().getTemp(),
                        apiResponse.getMain().getHumidity(),
                        apiResponse.getWind().getSpeed(),
                        apiResponse.getWind().getDeg(),
                        apiResponse.getRain() != null ? apiResponse.getRain().getVolumeLastHour() : 0.0,
                        apiResponse.getWeather()[0].getMain(),
                        apiResponse.getWeather()[0].getDescription(),
                        apiResponse.getName()
                );

                callback.onSuccess(weatherData);
            }
        });
    }

    public static class WeatherData {
        private final double temperature;
        private final int humidity;
        private final double windSpeed;
        private final double windDeg;
        private final double rainVolume;
        private final String weatherMain;
        private final String weatherDescription;
        private final String locationName;

        public WeatherData(double temperature, int humidity, double windSpeed, double windDeg,
                           double rainVolume, String weatherMain,
                           String weatherDescription, String locationName) {
            this.temperature = temperature;
            this.humidity = humidity;
            this.windSpeed = windSpeed;
            this.windDeg = windDeg;
            this.rainVolume = rainVolume;
            this.weatherMain = weatherMain;
            this.weatherDescription = weatherDescription;
            this.locationName = locationName;
        }

        // Геттеры
        public double getTemperature() { return temperature; }
        public int getHumidity() { return humidity; }
        public double getWindSpeed() { return windSpeed; }
        public double getWindDeg() { return windDeg; }
        public double getRainVolume() { return rainVolume; }
        public String getWeatherMain() { return weatherMain; }
        public String getWeatherDescription() { return weatherDescription; }
        public String getLocationName() { return locationName; }
    }
}