package com.example.mugangaconnect.data.local;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mugangaconnect.data.model.User;

public class UserDao {
    private final AppDatabase db;

    public UserDao(AppDatabase db) {
        this.db = db;
    }

    public void upsert(User user) {
        if (user == null || user.getUid() == null) return;
        ContentValues cv = new ContentValues();
        cv.put(AppDatabase.COL_USER_UID, user.getUid());
        cv.put(AppDatabase.COL_USER_FULL_NAME, user.getFullName());
        cv.put(AppDatabase.COL_USER_EMAIL, user.getEmail());
        cv.put(AppDatabase.COL_USER_PHONE, user.getPhone());
        cv.put(AppDatabase.COL_USER_DOB, user.getDob());
        cv.put(AppDatabase.COL_USER_GENDER, user.getGender());
        cv.put(AppDatabase.COL_USER_BLOOD_TYPE, user.getBloodType());
        cv.put(AppDatabase.COL_USER_INSURANCE_ID, user.getInsuranceId());
        cv.put(AppDatabase.COL_USER_ALLERGIES, user.getAllergies());
        cv.put(AppDatabase.COL_USER_EMERGENCY_CONTACT, user.getEmergencyContact());

        db.getWritableDatabase().insertWithOnConflict(
                AppDatabase.TABLE_USERS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public User getByUid(String uid) {
        if (uid == null) return null;
        Cursor c = db.getReadableDatabase().query(
                AppDatabase.TABLE_USERS, null,
                AppDatabase.COL_USER_UID + "=?",
                new String[]{uid}, null, null, null);
        try {
            if (!c.moveToFirst()) return null;
            User user = new User();
            user.setUid(c.getString(c.getColumnIndexOrThrow(AppDatabase.COL_USER_UID)));
            user.setFullName(c.getString(c.getColumnIndexOrThrow(AppDatabase.COL_USER_FULL_NAME)));
            user.setEmail(c.getString(c.getColumnIndexOrThrow(AppDatabase.COL_USER_EMAIL)));
            user.setPhone(c.getString(c.getColumnIndexOrThrow(AppDatabase.COL_USER_PHONE)));
            user.setDob(c.getString(c.getColumnIndexOrThrow(AppDatabase.COL_USER_DOB)));
            user.setGender(c.getString(c.getColumnIndexOrThrow(AppDatabase.COL_USER_GENDER)));
            user.setBloodType(c.getString(c.getColumnIndexOrThrow(AppDatabase.COL_USER_BLOOD_TYPE)));
            user.setInsuranceId(c.getString(c.getColumnIndexOrThrow(AppDatabase.COL_USER_INSURANCE_ID)));
            user.setAllergies(c.getString(c.getColumnIndexOrThrow(AppDatabase.COL_USER_ALLERGIES)));
            user.setEmergencyContact(c.getString(c.getColumnIndexOrThrow(AppDatabase.COL_USER_EMERGENCY_CONTACT)));
            return user;
        } finally {
            c.close();
        }
    }
}
