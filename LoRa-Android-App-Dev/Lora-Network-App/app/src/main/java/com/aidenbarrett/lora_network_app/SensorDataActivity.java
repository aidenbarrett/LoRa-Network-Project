package com.aidenbarrett.lora_network_app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SensorDataActivity extends AppCompatActivity {
    private static final String TAG = SensorDataActivity.class.getSimpleName();
    //  private static final String API_URL = "https://d83d9c1a-0bf4-418b-990b-a93fdbc8a456-bluemix.cloudant.com/aiden_raspi3_db/_all_docs?include_docs=true&limit=1&descending=true";
    private static final String API_URL = "https://96aa99da-2ca2-40ca-8909-e6c4bc00029c-bluemix.cloudant.com/aiden_14042017_db/_all_docs?include_docs=true&limit=2&descending=true";
    private TextView mNode1IdView;
    private TextView mSensor1_1View;
    private TextView mSensor2_1View;
    private TextView mNode2IdView;
    private TextView mSensor1_2View;
    private TextView mSensor2_2View;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }

        initViews();

        final Handler h = new Handler();
        final int delay = 10000; //milliseconds

        h.postDelayed(new Runnable(){
            public void run(){
                updateData();
                h.postDelayed(this, delay);
            }
        }, delay);
        updateData();
    }

    private void updateData() {
        if (isConnectingToInternet()) {
            GetSensorValuesTask task = new GetSensorValuesTask();
            task.execute();
        }
    }

    private void initViews() {
        mNode1IdView = (TextView) findViewById(R.id.node_one_id_textview);
        mSensor1_1View = (TextView) findViewById(R.id.sensor_1_textview);
        mSensor2_1View = (TextView) findViewById(R.id.sensor_2_textview);
        mNode2IdView = (TextView) findViewById(R.id.textView_rain);
        mSensor1_2View = (TextView) findViewById(R.id.textView_humidity);
        mSensor2_2View = (TextView) findViewById(R.id.textView_pressure);
    }

    private class GetSensorValuesTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String auth = "Basic bWVudGljbGV0ZWx5c2Vlc29pbGx5YXJhOmM3YTI4NGQ4OGU1ZGU3OGJlZDM5ZmJlMWViMWM4YmZkY2MzMDc5YTA=";
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
                String sensor0Val1="", sensor1Val1="", sensor0Val2="", sensor1Val2="";

                JSONObject mainObj = new JSONObject(resp);
                JSONArray rowsArr = mainObj.getJSONArray("rows");

                JSONObject sensorValObj_1 = rowsArr.getJSONObject(1);
                JSONObject docObj_1 = sensorValObj_1.getJSONObject("doc");
                JSONObject dObj_1 = docObj_1.getJSONObject("d");
                String deviceId_1 = dObj_1.getString("NodeNumber");

                JSONObject sensorValObj_2 = rowsArr.getJSONObject(0);
                JSONObject docObj_2 = sensorValObj_2.getJSONObject("doc");
                JSONObject dObj_2 = docObj_2.getJSONObject("d");
                String deviceId_2 = dObj_2.getString("NodeNumber");

                sensor0Val1 = dObj_1.getString("sensor0Val1");
                sensor1Val1 = dObj_1.getString("sensor1Val1");
                sensor0Val2 = dObj_2.getString("sensor0Val2");
                sensor1Val2 = dObj_2.getString("sensor1Val2");

                if(deviceId_1=="1"){
                    mNode1IdView.setText(deviceId_1);
                    mSensor1_1View.setText(sensor0Val1 + "");
                    mSensor2_1View.setText(sensor1Val1 + "");
                }
                else{
                    mNode2IdView.setText(deviceId_1);
                    mSensor1_2View.setText(sensor0Val1 + "");
                    mSensor2_2View.setText(sensor1Val1 + "");
                }
                if(deviceId_2=="2"){
                    mNode2IdView.setText(deviceId_2);
                    mSensor1_2View.setText(sensor0Val2 + "");
                    mSensor2_2View.setText(sensor1Val2 + "");
                }
                else{
                    mNode1IdView.setText(deviceId_2);
                    mSensor1_1View.setText(sensor0Val2 + "");
                    mSensor2_1View.setText(sensor1Val2 + "");
                }
            } catch (Exception e) {
                Log.e("parsing err", " " + e.getLocalizedMessage());
                e.printStackTrace();
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


}
