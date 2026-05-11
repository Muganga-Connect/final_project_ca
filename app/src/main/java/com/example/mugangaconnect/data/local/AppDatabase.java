package com.example.mugangaconnect.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.mugangaconnect.data.model.User;
import com.example.mugangaconnect.data.model.ChatMessage;

@Database(
    entities = {User.class, ChatMessage.class},
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    
    public abstract UserDao userDao();
    public abstract ChatDao chatDao();
    
    private static volatile AppDatabase INSTANCE;
    
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "muganga_db"
                        )
                        .fallbackToDestructiveMigration()
                        .build();
                }
            }
        }
        return instance;
    }

    private AppDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS);
        db.execSQL(CREATE_APPOINTMENTS);
        db.execSQL(CREATE_CHAT);
        createIndexes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(CREATE_CHAT);
        }
        if (oldVersion < 3) {
            db.execSQL(CREATE_USERS);
        }
        if (oldVersion < 5) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT);
            db.execSQL(CREATE_APPOINTMENTS);
            db.execSQL(CREATE_CHAT);
            createIndexes(db);
        }
    }

    private void createIndexes(SQLiteDatabase db) {
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_appointments_patient ON " +
                TABLE_APPOINTMENTS + "(" + COL_PATIENT_ID + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_appointments_doctor ON " +
                TABLE_APPOINTMENTS + "(" + COL_DOCTOR_ID + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_chat_patient ON " +
                TABLE_CHAT + "(" + COL_CHAT_PID + ")");
    }
}
