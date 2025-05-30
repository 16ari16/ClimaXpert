package com.example.climaxpert.WeatherApi;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    private Main main;
    private Wind wind;
    private Rain rain;
    private Weather[] weather;
    private String name;

    public static class Main {
        private double temp;
        private int humidity;

        public double getTemp() { return temp; }
        public int getHumidity() { return humidity; }
    }

    public static class Wind {
        private double speed;
        private double deg;

        public double getSpeed() { return speed; }
        public double getDeg() { return deg; }
    }

    public static class Rain {
        @SerializedName("1h")
        private Double volumeLastHour;

        public Double getVolumeLastHour() { return volumeLastHour; }
    }

    public static class Weather {
        private String main;
        private String description;

        public String getMain() { return main; }
        public String getDescription() { return description; }
    }

    // Геттеры
    public Main getMain() { return main; }
    public Wind getWind() { return wind; }
    public Rain getRain() { return rain; }
    public Weather[] getWeather() { return weather; }
    public String getName() { return name; }
}