package com.example.mugangaconnect.data.repository;

import com.example.mugangaconnect.data.model.Doctor;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DoctorRepository {
    private final FirebaseFirestore db;
    private static final String COLLECTION_NAME = "doctors";

    public DoctorRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void getAllDoctors(Callback<List<Doctor>> callback) {
        db.collection(COLLECTION_NAME)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<Doctor> doctors = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        doctors.add(document.toObject(Doctor.class));
                    }
                    callback.onResult(doctors);
                } else {
                    callback.onError(task.getException() != null ? task.getException().getMessage() : "Unknown error");
                }
            });
    }

    public void getByDepartment(String department, Callback<List<Doctor>> callback) {
        db.collection(COLLECTION_NAME)
            .whereEqualTo("department", department)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<Doctor> doctors = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        doctors.add(document.toObject(Doctor.class));
                    }
                    callback.onResult(doctors);
                } else {
                    callback.onError(task.getException() != null ? task.getException().getMessage() : "Unknown error");
                }
            });
    }

    public interface Callback<T> {
        void onResult(T result);
        void onError(String errorMessage);
    }
}
