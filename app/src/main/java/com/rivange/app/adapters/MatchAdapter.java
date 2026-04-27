package com.rivange.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rivange.app.R;
import com.rivange.app.models.Match;

import java.util.ArrayList;
import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    public interface OnJoinClickListener {
        void onJoinClick(Match match);
    }

    public interface OnScoreClickListener {
        void onScoreClick(Match match);
    }

    private final List<Match> matches = new ArrayList<>();
    private final OnJoinClickListener listener;
    private final OnScoreClickListener scoreListener;

    public MatchAdapter(OnJoinClickListener listener, OnScoreClickListener scoreListener) {
        this.listener = listener;
        this.scoreListener = scoreListener;
    }

    public void submitList(List<Match> newList) {
        matches.clear();
        if (newList != null) {
            matches.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        Match match = matches.get(position);

        holder.tvMatchTitle.setText(match.getTitle());
        holder.tvMatchDate.setText(match.getDate() + " · " + match.getTime());
        holder.tvMatchCourt.setText(match.getCourtName());
        String levelText = match.getSport().isEmpty() ? match.getLevel() : (match.getSport() + " - " + match.getLevel());
        holder.tvMatchLevel.setText(levelText);
        holder.tvMatchPlayers.setText(match.getCurrentPlayers() + "/" + match.getMaxPlayers());
        holder.tvMatchReferee.setText(match.getRefereeName().isEmpty() ? "Sin arbitro" : "Arbitro: " + match.getRefereeName());
        holder.tvMatchScore.setText(match.getScore().isEmpty() ? "Marcador pendiente" : "Marcador: " + match.getScore());

        boolean canJoin = match.isOpen() && match.getCurrentPlayers() < match.getMaxPlayers();
        holder.btnJoinMatch.setEnabled(canJoin);

        holder.btnJoinMatch.setOnClickListener(v -> {
            if (listener != null) {
                listener.onJoinClick(match);
            }
        });
        holder.btnScoreMatch.setOnClickListener(v -> {
            if (scoreListener != null) {
                scoreListener.onScoreClick(match);
            }
        });
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    static class MatchViewHolder extends RecyclerView.ViewHolder {

        TextView tvMatchTitle;
        TextView tvMatchDate;
        TextView tvMatchCourt;
        TextView tvMatchLevel;
        TextView tvMatchPlayers;
        TextView tvMatchReferee;
        TextView tvMatchScore;
        Button btnJoinMatch;
        Button btnScoreMatch;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMatchTitle = itemView.findViewById(R.id.tvMatchTitle);
            tvMatchDate = itemView.findViewById(R.id.tvMatchDate);
            tvMatchCourt = itemView.findViewById(R.id.tvMatchCourt);
            tvMatchLevel = itemView.findViewById(R.id.tvMatchLevel);
            tvMatchPlayers = itemView.findViewById(R.id.tvMatchPlayers);
            tvMatchReferee = itemView.findViewById(R.id.tvMatchReferee);
            tvMatchScore = itemView.findViewById(R.id.tvMatchScore);
            btnJoinMatch = itemView.findViewById(R.id.btnJoinMatch);
            btnScoreMatch = itemView.findViewById(R.id.btnScoreMatch);
        }
    }
}
