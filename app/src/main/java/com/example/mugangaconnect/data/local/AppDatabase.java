package com.example.mugangaconnect.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mugangaconnect.data.model.ChatMessage;
import com.example.mugangaconnect.data.model.User;

// ── Room database (User + ChatMessage) ───────────────────────────────────────
@Database(
    entities = {User.class, ChatMessage.class},
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    // ── Room DAOs ─────────────────────────────────────────────────────────────
    public abstract UserDao userDao();
    public abstract ChatDao chatDao();

    // ── Room singleton ────────────────────────────────────────────────────────
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "muganga_room_db"
                        )
                        .fallbackToDestructiveMigration()
                        .build();
                }
            }
        }
        return INSTANCE;
    }

    // ── SQLite constants used by AppointmentDao ───────────────────────────────
    static final String DB_NAME            = "muganga_appointments.db";
    static final int    DB_VERSION         = 5;

    public static final String TABLE_APPOINTMENTS = "appointments";
    public static final String COL_ID             = "id";
    public static final String COL_PATIENT_ID     = "patientId";
    public static final String COL_DOCTOR_ID      = "doctorId";
    public static final String COL_DOCTOR_NAME    = "doctorName";
    public static final String COL_DEPARTMENT     = "department";
    public static final String COL_DATE           = "date";
    public static final String COL_TIME           = "time";
    public static final String COL_STATUS         = "status";
    public static final String COL_RISK_LEVEL     = "riskLevel";
    public static final String COL_CREATED_AT     = "createdAt";

    static final String TABLE_CHAT    = "chat_messages";
    static final String COL_CHAT_PID  = "patientId";

    private static final String CREATE_APPOINTMENTS =
        "CREATE TABLE IF NOT EXISTS " + TABLE_APPOINTMENTS + " (" +
        COL_ID          + " TEXT PRIMARY KEY, " +
        COL_PATIENT_ID  + " TEXT, " +
        COL_DOCTOR_ID   + " TEXT, " +
        COL_DOCTOR_NAME + " TEXT, " +
        COL_DEPARTMENT  + " TEXT, " +
        COL_DATE        + " TEXT, " +
        COL_TIME        + " TEXT, " +
        COL_STATUS      + " TEXT, " +
        COL_RISK_LEVEL  + " TEXT, " +
        COL_CREATED_AT  + " INTEGER)";

    // ── SQLiteOpenHelper singleton for AppointmentDao ─────────────────────────
    private static volatile SQLiteOpenHelper APPT_HELPER;

    public static SQLiteOpenHelper getInstance(Context context, boolean forAppointments) {
        if (APPT_HELPER == null) {
            synchronized (AppDatabase.class) {
                if (APPT_HELPER == null) {
                    APPT_HELPER = new SQLiteOpenHelper(
                            context.getApplicationContext(), DB_NAME, null, DB_VERSION) {
                        @Override
                        public void onCreate(SQLiteDatabase db) {
                            db.execSQL(CREATE_APPOINTMENTS);
                            db.execSQL("CREATE INDEX IF NOT EXISTS idx_appt_patient ON " +
                                    TABLE_APPOINTMENTS + "(" + COL_PATIENT_ID + ")");
                        }
                        @Override
                        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                            if (oldVersion < 5) {
                                db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
                                onCreate(db);
                            }
                        }
                    };
                }
            }
        }
        return APPT_HELPER;
    }

    public SQLiteDatabase getWritableDatabase(boolean forAppointments) {
        return APPT_HELPER != null ? APPT_HELPER.getWritableDatabase() : null;
    }
    public SQLiteDatabase getReadableDatabase(boolean forAppointments) {
        return APPT_HELPER != null ? APPT_HELPER.getReadableDatabase() : null;
    }
}
