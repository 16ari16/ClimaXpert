package com.example.climaxpert.OpenMeteoApi;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenMeteoWeatherCodes {
    public static List<Map<String, String>> getWeatherCodes() {
        List<Map<String, String>> weatherCodes = new ArrayList<>();

        // Добавляем все коды погоды
        addWeatherCode(weatherCodes, "0", "Ясное небо", "clear_sky");
        addWeatherCode(weatherCodes, "1", "Преимущественно Ясно", "mainly_clear");
        addWeatherCode(weatherCodes, "2", "Переменная облачность", "partly_cloudy");
        addWeatherCode(weatherCodes, "3", "Пасмурно", "overcast");
        addWeatherCode(weatherCodes, "45", "Туман", "fog");
        addWeatherCode(weatherCodes, "48", "Изморозь", "depositing_rime_fog");
        addWeatherCode(weatherCodes, "51", "Морось Слабая", "drizzle_Light");
        addWeatherCode(weatherCodes, "53", "Морось Умеренная", "drizzle_moderate");
        addWeatherCode(weatherCodes, "55", "Морось Сильная", "drizzle_dense");
        addWeatherCode(weatherCodes, "56", "Ледяная морось Слабая", "freezing_drizzle_light");
        addWeatherCode(weatherCodes, "57", "Ледяная морось Густая", "freezing_drizzle_dense");
        addWeatherCode(weatherCodes, "61", "Дождь Слабый", "rain_slight");
        addWeatherCode(weatherCodes, "63", "Дождь Умеренный", "rain_moderate");
        addWeatherCode(weatherCodes, "65", "Дождь Сильный", "rain_heavy");
        addWeatherCode(weatherCodes, "66", "Ледяной дождь Слабый", "freezing_rain_light");
        addWeatherCode(weatherCodes, "67", "Ледяной дождь Сильный", "freezing_rain_heavy");
        addWeatherCode(weatherCodes, "71", "Снегопад Слабый", "snow_fall_slight");
        addWeatherCode(weatherCodes, "73", "Снегопад Умеренный", "snow_fall_moderate");
        addWeatherCode(weatherCodes, "75", "Снегопад Сильный", "snow_fall_heavy");
        addWeatherCode(weatherCodes, "77", "Снежный лед", "snow_grains");
        addWeatherCode(weatherCodes, "80", "Ливневый дождь Слабый", "rain_shower_slight");
        addWeatherCode(weatherCodes, "81", "Ливневый дождь Умеренный", "rain_shower_moderate");
        addWeatherCode(weatherCodes, "82", "Ливневый дождь Сильный", "rain_shower_violent");
        addWeatherCode(weatherCodes, "85", "Ливневый снег Слабый", "snow_showers_slight");
        addWeatherCode(weatherCodes, "86", "Ливневый снег Сильный", "snow_showers_heavy");
        addWeatherCode(weatherCodes, "95", "Гроза Слабая", "thunderstorm");
        addWeatherCode(weatherCodes, "*", "Гроза", "thundersotrm");
        addWeatherCode(weatherCodes, "96", "Гроза", "thunderstorm");
        addWeatherCode(weatherCodes, "99", "Гроза", "thunderstorm");

        return weatherCodes;
    }

    private static void addWeatherCode(List<Map<String, String>> list,
                                       String code, String text, String image) {
        Map<String, String> entry = new HashMap<>();
        entry.put("code", code);
        entry.put("text", text);
        entry.put("image", image);
        list.add(entry);
    }

    public static String getCodeImage(int weather_code) {
        String wc = String.valueOf(weather_code);

        List<Map<String, String>> weatherCodes = OpenMeteoWeatherCodes.getWeatherCodes();
        for (Map<String, String> el : weatherCodes) {
            if (el.get("code").equals(wc)) {
                return el.get("image");
            }
        }
        return "clear_sky";
    }
}