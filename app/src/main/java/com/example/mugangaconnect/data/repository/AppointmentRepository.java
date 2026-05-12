package com.example.mugangaconnect.data.repository;

import com.example.mugangaconnect.data.model.Appointment;
import com.example.mugangaconnect.data.model.Doctor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class AppointmentRepository {
    
    private FirebaseFirestore db;
    private static final String COLLECTION_NAME = "appointments";
    
    public AppointmentRepository() {
        db = FirebaseFirestore.getInstance();
    }
    
    public void getById(String appointmentId, Callback<Appointment> callback) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            callback.onError("Appointment ID cannot be null or empty");
            return;
        }
        
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(appointmentId);
        
        docRef.get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        callback.onSuccess(documentToAppointment(document));
                    } else {
                        callback.onError("Appointment not found");
                    }
                } else {
                    callback.onError(task.getException().getMessage());
                }
            });
    }
    
    private Appointment documentToAppointment(DocumentSnapshot document) {
        String patientId = document.getString("patientId");
        String doctorId = document.getString("doctorId");
        String doctorName = document.getString("doctorName");
        String department = document.getString("department");
        String date = document.getString("date");
        String time = document.getString("time");
        
        Appointment appointment = new Appointment(
            patientId != null ? patientId : "",
            doctorId != null ? doctorId : "",
            doctorName != null ? doctorName : "",
            department != null ? department : "",
            date != null ? date : "",
            time != null ? time : ""
        );
        appointment.setId(document.getId());
        appointment.setStatus(document.getString("status"));
        appointment.setRiskLevel(document.getString("riskLevel"));
        return appointment;
    }
    
    public void getForPatient(String patientId, Callback<List<Appointment>> callback) {
        db.collection(COLLECTION_NAME)
            .whereEqualTo("patientId", patientId)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<Appointment> appointments = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        appointments.add(documentToAppointment(document));
                    }
                    callback.onSuccess(appointments);
                } else {
                    callback.onError(task.getException().getMessage());
                }
            });
    }

    public void book(Appointment appointment, Callback<Appointment> callback) {
        db.collection(COLLECTION_NAME)
            .add(appointment)
            .addOnSuccessListener(documentReference -> {
                appointment.setId(documentReference.getId());
                callback.onSuccess(appointment);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void reschedule(String id, String date, String time, Callback<Void> callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("date", date);
        updates.put("time", time);
        updates.put("status", Appointment.Status.RESCHEDULED.name());

        db.collection(COLLECTION_NAME).document(id)
            .update(updates)
            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void updateStatus(String id, String userId, String status, Callback<Void> callback) {
        db.collection(COLLECTION_NAME).document(id)
            .update("status", status)
            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getCachedByStatus(String uid, String status, Callback<List<Appointment>> callback) {
        getForPatient(uid, new Callback<List<Appointment>>() {
            @Override
            public void onSuccess(List<Appointment> result) {
                List<Appointment> filtered = new ArrayList<>();
                for (Appointment appt : result) {
                    if (appt.getStatus().equalsIgnoreCase(status)) {
                        filtered.add(appt);
                    }
                }
                callback.onSuccess(filtered);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }
}