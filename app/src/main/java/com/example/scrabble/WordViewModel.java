package com.example.scrabble;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.scrabble.database.AppDatabase;
import com.example.scrabble.database.Word;
import com.example.scrabble.database.WordDao;

import java.util.List;

public class WordViewModel extends AndroidViewModel {
    private final LiveData<List<Word>> allWords;
    private final WordDao wordDao;

    public WordViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getDatabase(application);
        wordDao = database.wordDao();
        allWords = wordDao.getAllWordsLive();
    }

    public LiveData<List<Word>> getAllWords() {
        return allWords;
    }
}