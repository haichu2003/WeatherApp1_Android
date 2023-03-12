package com.example.weathernow;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public class MenuActivity extends AppCompatActivity implements onCityClickListener {

    private String location;

    //known location

    //TODO: function to add new cities and check if it is valid
    private final String [] city_name = {"Hanoi", "Rio De Janeiro", "Tampere", "Helsinki", "Oulu", "Chicago"};

    private final Set<String> filter = new HashSet<>();

    @Override
    public void onTextClick(String data) {
        //TODO
        location = data;
        Intent openMain = new Intent(this, MainActivity.class);
        openMain.putExtra("CITY_NAME", location);
        System.out.println("Location in menu: " + location);
        startActivity(openMain);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Intent intent = getIntent();
        location =  intent.getStringExtra("CITY_NAME");
        location = location.replaceAll(Pattern.quote("+"), " ");
        Objects.requireNonNull(getSupportActionBar()).hide();

        //add cities names to a set then convert the set to a list, implemented for future purpose (search for new cities and save to known location list)

        filter.add(location);
        filter.addAll(Arrays.asList(city_name));
        List<String> items = new LinkedList<>(filter);

        RecyclerView recyclerView = findViewById(R.id.knownLocation);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        weatherForecastAdapter adapter = new weatherForecastAdapter(items, this);
        recyclerView.setAdapter(adapter);
    }

    //back button, top right corner
    public void openMainActivity(View view) {
        Intent openMain = new Intent(this, MainActivity.class);
        openMain.putExtra("CITY_NAME", location);
        startActivity(openMain);
    }

    //open API provider webpage (openweathermap.org)
    public void openWeatherSource(View view) {
        String url = "https://openweathermap.org/api/one-call-3#current";
        Uri uri =Uri.parse(url);
        Intent openWebPage = new Intent(Intent.ACTION_VIEW, uri);
        try {startActivity(openWebPage);
        } catch (ActivityNotFoundException e) {
            //TODO
        }
    }
}