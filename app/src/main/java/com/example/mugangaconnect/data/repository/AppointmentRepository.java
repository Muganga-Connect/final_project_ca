package com.example.mugangaconnect.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.mugangaconnect.data.local.AppDatabase;
import com.example.mugangaconnect.data.local.AppointmentDao;
import com.example.mugangaconnect.data.model.Appointment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AppointmentRepository {

    private final FirebaseFirestore db;
    private final AppointmentDao dao;
    private static final String COLLECTION_NAME = "appointments";

    public AppointmentRepository(Context context) {
        db  = FirebaseFirestore.getInstance();
        dao = new AppointmentDao(AppDatabase.getInstance(context));
    }

    // ─────────────────────────────────────────────────────────────
    //  GET ALL APPOINTMENTS FOR A PATIENT
    //  ✅ Firestore first → save to SQLite → return list
    //  ✅ If Firestore fails → fallback to SQLite (offline support)
    // ─────────────────────────────────────────────────────────────
    public void getForPatient(String patientId, Callback<List<Appointment>> callback) {
        if (patientId == null || patientId.isEmpty()) {
            callback.onError("Patient ID is null");
            return;
        }

        db.collection(COLLECTION_NAME)
                .whereEqualTo("patientId", patientId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Appointment> list = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Appointment a = documentToAppointment(doc);
                        if (a != null) list.add(a);
                    }

                    // ✅ Save to SQLite for offline access
                    new Thread(() -> dao.upsertAll(list)).start();

                    callback.onResult(list);
                })
                .addOnFailureListener(e -> {
                    // ✅ Firestore failed → fallback to SQLite
                    new Thread(() -> {
                        List<Appointment> cached = dao.getByPatient(patientId);
                        callback.onResult(cached);
                    }).start();
                });
    }

    // ─────────────────────────────────────────────────────────────
    //  GET APPOINTMENTS BY STATUS
    //  ✅ Reads from SQLite only (fast, offline-first)
    //  Used by AppointmentHistoryActivity tabs
    // ─────────────────────────────────────────────────────────────
    public void getCachedByStatus(String patientId, String status, Callback<List<Appointment>> callback) {
        new Thread(() -> {
            try {
                List<Appointment> list = dao.getByStatus(patientId, status);
                callback.onResult(list);
            } catch (NullPointerException e) {
                Log.e("AppointmentRepository", "Null pointer in getCachedByStatus: patientId=" + patientId + ", status=" + status, e);
                callback.onError("Invalid patient ID or status");
            } catch (IllegalArgumentException e) {
                Log.e("AppointmentRepository", "Invalid argument in getCachedByStatus: " + e.getMessage(), e);
                callback.onError("Invalid parameter provided");
            } catch (Exception e) {
                Log.e("AppointmentRepository", "Local DB error in getCachedByStatus: " + e.getMessage(), e);
                callback.onError("Local DB error: " + e.getClass().getSimpleName());
            }
        }).start();
    }

    // ─────────────────────────────────────────────────────────────
    //  GET SINGLE APPOINTMENT BY ID
    // ─────────────────────────────────────────────────────────────
    public void getById(String appointmentId, Callback<Appointment> callback) {
        if (appointmentId == null || appointmentId.isEmpty()) {
            callback.onError("Appointment ID is null");
            return;
        }

        db.collection(COLLECTION_NAME).document(appointmentId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        callback.onResult(documentToAppointment(doc));
                    } else {
                        callback.onError("Appointment not found");
                    }
                })
                .addOnFailureListener(e -> callback.onError("Network error: " + e.getMessage()));
    }

    // ─────────────────────────────────────────────────────────────
    //  BOOK A NEW APPOINTMENT
    //  ✅ Saves to Firestore → then caches in SQLite
    // ─────────────────────────────────────────────────────────────
    public void book(Appointment appt, Callback<Appointment> callback) {
        db.collection(COLLECTION_NAME).add(appt)
                .addOnSuccessListener(docRef -> {
                    appt.setId(docRef.getId());
                    // ✅ Cache locally
                    new Thread(() -> dao.upsert(appt)).start();
                    callback.onResult(appt);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ─────────────────────────────────────────────────────────────
    //  UPDATE APPOINTMENT STATUS (CANCEL / ATTEND / MISS)
    //  ✅ Updates Firestore → then updates SQLite
    // ─────────────────────────────────────────────────────────────
    public void updateStatus(String appointmentId, String patientId, String status, Callback<Void> callback) {
        db.collection(COLLECTION_NAME).document(appointmentId)
                .update("status", status)
                .addOnSuccessListener(v -> {
                    // ✅ Update local cache
                    new Thread(() -> dao.updateStatus(appointmentId, status)).start();
                    callback.onResult(null);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ─────────────────────────────────────────────────────────────
    //  RESCHEDULE APPOINTMENT
    //  ✅ Updates Firestore → then updates SQLite
    // ─────────────────────────────────────────────────────────────
    public void reschedule(String appointmentId, String newDate, String newTime, Callback<Void> callback) {
        db.collection(COLLECTION_NAME).document(appointmentId)
                .update("date", newDate, "time", newTime)
                .addOnSuccessListener(v -> {
                    // ✅ Update local cache
                    new Thread(() -> dao.updateDateAndTime(appointmentId, newDate, newTime)).start();
                    callback.onResult(null);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ─────────────────────────────────────────────────────────────
    //  HELPER: Convert Firestore document → Appointment object
    // ─────────────────────────────────────────────────────────────
    private Appointment documentToAppointment(DocumentSnapshot doc) {
        try {
            Appointment a = buildAppointment(doc);
            populateOptionalFields(a, doc);
            return a;
        } catch (Exception e) {
            Log.e("AppointmentRepository", "Error converting document to appointment: " + e.getMessage(), e);
            return null;
        }
    }

    private Appointment buildAppointment(DocumentSnapshot doc) {
        Appointment a = new Appointment(
                getString(doc, "patientId"),
                getString(doc, "doctorId"),
                getString(doc, "doctorName"),
                getString(doc, "department"),
                getString(doc, "date"),
                getString(doc, "time")
        );
        a.setId(doc.getId());
        return a;
    }

    private void populateOptionalFields(Appointment a, DocumentSnapshot doc) {
        setStatusIfPresent(a, doc);
        setRiskLevelIfPresent(a, doc);
        a.setCreatedAt(getLongOrCurrentTime(doc, "createdAt"));
    }

    private String getString(DocumentSnapshot doc, String key) {
        String value = doc.getString(key);
        return value != null ? value : "";
    }

    private long getLongOrCurrentTime(DocumentSnapshot doc, String key) {
        Long value = doc.getLong(key);
        return value != null ? value : System.currentTimeMillis();
    }

    private void setStatusIfPresent(Appointment a, DocumentSnapshot doc) {
        String status = doc.getString("status");
        if (status != null) {
            a.setStatus(status);
        }
    }

    private void setRiskLevelIfPresent(Appointment a, DocumentSnapshot doc) {
        String riskLevel = doc.getString("riskLevel");
        if (riskLevel != null) {
            a.setRiskLevel(riskLevel);
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  CALLBACK INTERFACE
    // ─────────────────────────────────────────────────────────────
    public interface Callback<T> {
        void onResult(T data);
        void onError(String errorMessage);
    }
}