package com.example.climaxpert.DatabaseClient;

public class City {
    private int id;
    private String title;
    private float latitude;
    private float longitude;
    private String country;
    private String ISO2;

    // Конструктор
    public City(int id, String title, float latitude, float longitude, String country, String ISO2) {
        this.id = id;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.ISO2 = ISO2;
    }

    // Геттеры
    public int getId() { return id; }
    public String getTitle() { return title; }
    public float getLatitude() { return latitude; }
    public float getLongitude() { return longitude; }
    public String getCountry() { return country; }
    public String getISO2() { return ISO2; }

    // Для отображения в AutoCompleteTextView
    @Override
    public String toString() {
        return title + ", " + country;
    }
}