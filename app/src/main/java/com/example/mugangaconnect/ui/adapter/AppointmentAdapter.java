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

    private List<Appointment> appointmentList;
    private final OnAppointmentActionListener listener;

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
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        holder.bind(appointmentList.get(position));
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
        TextView txtDoctorName, txtSpecialty, txtStatus, txtDateTime;
        ImageView imgDoctor;
        MaterialButton btnReschedule, btnCancel, btnMarkAttended, btnMarkMissed;
        View layoutActions, layoutAdminActions;
        private Appointment currentAppointment;
        private final OnAppointmentActionListener listener;

        AppointmentViewHolder(View itemView, OnAppointmentActionListener listener) {
            super(itemView);
            this.listener = listener;
            txtDoctorName      = itemView.findViewById(R.id.txt_doctor_name);
            txtSpecialty       = itemView.findViewById(R.id.txt_specialty);
            txtStatus          = itemView.findViewById(R.id.txt_status);
            txtDateTime        = itemView.findViewById(R.id.txt_date_time);
            imgDoctor          = itemView.findViewById(R.id.img_doctor);
            btnReschedule      = itemView.findViewById(R.id.btn_reschedule);
            btnCancel          = itemView.findViewById(R.id.btn_cancel);
            btnMarkAttended    = itemView.findViewById(R.id.btn_mark_attended);
            btnMarkMissed      = itemView.findViewById(R.id.btn_mark_missed);
            layoutActions      = itemView.findViewById(R.id.layout_actions);
            layoutAdminActions = itemView.findViewById(R.id.layout_admin_actions);

            btnReschedule.setOnClickListener(v -> {
                if (listener != null && currentAppointment != null) listener.onReschedule(currentAppointment);
            });
            btnCancel.setOnClickListener(v -> {
                if (listener != null && currentAppointment != null) listener.onCancel(currentAppointment);
            });
            btnMarkAttended.setOnClickListener(v -> {
                if (listener != null && currentAppointment != null) listener.onMarkAttended(currentAppointment);
            });
            btnMarkMissed.setOnClickListener(v -> {
                if (listener != null && currentAppointment != null) listener.onMarkMissed(currentAppointment);
            });
        }

        void bind(Appointment appointment) {
            currentAppointment = appointment;
            txtDoctorName.setText(safeText(appointment.getDoctorName()));
            txtSpecialty.setText(safeText(appointment.getDepartment()));
            txtStatus.setText(safeText(appointment.getStatus()));
            txtDateTime.setText(formatDateTime(appointment.getDate(), appointment.getTime()));

            boolean isUpcoming = Appointment.Status.UPCOMING.name().equals(appointment.getStatus())
                    || Appointment.Status.CONFIRMED.name().equals(appointment.getStatus());
            if (layoutActions != null) layoutActions.setVisibility(isUpcoming ? View.VISIBLE : View.GONE);
            if (layoutAdminActions != null) layoutAdminActions.setVisibility(isUpcoming ? View.VISIBLE : View.GONE);
        }

        private String safeText(String value) {
            return TextUtils.isEmpty(value) ? "N/A" : value;
        }

        private String formatDateTime(String date, String time) {
            if (TextUtils.isEmpty(date) && TextUtils.isEmpty(time)) return "N/A";
            if (TextUtils.isEmpty(date)) return time;
            if (TextUtils.isEmpty(time)) return date;
            return date + " at " + time;
        }
    }
}
