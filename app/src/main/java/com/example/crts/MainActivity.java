package com.example.crts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private EditText phoneNumber;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // removing the toolbar
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){
            Toast.makeText(this, "Error Loading The App!!!", Toast.LENGTH_SHORT).show();
        }
        // initializing the variables
        phoneNumber = findViewById(R.id.phoneNumberEditText);
        sendButton = findViewById(R.id.sendButton);
        phoneNumber.setSelection(0); // cursor position

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = phoneNumber.getText().toString();
                if(isValidPhoneNUmber(number)) {
                    Toast.makeText(MainActivity.this, "OTP sent", Toast.LENGTH_SHORT).show();

                    // going to verify otp class
                    Intent myIntent = new Intent(MainActivity.this, VerifyOTP.class);
                    myIntent.putExtra("key", number);
                    MainActivity.this.startActivity(myIntent);
                }else {
                    Toast.makeText(MainActivity.this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    private boolean isValidPhoneNUmber(String phoneNumber){
        if(phoneNumber.length()!=10) return false;
        String regex = "[6-9][0-9]{9}";
        return(phoneNumber.matches(regex));

    }
}