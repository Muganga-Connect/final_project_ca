package com.example.appointment_schedule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mugangaconnect.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private final List<Appointment> appointmentList;
    private final OnAppointmentActionListener listener;

    public interface OnAppointmentActionListener {
        void onReschedule(Appointment appointment);
        void onCancel(Appointment appointment);
    }

    public AppointmentAdapter(List<Appointment> appointmentList, OnAppointmentActionListener listener) {
        this.appointmentList = appointmentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        holder.txtDoctorName.setText(appointment.getDoctor().getName());
        holder.txtSpecialty.setText(appointment.getDoctor().getSpecialty());
        holder.txtStatus.setText(appointment.getStatus());
        holder.txtDateTime.setText(appointment.getDate() + " at " + appointment.getTime());
        holder.imgDoctor.setImageResource(appointment.getDoctor().getImageResId());

        // Hide buttons for cancelled appointments
        if ("CANCELLED".equals(appointment.getStatus())) {
            holder.layoutActions.setVisibility(View.GONE);
        } else {
            holder.layoutActions.setVisibility(View.VISIBLE);
        }

        holder.btnReschedule.setOnClickListener(v -> listener.onReschedule(appointment));
        holder.btnCancel.setOnClickListener(v -> listener.onCancel(appointment));
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView txtDoctorName, txtSpecialty, txtStatus, txtDateTime;
        ImageView imgDoctor;
        MaterialButton btnReschedule, btnCancel;
        View layoutActions;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDoctorName = itemView.findViewById(R.id.txt_doctor_name);
            txtSpecialty = itemView.findViewById(R.id.txt_specialty);
            txtStatus = itemView.findViewById(R.id.txt_status);
            txtDateTime = itemView.findViewById(R.id.txt_date_time);
            imgDoctor = itemView.findViewById(R.id.img_doctor);
            btnReschedule = itemView.findViewById(R.id.btn_reschedule);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
            layoutActions = itemView.findViewById(R.id.layout_actions);
        }
    }
}