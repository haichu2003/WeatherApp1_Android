package com.example.weathernow;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

private RequestQueue requestQueue;

private String url = "https://api.openweathermap.org/data/2.5/weather?q=Tampere&units=metric&appid=9c3f5e90473eaacb9e34d78a25311fb1";

private Double temperature = 0D;

private String description = "";

//Default location TODO: get user's current location
private String location = "Hanoi";

private String lastLocation = "";

    //remove this key before pull
private final String apiKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();

        Intent intent = getIntent();
        if (intent.getStringExtra("CITY_NAME") != null) location = intent.getStringExtra("CITY_NAME");

        TextView city = findViewById(R.id.city);
        city.setText(location);
        location = location.replaceAll(" ", "+");

        //get device current language

        String lang = Locale.getDefault().getLanguage();

        url = "https://api.openweathermap.org/data/2.5/weather?q=" + location + "&units=metric&appid=" + apiKey +"&lang=" + lang;

        requestQueue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchData();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putDouble("LAST_TEMPERATURE", temperature);
        savedInstanceState.putString("LAST_DESCRIPTION", description);
        savedInstanceState.putString("LAST_LOCATION", location);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        temperature = savedInstanceState.getDouble("LAST_TEMPERATURE", temperature);
        description = savedInstanceState.getString("LAST_DESCRIPTION", description);
        lastLocation = savedInstanceState.getString("LAST_LOCATION");
    }

    protected void onResume() {super.onResume();}

    protected void onPause() {
        super.onPause();
    }

    protected void onStop() {
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    private void fetchData() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            Log.d("WEATHER_NOW", response);
            parseJSONAndUpdateUI(response);
        }, error -> Log.d("WEATHER_NOW", error.toString()));

        requestQueue.add(stringRequest);
    }

    private void parseJSONAndUpdateUI(String response) {
        try {
            JSONObject weatherResponse = new JSONObject(response);

            //get information from JSON

            temperature = weatherResponse.getJSONObject("main").getDouble("temp");
            description = weatherResponse.getJSONArray("weather").getJSONObject(0).getString("description");

            //show information to screen

            TextView temp = findViewById(R.id.temperature);
            temp.setText(String.format("%.1f˚C", temperature));
            temp = findViewById(R.id.description);
            temp.setText(description);

            //get current date and sunrise/ sunset time to change theme according to sunrise/ sunset

            long sunrise = weatherResponse.getJSONObject("sys").getLong("sunrise");
            long sunset = weatherResponse.getJSONObject("sys").getLong("sunset");
            Date date = new Date();
            long currentDate = date.getTime() / 1000;
            int id = Integer.parseInt(weatherResponse.getJSONArray("weather").getJSONObject(0).getString("id"));

            //change UI to current theme
            ImageView imageView = findViewById(R.id.weatherIcon);
            ImageButton menuButton = findViewById(R.id.menuButton);
            ImageButton refreshButton = findViewById(R.id.refreshButton);

            //If date is after sunset and before sunrise
            if (currentDate < sunrise || currentDate > sunset) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                menuButton.setBackgroundResource(R.drawable.menu_button_night);
                refreshButton.setBackgroundResource(R.drawable.refresh_night);
                if (id == 500) imageView.setImageResource(R.drawable.clear_sky_night);
                else if(id >= 200 && id < 300)
                    imageView.setImageResource(R.drawable.thunderstorm_sky_night);
                else if (id >= 300 && id < 400)
                    imageView.setImageResource(R.drawable.drizzle);
                else if (id >= 500 && id < 600)
                    imageView.setImageResource(R.drawable.rain_sky_night);
                else if (id >= 600 && id < 700)
                    imageView.setImageResource(R.drawable.snow_sky_night);
                else if (id == 801)
                    imageView.setImageResource(R.drawable.cloudy_sky_night);
                else if (id >= 802) imageView.setImageResource(R.drawable.overcast_clouds);
                else imageView.setImageResource(R.drawable.clear_sky_night);
            }
            //if date is after sunrise and before sunset
            else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                menuButton.setBackgroundResource(R.drawable.menu_button_day);
                refreshButton.setBackgroundResource(R.drawable.refresh_day);

                //id = 500: clear sky
                if (id == 500) imageView.setImageResource(R.drawable.clear_sky_day);

                //id = 3xx: thunder storm
                else if(id >= 200 && id < 300)
                    imageView.setImageResource(R.drawable.thunderstorm_sky_day);

                //id = 4xx: drizzle (small rain)
                else if (id >= 300 && id < 400)
                    imageView.setImageResource(R.drawable.drizzle);

                //id = 5xx: rain
                else if (id >= 500 && id < 600)
                    imageView.setImageResource(R.drawable.rain_sky_day);

                //id = 6xx: snow
                else if (id >= 600 && id < 700)
                    imageView.setImageResource(R.drawable.snow_sky_day);

                //id = 801: cloudy
                else if (id == 801)
                    imageView.setImageResource(R.drawable.cloudy_sky_day);

                //id = 80x, x >= 2: more cloudy
                else if (id >= 802) imageView.setImageResource(R.drawable.overcast_clouds);

                //default value
                else imageView.setImageResource(R.drawable.clear_sky_day);
            }

            //if the sky is clear and it is 1 hour around sunrise/ sunset: change image to sunrise/ sunset

            if (id == 500) {
                if (Math.abs((currentDate - sunrise)) < 30) imageView.setImageResource(R.drawable.sunrise);
                else if (Math.abs((currentDate - sunset)) < 30) imageView.setImageResource(R.drawable.sunset);
                else imageView.setImageResource(R.drawable.clear_sky_day);
            }

        } catch (JSONException e) {
            //TODO
        }
    }

    //menu button, top left corner
    public void openMenuActivity(View view) {
        Intent openMenu = new Intent(this, MenuActivity.class);
        openMenu.putExtra("CITY_NAME", location);
        startActivity(openMenu);
    }

    //refresh button, middle right edge
    public void fetchData(View view) {
        fetchData();
    }
}