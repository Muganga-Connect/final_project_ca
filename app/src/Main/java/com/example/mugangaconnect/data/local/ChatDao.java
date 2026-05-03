package com.example.mugangaconnect.data.local;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.mugangaconnect.data.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatDao {
    private final AppDatabase db;

    public ChatDao(AppDatabase db) {
        this.db = db;
    }

    public void insert(ChatMessage msg) {
        ContentValues cv = new ContentValues();
        cv.put(AppDatabase.COL_CHAT_PID, msg.getPatientId());
        cv.put(AppDatabase.COL_CHAT_ROLE, msg.getRole());
        cv.put(AppDatabase.COL_CHAT_TEXT, msg.getContent());
        cv.put(AppDatabase.COL_CHAT_TIME, msg.getTimestamp());
        long id = db.getWritableDatabase().insert(AppDatabase.TABLE_CHAT, null, cv);
        msg.setId(id);
    }

    public List<ChatMessage> getHistory(String patientId) {
        List<ChatMessage> list = new ArrayList<>();
        Cursor c = db.getReadableDatabase().query(
                AppDatabase.TABLE_CHAT, null,
                AppDatabase.COL_CHAT_PID + "=?",
                new String[]{patientId}, null, null,
                AppDatabase.COL_CHAT_TIME + " ASC");
        try {
            int idCol = c.getColumnIndexOrThrow(AppDatabase.COL_CHAT_ID);
            int pidCol = c.getColumnIndexOrThrow(AppDatabase.COL_CHAT_PID);
            int roleCol = c.getColumnIndexOrThrow(AppDatabase.COL_CHAT_ROLE);
            int textCol = c.getColumnIndexOrThrow(AppDatabase.COL_CHAT_TEXT);
            int timeCol = c.getColumnIndexOrThrow(AppDatabase.COL_CHAT_TIME);
            while (c.moveToNext()) {
                ChatMessage m = new ChatMessage();
                m.setId(c.getLong(idCol));
                m.setPatientId(c.getString(pidCol));
                m.setRole(c.getString(roleCol));
                m.setContent(c.getString(textCol));
                m.setTimestamp(c.getLong(timeCol));
                list.add(m);
            }
        } finally {
            c.close();
        }
        return list;
    }

    public void clearHistory(String patientId) {
        db.getWritableDatabase().delete(AppDatabase.TABLE_CHAT,
                AppDatabase.COL_CHAT_PID + "=?", new String[]{patientId});
    }
}
