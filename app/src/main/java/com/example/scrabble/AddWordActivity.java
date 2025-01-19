package com.example.scrabble;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.example.scrabble.database.AppDatabase;
import com.example.scrabble.database.Word;
import com.example.scrabble.game.Tile;

public class AddWordActivity extends AppCompatActivity {

    private EditText wordEditText;
    private Button addWordButton;
    private RecyclerView recyclerView;
    private WordAdapter wordAdapter;
    private WordViewModel wordViewModel;

    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        wordEditText = findViewById(R.id.wordEditText);
        addWordButton = findViewById(R.id.addWordButton);

        appDatabase = AppDatabase.getDatabase(this);
        recyclerView = findViewById(R.id.wordsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Инициализация адаптера с пустым списком
        wordAdapter = new WordAdapter(new ArrayList<>());
        recyclerView.setAdapter(wordAdapter);

        // Инициализация ViewModel
        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);

        // Наблюдение за изменениями в базе данных
        wordViewModel.getAllWords().observe(this, words -> {
            wordAdapter.updateWords(words);
        });

        addWordButton.setOnClickListener(v -> {
            String word = wordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(word)) {
                Toast.makeText(AddWordActivity.this, "Введите слово", Toast.LENGTH_SHORT).show();
            } else {
                // Добавление слова в базу данных
                addWordToDatabase(word);
            }
        });
    }

    private void addWordToDatabase(final String word) {
        new Thread(() -> {
            // Приводим слово к нижнему регистру для проверки уникальности
            String wordToCheck = word.toLowerCase();

            // Проверяем, существует ли слово
            int count = appDatabase.wordDao().isWordExists(wordToCheck);

            if (count > 0) {
                // Если слово уже есть, уведомляем пользователя
                runOnUiThread(() -> Toast.makeText(this, "Слово уже существует!", Toast.LENGTH_SHORT).show());
            } else {
                // Если слова нет, добавляем его
                Word newWord = new Word(wordToCheck);
                appDatabase.wordDao().insert(newWord);
                setResult(RESULT_OK);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Слово добавлено!", Toast.LENGTH_SHORT).show();
                    wordEditText.setText("");
                });
            }
        }).start();
    }
}
