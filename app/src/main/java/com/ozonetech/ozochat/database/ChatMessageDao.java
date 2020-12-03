package com.ozonetech.ozochat.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.ozonetech.ozochat.database.entity.ChatRoom;
import com.ozonetech.ozochat.model.Message;

import java.util.List;

@Dao
public interface ChatMessageDao {
    @Query("SELECT * FROM message")
    List<Message> getAll();

    /*@Query("SELECT * FROM message where group_id : groupId")
    public abstract List<Message> getAllNew(String groupId);*/
    @RawQuery
    List<Message> getGroupList(SupportSQLiteQuery query);

    @RawQuery
    int deleteGroupChat(SupportSQLiteQuery query);


    @Insert
    void insert(Message chatRoom);


    @Delete
    void delete(Message chatRoom);

    @Update
    void update(Message chatRoom);

    @Query("DELETE FROM message")
    void deleteAll();
}
