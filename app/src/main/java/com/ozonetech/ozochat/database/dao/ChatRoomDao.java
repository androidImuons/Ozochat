package com.ozonetech.ozochat.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ozonetech.ozochat.database.entity.ChatRoom;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface ChatRoomDao {

    @Query("SELECT * FROM chatroom ")
    List<ChatRoom> getAll();

    @Query("SELECT * FROM chatroom where user_contact_no=:mobile")
    List<ChatRoom> getRecentList(String mobile);

    @Insert
    void insert(ChatRoom chatRoom);


    @Delete
    void delete(ChatRoom chatRoom);

    @Update
    void update(ChatRoom chatRoom);

    @Query("DELETE FROM chatroom")
    void deleteAll();

}
