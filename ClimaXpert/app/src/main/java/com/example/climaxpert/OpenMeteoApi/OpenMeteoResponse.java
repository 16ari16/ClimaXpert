package com.example.climaxpert.OpenMeteoApi;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OpenMeteoResponse {
    @SerializedName("hourly")
    private HourlyData hourly;

    public static class HourlyData {
        @SerializedName("time")
        private List<String> times;

        @SerializedName("temperature_2m")
        private List<Double> temperatures;

        @SerializedName("rain")
        private List<Double> rain;

        @SerializedName("weather_code")
        private List<Integer> weather_code;

        public List<String> getTimes() { return times; }
        public List<Double> getTemperatures() { return temperatures; }
        public List<Double> getRain() { return rain; }
        public List<Integer> getWeatherCode() { return weather_code; }
    }

    public HourlyData getHourly() { return hourly; }
}