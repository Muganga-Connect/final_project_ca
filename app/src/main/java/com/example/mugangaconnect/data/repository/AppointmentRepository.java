package com.example.mugangaconnect.data.repository;

import android.content.Context;
import com.example.mugangaconnect.data.model.Appointment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AppointmentRepository {
    
    private FirebaseFirestore db;
    private static final String COLLECTION_NAME = "appointments";
    
    public AppointmentRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public AppointmentRepository(Context context) {
        this();
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
                        try {
                            Appointment appointment = documentToAppointment(document);
                            callback.onResult(appointment);
                        } catch (Exception e) {
                            callback.onError("Error parsing appointment data: " + e.getMessage());
                        }
                    } else {
                        callback.onError("Appointment not found");
                    }
                } else {
                    callback.onError("Failed to fetch appointment: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                }
            })
            .addOnFailureListener(e -> callback.onError("Network error: " + e.getMessage()));
    }

    public void getForPatient(String patientId, Callback<List<Appointment>> callback) {
        db.collection(COLLECTION_NAME)
            .whereEqualTo("patientId", patientId)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<Appointment> list = new ArrayList<>();
                    for (DocumentSnapshot doc : task.getResult()) {
                        list.add(documentToAppointment(doc));
                    }
                    callback.onResult(list);
                } else {
                    callback.onError(task.getException() != null ? task.getException().getMessage() : "Unknown error");
                }
            });
    }

    public void getCachedByStatus(String patientId, String status, Callback<List<Appointment>> callback) {
        db.collection(COLLECTION_NAME)
            .whereEqualTo("patientId", patientId)
            .whereEqualTo("status", status)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<Appointment> list = new ArrayList<>();
                    for (DocumentSnapshot doc : task.getResult()) {
                        list.add(documentToAppointment(doc));
                    }
                    callback.onResult(list);
                } else {
                    callback.onError(task.getException() != null ? task.getException().getMessage() : "Unknown error");
                }
            });
    }

    public void updateStatus(String appointmentId, String patientId, String status, Callback<Void> callback) {
        db.collection(COLLECTION_NAME).document(appointmentId)
            .update("status", status)
            .addOnSuccessListener(aVoid -> callback.onResult(null))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void book(Appointment appt, Callback<Appointment> callback) {
        db.collection(COLLECTION_NAME).add(appt)
            .addOnSuccessListener(docRef -> {
                appt.setId(docRef.getId());
                callback.onResult(appt);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void reschedule(String appointmentId, String newDate, String newTime, Callback<Void> callback) {
        db.collection(COLLECTION_NAME).document(appointmentId)
            .update("date", newDate, "time", newTime)
            .addOnSuccessListener(aVoid -> callback.onResult(null))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
    
    private Appointment documentToAppointment(DocumentSnapshot document) {
        String id = document.getId();
        String patientId = document.getString("patientId");
        String doctorId = document.getString("doctorId");
        String doctorName = document.getString("doctorName");
        String department = document.getString("department");
        String date = document.getString("date");
        String time = document.getString("time");
        String status = document.getString("status");
        
        Appointment appointment = new Appointment(
            patientId != null ? patientId : "",
            doctorId != null ? doctorId : "",
            doctorName != null ? doctorName : "",
            department != null ? department : "",
            date != null ? date : "",
            time != null ? time : ""
        );
        appointment.setId(id);
        if (status != null) appointment.setStatus(status);
        return appointment;
    }
    
    public interface Callback<T> {
        void onResult(T data);
        void onError(String errorMessage);
    }
}
