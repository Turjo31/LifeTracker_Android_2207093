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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HomePage extends AppCompatActivity {

    private ArrayList<String> habits;
    private HabitAdapter habitsAdapter;
    private RecyclerView habitsRecyclerView;
    private ActivityResultLauncher<Intent> addHabitLauncher;
    private int level = 1;
    private int exp = 0;
    private int expToNextLevel = 100;
    private TextView levelTextView;

    private TextView welcomeText;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
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
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Not logged in, redirect to MainActivity
            startActivity(new Intent(HomePage.this, MainActivity.class));
            finish();
            return;
        }
        userId = currentUser.getUid();

        welcomeText = findViewById(R.id.welcomeText);
        levelTextView = findViewById(R.id.levelTextView);

        fetchUserData();

        ImageButton goToLeaderboard = findViewById(R.id.goToLeaderboard);
        ImageButton goToProfile = findViewById(R.id.goToProfile);

        habitsRecyclerView = findViewById(R.id.habitsRecyclerView);
        habits = new ArrayList<>();
        habits.add("Reading 10 minutes");
        habits.add("Drink Water");

        habitsAdapter = new HabitAdapter(habits, position -> {
            new AlertDialog.Builder(HomePage.this)
                    .setTitle("Mark as complete?")
                    .setPositiveButton("Yes", (dialog, which) -> completeHabit(position))
                    .setNegativeButton("No", null)
                    .show();
        }, position -> {
            new AlertDialog.Builder(HomePage.this)
                    .setTitle("Delete Habit")
                    .setMessage("Would you like to delete this habit?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        habits.remove(position);
                        habitsAdapter.notifyItemRemoved(position);
                    })
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
                            habits.add(newHabit);
                            habitsAdapter.notifyItemInserted(habits.size() - 1);
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
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    level = document.getLong("level").intValue();
                    exp = document.getLong("exp").intValue();
                    expToNextLevel = 100 + (level - 1) * 50; // Recalculate expToNextLevel

                    welcomeText.setText("Welcome Back!");
                    updateLevelText();
                } else {
                    Toast.makeText(this, "No such document", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "get failed with " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void completeHabit(int position) {
        exp += 10;
        if (exp >= expToNextLevel) {
            level++;
            exp = exp - expToNextLevel;
            expToNextLevel += 50;
        }

        DocumentReference docRef = db.collection("users").document(userId);
        docRef.update("level", level, "exp", exp);

        updateLevelText();
        habits.remove(position);
        habitsAdapter.notifyItemRemoved(position);
    }

    private void updateLevelText() {
        levelTextView.setText("Level " + level + "\nEXP: " + exp + " / " + expToNextLevel);
    }
}
