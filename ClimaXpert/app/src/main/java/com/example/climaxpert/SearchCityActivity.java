package com.example.climaxpert;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.climaxpert.DatabaseClient.City;
import com.example.climaxpert.DatabaseClient.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class SearchCityActivity extends AppCompatActivity {
    LayoutInflater cityInflater;
    LinearLayout citiesContainer;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    List<City> cities;

    private void addCity(City city) {
        String title = city.getTitle();
        View city_card = cityInflater.inflate(R.layout.city_element, citiesContainer, false);
        ((TextView)city_card.findViewById(R.id.city_title)).setText(title);

        city_card.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sh = getSharedPreferences(
                            "Weather", Context.MODE_PRIVATE
                    );
                    SharedPreferences.Editor editor = sh.edit();
                    editor.putFloat("longitude", city.getLongitude());
                    editor.putFloat("latitude", city.getLatitude());
                    editor.putString("city", city.getTitle());
                    editor.putString("country", city.getCountry());
                    editor.commit();

                    Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                    startActivity(intent);
                    overridePendingTransition(
                            R.anim.slide_in_top,
                            R.anim.slide_out_bottom
                    );
                }
            }
        );

        citiesContainer.addView(city_card);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_city);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cityInflater = getLayoutInflater();
        citiesContainer = findViewById(R.id.cities_container);
        databaseHelper = new DatabaseHelper(getApplicationContext());
        databaseHelper.create_db();

        ((EditText)findViewById(R.id.city_input)).addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        cityQueryHandler();
                    }
                }
        );

        findViewById(R.id.home_menu_btn).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SearchCityActivity.this, HomeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                        );
                    }
                }
        );
    }

    public void cityQueryHandler() {
        EditText query_input = findViewById(R.id.city_input);
        String query = query_input.getText().toString();

        Log.e("[City Log]", query);
        Log.e("[City Log]", query_input.toString());

        try {
            db = databaseHelper.open();
            userCursor = db.rawQuery(
                    "SELECT id, title, latitude, longitude, country, ISO2 " +
                        "FROM City " +
                        "WHERE title " +
                        "LIKE '" + query + "%' " +
                        "LIMIT 20;",
                    null
            );
            userCursor.moveToFirst();

            citiesContainer.removeAllViews();
            while (userCursor.moveToNext()) {
                City city = new City(
                        userCursor.getInt(0),
                        userCursor.getString(1),
                        userCursor.getFloat(2),
                        userCursor.getFloat(3),
                        userCursor.getString(4),
                        userCursor.getString(5)
                );
                addCity(city);
            }
        } catch (Exception e) {
            Log.e("[City Log]", e.getMessage());
        }
    }
}
