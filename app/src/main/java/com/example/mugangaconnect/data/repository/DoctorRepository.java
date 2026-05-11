package com.example.mugangaconnect.data.repository;

import com.example.mugangaconnect.data.model.Doctor;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DoctorRepository {
    private final FirebaseFirestore db;
    private static final String COLLECTION = "doctors";

    public interface Callback<T> {
        void onResult(T data);
        void onError(String message);
    }

    public DoctorRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void getByDepartment(String department, Callback<List<Doctor>> callback) {
        db.collection(COLLECTION)
                .whereEqualTo("department", department)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Doctor> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Doctor doctor = doc.toObject(Doctor.class);
                        doctor.setId(doc.getId());
                        list.add(doctor);
                    }
                    callback.onResult(list);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getAll(Callback<List<Doctor>> callback) {
        db.collection(COLLECTION)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Doctor> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Doctor doctor = doc.toObject(Doctor.class);
                        doctor.setId(doc.getId());
                        list.add(doctor);
                    }
                    callback.onResult(list);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
