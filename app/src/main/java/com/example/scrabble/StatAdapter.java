package com.example.scrabble;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrabble.database.Stat;

import java.util.List;

public class StatAdapter extends RecyclerView.Adapter<StatAdapter.StatViewHolder> {
    private List<Stat> stats;

    public StatAdapter(List<Stat> stats) {
        this.stats = stats;
    }

    @NonNull
    @Override
    public StatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stat, parent, false);
        return new StatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatViewHolder holder, int position) {
        Stat stat = stats.get(position);
        holder.Name.setText(stat.getPlayer());
        holder.Score.setText(String.valueOf(stat.getScore()));
        holder.Games.setText(String.valueOf(stat.getTotalGames()));
    }

    @Override
    public int getItemCount() {
        return stats.size();
    }

    public static class StatViewHolder extends RecyclerView.ViewHolder {
        TextView Name, Score, Games;

        public StatViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.Name);
            Score = itemView.findViewById(R.id.Score);
            Games = itemView.findViewById(R.id.Games);
        }
    }
}
