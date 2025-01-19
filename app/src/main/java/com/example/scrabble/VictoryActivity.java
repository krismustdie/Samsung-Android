package com.example.scrabble;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class VictoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victory);

        // Получение данных из Intent
        String winner = getIntent().getStringExtra("winner");
        String scores = getIntent().getStringExtra("scores");

        // Установка текста победителя и результатов
        TextView tvWinner = findViewById(R.id.tvWinner);
        TextView tvScores = findViewById(R.id.tvScores);

        tvWinner.setText(Objects.equals(winner, "Ничья") ? "Ничья!" : "Победитель: " + winner);
        tvScores.setText("Результаты:\n" + scores);

        // Кнопка возврата на главный экран
        Button btnBackToMain = findViewById(R.id.btnBackToMain);
        btnBackToMain.setOnClickListener(v -> {
            Intent intent = new Intent(VictoryActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
