package com.rivange.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rivange.app.R;
import com.rivange.app.models.Reservation;

import java.util.ArrayList;
import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    public interface OnReservationActionListener {
        void onEdit(Reservation reservation);
        void onDelete(Reservation reservation);
        void onCreateMatch(Reservation reservation);
    }

    private final List<Reservation> reservations = new ArrayList<>();
    private final OnReservationActionListener actionListener;

    public ReservationAdapter(OnReservationActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void submitList(List<Reservation> newList) {
        reservations.clear();
        if (newList != null) {
            reservations.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);

        holder.tvCourtName.setText(reservation.getCourtName());
        holder.tvReservationSport.setText(reservation.getSport().isEmpty() ? "Deporte no definido" : reservation.getSport());
        holder.tvReservationDate.setText(reservation.getDate());
        holder.tvReservationTime.setText(reservation.getStartTime() + " - " + reservation.getEndTime());
        holder.tvReservationStatus.setText(reservation.getStatus());

        holder.btnEditReservation.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEdit(reservation);
            }
        });
        holder.btnDeleteReservation.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDelete(reservation);
            }
        });
        holder.btnCreateMatchFromReservation.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onCreateMatch(reservation);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    static class ReservationViewHolder extends RecyclerView.ViewHolder {

        TextView tvCourtName;
        TextView tvReservationSport;
        TextView tvReservationDate;
        TextView tvReservationTime;
        TextView tvReservationStatus;
        Button btnEditReservation;
        Button btnDeleteReservation;
        Button btnCreateMatchFromReservation;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCourtName = itemView.findViewById(R.id.tvCourtName);
            tvReservationSport = itemView.findViewById(R.id.tvReservationSport);
            tvReservationDate = itemView.findViewById(R.id.tvReservationDate);
            tvReservationTime = itemView.findViewById(R.id.tvReservationTime);
            tvReservationStatus = itemView.findViewById(R.id.tvReservationStatus);
            btnEditReservation = itemView.findViewById(R.id.btnEditReservation);
            btnDeleteReservation = itemView.findViewById(R.id.btnDeleteReservation);
            btnCreateMatchFromReservation = itemView.findViewById(R.id.btnCreateMatchFromReservation);
        }
    }
}
