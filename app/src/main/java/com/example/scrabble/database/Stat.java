package com.example.scrabble.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "stats", indices = {@Index(value = "player", unique = true)})  // Указываем имя таблицы
public class Stat {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "player")
    private String player;

    @ColumnInfo(name = "score")
    private int score;

    @ColumnInfo(name = "totalGames")
    private int totalGames;

    // Конструктор
    public Stat(String player, int score) {
        this.player = player;
        this.score = score;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


    public int getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(int totalGames) {
        this.totalGames = totalGames;
    }


}
