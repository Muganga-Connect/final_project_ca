package com.example.mugangaconnect.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.mugangaconnect.data.model.ChatMessage;

import java.util.List;

@Dao
public interface ChatDao {
    @Insert
    long insert(ChatMessage msg);

    @Query("SELECT * FROM chat_messages WHERE patientId = :patientId ORDER BY timestamp ASC")
    List<ChatMessage> getHistory(String patientId);

    @Query("DELETE FROM chat_messages WHERE patientId = :patientId")
    void clearHistory(String patientId);
}
