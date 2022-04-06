package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Button GetWeather;
    private EditText CityEnterText;
    private TextView City;
    private String APIkey = "012c7e75c561c77131a799bc2678d84f";
    private TextView Temp, FeelsLikeTemp, WeatherDesc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetWeather = findViewById(R.id.GetWeather);
        City = findViewById(R.id.City);
        CityEnterText = findViewById(R.id.CityEnterText);
        Temp = findViewById(R.id.Temp);
        FeelsLikeTemp = findViewById(R.id.FeelsLikeTemp);
        WeatherDesc = findViewById(R.id.WeatherDesc);

        GetWeather.setOnClickListener(view -> {
            if(CityEnterText.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Введите город", Toast.LENGTH_LONG).show();
            }
            else {
                String URL = "http://api.openweathermap.org/data/2.5/weather?q=" + CityEnterText.getText() + "&appid=" + APIkey + "&units=metric&lang=ru";
                new GetWeatherData().execute(URL);
            }
        });
    }
    public class GetWeatherData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer stringBuffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line).append("\n");
                }
                return stringBuffer.toString();
            }
            catch (MalformedURLException e) { e.printStackTrace(); }
            catch (IOException e) { e.printStackTrace(); }
            finally {
                if (connection != null) { connection.disconnect(); }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String Result) {
            super.onPostExecute(Result);

            try {
                JSONObject JSONobject = new JSONObject(Result);
                Temp.setText(JSONobject.getJSONObject("main").getDouble("temp") + "°C");
                FeelsLikeTemp.setText(JSONobject.getJSONObject("main").getDouble("feels_like") + "°C");
                WeatherDesc.setText(JSONobject.getJSONArray("weather").getJSONObject(0).getString("description"));
                City.setText(CityEnterText.getText());
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            catch (NullPointerException npe) {
                npe.printStackTrace();
                Toast.makeText(MainActivity.this, "Ошибка в названии города, повторите попытку", Toast.LENGTH_LONG).show();
            }
        }
    }
}
