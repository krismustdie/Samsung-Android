package com.example.scrabble.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Player {
    private String name;
    private List<Tile> rack = new ArrayList<>();
    private int score = 0;

    public void refillRack(TilePile bag) {
        rack.addAll(bag.drawTiles(Math.min(7-rack.size(), bag.getTilesCount())));
    }
    public Player(String name) {
        this.name = name;
        this.score = 0;
    }

    public void changeTilesInRack(List<Tile> changeTiles, TilePile bag) {
        rack.removeAll(changeTiles);
        rack.addAll(bag.changeTiles(changeTiles));
    }

    public void addScore(int points) {
        score += points;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<Tile> getRack() {
        return rack;
    }

    public void setRack(List<Tile> rack) {
        this.rack = rack;
    }

    public int getTileCount(){
        return rack.size();
    }

    public int removeTile(Tile draggedTile) {
        int i = rack.indexOf(draggedTile);
        rack.remove(draggedTile);
        return i;
    }
    public void addTiles(List<Tile> tiles) {
        rack.addAll(tiles);
    }
    public void addTile(Tile tiles) {
        rack.add(tiles);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        String rackString = rack.stream()
                .map(Tile::toString)
                .collect(Collectors.joining(","));
        return name + ";" + score + ";" + rackString;
    }

    public static Player fromString(String data) {
        String[] parts = data.split(";");
        String name = parts[0];
        int score = Integer.parseInt(parts[1]);
        List<Tile> rack = new ArrayList<>();
        if (parts.length > 2 && !parts[2].isEmpty()) {
            String[] tileStrings = parts[2].split(",");
            for (String tileString : tileStrings) {
                rack.add(Tile.fromString(tileString));
            }
        }
        Player player = new Player(name);
        player.setScore(score);
        player.setRack(rack);
        return player;
    }
}
