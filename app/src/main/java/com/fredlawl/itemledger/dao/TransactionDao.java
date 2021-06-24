package com.fredlawl.itemledger.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.fredlawl.itemledger.entity.Transaction;

import java.util.List;
import java.util.UUID;

@Dao
public interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Transaction transaction);
}
