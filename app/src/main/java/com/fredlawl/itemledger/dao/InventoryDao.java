package com.fredlawl.itemledger.dao;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface InventoryDao {
    @Query("SELECT it.item FROM InventoryItem it GROUP BY it.item ORDER BY it.item ASC")
    List<String> getNames();

    @Query("UPDATE `Transaction` SET item = :newName WHERE item = :currentName")
    void changeItemName(String currentName, String newName);
}
