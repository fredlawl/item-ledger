package com.fredlawl.itemledger.dao;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;
import java.util.UUID;

@Dao
public interface InventoryDao {
    @Query("SELECT it.item FROM InventoryItem it WHERE character_id = :characterId GROUP BY it.item ORDER BY it.item ASC")
    List<String> getNames(UUID characterId);

    @Query("UPDATE `Transaction` SET item = :newName WHERE item = :currentName AND character_id = :characterId")
    void changeItemName(UUID characterId, String currentName, String newName);
}
