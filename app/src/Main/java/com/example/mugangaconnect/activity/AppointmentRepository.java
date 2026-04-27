package com.example.mugangaconnect.activity;

import com.example.mugangaconnect.data.model.Appointment;
import com.example.mugangaconnect.data.model.Doctor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AppointmentRepository {
    
    private FirebaseFirestore db;
    private static final String COLLECTION_NAME = "appointments";
    
    public AppointmentRepository() {
        db = FirebaseFirestore.getInstance();
    }
    
    /**
     * Fetches a single appointment document by its ID from Firestore
     * @param appointmentId The ID of the appointment to fetch
     * @param callback Callback to handle success or error
     */
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
    
    /**
     * Converts Firestore DocumentSnapshot to Appointment object
     * @param document The Firestore document to convert
     * @return Appointment object
     */
    private Appointment documentToAppointment(DocumentSnapshot document) {
        String id = document.getId();
        
        // Extract doctor data
        Doctor doctor = null;
        if (document.contains("doctor")) {
            String doctorId = document.contains("doctor.id") ? document.getString("doctor.id") : "";
            String doctorName = document.getString("doctor.name");
            String doctorSpecialty = document.getString("doctor.specialty");
            String doctorDepartment = document.contains("doctor.department") ? document.getString("doctor.department") : "";
            String doctorAvailability = document.getString("doctor.availability");
            
            if (doctorName != null && doctorSpecialty != null) {
                doctor = new Doctor(doctorId, doctorName, doctorSpecialty, doctorDepartment, doctorAvailability);
            }
        }
        
        String date = document.getString("date");
        String time = document.getString("time");
        String status = document.getString("status");
        
        // Handle null values with defaults
        if (date == null) date = "";
        if (time == null) time = "";
        if (status == null) status = "UNKNOWN";
        
        return new Appointment(id, doctor, date, time, status);
    }
    
    /**
     * Callback interface for async operations
     */
    public interface Callback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }
}
