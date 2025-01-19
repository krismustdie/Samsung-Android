package com.example.scrabble.game;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrabble.R;

import java.util.List;
import java.util.Vector;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.ViewHolder> {

    private final List<Cell> boardCells;
    private final Context context;
    private final OnCellClickListener cellClickListener;

    public interface OnCellClickListener {
        void onCellClicked(int position);
    }

    public BoardAdapter(List<Cell> boardCells, Context context, OnCellClickListener cellClickListener) {
        this.boardCells = boardCells;
        this.context = context;
        this.cellClickListener = cellClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cell_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cell cell = boardCells.get(position);

        int backgroundColorResId;
        switch (cell.getBonusType()) {
            case 1: // Удвоение буквы
                backgroundColorResId = R.color.bonus_1;
                break;
            case 2: // Утроение буквы
                backgroundColorResId = R.color.bonus_2;
                break;
            case 3: // Удвоение слова
                backgroundColorResId = R.color.bonus_3;
                break;
            case 4: // Утроение слова
                backgroundColorResId = R.color.bonus_4;
                break;
            default: // Без бонуса
                backgroundColorResId = R.color.bonus_0;
                break;
        }

        holder.itemView.setBackgroundColor(holder.itemView.getContext().getColor(backgroundColorResId));

        // Проверяем, есть ли фишка в ячейке
        if (cell.getTile() != null) {
            Tile tile = cell.getTile();
            holder.tileContainer.setVisibility(View.VISIBLE);
            holder.tileContainer.removeAllViews();

            // Создаем представление фишки
            View tileView = LayoutInflater.from(context).inflate(R.layout.tile_item, holder.tileContainer, false);

            // Устанавливаем букву и очки фишки
            TextView tileLetter = tileView.findViewById(R.id.tileLetter);
            TextView tilePoints = tileView.findViewById(R.id.tilePoints);

            tileLetter.setText(String.valueOf(tile.getLetter()));
            tilePoints.setText(String.valueOf(tile.getValue()));

            holder.tileContainer.addView(tileView);
        } else {
            // Если ячейка пустая
            holder.tileContainer.setVisibility(View.GONE);
        }

        // Устанавливаем слушатель кликов
        holder.itemView.setOnClickListener(v -> cellClickListener.onCellClicked(position));
    }

    @Override
    public int getItemCount() {
        return boardCells.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewGroup tileContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tileContainer = itemView.findViewById(R.id.tileContainer);
        }
    }
}
