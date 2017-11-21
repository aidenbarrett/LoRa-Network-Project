package com.aidenbarrett.lora_network_app;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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
    private static final String API_URL = "https://96aa99da-2ca2-40ca-8909-e6c4bc00029c-bluemix.cloudant.com/aiden_14042017_db/_all_docs?include_docs=true&descending=true&limit=5";
    private TextView mNode1IdView;
    private TextView mSensor1View;
    private TextView mSensor2View;

    private TextView mNode2IdView;
    private TextView mNode2Sensor1View;
    private TextView mNode2Sensor2View;

    ProgressDialog Dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }

        Dialog = new ProgressDialog(SensorDataActivity.this);
        Dialog.setMessage("Getting Sensor data...");
        Dialog.setCancelable(false);

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
            Dialog.show();
            GetWeatherTask task = new GetWeatherTask();
            task.execute();
        }
    }

    private void initViews() {
        mNode1IdView = (TextView) findViewById(R.id.node_one_id_textview);
        mNode1IdView.setText("Node 1");
        mSensor1View = (TextView) findViewById(R.id.sensor_1_textview);
        mSensor2View = (TextView) findViewById(R.id.sensor_2_textview);

        mNode2IdView = (TextView) findViewById(R.id.node_two_id_textview);
        mNode2IdView.setText("Node 2");
        mNode2Sensor1View = (TextView) findViewById(R.id.node2_sensor_1_textview);
        mNode2Sensor2View = (TextView) findViewById(R.id.node2_sensor_2_textview);


//        mNode1IdView.setText(getString(R.string.device_id));

    }

    private class GetWeatherTask extends AsyncTask<Void, Void, String> {
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
                Log.e("sensor resp",resp);
                JSONObject mainObj = new JSONObject(resp);
                JSONArray rowsArr = mainObj.getJSONArray("rows");
                JSONObject sensorValObj = rowsArr.getJSONObject(0);
                JSONObject docObj = sensorValObj.getJSONObject("doc");

                int NodeNumber = docObj.getJSONObject("d")
                        .getInt("NodeNumber");
                int sensor0Val1=0,sensor1Val1=0;
                int sensor0Val2=0,sensor1Val2=0;
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

                mSensor1View.setText(sensor0Val1 + " ℃");
                mSensor2View.setText(sensor1Val1 + " ℃");
                mNode2Sensor1View.setText( sensor0Val2+ " ℃");
                mNode2Sensor2View.setText(sensor1Val2+ " ℃");
                Dialog.dismiss();
            } catch (Exception e) {
                Dialog.dismiss();
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
