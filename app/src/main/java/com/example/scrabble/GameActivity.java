package com.example.scrabble;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrabble.database.AppDatabase;
import com.example.scrabble.database.Stat;
import com.example.scrabble.game.BoardAdapter;
import com.example.scrabble.game.Cell;
import com.example.scrabble.game.GameBoard;
import com.example.scrabble.game.InvalidPlacementException;
import com.example.scrabble.game.Player;
import com.example.scrabble.game.Tile;
import com.example.scrabble.game.TilePile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class GameActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ScrabbleGamePrefs";
    private static final String KEY_PLAYER_TURN = "playerTurn";
    private static final String KEY_PLAYERS = "players";
    private static final String KEY_TILE_PILE = "tilePile";
    private static final String KEY_PLACED_TILES = "placedTiles";
    private static final String KEY_GAME_BOARD = "gameBoard";
    private static final String KEY_GAME_MODE = "gameMode";

    private TextView tilePileCount;
    private TextView turnInfo;
    private TextView[] playersName = new TextView[2];
    private TextView[] playersScore = new TextView[2];

    private RecyclerView boardRecyclerView;
    private RecyclerView tileRecyclerView;
    private BoardAdapter boardAdapter;
    private Button endTurnButton;
    private Button cancelTurnButton;
    private Button replaceTilesButton;

    private TilePile tilePile;
    private TileAdapter tileAdapter;
    private Tile selectedTile;
    private List<Tile> selectedTiles;
    private List<Tile> placedTiles;

    int gameMode;

    private AppDatabase appDatabase;
    private GameBoard gameBoard;
    private int startX, startY, finishX, finishY;
    private int wordOrientation;

    private final Player[] players = new Player[2];
    private int playerTurn = 0;
    private final int[] skippedTurns = new int[]{0, 0};

    private boolean isSwapping = false;
    private boolean isFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameMode = getIntent().getIntExtra("game_mode", 0);
        appDatabase = AppDatabase.getDatabase(this);
        Log.i("loading ", "1");
        tileRecyclerView = findViewById(R.id.tileRack);
        tileRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        boardRecyclerView = findViewById(R.id.gameBoard);
        boardRecyclerView.setLayoutManager(new GridLayoutManager(this, 15)); // 15x15
        playersName[0] = findViewById(R.id.player1Name);
        playersName[1] = findViewById(R.id.player2Name);
        playersScore[0] = findViewById(R.id.player1Score);
        playersScore[1] = findViewById(R.id.player2Score);
        cancelTurnButton = findViewById(R.id.cancelTurnButton);
        cancelTurnButton.setOnClickListener(v -> cancelTurn());

        ImageButton helpButton = findViewById(R.id.helpButton);
        helpButton.setOnClickListener(v -> showHelpDialog());

        ImageButton btnBackToMain = findViewById(R.id.backButton);
        btnBackToMain.setOnClickListener(v -> finish());

        endTurnButton = findViewById(R.id.endTurnButton);
        endTurnButton.setOnClickListener(v -> finishTurn());

        replaceTilesButton = findViewById(R.id.replaceTilesButton);
        replaceTilesButton.setOnClickListener(v -> changeTiles());

        tilePileCount = findViewById(R.id.tilePileCount);
        placedTiles = new ArrayList<>();
        turnInfo = findViewById(R.id.turnInfo);
        Log.i("loading ", "3");


        if (gameMode == 0) {
            restoreGameState();
        } else {
            startNewGame();
        }

        for (int i = 0; i < 2; i++) {
            playersName[i].setText(players[i].getName());
            playersScore[i].setText(String.format("%d", players[i].getScore()));
        }
        updateTilePileCount();
        setDefaultWordPosition();
        updatePlayerInfo();
    }

    @SuppressLint("DefaultLocale")
    private void startNewGame() {
        // Инициализация доски и фишек
        tilePile = new TilePile();
        gameBoard = new GameBoard();
        boardAdapter = new BoardAdapter(gameBoard.getCells(), this, this::onCellClicked);
        boardRecyclerView.setAdapter(boardAdapter);
        String[] playerNames = new String[]{getIntent().getStringExtra("player1_name"),
            getIntent().getStringExtra("player2_name")};

        for (int i = 0; i < 2; i++) {
            players[i] = new Player((i == 1 && gameMode == 2) ? "компьютер" : playerNames[i]);
            players[i].refillRack(tilePile);
        }
    }

    public void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams")
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_help, null);
        builder.setView(dialogView);

        builder.setPositiveButton("Закрыть", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updatePlayerInfo() {
        String turn = "Ходит " + players[playerTurn].getName();
        turnInfo.setText(turn);
        for (int i = 0; i < 2; i++) {
            playersScore[i].setText(String.format("%d", players[i].getScore()));
        }
        tileAdapter = new TileAdapter(players[playerTurn].getRack(), this, this::onTileClicked);
        tileRecyclerView.setAdapter(tileAdapter);
    }

    private void setDefaultWordPosition() {
        startX = 15;
        startY = 15;
        finishX = 0;
        finishY = 0;
    }

    private void updateTilePileCount() {
        int count = tilePile.getTilesCount();
        tilePileCount.setText(String.valueOf(count));
    }

    private void changeTiles() {
        if (isSwapping) {
//           showToast(String.valueOf(selectedTiles.size()));
            // Если уже в режиме замены, показать диалог подтверждения
            if (selectedTiles.isEmpty()) {
                // Если не выбраны фишки, просто выйти из режима замены
                isSwapping = false;
                String turn = "Ходит " + players[playerTurn].getName();
                turnInfo.setText(turn);
                return;
            }

            // Показать диалог подтверждения
            new AlertDialog.Builder(this)
                    .setTitle("Подтверждение замены")
                    .setMessage("Вы уверены, что хотите заменить выбранные фишки?")
                    .setPositiveButton("Да", (dialog, which) -> {
                        // Выполнить замену фишек
                        players[playerTurn].changeTilesInRack(selectedTiles, tilePile);
                        // Очистить выбор и выйти из режима замены
                        endSwapping();
                        switchToNextPlayer();
                    })
                    .setNegativeButton("Отмена", (dialog, which) -> {
                        String turn = "Ходит " + players[playerTurn].getName();
                        turnInfo.setText(turn);
                        // Отмена замены, выйти из режима замены
                        endSwapping();
                    })
                    .setOnCancelListener(dialog -> {
                        // Закрытие диалога — продолжить выбор фишек
                    })
                    .show();
        } else {
            // Вход в режим замены
            turnInfo.setText("Выберите фишки");
            cancelTurn();
            selectedTile = null;
            selectedTiles = new ArrayList<>();
            tileAdapter.notifyDataSetChanged();
            isSwapping = true;
        }
    }

    private void endSwapping() {
        selectedTiles.clear();
        tileAdapter.setSelectedTile(selectedTile);
        isSwapping = false;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void finishTurn() {
        if (placedTiles.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Пропуск хода")
                    .setMessage("Вы не разместили ни одной фишки. Пропустить ход?")
                    .setPositiveButton("Да", (dialog, which) -> {
                        skippedTurns[playerTurn]++;
                        switchToNextPlayer();
                    })
                    .setNegativeButton("Нет", null)
                    .show();
        } else {
            try {
//                Toast.makeText(this, startX + " " + startY + " " +finishX+" " + finishY+ " "+ wordOrientation, Toast.LENGTH_LONG).show();
                String placedWord = gameBoard.placeWord(startX, startY, wordOrientation, placedTiles);
//                Toast.makeText(this,placedWord,Toast.LENGTH_LONG).show();
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.submit(() -> {
                    // Выполняем запрос в базе данных в фоновом потоке
                    boolean wordExists = appDatabase.wordDao().isWordExists(placedWord) > 0;

                    runOnUiThread(() -> {
                        if (!wordExists) {
                            showToast("Ошибка: слово не существует!");
                        } else {
                            skippedTurns[playerTurn] = 0;
                            Log.i("score", Integer.toString(gameBoard.getScore()));
                            players[playerTurn].addScore(gameBoard.getScore());
                            players[playerTurn].refillRack(tilePile);
                            switchToNextPlayer();
                            updatePlayerInfo();
                            placedTiles.clear();
                            gameBoard.firstTurnFinished();
                            updateTilePileCount();
                            tileAdapter.notifyDataSetChanged();
                        }
                    });
                });
                executorService.shutdown();
            } catch (InvalidPlacementException e) {
                showToast("Ошибка: " + e.getMessage());
            } catch (RuntimeException e) {
                showToast("Неизвестная ошибка: " + e.getMessage());
            }
        }
    }

    private void checkGameEnd() {
        boolean isRackEmpty = players[playerTurn].getRack().isEmpty() && tilePile.getTilesCount() == 0;
        boolean isSkipped = skippedTurns[0] == 2 && skippedTurns[1] == 2;
        if (isRackEmpty || isSkipped) {

            isFinished = true;
            recordStats();
            String winner;
            if (players[0].getScore() == players[1].getScore()) {
                winner = "Ничья";
            } else if (players[0].getScore() > players[1].getScore()) {
                winner = players[0].getName();
            } else {
                winner = players[1].getName();
            }

            StringBuilder scores = new StringBuilder();
            for (Player player: players) {
                scores.append(player.getName()).append(": ").
                        append(player.getScore()).append("\n");
            }
            // Переход на экран победы
            Intent intent = new Intent(GameActivity.this, VictoryActivity.class);
            intent.putExtra("winner", winner);
            intent.putExtra("scores", scores.toString());
            startActivity(intent);
            finish();
        }
    }

    private void recordStats(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            for (Player player: players) {
                String playerName = player.getName();
                int finalScore = player.getScore();

                if (appDatabase.statDao().playerExists(playerName)) {
                    // Обновить запись игрока
                    int existingScore = appDatabase.statDao().getPlayerScore(playerName);
                    appDatabase.statDao().updateStat(playerName, Math.max(finalScore, existingScore));
                } else {
                    // Добавить новую запись
                    Stat newStat = new Stat(playerName, finalScore);
                    newStat.setTotalGames(1);
                    appDatabase.statDao().insert(newStat);
                }
            }
        });

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void switchToNextPlayer() {
        checkGameEnd();
        setDefaultWordPosition();
        playerTurn = (playerTurn + 1) % 2;
        updatePlayerInfo();
    }

    private void onTileClicked(Tile tile) {
        if (!isSwapping) {
            selectedTile = tile;
            tileAdapter.setSelectedTile(tile);
            return;
        }
        if (selectedTiles.contains(tile)){
            selectedTiles.remove(tile);
        }
        else selectedTiles.add(tile);
        tileAdapter.addSelectedTiles(tile);
    }

    private void onCellClicked(int position) {
        if (selectedTile != null) {
            Cell targetCell = gameBoard.getCell(position);

            if (targetCell.getTile() == null) { // Если в ячейку можно поставить элемент
                targetCell.setTile(selectedTile);
                updateWordPosition(position % 15, position / 15);
                placedTiles.add(selectedTile); // Сохраняем фишку как размещенную
                players[playerTurn].removeTile(selectedTile); // Удаляем фишку из подставки игрока
                selectedTile = null;
                tileAdapter.setSelectedTile(null);
                boardAdapter.notifyItemChanged(position);
            }
        }
    }

    private void updateWordPosition(int x, int y) {
        Log.d("word placement", startX + " " + startY + " " +finishX+" " + finishY+ " "+ wordOrientation);
        startX = Math.min(startX, x);
        startY = Math.min(startY, y);
        finishX = Math.max(finishX, x);
        finishY = Math.max(finishY, y);
        // 0 - horizontal, 1 - vertical, 2 - one letter, -1 - incorrect
        if (startY != finishY && startX == finishX) {
            wordOrientation = 0;
        } else if (startY == finishY && startX != finishX) {
            wordOrientation = 1;
        } else if (startY == finishY) {
            wordOrientation = 2;
        }
        else wordOrientation = -1;
        Log.d("word placement", startX + " " + startY + " " +finishX+" " + finishY+ " "+ wordOrientation);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void cancelTurn() {
        players[playerTurn].addTiles(placedTiles);

        // Убираем фишки с поля
        for (Cell cell : gameBoard.getCells()) {
            if (placedTiles.contains(cell.getTile())) {
                cell.setTile(null); // Очищаем ячейку от фишки
            }
        }
        setDefaultWordPosition();

        placedTiles.clear(); // Очищаем список размещенных фишек
        tileAdapter.notifyDataSetChanged();
        boardAdapter.notifyDataSetChanged(); // Обновляем подставку игрока
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinished){
            clearSavedGame();
        }
        else {
            cancelTurn();
            saveGameState();
        }
    }

    private void saveGameState() {
        Toast.makeText(this, "Игра сохранена", Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_PLAYER_TURN, playerTurn);

        String playersData = Arrays.stream(players)
                .map(Player::toString)
                .collect(Collectors.joining("|"));
        editor.putString(KEY_PLAYERS, playersData);

        editor.putString(KEY_TILE_PILE, tilePile.toString());

        editor.putString(KEY_GAME_BOARD, gameBoard.toString());
        editor.putInt(KEY_GAME_MODE, gameMode);

        StringBuilder placedTilesData = new StringBuilder();
        for (Tile tile : placedTiles) {
            placedTilesData.append(tile.toString()).append("|");
        }
        Log.i("saved tiles ", placedTilesData.toString());
        editor.putString(KEY_PLACED_TILES, placedTilesData.toString());
        editor.apply();
    }

    private void restoreGameState() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Восстановление текущего игрока
        playerTurn = sharedPreferences.getInt(KEY_PLAYER_TURN, 0);

        // Восстановление игроков
        String playersData = sharedPreferences.getString(KEY_PLAYERS, null);
        if (playersData != null) {
            String[] playerEntries = playersData.split("\\|");
            for (int i = 0; i < 2; i++) {
                players[i] = Player.fromString(playerEntries[i]);
            }
        }

        // Восстановление состояния мешка с фишками
        String tilePileData = sharedPreferences.getString(KEY_TILE_PILE, null);
        if (tilePileData != null) {
            tilePile = TilePile.fromString(tilePileData);
        }
        Log.i("loading pile ", String.valueOf(tilePile));

        // Восстановление состояния доски
        String gameBoardData = sharedPreferences.getString(KEY_GAME_BOARD, null);
        if (gameBoardData != null) {
            gameBoard = GameBoard.fromString(gameBoardData);
            Log.i("loaded board  ", "S");
            boardAdapter = new BoardAdapter(gameBoard.getCells(), this, this::onCellClicked);;
            boardRecyclerView.setAdapter(boardAdapter);
        }
        Log.i("loading board ", String.valueOf(gameBoard));

        // Восстановление размещённых фишек
        String placedTilesData = sharedPreferences.getString(KEY_PLACED_TILES, "");
        Log.i("load tiles", placedTilesData);
        if (!placedTilesData.isEmpty()) {
            String[] placedTilesEntries = placedTilesData.split("\\|");
            for (String placedTilesEntry : placedTilesEntries) {
                placedTiles.add(Tile.fromString(placedTilesEntry));
            }
        }

        gameMode = sharedPreferences.getInt(KEY_GAME_MODE, 0);

        updatePlayerInfo();
        updateTilePileCount();
    }

    private void clearSavedGame() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();
        showToast("Сохранение удалено");
    }
}