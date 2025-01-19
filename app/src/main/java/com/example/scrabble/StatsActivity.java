package com.example.scrabble;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrabble.database.Stat;
import com.example.scrabble.database.StatDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.scrabble.database.AppDatabase;

public class StatsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private StatAdapter adapter;
    private List<Stat> stats = new ArrayList<>();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StatAdapter(stats);
        recyclerView.setAdapter(adapter);

        loadStatistics();
    }

    private void loadStatistics() {
        executorService.execute(() -> {
            StatDao statDao = AppDatabase.getDatabase(this).statDao();
            List<Stat> loadedStats = statDao.getAll();

            runOnUiThread(() -> {
                stats.clear();
                stats.addAll(loadedStats);
                adapter.notifyDataSetChanged();
            });
        });
    }
}

