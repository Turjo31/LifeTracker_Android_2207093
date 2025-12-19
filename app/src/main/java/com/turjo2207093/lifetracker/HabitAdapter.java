package com.turjo2207093.lifetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.ViewHolder> {

    private ArrayList<String> habits;
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public HabitAdapter(ArrayList<String> habits, OnItemClickListener clickListener, OnItemLongClickListener longClickListener) {
        this.habits = habits;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    public HabitAdapter(ArrayList<String> habits, OnItemLongClickListener longClickListener) {
        this.habits = habits;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String habit = habits.get(position);
        holder.habitName.setText(habit);
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView habitName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            habitName = (TextView) itemView;
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        clickListener.onItemClick(position);
                    }
                }
            });
            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        longClickListener.onItemLongClick(position);
                    }
                }
                return true;
            });
        }
    }
}
