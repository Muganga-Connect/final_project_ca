package com.example.mugangaconnect.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mugangaconnect.data.model.Appointment;

import java.util.ArrayList;
import java.util.List;

public class AppointmentDao {

    private final SQLiteOpenHelper helper;

    public AppointmentDao(Context context) {
        this.helper = AppDatabase.getInstance(context, true);
    }

    public void upsert(Appointment a) {
        helper.getWritableDatabase().insertWithOnConflict(
                AppDatabase.TABLE_APPOINTMENTS, null,
                toContentValues(a),
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void upsertAll(List<Appointment> appointments) {
        SQLiteDatabase w = helper.getWritableDatabase();
        w.beginTransaction();
        try {
            for (Appointment a : appointments) {
                w.insertWithOnConflict(AppDatabase.TABLE_APPOINTMENTS, null,
                        toContentValues(a), SQLiteDatabase.CONFLICT_REPLACE);
            }
            w.setTransactionSuccessful();
        } finally {
            w.endTransaction();
        }
    }

    public List<Appointment> getByPatient(String patientId) {
        List<Appointment> list = new ArrayList<>();
        Cursor c = helper.getReadableDatabase().rawQuery(
                "SELECT * FROM " + AppDatabase.TABLE_APPOINTMENTS +
                " WHERE " + AppDatabase.COL_PATIENT_ID + " = ?" +
                " ORDER BY " + AppDatabase.COL_DATE + " ASC",
                new String[]{patientId});
        try { while (c.moveToNext()) list.add(fromCursor(c)); } finally { c.close(); }
        return list;
    }

    public List<Appointment> getByStatus(String patientId, String status) {
        List<Appointment> list = new ArrayList<>();
        Cursor c = helper.getReadableDatabase().rawQuery(
                "SELECT * FROM " + AppDatabase.TABLE_APPOINTMENTS +
                " WHERE " + AppDatabase.COL_PATIENT_ID + " = ?" +
                " AND " + AppDatabase.COL_STATUS + " = ?" +
                " ORDER BY " + AppDatabase.COL_DATE + " ASC",
                new String[]{patientId, status});
        try { while (c.moveToNext()) list.add(fromCursor(c)); } finally { c.close(); }
        return list;
    }

    public void updateStatus(String appointmentId, String status) {
        ContentValues cv = new ContentValues();
        cv.put(AppDatabase.COL_STATUS, status);
        helper.getWritableDatabase().update(AppDatabase.TABLE_APPOINTMENTS, cv,
                AppDatabase.COL_ID + "=?", new String[]{appointmentId});
    }

    public void updateDateAndTime(String appointmentId, String date, String time) {
        ContentValues cv = new ContentValues();
        cv.put(AppDatabase.COL_DATE, date);
        cv.put(AppDatabase.COL_TIME, time);
        helper.getWritableDatabase().update(AppDatabase.TABLE_APPOINTMENTS, cv,
                AppDatabase.COL_ID + "=?", new String[]{appointmentId});
    }

    public void delete(String appointmentId) {
        helper.getWritableDatabase().delete(AppDatabase.TABLE_APPOINTMENTS,
                AppDatabase.COL_ID + "=?", new String[]{appointmentId});
    }

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
