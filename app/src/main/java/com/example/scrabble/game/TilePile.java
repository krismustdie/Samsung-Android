package com.example.scrabble.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TilePile {
    private List<Tile> tiles = new ArrayList<>();
    private final Random random = new Random();

    public TilePile() {
        initializeBag();
    }

    private void initializeBag() {
        addTiles('О', 10, 1);
        addTiles('А', 8, 1);
        addTiles('Е', 8, 1);
        addTiles('И', 5, 1);
        addTiles('Н', 5, 1);
        addTiles('Р', 5, 1);
        addTiles('С', 5, 1);
        addTiles('Т', 5, 1);
        addTiles('В', 4, 1);
        addTiles('Д', 4, 2);
        addTiles('К', 4, 2);
        addTiles('Л', 4, 2);
        addTiles('П', 4, 2);
        addTiles('У', 4, 2);
        addTiles('М', 3, 2);
        addTiles('Б', 2, 3);
        addTiles('Г', 2, 3);
        addTiles('Ь', 2, 3);
        addTiles('Я', 2, 3);
        addTiles('Ё', 1, 3);
        addTiles('Ы', 2, 4);
        addTiles('Й', 1, 4);
        addTiles('З', 2, 5);
        addTiles('Ж', 1, 5);
        addTiles('Х', 1, 5);
        addTiles('Ц', 1, 5);
        addTiles('Ч', 1, 5);
        addTiles('Ш', 1, 8);
        addTiles('Э', 1, 8);
        addTiles('Ю', 1, 8);
        addTiles('Ф', 1, 10);
        addTiles('Щ', 1, 10);
        addTiles('Ъ', 1, 10);
    }

    private void addTiles(char letter, int count, int value) {
        for (int i = 0; i < count; i++) {
            tiles.add(new Tile(letter, value));
        }
    }

    public List<Tile> changeTiles(List<Tile> incomingTiles) {
        List<Tile> drawnTiles = new ArrayList<>();
        for (int i = 0; i < incomingTiles.size() && tiles.size() >= 7; i++) {
            drawnTiles.add(tiles.remove(random.nextInt(tiles.size())));
        }
        tiles.addAll(incomingTiles);
        return drawnTiles;
    }
    public List<Tile> drawTiles(int count) {
        List<Tile> drawnTiles = new ArrayList<>();
        for (int i = 0; i < count && tiles.size() >= 7; i++) {
            drawnTiles.add(tiles.remove(random.nextInt(tiles.size())));
        }
        return drawnTiles;
    }

    public int getTilesCount(){
        return tiles.size();
    }

    @Override
    public String toString() {
        return tiles.stream()
                .map(Tile::toString)
                .collect(Collectors.joining(","));
    }

    public static TilePile fromString(String data) {
        TilePile tilePile = new TilePile();
        tilePile.tiles = new ArrayList<>();
        if (!data.isEmpty()) {
            String[] tileStrings = data.split(",");
            for (String tileString : tileStrings) {
                tilePile.tiles.add(Tile.fromString(tileString));
            }
        }
        return tilePile;
    }
}
