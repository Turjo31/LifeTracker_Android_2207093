package com.turjo2207093.lifetracker;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView usersRecyclerView;
    private AdminUserAdapter adminUserAdapter;
    private List<User> userList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        adminUserAdapter = new AdminUserAdapter(userList, user -> {
            new AlertDialog.Builder(AdminDashboardActivity.this)
                    .setTitle("Reset Progress")
                    .setMessage("Are you sure you want to reset progress for " + user.getName() + "?")
                    .setPositiveButton("Yes", (dialog, which) -> resetUserProgress(user))
                    .setNegativeButton("No", null)
                    .show();
        });
        usersRecyclerView.setAdapter(adminUserAdapter);

        fetchAllUsers();
    }

    private void fetchAllUsers() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        user.setUid(snapshot.getKey());
                        userList.add(user);
                    }
                }
                adminUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminDashboardActivity.this, "Failed to load users.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetUserProgress(User user) {
        DatabaseReference userRef = mDatabase.child(user.getUid());
        Map<String, Object> updates = new HashMap<>();
        updates.put("level", 1);
        updates.put("exp", 0);
        userRef.updateChildren(updates).addOnSuccessListener(aVoid -> {
            Toast.makeText(AdminDashboardActivity.this, user.getName() + "'s progress has been reset.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(AdminDashboardActivity.this, "Failed to reset progress.", Toast.LENGTH_SHORT).show();
        });
    }
}
