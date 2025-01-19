package com.example.scrabble;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.scrabble.database.AppDatabase;


public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "ScrabbleGamePrefs";
    private static final String KEY_GAME_BOARD = "gameBoard";

    private ImageButton addWordButton;
    private ImageButton showStatsButton;
    private Button continueButton;
    private Button multiplayerButton;

    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        appDatabase = AppDatabase.getDatabase(this);
        initializeDatabase();
        // Инициализация кнопок
        addWordButton = findViewById(R.id.addWord);
        showStatsButton = findViewById(R.id.showStats);
        continueButton = findViewById(R.id.continueButton);
        multiplayerButton = findViewById(R.id.multiplayerButton);

        addWordButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddWordActivity.class);
            startActivity(intent);
        });

        showStatsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StatsActivity.class);
            startActivity(intent);
        });


        continueButton.setOnClickListener(v -> {
            if (canContinueGame()) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("game_mode", 0);
                startActivity(intent);
            }
            else {
                Toast.makeText(this, "Отсутсвуют сохранённые игры", Toast.LENGTH_SHORT).show();
            }
        });

        multiplayerButton.setOnClickListener(v -> startNewGame());

        ImageButton helpButton = findViewById(R.id.helpButton);
        helpButton.setOnClickListener(v -> showHelpDialog());
    }

    private boolean canContinueGame() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return sharedPreferences.getString(KEY_GAME_BOARD, null) != null;
    }

    private void initializeDatabase() {
        new Thread(() -> {
            // Проверяем, пустая ли база данных
            int wordCount = appDatabase.wordDao().getWordCount();
            if (wordCount == 0) {
                // Если база данных пуста, загружаем слова из файла
                WordLoader.loadWordsFromFile(MainActivity.this, appDatabase);
            }
            Log.d("DatabaseInit", "База данных уже заполнена. Количество слов: " + wordCount);
        }).start();
    }

    private void startNewGame() {
        if (canContinueGame()) {
            // Показываем диалог подтверждения
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Начать новую игру?")
                    .setMessage("У вас есть сохранённая игра. Вы уверены, что хотите начать новую? Это удалит текущий прогресс.")
                    .setPositiveButton("Да", (dialog, which) -> {
                        showPlayerNameDialog();
                    })
                    .setNegativeButton("Нет", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            showPlayerNameDialog();
        }
    }

    private void showPlayerNameDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        @SuppressLint("InflateParams")
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_player_names, null);
        builder.setView(dialogView);

        // Найти элементы в разметке
        EditText player1NameInput = dialogView.findViewById(R.id.player1NameInput);
        EditText player2NameInput = dialogView.findViewById(R.id.player2NameInput);

        builder.setPositiveButton("Начать", (dialog, which) -> {
            String player1Name = player1NameInput.getText().toString().trim();
            String player2Name = player2NameInput.getText().toString().trim();

            if (player1Name.isEmpty() || player2Name.isEmpty()) {
                Toast.makeText(MainActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            } else {
                startGame(player1Name, player2Name);
            }
        });

        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void startGame(String player1Name, String player2Name) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra("game_mode", 1);
        intent.putExtra("player1_name", player1Name);
        intent.putExtra("player2_name", player2Name);
        startActivity(intent);
    }

    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams")
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_help, null);
        builder.setView(dialogView);

        builder.setPositiveButton("Закрыть", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}