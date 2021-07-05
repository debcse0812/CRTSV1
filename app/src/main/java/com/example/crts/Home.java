package com.example.crts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Home extends AppCompatActivity {
    private TextView phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent myIntent = getIntent();
        String number = myIntent.getExtras().getString("key");
        phoneNumber = findViewById(R.id.phoneNumber);
        phoneNumber.setText(number);
    }
}