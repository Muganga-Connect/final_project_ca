package com.example.mugangaconnect.data.repository;

import com.example.mugangaconnect.data.model.Doctor;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class DoctorRepository {
    private FirebaseFirestore db;
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
                        Doctor doctor = document.toObject(Doctor.class);
                        doctor.setId(document.getId());
                        doctors.add(doctor);
                    }
                    callback.onSuccess(doctors);
                } else {
                    callback.onError(task.getException().getMessage());
                }
            });
    }

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }
}