package com.example.crts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.chaos.view.PinView;

import java.util.Objects;

public class VerifyOTP extends AppCompatActivity {

    private PinView pinView;
    private Button verifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        // removing the toolbar
        try
        {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException e){
            Toast.makeText(this, "Error Loading The App!!!", Toast.LENGTH_SHORT).show();
        }

        // starting the current intent with
        Intent intent = getIntent();
        String phoneNumber = intent.getStringExtra("key"); //phone number.

        pinView = findViewById(R.id.otpPinView);
        verifyButton = findViewById(R.id.verifyButton);

        verifyButton.setOnClickListener(view -> {
            Toast.makeText(VerifyOTP.this, "OTP verified for "+ phoneNumber+".", Toast.LENGTH_SHORT).show();
            // after OTP verification, we'll move to userHome Activity:

            // going to user home activity
            Intent myIntent = new Intent(VerifyOTP.this, UserHomeActivity.class);
            myIntent.putExtra("key", phoneNumber);
            VerifyOTP.this.startActivity(myIntent);
        });

    }
}