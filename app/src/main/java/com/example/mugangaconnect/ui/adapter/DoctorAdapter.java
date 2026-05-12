package com.example.mugangaconnect.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.model.Doctor;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ViewHolder> {

    public interface OnDoctorSelectedListener {
        void onDoctorSelected(Doctor doctor);
    }

    private final List<Doctor> doctors;
    private final OnDoctorSelectedListener listener;
    private int selectedPosition = -1;

    public DoctorAdapter(List<Doctor> doctors, OnDoctorSelectedListener listener) {
        this.doctors = doctors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Doctor doctor = doctors.get(position);
        holder.txtName.setText(doctor.getName());
        holder.txtSpecialty.setText(doctor.getSpecialty());
        holder.txtAvailability.setText(doctor.getAvailability());

        if (doctor.getImageUrl() != null && !doctor.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(doctor.getImageUrl())
                    .placeholder(R.drawable.ic_doctor_placeholder)
                    .into(holder.imgDoctor);
        } else {
            holder.imgDoctor.setImageResource(R.drawable.ic_doctor_placeholder);
        }

        boolean isSelected = selectedPosition == position;
        holder.imgSelected.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        holder.cardView.setStrokeWidth(isSelected ? 4 : 0);
        holder.cardView.setStrokeColor(holder.itemView.getContext().getColor(R.color.primary_blue));

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            listener.onDoctorSelected(doctor);
        });
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtSpecialty, txtAvailability;
        ImageView imgDoctor, imgSelected;
        MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_doctor_name);
            txtSpecialty = itemView.findViewById(R.id.txt_doctor_specialty);
            txtAvailability = itemView.findViewById(R.id.txt_doctor_availability);
            imgDoctor = itemView.findViewById(R.id.img_doctor);
            imgSelected = itemView.findViewById(R.id.img_selected);
            cardView = itemView.findViewById(R.id.card_doctor);
        }
    }
}
