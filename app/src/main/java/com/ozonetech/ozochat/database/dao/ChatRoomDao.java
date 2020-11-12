package com.ozonetech.ozochat.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ozonetech.ozochat.database.entity.ChatRoom;

import java.util.List;

@Dao
public interface ChatRoomDao {

    @Query("SELECT * FROM chatroom")
    List<ChatRoom> getAll();

    @Insert
    void insert(ChatRoom chatRoom);

    @Delete
    void delete(ChatRoom chatRoom);

    @Update
    void update(ChatRoom chatRoom);

}
