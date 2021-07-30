package com.example.crts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserHomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;

    private TextView userPhoneInNavHeader;
    String phoneNumber, userToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        // starting the current intent with the variables
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        phoneNumber = bundle.getString("phone"); //phone number.
        userToken = bundle.getString("userToken"); // userToken

        // initialization:
        drawerLayout = findViewById(R.id.user_home_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);

        // set our custom toolbar:
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // setting user phone number in the side nav bar header
        View headerView = navigationView.getHeaderView(0);
        userPhoneInNavHeader = headerView.findViewById(R.id.phone);
        userPhoneInNavHeader.setText(phoneNumber);

        // Getting the side navigation buttons to provide functionality:
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                setDrawerClick(item.getItemId());
                drawerLayout.closeDrawers();
                return true;
            }
        });
        // Start Home Fragment by default:
        getSupportFragmentManager().beginTransaction().replace(R.id.content, HomeFragment.newInstance(phoneNumber, userToken)).commit();
        // pass the Open and Close toggle for the drawer layout listener to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        // to make the Navigation drawer icon always appear on the action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }

    private void setDrawerClick(int itemId) {
        switch (itemId){
            case R.id.action_home: {
                break;
            }
            case R.id.action_refresh: {
                getSupportFragmentManager().beginTransaction().replace(R.id.content, HomeFragment.newInstance(phoneNumber,userToken)).commit();
                break;
            }
            case R.id.action_exit: {
                Toast.makeText(this, "Goodbye!", Toast.LENGTH_SHORT).show();
                finishAndRemoveTask();
                break;
            }
            case R.id.action_help: {
                onCall(); // it's used to take permission and make a phone call if granted
            }
            case R.id.action_logout: {
                startActivity(new Intent(UserHomeActivity.this, MainActivity.class));
                finish();
                break; // all complaint clicked
            }
            case R.id.action_share: {
                Intent appShareIntent = new Intent(Intent.ACTION_SEND);
                appShareIntent.setType("text/plain");
                String shareBody = "Hey, Try this awesome app to register your complaints and get them resolved faster.\nClick here: https://1fyo.short.gy/Q5p2VE";
                appShareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(appShareIntent, "Share via"));
                break;
            }



        }
    }

    public void onCall() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    Integer.parseInt("123"));
        } else {
            startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:7002457828")));
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case 123:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    onCall();
                } else {
                    Toast.makeText(this, "Grant permission to make a call", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // disable transition when coming back from an activity
        overridePendingTransition(0, 0);
    }

    // Using the side navigation menu:
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }
    private long pressedTime;
    @Override
    public void onBackPressed() {

        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }
}