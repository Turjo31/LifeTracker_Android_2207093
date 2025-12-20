package com.turjo2207093.lifetracker;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String name = intent.getStringExtra("USER_NAME");
        String gender = intent.getStringExtra("USER_GENDER");
        String age = intent.getStringExtra("USER_AGE");
        String email = intent.getStringExtra("USER_EMAIL");

        TextView nameTextView = findViewById(R.id.nameTextView);
        TextView genderTextView = findViewById(R.id.genderTextView);
        TextView ageTextView = findViewById(R.id.ageTextView);
        TextView emailTextView = findViewById(R.id.emailTextView);

        nameTextView.setText("Name: " + (name != null ? name : ""));
        genderTextView.setText("Gender: " + (gender != null ? gender : ""));
        ageTextView.setText("Age: " + (age != null ? age : ""));
        emailTextView.setText("Email: " + (email != null ? email : ""));
    }
}