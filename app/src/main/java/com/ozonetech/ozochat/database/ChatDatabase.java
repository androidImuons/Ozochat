package com.ozonetech.ozochat.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.ozonetech.ozochat.database.dao.ChatRoomDao;
import com.ozonetech.ozochat.database.entity.ChatRoom;

@Database(entities = {ChatRoom.class}, version = 1)
public abstract class ChatDatabase extends RoomDatabase {

    public abstract ChatRoomDao chatRoomDao();
}
