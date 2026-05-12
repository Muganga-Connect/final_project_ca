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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Doctor doctor = doctors.get(position);
        holder.txtName.setText(doctor.getName());
        holder.txtSpecialty.setText(doctor.getSpecialty());
        
        // Load Image with Glide
        String imageUrl = doctor.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .circleCrop()
                .into(holder.imgDoctor);
        } else {
            holder.imgDoctor.setImageResource(android.R.drawable.ic_menu_gallery);
        }
        
        String availability = doctor.getAvailability();
        if (availability != null && !availability.isEmpty()) {
            holder.txtAvailability.setText(availability);
            holder.txtAvailability.setVisibility(View.VISIBLE);
        } else {
            holder.txtAvailability.setText("Available Today");
            holder.txtAvailability.setVisibility(View.VISIBLE);
        }
        
        holder.itemView.setOnClickListener(v -> listener.onDoctorSelected(doctor));
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    public void updateData(List<Doctor> newDoctors) {
        this.doctors = newDoctors;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtSpecialty, txtAvailability;
        ImageView imgDoctor;

        ViewHolder(View view) {
            super(view);
            txtName = view.findViewById(R.id.txt_doctor_name);
            txtSpecialty = view.findViewById(R.id.txt_doctor_specialty);
            txtAvailability = view.findViewById(R.id.txt_doctor_availability);
            imgDoctor = view.findViewById(R.id.img_doctor);
        }
    }
}
