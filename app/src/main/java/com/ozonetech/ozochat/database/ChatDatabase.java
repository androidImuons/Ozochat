package com.ozonetech.ozochat.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ozonetech.ozochat.database.dao.ChatRoomDao;
import com.ozonetech.ozochat.database.entity.ChatRoom;
import com.ozonetech.ozochat.database.entity.ValidContact;
import com.ozonetech.ozochat.model.ContactModel;
import com.ozonetech.ozochat.model.Message;
import com.ozonetech.ozochat.viewmodel.Contacts;

@Database(entities = {ChatRoom.class , Message.class, ContactModel.class}, version = 1)
public abstract class ChatDatabase extends RoomDatabase {

    private static ChatDatabase chatDatabase;
    public abstract ChatRoomDao chatRoomDao();
    public abstract ChatMessageDao chatMessageDao();
    public abstract ValidContact ValidContact();
    private Context mCtx;
    public static ChatDatabase getInstance(Context context) {
        if (null == chatDatabase) {
            chatDatabase = DatabaseClient(context);
        }
        return chatDatabase;
    }
    private static ChatDatabase DatabaseClient(Context mCtx) {


        //creating the app database with Room database builder
        //MyToDos is the name of the database
         return chatDatabase = Room.databaseBuilder(mCtx, ChatDatabase.class, "OzoChatDB").allowMainThreadQueries().build();
    }
}
