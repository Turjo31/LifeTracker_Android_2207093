package com.turjo2207093.lifetracker;

import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DiaryActivity extends AppCompatActivity {

    private EditText diaryEditText;
    private TextView wordCountTextView;
    private Button saveDiaryButton;
    private ColorStateList defaultTextColor;

    private DatabaseReference diaryRef;
    private final int MAX_WORDS = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        diaryEditText = findViewById(R.id.diaryEditText);
        wordCountTextView = findViewById(R.id.wordCountTextView);
        saveDiaryButton = findViewById(R.id.saveDiaryButton);
        defaultTextColor = wordCountTextView.getTextColors();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            diaryRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid()).child("diaryEntry");
            loadDiaryEntry();
        }

        diaryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString().trim();
                int wordCount = text.isEmpty() ? 0 : text.split("\\s+").length;
                wordCountTextView.setText(wordCount + "/" + MAX_WORDS + " words");

                if (wordCount > MAX_WORDS) {
                    saveDiaryButton.setEnabled(false);
                    wordCountTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    saveDiaryButton.setEnabled(true);
                    wordCountTextView.setTextColor(defaultTextColor);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        saveDiaryButton.setOnClickListener(v -> saveDiaryEntry());
    }

    private void loadDiaryEntry() {
        diaryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String diaryText = dataSnapshot.getValue(String.class);
                    diaryEditText.setText(diaryText);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DiaryActivity.this, "Failed to load diary.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveDiaryEntry() {
        String diaryText = diaryEditText.getText().toString().trim();
        int wordCount = diaryText.isEmpty() ? 0 : diaryText.split("\\s+").length;

        if (wordCount > MAX_WORDS) {
            Toast.makeText(this, "Your diary entry exceeds the 200-word limit.", Toast.LENGTH_SHORT).show();
            return;
        }

        diaryRef.setValue(diaryText).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(DiaryActivity.this, "Diary saved!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(DiaryActivity.this, "Failed to save diary.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
