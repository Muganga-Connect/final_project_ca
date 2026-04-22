package com.example.mugangaconnect.data.repository;

import com.example.mugangaconnect.data.model.Doctor;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DoctorRepository {

    private static final String COLLECTION = "doctors";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface Callback<T> {
        void onResult(T data);
        void onError(String message);
    }

    public void getByDepartment(String department, Callback<List<Doctor>> callback) {
        db.collection(COLLECTION)
          .whereEqualTo("department", department)
          .get()
          .addOnSuccessListener(snapshot -> {
              List<Doctor> list = new ArrayList<>();
              for (QueryDocumentSnapshot doc : snapshot) {
                  Doctor d = doc.toObject(Doctor.class);
                  d.setId(doc.getId());
                  list.add(d);
              }
              callback.onResult(list);
          })
          .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getAll(Callback<List<Doctor>> callback) {
        db.collection(COLLECTION).get()
          .addOnSuccessListener(snapshot -> {
              List<Doctor> list = new ArrayList<>();
              for (QueryDocumentSnapshot doc : snapshot) {
                  Doctor d = doc.toObject(Doctor.class);
                  d.setId(doc.getId());
                  list.add(d);
              }
              callback.onResult(list);
          })
          .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
