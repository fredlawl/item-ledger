package com.fredlawl.itemledger.dao;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.fredlawl.itemledger.entity.Character;
import com.fredlawl.itemledger.entity.Transaction;

@Database(entities = {
    Character.class,
    Transaction.class
}, views = {InventoryItem.class}, version = 1)
@TypeConverters({
    UUIDConverter.class,
    InstantConverter.class
})
public abstract class AppDatabase extends RoomDatabase {
    public static final String DB_NAME = "itemledger.db";
    private static volatile AppDatabase instance = null;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }

        return instance;
    }

    public static AppDatabase create(final Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
            .allowMainThreadQueries()
            .build();
    }

    protected AppDatabase() {}

    public abstract CharacterDao characterDao();
}
