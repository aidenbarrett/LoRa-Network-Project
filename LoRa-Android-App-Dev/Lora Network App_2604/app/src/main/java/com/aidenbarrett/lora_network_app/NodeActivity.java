package com.aidenbarrett.lora_network_app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class NodeActivity extends AppCompatActivity {

    private static final String TAG = "MyFavouriteMessages";

    private Button mLed_On;
    private Button mLed_Off;
    private Button mLed2_On;
    private Button mLed2_Off;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node);

        // On1 & Off1 Button making ... BEGIN
        mLed_On = (Button) findViewById(R.id.led_on);
        mLed_On.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "LED_ON CLICKED");
                if (isConnectingToInternet()) {
                    new SendOnCommand().execute();
                }
            }
        });

        mLed_Off = (Button) findViewById(R.id.led_off);
        mLed_Off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "LED_OFF CLICKED");
                if (isConnectingToInternet()) {
                    new SendOffCommand().execute();
                }
            }
        });
        // On1 & Off1 Button making ... END

        // On2 & Off2 Button making ... BEGIN
        mLed2_On = (Button) findViewById(R.id.led2_on);
        mLed2_On.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "LED2_ON CLICKED");
                if (isConnectingToInternet()) {
                    new SendOn2Command().execute();
                }
            }
        });

        mLed2_Off = (Button) findViewById(R.id.led2_off);
        mLed2_Off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "LED2_OFF CLICKED");
                if (isConnectingToInternet()) {
                    new SendOff2Command().execute();
                }
            }
        });
        // On2 & Off2 Button making ... END
    }

    // Sends On1 Command ... BEGIN
    private class SendOnCommand extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try
            {
                String apiURL = "https://aiden-14042017.mybluemix.net/led?state=1";
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
                Toast.makeText(NodeActivity.this,"LED 1 on",Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e("parsing err", " " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }
    // Sends On1 Command ... END

    // Sends Off1 Command ... BEGIN
    private class SendOffCommand extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try
            {
                String apiURL = "https://aiden-14042017.mybluemix.net/led?state=0";
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
                Toast.makeText(NodeActivity.this,"LED 1 off",Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e("parsing err", " " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }
    // Sends Off1 Command ... END

    // Sends On2 Command ... BEGIN
    private class SendOn2Command extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try
            {
                String apiURL = "https://aiden-14042017.mybluemix.net/led?state=3";
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
                Toast.makeText(NodeActivity.this,"LED 2 on",Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e("parsing err", " " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }
    // Sends On2 Command ... END

    // Sends Off2 Command ... BEGIN
    private class SendOff2Command extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try
            {
                String apiURL = "https://aiden-14042017.mybluemix.net/led?state=2";
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
                Toast.makeText(NodeActivity.this,"LED 2 off",Toast.LENGTH_LONG).show();
                Log.e("onPostExecute resp",resp);
            } catch (Exception e) {
                Log.e("parsing err", " " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }
    // Sends Off2 Command ... END

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