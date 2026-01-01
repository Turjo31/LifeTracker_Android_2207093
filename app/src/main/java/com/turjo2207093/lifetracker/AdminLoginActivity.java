package com.turjo2207093.lifetracker;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText emailInfo, passwordInfo;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        emailInfo = findViewById(R.id.emailInfo);
        passwordInfo = findViewById(R.id.passwordInfo);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            String email = emailInfo.getText().toString().trim();
            String password = passwordInfo.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(AdminLoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                checkAdminStatus(user.getUid());
                            }
                        } else {
                            Toast.makeText(AdminLoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void checkAdminStatus(String userId) {
        mDatabase.child("users").child(userId).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && "admin".equals(dataSnapshot.getValue(String.class))) {
                    Toast.makeText(AdminLoginActivity.this, "Admin Login Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AdminLoginActivity.this, AdminDashboardActivity.class));
                    finish();
                } else {
                    mAuth.signOut();
                    Toast.makeText(AdminLoginActivity.this, "You are not an admin.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mAuth.signOut();
                Toast.makeText(AdminLoginActivity.this, "Failed to verify admin status.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
