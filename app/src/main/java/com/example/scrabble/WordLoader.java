package com.example.scrabble;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.example.scrabble.database.AppDatabase;
import com.example.scrabble.database.Word;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WordLoader {

    public static void loadWordsFromFile(Context context, AppDatabase appDatabase) {
        AssetManager assetManager = context.getAssets();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open("words.txt")));
            String line;
            while ((line = reader.readLine()) != null) {
                Word word = new Word(line.trim().toLowerCase());
                appDatabase.wordDao().insert(word);
                Log.d("WordLoader", "Добавлено слово: " + line);
            }
            reader.close();
        } catch (IOException e) {
            Log.e("WordLoader", "Ошибка при чтении файла", e);
        }
    }
}

