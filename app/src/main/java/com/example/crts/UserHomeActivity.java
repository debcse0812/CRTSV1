package com.example.crts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class UserHomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView text;
    private Button homeButton, plusButton, helpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        // starting the current intent with
        Intent intent = getIntent();
        String phoneNumber = intent.getStringExtra("key"); //phone number.

        // Setting up the side navigation bar:
        drawerLayout = findViewById(R.id.user_home_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        // to make the Navigation drawer icon always appear on the action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        text = findViewById(R.id.text);
        text.setText(phoneNumber);

        homeButton = findViewById(R.id.homeButton);
        plusButton = findViewById(R.id.plusButton);
        helpButton = findViewById(R.id.helpButton);

        plusButton.setOnClickListener(view -> {
            Toast.makeText(UserHomeActivity.this, "Fill the form for a new complaint.", Toast.LENGTH_SHORT).show();

            // going to user home Complaint Form Activity
            Intent myIntent = new Intent(UserHomeActivity.this, ComplaintFormActivity.class);
            myIntent.putExtra("key", phoneNumber);
            UserHomeActivity.this.startActivity(myIntent);
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }
}