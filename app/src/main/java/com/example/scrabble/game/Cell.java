package com.example.scrabble.game;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Cell {
    private Tile tile;  // Буква, размещенная в клетке
    private final int bonus;// Бонус клетки: 0 - нет бонуса, 1 - удвоение буквы, 2 - утроение буквы, 3 - удвоение слова, 4 - утроение слова


    public Cell(int bonusType) {
        this.tile = null;
        this.bonus = bonusType;
    }

    public Tile getTile() {
        return tile;
    }

    public char getLetter() {
        return tile.getLetter();
    }

    public int getBonusType() {
        return bonus;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cell cell = (Cell) obj;
        return Objects.equals(tile, cell.tile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tile);
    }


    @NonNull
    @Override
    public String toString() {
        String tileString = (tile == null) ? "null" : tile.toString();
        return tileString + ":" + bonus;
    }

    public static Cell fromString(String data) {
        String[] parts = data.split(":");
        Tile tile = "null".equals(parts[0]) ? null : Tile.fromString(parts[0]);
        int bonus = Integer.parseInt(parts[1]);
        Cell cell = new Cell(bonus);
        cell.setTile(tile);
        return cell;
    }

}
