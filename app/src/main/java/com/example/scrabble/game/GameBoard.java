package com.example.scrabble.game;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameBoard {

    private List<Cell> board;
    private static final Set<Integer> x3WordBonus = new HashSet<>(Arrays.asList(0, 7, 14, 105, 119, 210, 217, 224));
    private static final Set<Integer> x2WordBonus = new HashSet<>(Arrays.asList(16, 28, 32, 42, 48, 56, 64, 70, 112, 154, 160, 168, 176, 182, 192, 196, 208));
    private static final Set<Integer> x3LetterBonus = new HashSet<>(Arrays.asList(20,24,76,80,84,88,136,140,144,148,200,204));
    private static final Set<Integer> x2LetterBonus = new HashSet<>(Arrays.asList(3,11,36,38,45,52,59,92,96,98,102,108,116,122,126,128,132,165,172,179,186,188,213,221));

    private boolean isFirstTurn;
    private int lastScore;
    private List<Integer> lastBonuses;


    public GameBoard() {
        this.board = new ArrayList<>();
        this.isFirstTurn = true;
        initializeBoard();
    }

    public GameBoard(boolean isFirstTurn, List<Cell> cells) {
        this.board = cells;
        this.isFirstTurn = isFirstTurn;
    }

    public List<Cell> getCells(){
        return board;
    }

    public void setCells(List<Cell> board){
        this.board = board;
    }

    public int getScore(){
        return lastScore;
    }

    public Cell getCell(int position){
        return board.get(position);
    }

    private void initializeBoard() {
        for (int i = 0; i < 15 * 15; i++) {
            board.add(new Cell(calculateBonus(i)));
        }
    }

    public int calculateBonus(int cellNumber) {
        if (x3WordBonus.contains(cellNumber)) return 4;
        if (x2WordBonus.contains(cellNumber)) return 3;
        if (x3LetterBonus.contains(cellNumber)) return 1;
        if (x2LetterBonus.contains(cellNumber)) return 2;
        return 0;
    }

    public void firstTurnFinished(){
        this.isFirstTurn = false;
    }

    public String placeWord(int startX, int startY, int orientation, List<Tile> placedTiles) throws InvalidPlacementException{
        if (orientation == -1){
            throw new InvalidPlacementException("Неверная ориентация слова.");
        }
        StringBuilder word = new StringBuilder();
        boolean isValidPlacement = false;
        int start = startY * 15 + startX;
        int step;
        // Если ориентация не определена, попытка вычислить её
        if (orientation == 2) {
            orientation = determineOrientation(startX, startY, placedTiles);
            isValidPlacement = true;
        }
        step = orientation == 0 ? 15 : 1;
        start = findWordStart(start, step);
        lastScore = 0;
        lastBonuses = new ArrayList<>();
        for (int i = start; i < 15*15; i+=step){
            Cell cell = board.get(i);
            if (cell.getTile() == null){
                break;
            }
            if (isFirstTurn && board.get(112).getTile() == null){
                throw new InvalidPlacementException("Некорректное первое слово.");
            }
            if (isFirstTurn || !placedTiles.contains(cell.getTile())){
                isValidPlacement = true;
            }
            word.append(cell.getLetter());
            lastScore += calculateScore(cell);
        }
        if (!isValidPlacement) {
            throw new InvalidPlacementException("Некорректное размещение слова.");
        }
        if (!lastBonuses.isEmpty()){
            for (int bonus: lastBonuses) {
                lastScore *= bonus;
            }
        }
        return word.toString().toLowerCase().trim();
    }

    private int findWordStart(int start, int step) {
        // Ищем начало слова, перемещаясь в обратную сторону
        while (start >= 0 && board.get(start).getTile() != null) {
            start -= step;
        }
        return start + step; // Возвращаемся к первой занятой ячейке
    }

    private int determineOrientation(int startX, int startY, List<Tile> placedTiles) throws InvalidPlacementException {
        boolean hasVerticalNeighbor = false;
        boolean hasHorizontalNeighbor = false;

        // Проверяем соседей для определения ориентации
        if (startY > 0 && board.get((startY - 1) * 15 + startX).getTile() != null) {
            hasVerticalNeighbor = true;
        }
        if (startY < 14 && board.get((startY + 1) * 15 + startX).getTile() != null) {
            hasVerticalNeighbor = true;
        }
        if (startX > 0 && board.get(startY * 15 + startX - 1).getTile() != null) {
            hasHorizontalNeighbor = true;
        }
        if (startX < 14 && board.get(startY * 15 + startX + 1).getTile() != null) {
            hasHorizontalNeighbor = true;
        }

        if (hasVerticalNeighbor && hasHorizontalNeighbor) {
            throw new InvalidPlacementException("Не удалось определить ориентацию слова.");
        }

        return hasVerticalNeighbor ? 0 : 1;
    }

    private int calculateScore(Cell cell) {
        int score = cell.getTile().getValue();
        switch (cell.getBonusType()){
            case (1):
                return score*3;
            case (2):
                return score*2;
            case (3):
                lastBonuses.add(2);
                return score;
            case (4):
                lastBonuses.add(3);
                return score;
            default:
                return score;
        }
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(isFirstTurn).append("|");
        for (Cell cell : board) {
            builder.append(cell.toString()).append(";");
        }
        return builder.toString();
    }

    public static GameBoard fromString(String data) {
        Log.i("loading board ", data);
        String[] parts = data.split("\\|");
        boolean isFirstTurn = Boolean.parseBoolean(parts[0]);
        List<Cell> loadedBoard = new ArrayList<>();
        String[] cellStrings = parts[1].split(";");
        int counter = 1;
        for (String cellString : cellStrings) {
            loadedBoard.add(Cell.fromString(cellString));
            Log.i("loading board ", String.valueOf(counter));
            counter++;
        }
        GameBoard loadedGame = new GameBoard(isFirstTurn, loadedBoard);
        return loadedGame;
    }
}