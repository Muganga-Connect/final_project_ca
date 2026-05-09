package com.example.mugangaconnect.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mugangaconnect.data.model.Doctor;
import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ViewHolder> {
    
    private List<Doctor> doctors;
    private OnDoctorSelectedListener listener;

    public interface OnDoctorSelectedListener {
        void onDoctorSelected(Doctor doctor);
    }

    public DoctorAdapter(List<Doctor> doctors, OnDoctorSelectedListener listener) {
        this.doctors = doctors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // We just use a basic android layout for dummy implementation since it's missing
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Doctor doctor = doctors.get(position);
        holder.tvName.setText(doctor.getName());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDoctorSelected(doctor);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctors != null ? doctors.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(android.R.id.text1);
        }
    }
}
