package com.turjo2207093.lifetracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomePage extends AppCompatActivity {

    private LinearLayout habitsContainer;
    private ActivityResultLauncher<Intent> addHabitLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        habitsContainer = findViewById(R.id.habitsContainer);

        addHabitLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String newHabit = result.getData().getStringExtra("newHabit");
                        if (newHabit != null) {
                            addHabitCard(newHabit);
                        }
                    }
                });

        TextView addHabit = findViewById(R.id.addHabit);
        addHabit.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, AddHabitActivity.class);
            addHabitLauncher.launch(intent);
        });

        // Add initial habits
        addHabitCard("Reading 10 minutes");
        addHabitCard("Drink Water");
    }

    private void addHabitCard(String habitName) {
        CardView habitCard = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(42, 0, 42, 21);
        habitCard.setLayoutParams(cardParams);
        habitCard.setCardBackgroundColor(0xFFFFFFFF);
        habitCard.setRadius(21);
        habitCard.setCardElevation(6);

        TextView habitText = new TextView(this);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        habitText.setLayoutParams(textParams);
        habitText.setText(habitName);
        habitText.setTextColor(0xFF517664);
        habitText.setTextSize(20);
        habitText.setPadding(42, 42, 42, 42);
        habitText.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));

        habitCard.addView(habitText);
        setHabitLongClickListener(habitCard);

        habitsContainer.addView(habitCard);
    }

    private void setHabitLongClickListener(final CardView habitCard) {
        habitCard.setOnLongClickListener(v -> {
            new AlertDialog.Builder(HomePage.this)
                    .setTitle("Delete Habit")
                    .setMessage("Would you like to delete this habit?")
                    .setPositiveButton("Yes", (dialog, which) -> habitsContainer.removeView(habitCard))
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });
    }
}
