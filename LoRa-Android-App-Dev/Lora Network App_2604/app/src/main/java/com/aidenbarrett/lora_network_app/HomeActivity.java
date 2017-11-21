package com.aidenbarrett.lora_network_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    private Button mCurrent_Location;
    private Button mDublin;
    private Button mDonegal;

    //  onCreate launches code on creation of activity
    //  setContentView focuses the activity on the reference layout - links to XML
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }


        //  findViewByID - this declares a button mCurrent_Location and links it to current location button activity home in XML
        //  OnClickListener - upon clicking of referenced current location button, it will call the onClick method (intents).
        mCurrent_Location = (Button) findViewById(R.id.current_location);
        mCurrent_Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        //  findViewByID - this declares a button mDublin and links it to Dublin button activity home in XML
        //  OnClickListener - upon clicking of referenced Dublin button, it will call the onClick method (intents).
        mDublin = (Button) findViewById(R.id.dublin);
        mDublin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,Weather_One_Activity.class);
                intent.putExtra("location_name","Dublin");
                intent.putExtra("location_latitude","53.34");
                intent.putExtra("location_longitude","-6.27");
                startActivity(intent);
            }
        });

        //  findViewByID - this declares a button mDonegal and links it to Donegal button activity home in XML
        //  OnClickListener - upon clicking of referenced Donegal button, it will call the onClick method (intents).
        mDonegal = (Button) findViewById(R.id.donegal);
        mDonegal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,Weather_Two_Activity.class);
                intent.putExtra("location_name","Donegal");
                intent.putExtra("location_latitude","54.65");
                intent.putExtra("location_longitude","-8.12");
                startActivity(intent);
            }
        });
    }

}
