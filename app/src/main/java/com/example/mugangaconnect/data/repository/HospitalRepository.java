package com.example.mugangaconnect.data.repository;

import com.example.mugangaconnect.data.model.Hospital;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class HospitalRepository {
    private FirebaseFirestore db;
    private static final String COLLECTION_NAME = "hospitals";

    public HospitalRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void getAll(Callback<List<Hospital>> callback) {
        db.collection(COLLECTION_NAME)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<Hospital> hospitals = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Hospital hospital = document.toObject(Hospital.class);
                        hospital.setId(document.getId());
                        hospitals.add(hospital);
                    }
                    callback.onSuccess(hospitals);
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
