package com.example.climaxpert.OpenMeteoApi;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OpenMeteoDailyResponse {
    @SerializedName("daily")
    private DailyData daily;
    @SerializedName("daily_units")
    private DailyUnits units;

    public static class DailyData {
        private List<String> time;
        @SerializedName("weather_code")
        private List<Integer> weatherCodes;
        @SerializedName("temperature_2m_max")
        private List<Double> tempMax;
        @SerializedName("temperature_2m_min")
        private List<Double> tempMin;

        public List<String> getTime() { return time; }
        public List<Integer> getWeatherCodes() { return weatherCodes; }
        public List<Double> getTempMax() { return tempMax; }
        public List<Double> getTempMin() { return tempMin; }
    }

    public static class DailyUnits {
        @SerializedName("temperature_2m_max")
        private String tempMaxUnit;
        @SerializedName("temperature_2m_min")
        private String tempMinUnit;

        public String getTempMaxUnit() { return tempMaxUnit; }
        public String getTempMinUnit() { return tempMinUnit; }
    }

    public DailyData getDaily() { return daily; }
    public DailyUnits getUnits() { return units; }
}