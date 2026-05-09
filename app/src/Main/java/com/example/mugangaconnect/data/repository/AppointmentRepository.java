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
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            try {
                                Appointment appointment = documentToAppointment(document);
                                callback.onSuccess(appointment);
                            } catch (Exception e) {
                                callback.onError("Error parsing appointment data: " + e.getMessage());
                            }
                        } else {
                            callback.onError("Appointment not found");
                        }
                    } else {
                        callback.onError("Failed to fetch appointment: " + task.getException().getMessage());
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    callback.onError("Network error: " + e.getMessage());
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
        if (patientId == null || patientId.trim().isEmpty()) {
            callback.onError("Patient ID cannot be null or empty");
            return;
        }

        db.collection(COLLECTION_NAME)
            .whereEqualTo("patientId", patientId)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<Appointment> appointments = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            appointments.add(documentToAppointment(document));
                        }
                        callback.onSuccess(appointments);
                    } else {
                        callback.onError("Failed to fetch appointments: " + task.getException().getMessage());
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    callback.onError("Network error: " + e.getMessage());
                }
            });
    }

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }
}