package com.example.mugangaconnect.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.model.Hospital;
import java.util.List;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.ViewHolder> {
    private List<Hospital> hospitals;
    private OnHospitalSelectedListener listener;
    private int selectedPosition = -1;

    public interface OnHospitalSelectedListener {
        void onHospitalSelected(Hospital hospital);
    }

    public HospitalAdapter(List<Hospital> hospitals, OnHospitalSelectedListener listener) {
        this.hospitals = hospitals;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hospital_chip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hospital hospital = hospitals.get(position);
        holder.tvHospitalName.setText(hospital.getName());
        
        boolean isSelected = position == selectedPosition;
        holder.tvHospitalName.setBackgroundResource(isSelected ? R.drawable.bg_tab_active : R.drawable.bg_tab_inactive);
        holder.tvHospitalName.setTextColor(isSelected ? 0xFFFFFFFF : 0xFF667A90);

        holder.itemView.setOnClickListener(v -> {
            int oldPos = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(oldPos);
            notifyItemChanged(selectedPosition);
            listener.onHospitalSelected(hospital);
        });
    }

    @Override
    public int getItemCount() { return hospitals.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHospitalName;
        ViewHolder(View view) {
            super(view);
            tvHospitalName = (TextView) view;
        }
    }
}
