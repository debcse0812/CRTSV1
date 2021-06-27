package com.example.crts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;

public class VerifyOTP extends AppCompatActivity {

    private Pinview pinView;
    private Button verifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        // removing the toolbar
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){
            Toast.makeText(this, "Error Loading The App!!!", Toast.LENGTH_SHORT).show();
        }

        // starting the current intent with
        Intent intent = getIntent();
        String value = intent.getStringExtra("key"); //phone number.

        pinView = findViewById(R.id.otpPinView);
        verifyButton = findViewById(R.id.verifyButton);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(VerifyOTP.this, "OTP verified!!!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}