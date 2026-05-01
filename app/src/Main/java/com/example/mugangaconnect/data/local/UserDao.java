package com.example.mugangaconnect.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.mugangaconnect.data.model.User;

@Dao
public interface UserDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(User user);
    
    @Query("SELECT * FROM users WHERE uid = :uid")
    User getByUid(String uid);
    
    @Update
    void update(User user);
    
    @Query("DELETE FROM users WHERE uid = :uid")
    void deleteByUid(String uid);
}