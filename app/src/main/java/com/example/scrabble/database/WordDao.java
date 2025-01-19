package com.example.scrabble.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.lifecycle.LiveData;
import java.util.List;

@Dao
public interface WordDao {

    // Вставить новое слово
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Word word);

    // Получить все слова
    @Query("SELECT * FROM words")
    List<Word> getAllWords();

    @Query("SELECT COUNT(*) FROM words WHERE word = :word")
    int isWordExists(String word);

    @Query("SELECT COUNT(*) FROM words")
    int getWordCount();

    @Query("SELECT * FROM words ORDER BY word ASC")
    LiveData<List<Word>> getAllWordsLive();
}