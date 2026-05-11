package com.example.mugangaconnect.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.model.Appointment;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
    private List<Appointment> appointments;
    private OnAppointmentActionListener listener;

    public interface OnAppointmentActionListener {
        void onReschedule(Appointment appointment);
        void onCancel(Appointment appointment);
        void onMarkAttended(Appointment appointment);
        void onMarkMissed(Appointment appointment);
    }

    public AppointmentAdapter(List<Appointment> appointmentList, OnAppointmentActionListener listener) {
        this.appointmentList = appointmentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.txtDoctorName.setText(appointment.getDoctorName());
        holder.txtSpecialty.setText(appointment.getDepartment());
        holder.txtDateTime.setText(appointment.getDate() + " " + appointment.getTime());
        holder.txtStatus.setText(appointment.getStatus());

        holder.btnReschedule.setOnClickListener(v -> listener.onReschedule(appointment));
        holder.btnCancel.setOnClickListener(v -> listener.onCancel(appointment));
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public void updateData(List<Appointment> newAppointments) {
        this.appointmentList = newAppointments;
        notifyDataSetChanged();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView txtDoctorName, txtSpecialty, txtDateTime, txtStatus;
        View btnReschedule, btnCancel;

        ViewHolder(View view) {
            super(view);
            txtDoctorName = view.findViewById(R.id.txt_doctor_name);
            txtSpecialty = view.findViewById(R.id.txt_specialty);
            txtDateTime = view.findViewById(R.id.txt_date_time);
            txtStatus = view.findViewById(R.id.txt_status);
            btnReschedule = view.findViewById(R.id.btn_reschedule);
            btnCancel = view.findViewById(R.id.btn_cancel);
        }
    }
}
