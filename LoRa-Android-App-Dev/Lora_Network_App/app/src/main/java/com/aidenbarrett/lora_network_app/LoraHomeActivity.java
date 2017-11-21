package com.aidenbarrett.lora_network_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class LoraHomeActivity extends AppCompatActivity {

    private Button mSensorInfo;
    private Button mNodeControl;
    private Button mWeatherData;

    //  onCreate launches code on creation of activity
    //  setContentView focuses the activity on the reference layout - links to XML
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lora_home);

        //  findViewByID - this declares a button mSensorInfo and links it to sensor info button activity home in XML
        //  OnClickListener - upon clicking of referenced sensor info button, it will call the onClick method (intents).
        mSensorInfo = (Button) findViewById(R.id.sensor_info);
        mSensorInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoraHomeActivity.this,SensorDataActivity.class);
                startActivity(intent);
            }
        });

        //  findViewByID - this declares a button mLED_Toggle and links it to led toggle button activity home in XML
        //  OnClickListener - upon clicking of referenced LED_Toggle button, it will call the onClick method (intents).
        mNodeControl = (Button) findViewById(R.id.node_control);
        mNodeControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoraHomeActivity.this,NodeActivity.class);
                startActivity(intent);
            }
        });

        //  findViewByID - this declares a button mWeatherData and links it to weather data button activity home in XML
        //  OnClickListener - upon clicking of referenced weather data button, it will call the onClick method (intents).
        mWeatherData = (Button) findViewById(R.id.weather_data);
        mWeatherData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoraHomeActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });
    }

}
