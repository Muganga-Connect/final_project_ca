package com.example.mugangaconnect.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "muganga.db";
    private static final int DB_VERSION = 1;

    // Appointments table
    public static final String TABLE_APPOINTMENTS = "appointments";
    public static final String COL_ID = "id";
    public static final String COL_PATIENT_ID = "patient_id";
    public static final String COL_DOCTOR_ID = "doctor_id";
    public static final String COL_DOCTOR_NAME = "doctor_name";
    public static final String COL_DEPARTMENT = "department";
    public static final String COL_DATE = "date";
    public static final String COL_TIME = "time";
    public static final String COL_STATUS = "status";
    public static final String COL_RISK_LEVEL = "risk_level";
    public static final String COL_CREATED_AT = "created_at";

    private static final String CREATE_APPOINTMENTS =
            "CREATE TABLE " + TABLE_APPOINTMENTS + " (" +
            COL_ID + " TEXT PRIMARY KEY, " +
            COL_PATIENT_ID + " TEXT NOT NULL, " +
            COL_DOCTOR_ID + " TEXT, " +
            COL_DOCTOR_NAME + " TEXT, " +
            COL_DEPARTMENT + " TEXT, " +
            COL_DATE + " TEXT, " +
            COL_TIME + " TEXT, " +
            COL_STATUS + " TEXT DEFAULT 'UPCOMING', " +
            COL_RISK_LEVEL + " TEXT DEFAULT 'LOW', " +
            COL_CREATED_AT + " INTEGER)";

    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new AppDatabase(context.getApplicationContext());
        }
        return instance;
    }

    private AppDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_APPOINTMENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
        onCreate(db);
    }
}
