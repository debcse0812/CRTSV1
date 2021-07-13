package com.example.crts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private EditText phoneNumber;
    private FirebaseAuth mAuth;
    private String verificationId, verification, number;
    private PinView OTP;
    private TextView alreadyVerifiedPhone;
    Button verifyButton, sendButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // removing the toolbar
        try
        {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException e){
            Toast.makeText(this, "Error Loading The App!!!", Toast.LENGTH_SHORT).show();
        }
        // initializing the variables
        phoneNumber = findViewById(R.id.phoneNumberEditText);
        sendButton = findViewById(R.id.sendButton);
        phoneNumber.setSelection(0); // cursor position
        mAuth = FirebaseAuth.getInstance();
        OTP = findViewById(R.id.otpPinView);
        verifyButton = findViewById(R.id.verifyButton);
        alreadyVerifiedPhone = findViewById(R.id.alreadyVerifiedPhone);
        // sending otp to the provided phone number:
        sendButton.setOnClickListener(view -> {
            number = phoneNumber.getText().toString();
            if(isValidPhoneNUmber(number)) {
                number = "+91" + number;
                Toast.makeText(MainActivity.this, "Sending OTP... ", Toast.LENGTH_SHORT).show();
                sendVerificationCode(number);
            }else {
                Toast.makeText(MainActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            }
        });
        // verifying the otp:
        verifyButton.setOnClickListener(view -> {
            verification = Objects.requireNonNull(OTP.getText()).toString();
            if (verification.length()<6) {
                Toast.makeText(MainActivity.this, "Enter a valid OTP", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Verifying OTP...", Toast.LENGTH_SHORT).show();

                verifyCode(verification);
            }
        });
        // this is similar to login
        alreadyVerifiedPhone.setOnClickListener(view -> {
            number = phoneNumber.getText().toString();
            if(isValidPhoneNUmber(number)) {
                number = "+91" + number;
                Toast.makeText(MainActivity.this, "Verifying User...", Toast.LENGTH_SHORT).show();
                // verify if the phone number already exists in our database:
                loginUser(number);
            }else{
//                    Toast.makeText(MainActivity.this, "Please enter the verified number", Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "Enter valid phone number to verify", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser(String number) {
        HashMap<String, String> parameters = new HashMap<>(); // for our app, we are using the verified phone number as parameters
        parameters.put("phone", number);

        String apiKey = "https://crtsapp.herokuapp.com/api/crts/auth/login/"; // change this whenever you upload the project to some other backend service.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                apiKey,
                new JSONObject(parameters),
                response -> {
                    try {
                        if(response.getBoolean("success")){
                            // Move on to UserHomeActivity
                            Intent myIntent = new Intent(MainActivity.this, UserHomeActivity.class);
                            myIntent.putExtra("key", number);
                            MainActivity.this.startActivity(myIntent);
                            finish();
                        }else{
                            Toast.makeText(MainActivity.this, "Please click on Verify again", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                },
                error -> {

                    NetworkResponse response = error.networkResponse;
                    if(error instanceof ServerError && response!=null){
                        try {
                            String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            JSONObject obj = new JSONObject(res);
                            Toast.makeText(MainActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();

                        }catch (JSONException | UnsupportedEncodingException jsonException){
                            jsonException.printStackTrace();
                        }
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Adding a retry policy to ensure user can try again to login in case there is an issue with the backend.
        int socketTime = 5000;  // 5sec time is given to register
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTime,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
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
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
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
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // after getting credential we are calling sign in method.
        signInWithCredential(credential);
    }
    private void signInWithCredential(PhoneAuthCredential credential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // register the phone no in the database
                        registerUser(number);

                    } else {
                        Toast.makeText(MainActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void registerUser(String number) {
        // send request to backend for registering the user with phone number = number:
        // we have to send phone number by using key-value pair like: ("phone": "9800000011")
        HashMap<String, String> parameters = new HashMap<>(); // for our app, we are using the verified phone number as parameters
        parameters.put("phone", number);

        String apiKey = "https://crtsapp.herokuapp.com/api/crts/auth/register/"; // change this whenever you upload the project to some other backend service.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                apiKey,
                new JSONObject(parameters),
                response -> {
                    try {
                        if(response.getBoolean("success")){
                            // On successful registration, we will login the user subsequently.
                            // Logging in the user will also help to get the information of the user
                            // that can be stored in the local storage(Using SharedPreference) to fasten the App.
                            loginUser(number);
                        }else{
                            Toast.makeText(MainActivity.this, "Please click on Verify again", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                },
                error -> {

                    NetworkResponse response = error.networkResponse;
                    if(error instanceof ServerError && response!=null){
                        try {
                            String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            JSONObject obj = new JSONObject(res);
                            Toast.makeText(MainActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();

                        }catch (JSONException | UnsupportedEncodingException jsonException){
                            jsonException.printStackTrace();
                        }
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Adding a retry policy to ensure user can try again to login in case there is an issue with the backend.
        int socketTime = 5000;  // 5sec time is given to register
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTime,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }



}