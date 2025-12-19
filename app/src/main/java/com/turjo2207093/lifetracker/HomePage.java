package com.turjo2207093.lifetracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        TextView welcomeText = findViewById(R.id.welcomeText);
        String userName = getIntent().getStringExtra("USER_NAME");
        if (userName != null && !userName.isEmpty()) {
            welcomeText.setText("Welcome Back, " + userName + "!");
        }

        levelTextView = findViewById(R.id.levelTextView);
        updateLevelText();

        ImageButton goToProfile = findViewById(R.id.goToProfile);

        habitsRecyclerView = findViewById(R.id.habitsRecyclerView);
        habits = new ArrayList<>();
        habits.add("Sample Habit");


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
        goToProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, Profile.class);
            startActivity(intent);
        });
    }

    private void completeHabit(int position) {
        exp += 10;
        if (exp >= expToNextLevel) {
            level++;
            exp = exp - expToNextLevel;
            expToNextLevel += 50;
        }
        updateLevelText();
        habits.remove(position);
        habitsAdapter.notifyItemRemoved(position);
    }

    private void updateLevelText() {
        levelTextView.setText("Level " + level + "\nEXP: " + exp + " / " + expToNextLevel);
    }
}
