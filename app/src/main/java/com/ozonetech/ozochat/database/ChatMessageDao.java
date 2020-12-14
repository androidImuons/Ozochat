package com.ozonetech.ozochat.database;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;
import com.ozonetech.ozochat.database.entity.ChatRoom;
import com.ozonetech.ozochat.model.Message;
import java.util.List;

@Dao
public interface ChatMessageDao {
    @Query("SELECT * FROM message where group_id=:group_id")
    List<Message> getAll(String group_id);
    /*@Query("SELECT * FROM message where group_id : groupId")
    public abstract List<Message> getAllNew(String groupId);*/
    @RawQuery
    List<Message> getGroupList(SupportSQLiteQuery query);

    @RawQuery
    int deleteGroupChat(SupportSQLiteQuery query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Message chatRoom);

    @Delete
    void delete(Message chatRoom);

    @Update
    void update(Message chatRoom);

    @Query("UPDATE message SET created=:time WHERE id = :id")
    int updated_time(String time,int id);


    @Query("DELETE FROM message")
    void deleteAll();

    @Query("SELECT * from message WHERE id= :id")
    List<Message> getItemById(int id);

//    @Insert
//    void insert(Message products);


    @Query("UPDATE message SET storageFile=:file WHERE id = :id")
    void updateStorage(String file, int id);

    @Query("Select * from message WHERE group_id = :g_id AND id=:id")
    Message getFile(String g_id, int id);
}
