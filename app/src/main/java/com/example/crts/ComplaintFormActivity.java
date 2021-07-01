package com.example.crts;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ComplaintFormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_form);

        // starting the current intent with
        Intent intent = getIntent();
        String phoneNumber = intent.getStringExtra("key"); //phone number.

        // setting the phone no from the previous activity in the editText
        EditText phone = findViewById(R.id.edit_phone);
        phone.setText(phoneNumber);
        phone.setFocusable(false);


    }



    // For radio selectors
    public void onRadioClicked(View view){
        Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
    }
    // for date input
    public void onCheckClicked(View view){

    }
    // for checkbox
    EditText picked;
    public void showDateDialog(View view){
        picked = (EditText) view;   // Store the dialog to be picked

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, this::onDateSet,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
        // If done picking date
        String date = d + "/" + (m+1) + "/" + y;
        picked.setText(date);
    }

}