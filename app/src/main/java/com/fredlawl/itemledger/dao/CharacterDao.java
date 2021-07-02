package com.fredlawl.itemledger.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.fredlawl.itemledger.entity.Character;
import com.fredlawl.itemledger.entity.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Dao
public interface CharacterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Character character);

    @Query("SELECT * FROM Character")
    List<Character> getAll();

    @Query("SELECT * FROM InventoryItem WHERE character_id = :characterId AND qty > 0")
    List<InventoryItem> getInventory(UUID characterId);

    @Query("SELECT t.* FROM `Transaction` t WHERE t.character_id = :characterId ORDER BY t.session DESC, t.transaction_on DESC, t.item ASC")
    List<Transaction> getLedger(UUID characterId);

    @Query("UPDATE Character SET saved_session = :newSession WHERE id = :characterId")
    void updateSession(UUID characterId, int newSession);

    @Query("SELECT * FROM character WHERE id = :characterId")
    Optional<Character> getById(UUID characterId);

    @Query("SELECT * FROM character WHERE character = :name AND campaign = :campaign ORDER BY created_on ASC, character ASC, campaign ASC")
    Optional<Character> getByNameAndCampaign(String name, String campaign);

    @Query("SELECT COUNT(*) FROM Character")
    int hasCharacters();

    @Query("DELETE FROM Character WHERE character = :name AND campaign = :campaign")
    void deleteCharacterByNameAndCampaign(String name, String campaign);
}
