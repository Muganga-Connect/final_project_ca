package com.example.mugangaconnect.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.model.Doctor;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private final List<Doctor> doctorList;
    private int selectedPosition = -1;
    private final OnDoctorSelectedListener listener;

    public interface OnDoctorSelectedListener {
        void onDoctorSelected(Doctor doctor);
    }

    public DoctorAdapter(List<Doctor> doctorList, OnDoctorSelectedListener listener) {
        this.doctorList = doctorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        if (position < 0 || position >= doctorList.size()) return;

        Doctor doctor = doctorList.get(position);
        holder.txtName.setText(doctor.getName());
        holder.txtSpecialty.setText(doctor.getSpecialty());
        holder.txtAvailability.setText(doctor.getAvailability());

        if (selectedPosition == position) {
            holder.cardDoctor.setStrokeWidth(4);
            holder.cardDoctor.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.blue_primary));
            holder.imgSelected.setVisibility(View.VISIBLE);
        } else {
            holder.cardDoctor.setStrokeWidth(0);
            holder.imgSelected.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            int prev = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            if (prev != -1) notifyItemChanged(prev);
            notifyItemChanged(selectedPosition);
            if (listener != null) listener.onDoctorSelected(doctor);
        });
    }

    @Override
    public int getItemCount() {
        return doctorList != null ? doctorList.size() : 0;
    }

    static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtSpecialty, txtAvailability;
        ImageView imgDoctor, imgSelected;
        MaterialCardView cardDoctor;

        DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName         = itemView.findViewById(R.id.txt_doctor_name);
            txtSpecialty    = itemView.findViewById(R.id.txt_doctor_specialty);
            txtAvailability = itemView.findViewById(R.id.txt_doctor_availability);
            imgDoctor       = itemView.findViewById(R.id.img_doctor);
            imgSelected     = itemView.findViewById(R.id.img_selected);
            cardDoctor      = itemView.findViewById(R.id.card_doctor);
        }
    }
}
