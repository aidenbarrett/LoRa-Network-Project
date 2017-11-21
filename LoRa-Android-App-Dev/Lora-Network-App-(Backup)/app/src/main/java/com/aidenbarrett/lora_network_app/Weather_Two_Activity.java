package com.aidenbarrett.lora_network_app;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Weather_Two_Activity extends AppCompatActivity {

    String finalLatitude;
    String finalLongitude;

    ProgressDialog Dialog;

    String location_name = " ";

    TextView mClouds ,mTemperature, mRain, mHumidity, mPressure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather__two_);

//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);

        //Setup dialogue
        Dialog = new ProgressDialog(Weather_Two_Activity.this);
        Dialog.setMessage("Getting Donegal Weather...");
        Dialog.setCancelable(false);

        mClouds = (TextView) findViewById(R.id.textView_clouds);
        mTemperature = (TextView) findViewById(R.id.textView_temperature);
        mRain = (TextView) findViewById(R.id.textView_rain);
        mHumidity = (TextView) findViewById(R.id.textView_humidity);
        mPressure = (TextView) findViewById(R.id.textView_pressure);

        location_name = getIntent().getStringExtra("location_name");
        finalLatitude = getIntent().getStringExtra("location_latitude");
        finalLongitude = getIntent().getStringExtra("location_longitude");
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check if Network is available or not
        if (isConnectingToInternet()) {
            Dialog.show();
            new GetWeatherTask().execute();
        }
    }

    // Calls Weather API using the current location
    private class GetWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try
            {
                String apiURL = "http://api.openweathermap.org/data/2.5/weather?lat=" + finalLatitude + "&lon=" + finalLongitude + "&appid=41d6b09689f0bb47fff3b40001678b09";
                URL url = new URL(apiURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }
                Log.e("response",builder.toString());
                urlConnection.disconnect();
                return builder.toString();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return " ";
            }
        }

        @Override
        protected void onPostExecute(String resp) {
            try {
                Log.e("onPostExecute resp",resp);
                Dialog.dismiss();

                JSONObject mainObj = new JSONObject(resp);
                JSONObject weatherObj = mainObj.getJSONObject("main");

                String humidity = weatherObj.getString("humidity");
                mHumidity.setText(humidity + " %");

                //Double temperature = weatherObj.getDouble("temp");
                mTemperature.setText(String.format("%.2f", weatherObj.getDouble("temp") - 273.15) + " ℃");

                String pressure = weatherObj.getString("pressure");
                mPressure.setText(pressure + " hPa");

                // OpenWeatherApp.org JSON info for Rain or precipitation is sporadic
                if(mainObj.has("rain")){
                    mRain.setText(mainObj.getJSONObject("rain").getString("3h")+ " mm");
                }else{
                    mRain.setText("Not Available");
                }

                // Weather condition JSON info is an array, not an object
                JSONObject clouds = mainObj.getJSONArray("weather").getJSONObject(0);
                mClouds.setText(clouds.getString("description"));

            } catch (Exception e) {
                Log.e("parsing err", " " + e.getLocalizedMessage());
                e.printStackTrace();
            }

        }
    }

    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
        }
        return false;
    }


}