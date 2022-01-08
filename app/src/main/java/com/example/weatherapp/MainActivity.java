package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    RelativeLayout relativehome;
    TextView cityname, weathertemp, weathercondition;
    RecyclerView recyclerview;
    TextInputEditText weathernameedttxt;
    ImageView search, weathericon, bgicon;
    ArrayList<weathermodal> weathermodalArrayList;
    weatheradapter weatheradapter;
    LocationManager locationManager;
    int PermissionCode = 1;
    String cityName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.weatherprogressbar);
        relativehome = findViewById(R.id.relativehome);
        cityname = findViewById(R.id.cityname);
        weathertemp = findViewById(R.id.weathertemp);
        weathercondition = findViewById(R.id.weathercondition);
        recyclerview = findViewById(R.id.weatherrecyclerview);
        weathernameedttxt = findViewById(R.id.weathernameedittxt);
        search = findViewById(R.id.search);
        weathericon = findViewById(R.id.weathericon);
        bgicon = findViewById(R.id.weatherbg);
        weathermodalArrayList = new ArrayList<>();
        weatheradapter = new weatheradapter(this,weathermodalArrayList);
        recyclerview.setAdapter(weatheradapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) !=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},PermissionCode);
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityName= getCityName(-0.11,51.52);
        getWeatherInfo(cityName);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = weathernameedttxt.getText().toString();
                if(city.isEmpty())
                {
                    Toast.makeText(MainActivity.this,"Please Enter City Name",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    cityname.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PermissionCode)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Please Provide the Permission", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private String getCityName(double longitude, double latitude)
    {
        cityName = "Not Found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try
        {
            List<Address> addresses = gcd.getFromLocation(latitude,longitude,10);

            for(Address adr : addresses)
            {
                if(adr!=null)
                {
                    String city = adr.getLocality();
                    if(city!=null && !city.equals(""));
                    cityName = city;
                }
                else
                {
                    Log.d("TAG","City Not Found");
                    Toast.makeText(this,"User City Not Found",Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return cityName;
    }


    public void getWeatherInfo(String cityName)
    {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=1428e4afbd20499aa53155018220701&q=" + cityName + "&days=1&aqi=no&alerts=no";
        cityname.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                relativehome.setVisibility(View.VISIBLE);
                weathermodalArrayList.clear();

                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    weathertemp.setText(temperature+"°C");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionicon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionicon)).into(weathericon);
                    weathercondition.setText(condition);

                    if(isDay == 1)
                    {
                        Picasso.get().load("https://www.vecteezy.com/vector-art/358552-spring-day-wallpaper").into(bgicon);
                    }
                    else
                    {
                        Picasso.get().load("https://www.vecteezy.com/vector-art/4865266-dramatic-night-scene-landscape-with-crescent-moon-lake-and-pine-trees").into(bgicon);
                    }

                    JSONObject forecast = response.getJSONObject("forecast");
                    JSONObject forecastO = forecast.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastO.getJSONArray("hour");

                    for(int i=0;i<hourArray.length();i++)
                    {
                        JSONObject hourobj = hourArray.getJSONObject(i);
                        String time = hourobj.getString("time");
                        String temp = hourobj.getString("temp_c");
                        String img = hourobj.getJSONObject("condition").getString("icon");
                        String wind = hourobj.getString("wind_kph");
                        weathermodalArrayList.add(new weathermodal(time,temp,img,wind));
                        weatheradapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter valid city Name", Toast.LENGTH_SHORT).show();
            }
        });


    }
}