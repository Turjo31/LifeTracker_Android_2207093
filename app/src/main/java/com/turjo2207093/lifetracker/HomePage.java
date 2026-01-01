package com.turjo2207093.lifetracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomePage extends AppCompatActivity {

    private ArrayList<String> habitNames;
    private ArrayList<String> habitKeys;
    private HabitAdapter habitsAdapter;
    private RecyclerView habitsRecyclerView;
    private ActivityResultLauncher<Intent> addHabitLauncher;
    private int level = 1;
    private int exp = 0;
    private int expToNextLevel = 100;
    private TextView levelTextView;

    private TextView welcomeText;

    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(HomePage.this, MainActivity.class));
            finish();
            return;
        }
        userId = currentUser.getUid();
        userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        welcomeText = findViewById(R.id.welcomeText);
        levelTextView = findViewById(R.id.levelTextView);

        fetchUserData();

        ImageButton goToLeaderboard = findViewById(R.id.goToLeaderboard);
        ImageButton goToProfile = findViewById(R.id.goToProfile);

        habitsRecyclerView = findViewById(R.id.habitsRecyclerView);
        habitNames = new ArrayList<>();
        habitKeys = new ArrayList<>();
        habitsAdapter = new HabitAdapter(habitNames, position -> {
            new AlertDialog.Builder(HomePage.this)
                    .setTitle("Mark as complete?")
                    .setPositiveButton("Yes", (dialog, which) -> completeHabit(position))
                    .setNegativeButton("No", null)
                    .show();
        }, position -> {
            new AlertDialog.Builder(HomePage.this)
                    .setTitle("Delete Habit")
                    .setMessage("Would you like to delete this habit?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteHabit(position))
                    .setNegativeButton("No", null)
                    .show();
        });

        habitsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        habitsRecyclerView.setAdapter(habitsAdapter);

        addHabitLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String newHabit = result.getData().getStringExtra("newHabit");
                        if (newHabit != null) {
                            addNewHabit(newHabit);
                        }
                    }
                });

        TextView addHabit = findViewById(R.id.addHabit);
        addHabit.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, AddHabitActivity.class);
            addHabitLauncher.launch(intent);
        });

        goToLeaderboard.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, LeaderboardActivity.class);
            startActivity(intent);
        });

        goToProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, ViewProfileActivity.class);
            startActivity(intent);
        });
    }

    private void fetchUserData() {
        userDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        level = user.getLevel();
                        exp = user.getExp();
                        expToNextLevel = 100 + (level - 1) * 50;

                        welcomeText.setText("Welcome Back!");
                        updateLevelText();

                        habitNames.clear();
                        habitKeys.clear();
                        if (user.getHabits() != null) {
                            for (Map.Entry<String, String> entry : user.getHabits().entrySet()) {
                                habitKeys.add(entry.getKey());
                                habitNames.add(entry.getValue());
                            }
                        }
                        habitsAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(HomePage.this, "User data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomePage.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addNewHabit(String habitName) {
        userDatabaseRef.child("habits").push().setValue(habitName);
    }

    private void deleteHabit(int position) {
        String habitKey = habitKeys.get(position);
        userDatabaseRef.child("habits").child(habitKey).removeValue();
    }

    private void completeHabit(int position) {
        exp += 10;
        if (exp >= expToNextLevel) {
            level++;
            exp = exp - expToNextLevel;
            expToNextLevel += 50;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("level", level);
        updates.put("exp", exp);
        userDatabaseRef.updateChildren(updates);

        deleteHabit(position);
    }

    private void updateLevelText() {
        levelTextView.setText("Level " + level + "\nEXP: " + exp + " / " + expToNextLevel);
    }
}
