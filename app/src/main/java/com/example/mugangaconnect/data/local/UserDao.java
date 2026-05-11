package com.example.mugangaconnect.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mugangaconnect.data.model.User;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(User user);

    @Query("SELECT * FROM users WHERE uid = :uid")
    User getByUid(String uid);
}
