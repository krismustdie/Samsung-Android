package com.example.scrabble;

import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrabble.game.Tile;

import java.util.ArrayList;
import java.util.List;

public class TileAdapter extends RecyclerView.Adapter<TileAdapter.ViewHolder> {

    private final List<Tile> tiles;
    private final Context context;
    private final OnTileClickListener tileClickListener;
    private List<Tile> selectedTiles = new ArrayList<>();

    public interface OnTileClickListener {
        void onTileClicked(Tile tile);
    }

    public TileAdapter(List<Tile> tiles, Context context, OnTileClickListener tileClickListener) {
        this.tiles = tiles;
        this.context = context;
        this.tileClickListener = tileClickListener;
    }

    public void setSelectedTile(Tile tiles) {
        this.selectedTiles.clear();
        this.selectedTiles.add(tiles);
        notifyDataSetChanged();
    }

    public void addSelectedTiles(Tile tile) {
        if (this.selectedTiles.contains(tile)){
            this.selectedTiles.remove(tile);
        }
        else this.selectedTiles.add(tile);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tile_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tile tile = tiles.get(position);

        holder.tileLetter.setText(String.valueOf(tile.getLetter()));
        holder.tilePoints.setText(String.valueOf(tile.getValue()));

        // Выделение выбранной фишки
//        if (selectedTiles.contains(tile)) {
//            holder.itemView.setBackgroundColor(Color.YELLOW);
//        } else {
//            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
//        }
        if (selectedTiles.contains(tile)) {
            holder.itemView.setForeground(
                    ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.tile_overlay)
            );
        } else {
            holder.itemView.setForeground(null); // Сбрасываем наложение
        }


        holder.itemView.setOnClickListener(v -> tileClickListener.onTileClicked(tile));
    }

    @Override
    public int getItemCount() {
        return tiles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tileLetter, tilePoints;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tileLetter = itemView.findViewById(R.id.tileLetter);
            tilePoints = itemView.findViewById(R.id.tilePoints);
        }
    }
}