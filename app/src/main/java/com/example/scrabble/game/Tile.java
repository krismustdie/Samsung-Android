package com.example.scrabble.game;

import java.util.UUID;

public class Tile {
    private final char letter;
    private final int points;
    private final String id;

    public Tile(char letter, int points) {
        this.letter = letter;
        this.points = points;
        this.id = UUID.randomUUID().toString();
    }

    public Tile(char letter, int points, String id) {
        this.letter = letter;
        this.points = points;
        this.id = id;
    }

    public char getLetter() {
        return letter;
    }

    public int getValue() {
        return points;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tile tile = (Tile) obj;
        return id.equals(tile.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return letter + "!" + points + "!" + id;
    }

    public static Tile fromString(String data) {
        String[] parts = data.split("!");
        char letter = parts[0].charAt(0);
        int score = Integer.parseInt(parts[1]);
        String id = parts[2];
        return new Tile(letter, score, id);
    }
}
