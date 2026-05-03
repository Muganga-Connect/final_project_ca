package com.example.mugangaconnect.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.mugangaconnect.R;
import com.google.android.material.card.MaterialCardView;

import java.util.Calendar;

public class SessionSelectionDialog extends DialogFragment {

    public interface OnSessionSelectedListener {
        void onSessionSelected(String date, String time);
    }

    private OnSessionSelectedListener listener;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button btnMorningSession;
    private Button btnAfternoonSession;
    private Button btnEveningSession;

    public SessionSelectionDialog() {
        // Required empty constructor
    }

    public void setOnSessionSelectedListener(OnSessionSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_session_selection, null);

        datePicker = view.findViewById(R.id.date_picker);
        timePicker = view.findViewById(R.id.time_picker);
        btnMorningSession = view.findViewById(R.id.btn_morning_session);
        btnAfternoonSession = view.findViewById(R.id.btn_afternoon_session);
        btnEveningSession = view.findViewById(R.id.btn_evening_session);
        Button btnConfirm = view.findViewById(R.id.btn_confirm_session);
        Button btnCancel = view.findViewById(R.id.btn_cancel_session);

        // Set minimum date to today
        Calendar calendar = Calendar.getInstance();
        datePicker.setMinDate(calendar.getTimeInMillis());

        // Setup quick session buttons
        setupQuickSessionButtons();

        btnConfirm.setOnClickListener(v -> {
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year = datePicker.getYear();
            
            int hour = timePicker.getCurrentHour();
            int minute = timePicker.getCurrentMinute();

            String date = String.format("%04d-%02d-%02d", year, month + 1, day);
            String time = String.format("%02d:%02d", hour, minute);

            if (listener != null) {
                listener.onSessionSelected(date, time);
            }
            dismiss();
        });

        btnCancel.setOnClickListener(v -> dismiss());

        builder.setView(view);
        return builder.create();
    }

    private void setupQuickSessionButtons() {
        btnMorningSession.setOnClickListener(v -> {
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year = datePicker.getYear();
            
            String date = String.format("%04d-%02d-%02d", year, month + 1, day);
            String time = "09:00"; // Morning session
            
            if (listener != null) {
                listener.onSessionSelected(date, time);
            }
            dismiss();
        });

        btnAfternoonSession.setOnClickListener(v -> {
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year = datePicker.getYear();
            
            String date = String.format("%04d-%02d-%02d", year, month + 1, day);
            String time = "14:00"; // Afternoon session
            
            if (listener != null) {
                listener.onSessionSelected(date, time);
            }
            dismiss();
        });

        btnEveningSession.setOnClickListener(v -> {
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year = datePicker.getYear();
            
            String date = String.format("%04d-%02d-%02d", year, month + 1, day);
            String time = "17:00"; // Evening session
            
            if (listener != null) {
                listener.onSessionSelected(date, time);
            }
            dismiss();
        });
    }
}
