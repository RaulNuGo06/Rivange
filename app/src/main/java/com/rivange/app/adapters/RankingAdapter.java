package com.rivange.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rivange.app.R;
import com.rivange.app.models.RankingEntry;

import java.util.ArrayList;
import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {

    private final List<RankingEntry> rankingList = new ArrayList<>();

    public void submitList(List<RankingEntry> newList) {
        rankingList.clear();
        if (newList != null) {
            rankingList.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ranking, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        RankingEntry entry = rankingList.get(position);

        holder.tvRankingPosition.setText(String.valueOf(entry.getPosition()));
        holder.tvRankingName.setText(entry.getUserName());
        holder.tvRankingPoints.setText(entry.getPoints() + " pts");
        holder.tvRankingRecord.setText(entry.getWins() + "V - " + entry.getLosses() + "D");
        holder.tvRankingTier.setText(entry.getRankName());
    }

    @Override
    public int getItemCount() {
        return rankingList.size();
    }

    static class RankingViewHolder extends RecyclerView.ViewHolder {

        TextView tvRankingPosition;
        TextView tvRankingName;
        TextView tvRankingPoints;
        TextView tvRankingRecord;
        TextView tvRankingTier;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);

            tvRankingPosition = itemView.findViewById(R.id.tvRankingPosition);
            tvRankingName = itemView.findViewById(R.id.tvRankingName);
            tvRankingPoints = itemView.findViewById(R.id.tvRankingPoints);
            tvRankingRecord = itemView.findViewById(R.id.tvRankingRecord);
            tvRankingTier = itemView.findViewById(R.id.tvRankingTier);
        }
    }
}
