package com.example.mugangaconnect.data.repository;

import com.example.mugangaconnect.data.model.Doctor;
import com.google.firebase.firestore.DocumentSnapshot;
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

    public void getByDepartment(String department, Callback<List<Doctor>> callback) {
        db.collection(COLLECTION_NAME)
            .whereEqualTo("department", department)
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

    public void getByHospital(String hospitalId, Callback<List<Doctor>> callback) {
        db.collection("doctors")
            .whereEqualTo("hospitalId", hospitalId)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<Doctor> doctors = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        Doctor doctor = documentToDoctor(document);
                        doctors.add(doctor);
                    }
                    callback.onSuccess(doctors);
                } else {
                    callback.onError(task.getException().getMessage());
                }
            });
    }

    private Doctor documentToDoctor(DocumentSnapshot document) {
        Doctor doctor = new Doctor();
        doctor.setId(document.getId());
        doctor.setName(document.getString("name"));
        doctor.setSpecialty(document.getString("specialty"));
        doctor.setDepartment(document.getString("department"));
        doctor.setHospitalId(document.getString("hospitalId"));
        // ... other fields
        return doctor;
    }

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }
}
