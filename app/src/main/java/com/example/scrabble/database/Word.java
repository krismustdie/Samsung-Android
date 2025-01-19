package com.example.scrabble.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "words", indices = {@Index(value = "word", unique = true)})  // Указываем имя таблицы
public class Word {

    @PrimaryKey(autoGenerate = true)  // Указываем, что id будет генерироваться автоматически
    private int id;

    @ColumnInfo(name = "word")
    private String word;  // Слово, которое будет храниться в базе данных

    // Конструктор
    public Word(String word) {
        this.word = word;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
