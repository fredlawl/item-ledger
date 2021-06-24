package com.fredlawl.itemledger.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.fredlawl.itemledger.entity.Character;
import com.fredlawl.itemledger.entity.Transaction;

import java.util.List;
import java.util.UUID;

@Dao
public interface CharacterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Character character);

    @Query("SELECT * FROM Character")
    List<Character> getAll();

    @Query("SELECT * FROM InventoryItem WHERE character_id = :characterId")
    List<InventoryItem> getInventory(UUID characterId);

    @Query("SELECT t.* FROM `Transaction` t WHERE t.character_id = :characterId ORDER BY t.session DESC, t.transaction_on DESC, t.item ASC")
    List<Transaction> getLedger(UUID characterId);
}
