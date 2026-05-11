package com.example.mugangaconnect.data.local;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mugangaconnect.data.model.Appointment;

import java.util.ArrayList;
import java.util.List;

public class AppointmentDao {

    private final AppDatabase db;

    public AppointmentDao(AppDatabase db) {
        this.db = db;
    }

    // ✅ Insert or replace a single appointment
    public void upsert(Appointment a) {
        db.getWritableDatabase().insertWithOnConflict(
                AppDatabase.TABLE_APPOINTMENTS, null,
                toContentValues(a),
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    // ✅ Insert or replace a list of appointments (bulk)
    public void upsertAll(List<Appointment> appointments) {
        SQLiteDatabase w = db.getWritableDatabase();
        w.beginTransaction();
        try {
            for (Appointment a : appointments) {
                w.insertWithOnConflict(
                        AppDatabase.TABLE_APPOINTMENTS, null,
                        toContentValues(a),
                        SQLiteDatabase.CONFLICT_REPLACE);
            }
            w.setTransactionSuccessful();
        } finally {
            w.endTransaction();
        }
    }

    // ✅ Get all appointments for a patient
    public List<Appointment> getByPatient(String patientId) {
        List<Appointment> list = new ArrayList<>();
        Cursor c = db.getReadableDatabase().rawQuery(
                "SELECT * FROM " + AppDatabase.TABLE_APPOINTMENTS +
                " WHERE " + AppDatabase.COL_PATIENT_ID + " = ?" +
                " ORDER BY " + AppDatabase.COL_DATE + " ASC",
                new String[]{patientId});
        try {
            while (c.moveToNext()) list.add(fromCursor(c));
        } finally {
            c.close();
        }
        return list;
    }

    // ✅ Get appointments by patient + status (for History tabs)
    public List<Appointment> getByStatus(String patientId, String status) {
        List<Appointment> list = new ArrayList<>();
        Cursor c = db.getReadableDatabase().rawQuery(
                "SELECT * FROM " + AppDatabase.TABLE_APPOINTMENTS +
                " WHERE " + AppDatabase.COL_PATIENT_ID + " = ?" +
                " AND " + AppDatabase.COL_STATUS + " = ?" +
                " ORDER BY " + AppDatabase.COL_DATE + " ASC",
                new String[]{patientId, status});
        try {
            while (c.moveToNext()) list.add(fromCursor(c));
        } finally {
            c.close();
        }
        return list;
    }

    // ✅ Update only the status column
    public void updateStatus(String appointmentId, String status) {
        ContentValues cv = new ContentValues();
        cv.put(AppDatabase.COL_STATUS, status);
        db.getWritableDatabase().update(
                AppDatabase.TABLE_APPOINTMENTS, cv,
                AppDatabase.COL_ID + "=?",
                new String[]{appointmentId});
    }

    // ✅ Update date and time (reschedule)
    public void updateDateAndTime(String appointmentId, String date, String time) {
        ContentValues cv = new ContentValues();
        cv.put(AppDatabase.COL_DATE, date);
        cv.put(AppDatabase.COL_TIME, time);
        db.getWritableDatabase().update(
                AppDatabase.TABLE_APPOINTMENTS, cv,
                AppDatabase.COL_ID + "=?",
                new String[]{appointmentId});
    }

    // ✅ Delete an appointment
    public void delete(String appointmentId) {
        db.getWritableDatabase().delete(
                AppDatabase.TABLE_APPOINTMENTS,
                AppDatabase.COL_ID + "=?",
                new String[]{appointmentId});
    }

    // ─────────────────────────────────────────
    //  HELPERS
    // ─────────────────────────────────────────
    private ContentValues toContentValues(Appointment a) {
        ContentValues cv = new ContentValues();
        cv.put(AppDatabase.COL_ID,          a.getId());
        cv.put(AppDatabase.COL_PATIENT_ID,  a.getPatientId());
        cv.put(AppDatabase.COL_DOCTOR_ID,   a.getDoctorId());
        cv.put(AppDatabase.COL_DOCTOR_NAME, a.getDoctorName());
        cv.put(AppDatabase.COL_DEPARTMENT,  a.getDepartment());
        cv.put(AppDatabase.COL_DATE,        a.getDate());
        cv.put(AppDatabase.COL_TIME,        a.getTime());
        cv.put(AppDatabase.COL_STATUS,      a.getStatus());
        cv.put(AppDatabase.COL_RISK_LEVEL,  a.getRiskLevel());
        cv.put(AppDatabase.COL_CREATED_AT,  a.getCreatedAt());
        return cv;
    }

    private Appointment fromCursor(Cursor c) {
        Appointment a = new Appointment();
        a.setId(c.getString(c.getColumnIndexOrThrow(AppDatabase.COL_ID)));
        a.setPatientId(c.getString(c.getColumnIndexOrThrow(AppDatabase.COL_PATIENT_ID)));
        a.setDoctorId(c.getString(c.getColumnIndexOrThrow(AppDatabase.COL_DOCTOR_ID)));
        a.setDoctorName(c.getString(c.getColumnIndexOrThrow(AppDatabase.COL_DOCTOR_NAME)));
        a.setDepartment(c.getString(c.getColumnIndexOrThrow(AppDatabase.COL_DEPARTMENT)));
        a.setDate(c.getString(c.getColumnIndexOrThrow(AppDatabase.COL_DATE)));
        a.setTime(c.getString(c.getColumnIndexOrThrow(AppDatabase.COL_TIME)));
        a.setStatus(c.getString(c.getColumnIndexOrThrow(AppDatabase.COL_STATUS)));
        a.setRiskLevel(c.getString(c.getColumnIndexOrThrow(AppDatabase.COL_RISK_LEVEL)));
        a.setCreatedAt(c.getLong(c.getColumnIndexOrThrow(AppDatabase.COL_CREATED_AT)));
        return a;
    }
}