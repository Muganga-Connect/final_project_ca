package com.example.mugangaconnect.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.model.Appointment;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private List<Appointment> appointments;
    private OnAppointmentActionListener listener;

    public interface OnAppointmentActionListener {
        void onReschedule(Appointment appointment);
        void onCancel(Appointment appointment);
    }

    public AppointmentAdapter(List<Appointment> appointments, OnAppointmentActionListener listener) {
        this.appointments = appointments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.txtDoctorName.setText(appointment.getDoctorName());
        holder.txtSpecialty.setText(appointment.getDepartment());
        holder.txtStatus.setText(appointment.getStatus());
        holder.txtDateTime.setText(appointment.getDate() + " at " + appointment.getTime());

        holder.btnReschedule.setOnClickListener(v -> {
            if (listener != null) listener.onReschedule(appointment);
        });

        holder.btnCancel.setOnClickListener(v -> {
            if (listener != null) listener.onCancel(appointment);
        });
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public void updateData(List<Appointment> newAppointments) {
        this.appointments = newAppointments;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDoctorName, txtSpecialty, txtStatus, txtDateTime;
        View btnReschedule, btnCancel;

        ViewHolder(View itemView) {
            super(itemView);
            txtDoctorName = itemView.findViewById(R.id.txt_doctor_name);
            txtSpecialty = itemView.findViewById(R.id.txt_specialty);
            txtStatus = itemView.findViewById(R.id.txt_status);
            txtDateTime = itemView.findViewById(R.id.txt_date_time);
            btnReschedule = itemView.findViewById(R.id.btn_reschedule);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
        }
    }
}
