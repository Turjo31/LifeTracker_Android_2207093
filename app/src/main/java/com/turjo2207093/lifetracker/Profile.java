package com.turjo2207093.lifetracker;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Profile extends AppCompatActivity {

    private EditText nameInfo;
    private Button saveProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        nameInfo = findViewById(R.id.nameInfo);
        saveProfile = findViewById(R.id.saveProfile);

        saveProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, HomePage.class);
            intent.putExtra("USER_NAME", nameInfo.getText().toString());
            startActivity(intent);
        });
    }
}