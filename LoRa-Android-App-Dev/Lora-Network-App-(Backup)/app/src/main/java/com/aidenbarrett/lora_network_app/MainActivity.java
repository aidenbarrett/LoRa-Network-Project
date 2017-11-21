package com.aidenbarrett.lora_network_app;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.SystemClock;
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

public class MainActivity extends AppCompatActivity {

    LocationManager mLocationManager;
    MyLocationListener obj_map_location_listener;
    ProgressDialog Dialog;

    double finalLatitude;
    double finalLongitude;

    String city = " ";

    TextView mCity, mClouds ,mTemperature, mRain, mHumidity, mPressure;

    // Location timeout is the amount of time before considering the location out of date in milliseconds
    public static int LOCATION_TIMEOUT = 60000; // 1 minute

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);

        //Setup dialogue
        Dialog = new ProgressDialog(MainActivity.this);
        Dialog.setMessage("Getting Current Location...");
        Dialog.setCancelable(false);

        mCity = (TextView) findViewById(R.id.textView_city);
        mClouds = (TextView) findViewById(R.id.textView_clouds);
        mTemperature = (TextView) findViewById(R.id.textView_temperature);
        mRain = (TextView) findViewById(R.id.textView_rain);
        mHumidity = (TextView) findViewById(R.id.textView_humidity);
        mPressure = (TextView) findViewById(R.id.textView_pressure);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check if Network is available or not
        if (isConnectingToInternet()) {

            // If Network is available start getting users current location
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            String provider = mLocationManager.getBestProvider(searchProviderCriteria, true);

            Location loc = mLocationManager.getLastKnownLocation(provider);
            if (loc == null ||  (SystemClock.elapsedRealtime() - loc.getTime()) > LOCATION_TIMEOUT) {

                // We request another update Location
                Dialog.show();
                Log.e("SwA", "Request location");
                obj_map_location_listener = new MyLocationListener();
                mLocationManager.requestSingleUpdate(provider, obj_map_location_listener, null);
            }
            else {
                //Location is not null so call API and get weather data
                finalLatitude = loc.getLatitude();
                finalLongitude = loc.getLongitude();
                Dialog.show();
                new GetWeatherTask().execute();
            }
        }
    }

    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location loc) {
            finalLatitude=loc.getLatitude();
            finalLongitude=loc.getLongitude();
            mLocationManager.removeUpdates(obj_map_location_listener);
            new GetWeatherTask().execute();
        }

        public void onProviderDisabled(String provider) {
            Dialog.dismiss();
        }

        public void onProviderEnabled(String provider) {
            Dialog.dismiss();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            Dialog.dismiss();
        }
    }

    private static Criteria searchProviderCriteria = new Criteria();
    // Location Criteria
    static {
        searchProviderCriteria.setPowerRequirement(Criteria.POWER_LOW);
        searchProviderCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
        searchProviderCriteria.setCostAllowed(false);
    }

    // Calls Weather API using the current location
    private class GetWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try
            {
                String apiURL="http://api.openweathermap.org/data/2.5/weather?lat=" + finalLatitude + "&lon=" + finalLongitude + "&appid=41d6b09689f0bb47fff3b40001678b09";
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

                //JSON Parsing and handle the response
                JSONObject mainObj = new JSONObject(resp);
                city = mainObj.getString("name");
                mCity.setText(city);

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