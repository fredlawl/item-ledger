package com.fredlawl.itemledger.dao;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.fredlawl.itemledger.entity.Character;
import com.fredlawl.itemledger.entity.InventoryItem;
import com.fredlawl.itemledger.entity.Transaction;

@Database(
version = 2,
entities = {
    Character.class,
    Transaction.class
},
views = {InventoryItem.class})
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
            .addMigrations(MIGRATION_1_2)
            .build();
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Character ADD COLUMN saved_session INTEGER NOT NULL DEFAULT 0");
        }
    };

    protected AppDatabase() {}

    public abstract CharacterDao characterDao();
    public abstract TransactionDao transactionDao();
    public abstract InventoryDao inventoryDao();
}
