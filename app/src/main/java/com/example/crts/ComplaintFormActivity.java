package com.example.crts;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

public class ComplaintFormActivity extends AppCompatActivity {

    // Declaring the input variables from the user in the form:
    private String name, phone, email, address, complaint_type, complaint_detail, date;

    // Declaring the editText that are to be received from user input:
    private EditText edit_name, edit_phone, edit_email, edit_add, edit_complaint_detail, edit_date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_form);

        // starting the current intent with
        Intent intent = getIntent();
        phone = intent.getStringExtra("key"); //phone number from previous activity.

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

            // show a message about the success or failure of the POST request: "Complaint Registered Successfully."
            Toast.makeText(ComplaintFormActivity.this, "Complaint Registered Successfully.", Toast.LENGTH_SHORT).show();
        });
    }



    // For radio selectors
    public void onRadioClicked(View view){
        // to get the text from the clicked button of the radio buttons:
        RadioButton complaintTypeButton = findViewById(view.getId());
        complaint_type = complaintTypeButton.getText().toString();

        Toast.makeText(ComplaintFormActivity.this, complaint_type, Toast.LENGTH_SHORT).show();
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
    // for setting date in UI and updating the dat evariable:
    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
        // If done picking date
        String date_picked = d + "/" + (m+1) + "/" + y;
        date = date_picked;
        edit_date.setText(date_picked);
    }
    // email validator:
    public static boolean isEmailValid(String email){
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