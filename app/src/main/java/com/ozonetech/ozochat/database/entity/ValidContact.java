package com.ozonetech.ozochat.database.entity;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ozonetech.ozochat.model.ContactModel;
import com.ozonetech.ozochat.viewmodel.Contacts;

import java.util.List;

@Dao
public interface ValidContact {

    @Query("SELECT * FROM contactmodel ")
    List<ContactModel> getAllContact();

    @Insert
    void insert(ContactModel contactmodel);

    @Delete
    void delete(ContactModel contactmodel);

    @Update
    void update(ContactModel contactmodel);

    @Query("DELETE FROM contactmodel")
    void deleteAll();

}
