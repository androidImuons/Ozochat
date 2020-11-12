package com.ozonetech.ozochat.database;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {
    private Context mCtx;
    private static DatabaseClient mInstance;

    //our app database object
    private ChatDatabase chatDatabase;

    private DatabaseClient(Context mCtx) {
        this.mCtx = mCtx;

        //creating the app database with Room database builder
        //MyToDos is the name of the database
        chatDatabase = Room.databaseBuilder(mCtx, ChatDatabase.class, "MyChats").build();
    }

    public static synchronized DatabaseClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new DatabaseClient(mCtx);
        }
        return mInstance;
    }

    public ChatDatabase getChatDatabase() {
        return chatDatabase;
    }
}