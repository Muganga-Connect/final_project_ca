package com.example.mugangaconnect.data.repository;

import android.content.Context;

import com.example.mugangaconnect.data.local.AppDatabase;
import com.example.mugangaconnect.data.local.AppointmentDao;
import com.example.mugangaconnect.data.model.Appointment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppointmentRepository {

    private static final String COLLECTION = "appointments";

    private final FirebaseFirestore remote;
    private final AppointmentDao local;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public interface Callback<T> {
        void onResult(T data);
        void onError(String message);
    }

    public AppointmentRepository(Context context) {
        this.remote = FirebaseFirestore.getInstance();
        this.local = new AppointmentDao(AppDatabase.getInstance(context));
    }

    /** Book a new appointment — writes to Firestore, caches locally */
    public void book(Appointment appointment, Callback<Appointment> callback) {
        String id = UUID.randomUUID().toString();
        appointment.setId(id);

        remote.collection(COLLECTION).document(id).set(appointment)
              .addOnSuccessListener(v -> {
                  executor.execute(() -> {
                      local.upsert(appointment);
                      callback.onResult(appointment);
                  });
              })
              .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /** Fetch all appointments for a patient — syncs remote → local cache */
    public void getForPatient(String patientId, Callback<List<Appointment>> callback) {
        remote.collection(COLLECTION)
              .whereEqualTo("patientId", patientId)
              .get()
              .addOnSuccessListener(snapshot -> {
                  List<Appointment> list = new ArrayList<>();
                  for (QueryDocumentSnapshot doc : snapshot) {
                      Appointment a = doc.toObject(Appointment.class);
                      a.setId(doc.getId());
                      list.add(a);
                  }
                  executor.execute(() -> {
                      local.upsertAll(list);
                      callback.onResult(list);
                  });
              })
              .addOnFailureListener(e -> {
                  // Fallback to local cache on network failure
                  executor.execute(() -> {
                      List<Appointment> cached = local.getByPatient(patientId);
                      callback.onResult(cached);
                  });
              });
    }

    /** Update appointment status (cancel / reschedule / attend) */
    public void updateStatus(String appointmentId, String patientId,
                             String newStatus, Callback<Void> callback) {
        remote.collection(COLLECTION).document(appointmentId)
              .get()
              .addOnSuccessListener(snapshot -> {
                  Appointment appointment = snapshot.toObject(Appointment.class);
                  if (appointment == null || appointment.getPatientId() == null ||
                          !appointment.getPatientId().equals(patientId)) {
                      callback.onError("Unauthorized");
                      return;
                  }
                  remote.collection(COLLECTION).document(appointmentId)
                          .update("status", newStatus)
                          .addOnSuccessListener(v -> executor.execute(() -> {
                              local.updateStatus(appointmentId, newStatus);
                              callback.onResult(null);
                          }))
                          .addOnFailureListener(e -> callback.onError(e.getMessage()));
              })
              .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /** Reschedule — updates date, time, and resets status to UPCOMING */
    public void reschedule(String appointmentId, String newDate, String newTime,
                           Callback<Void> callback) {
        remote.collection(COLLECTION).document(appointmentId)
              .update("date", newDate, "time", newTime,
                      "status", Appointment.Status.UPCOMING.name())
              .addOnSuccessListener(v -> {
                  executor.execute(() -> {
                      local.updateDateAndTime(appointmentId, newDate, newTime);
                      local.updateStatus(appointmentId, Appointment.Status.UPCOMING.name());
                  });
                  callback.onResult(null);
              })
              .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /** Get cached appointments by status (offline-first) */
    public void getCachedByStatus(String patientId, String status,
                                  Callback<List<Appointment>> callback) {
        executor.execute(() -> callback.onResult(local.getByStatus(patientId, status)));
    }

    /** Returns missed count and total for no-show prediction */
    public void getMissedStats(String patientId, Callback<int[]> callback) {
        executor.execute(() -> {
            int missed = local.countMissed(patientId);
            int total = local.countTotal(patientId);
            callback.onResult(new int[]{missed, total});
        });
    }
}
