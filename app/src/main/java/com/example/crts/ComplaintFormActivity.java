package com.example.crts;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ComplaintFormActivity extends AppCompatActivity {

    // Declaring the input variables from the user in the form:
    private String userToken, name, phone, email, address, complaint_type, complaint_detail, date;

    // Declaring the editText that are to be received from user input:
    private EditText edit_name, edit_phone, edit_email, edit_add, edit_complaint_detail, edit_date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_form);

        // starting the current intent with
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String phone = bundle.getString("phone"); //phone number.
        userToken = bundle.getString("userToken"); // userToken

        // setting the phone no from the previous activity in the editText
        edit_phone = findViewById(R.id.edit_phone);
        edit_phone.setText(phone);
        edit_phone.setFocusable(false);

        // initializing the EditTexts to get the data inputs from user:
        edit_name = findViewById(R.id.edit_name);
        edit_email = findViewById(R.id.edit_email);
        edit_add = findViewById(R.id.edit_add);
        edit_complaint_detail = findViewById(R.id.edit_complaint_detail);
        edit_date = findViewById(R.id.edit_date);


        // final submit button:
        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(view -> {

            // initializing the input variables to be stored in the database:
            // ( phone, complaint_type, date will be initialized accordingly )
            name = edit_name.getText().toString();
            email = edit_email.getText().toString();
            address = edit_add.getText().toString();
            complaint_detail = edit_complaint_detail.getText().toString();
            date = edit_date.getText().toString();

            // check if all inputs are provided:
            if(name.isEmpty() || email.isEmpty() || address.isEmpty() || complaint_type.isEmpty()
                    || complaint_detail.isEmpty() || date.isEmpty()){
                Toast.makeText(this, "All Fields are mandatory", Toast.LENGTH_SHORT).show();
                return;
            }
            // validate Email:
            if(!isEmailValid(email)){
                Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
                return;
            }

            // make a post request to store the in the database:
            addNewComplaint();

        });
    }

    private void addNewComplaint() {
        String url = "https://crtsapp.herokuapp.com/api/complaint/";
        HashMap<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("email", email);
        body.put("address", address);
        body.put("c_type", complaint_type);
        body.put("c_detail", complaint_detail);
        body.put("date", date);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                new JSONObject(body),
                response -> {
                    try {
                        if(response.getBoolean("success")){

                            Toast.makeText(this, "Complaint Registered Successfully", Toast.LENGTH_SHORT).show();
                            new android.os.Handler(Looper.getMainLooper()).postDelayed(
                                    () -> {
                                        // this function is to add some delay on successful complaint registration
                                    }, 3000);

                            // Going back to User Home Activity on successful registration:
                            Intent myIntent = new Intent(ComplaintFormActivity.this, UserHomeActivity.class);
                            startActivity(myIntent);

                        }else{
                            Toast.makeText(ComplaintFormActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ComplaintFormActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();

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
                headers.put("Authorization", userToken);
                return headers;
            }
        };

        // Adding a retry policy to ensure user can try again to login in case there is an issue with the backend.
        int socketTime = 3000;  // 3sec time is given to try registering again
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTime,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);

        // adding to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }


    // For radio selectors
    public void onRadioClicked(View view){
        // to get the text from the clicked button of the radio buttons:
        RadioButton complaintTypeButton = findViewById(view.getId());
        complaint_type = complaintTypeButton.getText().toString();
//        Toast.makeText(this, complaint_type, Toast.LENGTH_SHORT).show();
    }

    // for date input
    public void showDateDialog(View view){
        edit_date = (EditText) view;   // Store the dialog to be picked
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, this::onDateSet,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }
    // for setting date in UI and updating the date variable:
    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
        // If done picking date
        String date_picked = d + "/" + (m+1) + "/" + y;
        date = date_picked;
        edit_date.setText(date_picked);
    }
    // email validator:
    private static boolean isEmailValid(String email){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

}