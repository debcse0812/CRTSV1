package com.example.crts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private EditText phoneNumber;
    private FirebaseAuth mAuth;
    private String verificationId;
    private PinView OTP;
    private String verification;
    private String number;
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
        Button sendButton = findViewById(R.id.sendButton);
        phoneNumber.setSelection(0); // cursor position
        mAuth = FirebaseAuth.getInstance();
        OTP = findViewById(R.id.otpPinView);
        Button verifyButton = findViewById(R.id.verifyButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 number = phoneNumber.getText().toString();
                if(isValidPhoneNUmber(number)) {
                    number = "+91" + number;
                    Toast.makeText(MainActivity.this, "OTP sent to "+number, Toast.LENGTH_SHORT).show();
                    sendVerificationCode(number);
                    // going to verify otp class

                }else {
                    Toast.makeText(MainActivity.this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verification = OTP.getText().toString();
                if (verification.length()<6) {
                    Toast.makeText(MainActivity.this, "Please enter valid OTP", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Verifying OTP", Toast.LENGTH_SHORT).show();

                    verifyCode(verification);
                }
            }
        });
    }

    private boolean isValidPhoneNUmber(String phoneNumber){
        if(phoneNumber.length()!=10) return false;
        String regex = "[6-9][0-9]{9}";
        return(phoneNumber.matches(regex));

    }

    private void sendVerificationCode(String number) {
        // this method is used for getting
        // OTP on user phone number.
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack
            = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        // below method is used when
        // OTP is sent from Firebase
        @Override
        public void onCodeSent( String s,  PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }
        // this method is called when user
        // receive OTP from Firebase.
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            final String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                OTP.setText(code);
                verifyCode(code);
            }
        }
        // this method is called when firebase doesn't
        // sends our OTP code due to any error or issue.
        @Override
        public void onVerificationFailed(FirebaseException e) {

            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void signInWithCredential(PhoneAuthCredential credential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent myIntent = new Intent(MainActivity.this, Home.class);
                            myIntent.putExtra("key", number);
                            MainActivity.this.startActivity(myIntent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential);
    }

}