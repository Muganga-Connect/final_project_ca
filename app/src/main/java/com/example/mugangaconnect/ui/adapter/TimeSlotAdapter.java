package com.example.mugangaconnect.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mugangaconnect.R;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.ViewHolder> {

    private List<String> slots;
    private List<String> bookedSlots;
    private String selectedSlot;
    private OnSlotSelectedListener listener;

    public interface OnSlotSelectedListener {
        void onSlotSelected(String slot);
    }

    public TimeSlotAdapter(List<String> slots, List<String> bookedSlots, OnSlotSelectedListener listener) {
        this.slots = slots;
        this.bookedSlots = bookedSlots != null ? bookedSlots : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_slot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String slot = slots.get(position);
        boolean isBooked = bookedSlots.contains(slot);
        boolean isSelected = slot.equals(selectedSlot);

        holder.txtSlot.setText(slot);

        if (isBooked) {
            holder.itemView.setEnabled(false);
            holder.itemView.setAlpha(0.5f);
            holder.txtSlot.setBackgroundResource(R.drawable.slot_booked_background);
            holder.txtSlot.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_secondary));
            holder.txtSlot.setText(slot + " (Booked)");
        } else if (isSelected) {
            holder.itemView.setEnabled(true);
            holder.itemView.setAlpha(1.0f);
            holder.txtSlot.setBackgroundResource(R.drawable.slot_selected_background);
            holder.txtSlot.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white));
        } else {
            holder.itemView.setEnabled(true);
            holder.itemView.setAlpha(1.0f);
            holder.txtSlot.setBackgroundResource(R.drawable.slot_available_background);
            holder.txtSlot.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_blue));
        }

        holder.itemView.setOnClickListener(v -> {
            if (!isBooked) {
                selectedSlot = slot;
                notifyDataSetChanged();
                listener.onSlotSelected(slot);
            }
        });
    }

    @Override
    public int getItemCount() {
        return slots.size();
    }

    public void updateBookedSlots(List<String> bookedSlots) {
        this.bookedSlots = bookedSlots != null ? bookedSlots : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtSlot;

        ViewHolder(View view) {
            super(view);
            txtSlot = (TextView) view; // Assuming item_time_slot is just a TextView or has a TextView with ID txt_time_slot
            // If it's a layout: txtSlot = view.findViewById(R.id.txt_time_slot);
        }
    }
}
