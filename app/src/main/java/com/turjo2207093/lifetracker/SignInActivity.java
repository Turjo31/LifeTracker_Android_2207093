package com.turjo2207093.lifetracker;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {

    private EditText nameInfo, genderInfo, ageInfo, emailInfo, passwordInfo;
    private Button signInButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        nameInfo = findViewById(R.id.nameInfo);
        genderInfo = findViewById(R.id.genderInfo);
        ageInfo = findViewById(R.id.ageInfo);
        emailInfo = findViewById(R.id.emailInfo);
        passwordInfo = findViewById(R.id.passwordInfo);
        signInButton = findViewById(R.id.signInButton);

        signInButton.setOnClickListener(v -> {
            String name = nameInfo.getText().toString().trim();
            String gender = genderInfo.getText().toString().trim();
            String age = ageInfo.getText().toString().trim();
            String email = emailInfo.getText().toString().trim();
            String password = passwordInfo.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(gender) || TextUtils.isEmpty(age) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(SignInActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("name", name);
                                userData.put("gender", gender);
                                userData.put("age", age);
                                userData.put("email", email);
                                userData.put("level", 1);
                                userData.put("exp", 0);

                                db.collection("users").document(userId)
                                        .set(userData)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(SignInActivity.this, "Sign In Successful", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignInActivity.this, HomePage.class);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(SignInActivity.this, "Error saving user data.", Toast.LENGTH_SHORT).show());
                            }
                        } else {
                            Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
