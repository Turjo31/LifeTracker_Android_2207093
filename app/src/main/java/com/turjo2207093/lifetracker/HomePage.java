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
        ImageButton goToProfile = findViewById(R.id.goToProfile);

        habitsRecyclerView = findViewById(R.id.habitsRecyclerView);
        habits = new ArrayList<>();
        habits.add("Reading 10 minutes");
        habits.add("Drink Water");

        habitsAdapter = new HabitAdapter(habits, new HabitAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                new AlertDialog.Builder(HomePage.this)
                        .setTitle("Delete Habit")
                        .setMessage("Would you like to delete this habit?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                habits.remove(position);
                                habitsAdapter.notifyItemRemoved(position);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
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
}
