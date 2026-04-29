package com.example.mugangaconnect.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "muganga.db";
    private static final int DB_VERSION = 3;

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

    // Users table
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_UID = "uid";
    public static final String COL_USER_FULL_NAME = "full_name";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PHONE = "phone";
    public static final String COL_USER_DOB = "dob";
    public static final String COL_USER_GENDER = "gender";
    public static final String COL_USER_BLOOD_TYPE = "blood_type";
    public static final String COL_USER_INSURANCE_ID = "insurance_id";
    public static final String COL_USER_ALLERGIES = "allergies";
    public static final String COL_USER_EMERGENCY_CONTACT = "emergency_contact";

    // Chat messages table
    public static final String TABLE_CHAT = "chat_messages";
    public static final String COL_CHAT_ID = "id";
    public static final String COL_CHAT_PID = "patient_id";
    public static final String COL_CHAT_ROLE = "role";
    public static final String COL_CHAT_TEXT = "content";
    public static final String COL_CHAT_TIME = "timestamp";

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

    private static final String CREATE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COL_USER_UID + " TEXT PRIMARY KEY, " +
                    COL_USER_FULL_NAME + " TEXT, " +
                    COL_USER_EMAIL + " TEXT, " +
                    COL_USER_PHONE + " TEXT, " +
                    COL_USER_DOB + " TEXT, " +
                    COL_USER_GENDER + " TEXT, " +
                    COL_USER_BLOOD_TYPE + " TEXT, " +
                    COL_USER_INSURANCE_ID + " TEXT, " +
                    COL_USER_ALLERGIES + " TEXT, " +
                    COL_USER_EMERGENCY_CONTACT + " TEXT)";

    private static final String CREATE_CHAT =
            "CREATE TABLE " + TABLE_CHAT + " (" +
                    COL_CHAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_CHAT_PID + " TEXT NOT NULL, " +
                    COL_CHAT_ROLE + " TEXT NOT NULL, " +
                    COL_CHAT_TEXT + " TEXT NOT NULL, " +
                    COL_CHAT_TIME + " INTEGER NOT NULL)";

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
        db.execSQL(CREATE_USERS);
        db.execSQL(CREATE_CHAT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(CREATE_CHAT);
        }
        if (oldVersion < 3) {
            db.execSQL(CREATE_USERS);
        }
    }
}
