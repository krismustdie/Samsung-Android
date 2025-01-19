package com.example.scrabble.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StatDao {
    // Вставить новую игру
    @Insert
    void insert(Stat stat);

    // Получить все игры
    @Query("SELECT * FROM stats")
    List<Stat> getAll();

    // Обновить существующую запись
    @Query("UPDATE stats SET score = :score, totalGames = totalGames + 1 WHERE player = :player")
    void updateStat(String player, int score);

    // Проверить наличие игрока в базе данных
    @Query("SELECT EXISTS(SELECT 1 FROM stats WHERE player = :player)")
    boolean playerExists(String player);

    @Query("SELECT score FROM stats WHERE player = :player")
    int getPlayerScore(String player);

}


