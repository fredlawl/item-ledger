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
version = 3,
entities = {
    Character.class,
    Transaction.class
},
views = {InventoryItem.class})
@TypeConverters({
    UUIDConverter.class,
    InstantConverter.class,
    BigDecimalConverter.class
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
            .addMigrations(MIGRATION_2_3)
            .build();
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Character ADD COLUMN saved_session INTEGER NOT NULL DEFAULT 0");
        }
    };

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            String currentTransactionTable = "Transaction";
            String tmpTransactionTable = "Transaction_MigrationTmp";
            String inventoryViewTable = "InventoryItem";
            database.beginTransaction();
            try {
                database.execSQL("DROP VIEW IF EXISTS `" + inventoryViewTable + "`");
                database.execSQL("CREATE TABLE IF NOT EXISTS `" + tmpTransactionTable + "` (`id` TEXT NOT NULL, `character_id` TEXT NOT NULL, `session` INTEGER NOT NULL DEFAULT 0, `item` TEXT NOT NULL, `quantity` TEXT, `transaction_on` INTEGER NOT NULL, `memo` TEXT NOT NULL, `created_on` INTEGER NOT NULL, PRIMARY KEY(`id`))");
                database.execSQL("INSERT INTO `" + tmpTransactionTable + "` SELECT * FROM `" + currentTransactionTable + "`");
                database.execSQL("DROP TABLE `" + currentTransactionTable + "`");
                database.execSQL("ALTER TABLE `" + tmpTransactionTable + "` RENAME TO `" + currentTransactionTable + "`");
                database.execSQL("CREATE VIEW `" + inventoryViewTable + "` AS SELECT t.character_id, t.item, SUM(t.quantity) AS qty FROM `Transaction` t GROUP BY t.character_id, t.item");
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        }
    };


    protected AppDatabase() {}

    public abstract CharacterDao characterDao();
    public abstract TransactionDao transactionDao();
    public abstract InventoryDao inventoryDao();
}
