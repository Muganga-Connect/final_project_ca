package com.example.mugangaconnect.data.repository;

import android.content.Context;
import com.example.mugangaconnect.data.model.Appointment;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AppointmentRepository {
    private final FirebaseFirestore db;
    private static final String COLLECTION = "appointments";

    public interface Callback<T> {
        void onResult(T data);
        void onError(String message);
    }

    public AppointmentRepository(Context context) {
        this.db = FirebaseFirestore.getInstance();
    }

    public void getForPatient(String patientId, Callback<List<Appointment>> callback) {
        db.collection(COLLECTION)
                .whereEqualTo("patientId", patientId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Appointment> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Appointment appt = doc.toObject(Appointment.class);
                        appt.setId(doc.getId());
                        list.add(appt);
                    }
                    callback.onResult(list);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void book(Appointment appt, Callback<Appointment> callback) {
        db.collection(COLLECTION).add(appt)
                .addOnSuccessListener(ref -> {
                    appt.setId(ref.getId());
                    callback.onResult(appt);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void reschedule(String id, String date, String time, Callback<Void> callback) {
        db.collection(COLLECTION).document(id)
                .update("date", date, "time", time, "status", Appointment.Status.RESCHEDULED.name())
                .addOnSuccessListener(v -> callback.onResult(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void updateStatus(String id, String uid, String status, Callback<Void> callback) {
        db.collection(COLLECTION).document(id)
                .update("status", status)
                .addOnSuccessListener(v -> callback.onResult(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getById(String appointmentId, Callback<Appointment> callback) {
        db.collection(COLLECTION).document(appointmentId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Appointment appt = doc.toObject(Appointment.class);
                        if (appt != null) {
                            appt.setId(doc.getId());
                            callback.onResult(appt);
                        } else {
                            callback.onError("Parsing error");
                        }
                    } else {
                        callback.onError("Not found");
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
