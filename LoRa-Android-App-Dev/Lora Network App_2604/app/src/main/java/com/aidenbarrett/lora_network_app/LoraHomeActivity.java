package com.aidenbarrett.lora_network_app;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LoraHomeActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1;
    private Button mSensorInfo;
    private Button mNodeControl;
    private Button mWeatherData;

    //  onCreate launches code on creation of activity
    //  setContentView focuses the activity on the reference layout - links to XML

    private static final String TAG = LoraHomeActivity.class.getSimpleName();
    LocationManager mLocationManager;
    MyLocationListener obj_map_location_listener;
    ProgressDialog Dialog;
    String city = "",temperature = "",clouds_description = "",share_Content = "";
    int sensor0Val1=0,sensor1Val1=0;
    int sensor0Val2=0,sensor1Val2=0;
    double finalLatitude;
    double finalLongitude;
    Boolean isShare = false;
    // Location timeout is the amount of time before considering the location out of date in milliseconds
    public static int LOCATION_TIMEOUT = 60000; // 1 minute
    private static final String API_URL = "https://96aa99da-2ca2-40ca-8909-e6c4bc00029c-bluemix.cloudant.com/aiden_14042017_db/_all_docs?include_docs=true&descending=true&limit=5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lora_home);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }

        //Setup dialogue
        Dialog = new ProgressDialog(LoraHomeActivity.this);
        Dialog.setCancelable(false);

        //  findViewByID - this declares a button mSensorInfo and links it to sensor info button activity home in XML
        //  OnClickListener - upon clicking of referenced sensor info button, it will call the onClick method (intents).
        mSensorInfo = (Button) findViewById(R.id.sensor_info);
        mSensorInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoraHomeActivity.this, SensorDataActivity.class);
                startActivity(intent);
            }
        });

        //  findViewByID - this declares a button mLED_Toggle and links it to led toggle button activity home in XML
        //  OnClickListener - upon clicking of referenced LED_Toggle button, it will call the onClick method (intents).
        mNodeControl = (Button) findViewById(R.id.node_control);
        mNodeControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoraHomeActivity.this, NodeActivity.class);
                startActivity(intent);
            }
        });

        //  findViewByID - this declares a button mWeatherData and links it to weather data button activity home in XML
        //  OnClickListener - upon clicking of referenced weather data button, it will call the onClick method (intents).
        mWeatherData = (Button) findViewById(R.id.weather_data);
        mWeatherData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoraHomeActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        checkPermissions();

    }

    private boolean checkPermissions() {
        int fineLocationCheck = ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocationCheck = ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (fineLocationCheck != PackageManager.PERMISSION_GRANTED &&
                coarseLocationCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            isShare =true;
            if (checkPermissions()) {
                initLocation();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (grantResults.length > 0) {
                    if(isShare) {
                        initLocation();
                    }
                }
                break;
            default:
                Log.e(TAG, "Unknown Request Code");
        }
    }

    private void initLocation() {
        // Check if Network is available or not
        if (isConnectingToInternet()) {

            // If Network is available start getting users current location
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            String provider = mLocationManager.getBestProvider(searchProviderCriteria, true);

            Location loc = mLocationManager.getLastKnownLocation(provider);
            if (loc == null || (SystemClock.elapsedRealtime() - loc.getTime()) > LOCATION_TIMEOUT) {

                // We request another update Location
                Dialog.setMessage("Getting Current Location...");
                Dialog.show();
                Log.e("SwA", "Request location");
                obj_map_location_listener = new MyLocationListener();
                mLocationManager.requestSingleUpdate(provider, obj_map_location_listener, null);
            } else {
                //Location is not null so call API and get weather data
                finalLatitude = loc.getLatitude();
                finalLongitude = loc.getLongitude();
                Dialog.setMessage("Getting Current Location...");
                Dialog.show();
                new GetWeatherTask().execute();
            }
        }
    }

    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location loc) {
            finalLatitude = loc.getLatitude();
            finalLongitude = loc.getLongitude();
            mLocationManager.removeUpdates(obj_map_location_listener);
            Dialog.setMessage("Getting Weather data...");
            new GetWeatherTask().execute();
        }

        public void onProviderDisabled(String provider) {
            Dialog.dismiss();
        }

        public void onProviderEnabled(String provider) {
            //Dialog.dismiss();
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
            try {
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
                Log.e("response", builder.toString());
                urlConnection.disconnect();
                return builder.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return " ";
            }
        }

        @Override
        protected void onPostExecute(String resp) {
            try {
                Log.e("Weather api resp", resp);

                //JSON Parsing and handle the response
                JSONObject mainObj = new JSONObject(resp);
                city = mainObj.getString("name");
                JSONObject weatherObj = mainObj.getJSONObject("main");
                temperature = String.format("%.2f", weatherObj.getDouble("temp") - 273.15) + " ℃";
                // Weather condition JSON info is an array, not an object
                JSONObject clouds = mainObj.getJSONArray("weather").getJSONObject(0);
                clouds_description = clouds.getString("description");
                Dialog.setMessage("Getting Sensor data...");
                new GetSensorDataTask().execute();

            } catch (Exception e) {
                Log.e("parsing err", " " + e.getLocalizedMessage());
                e.printStackTrace();
                Dialog.setMessage("Getting Weather data...");
                new GetSensorDataTask().execute();
            }

        }
    }

    private class GetSensorDataTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String auth = getString(R.string.bluemix_auth);
                urlConnection.setRequestProperty("Authorization", auth);

                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }
                Log.e("response", builder.toString());
                urlConnection.disconnect();
                return builder.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return " ";
            }
        }

        @Override
        protected void onPostExecute(String resp) {
            try {

                Log.e("SensorData api resp", resp.trim());
                JSONObject mainObj = new JSONObject(resp);
                JSONArray rowsArr = mainObj.getJSONArray("rows");
                JSONObject sensorValObj = rowsArr.getJSONObject(0);
                JSONObject docObj = sensorValObj.getJSONObject("doc");

                int NodeNumber = docObj.getJSONObject("d")
                        .getInt("NodeNumber");

                if(NodeNumber==1){
                    sensor0Val1 = docObj.getJSONObject("d")
                            .getInt("sensor0Val1");
                    sensor1Val1 = docObj.getJSONObject("d")
                            .getInt("sensor1Val1");
                }else if(NodeNumber==2){
                    sensor0Val2 = docObj.getJSONObject("d")
                            .getInt("sensor0Val2");
                    sensor1Val2 = docObj.getJSONObject("d")
                            .getInt("sensor1Val2");
                }

                JSONObject sensorValObj1 = rowsArr.getJSONObject(1);
                JSONObject docObj1 = sensorValObj1.getJSONObject("doc");

                int NodeNumber2 = docObj1.getJSONObject("d")
                        .getInt("NodeNumber");
                if(NodeNumber2==1){
                    sensor0Val1 = docObj1.getJSONObject("d")
                            .getInt("sensor0Val1");
                    sensor1Val1 = docObj1.getJSONObject("d")
                            .getInt("sensor1Val1");
                }else if(NodeNumber2==2){
                    sensor0Val2 = docObj1.getJSONObject("d")
                            .getInt("sensor0Val2");
                    sensor1Val2 = docObj1.getJSONObject("d")
                            .getInt("sensor1Val2");
                }

                shareContent();
            } catch (Exception e) {
                Log.e("parsing err", " " + e.getLocalizedMessage());
                e.printStackTrace();
                shareContent();
            }
        }
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public void shareContent(){
        Dialog.dismiss();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TITLE, "Share Weather Info to..");
        share_Content = "----------------------------" +"\n\n"+
                        "Current Date and Time: "+getUserCurrentTIme()+"\n"+
                        "Current Location name: "+city+"\n\n"+
                        "----------------------------" +"\n\n"+
                        "Node ID: RasPi3_S1"+"\n"+
                        "DS1820: "+(sensor0Val1 == 0?"Not Available":sensor0Val1)+"\n"+
                        "DS1620: "+(sensor1Val1 == 0?"Not Available":sensor1Val1)+"\n\n"+
                        "----------------------------" +"\n\n"+
                        "Outside Temperature: "+temperature+"\n"+
                        "Cloud Forecast: "+clouds_description+"\n\n"+
                        "----------------------------" +"\n";

        Log.d("description: ", share_Content + "");
        if (share_Content.length() >= 500) {
            share_Content = share_Content.substring(0, 500);
        }
        share.putExtra(Intent.EXTRA_TEXT, share_Content);
        startActivity(Intent.createChooser(share, "Share to.."));
    }

    public static String getUserCurrentTIme() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM, dd yyyy HH:mm:ss");
        return sdf.format(cal.getTime());
    }
}
