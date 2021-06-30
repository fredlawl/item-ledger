package com.fredlawl.itemledger.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.fredlawl.itemledger.entity.Transaction;

@Dao
public interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Transaction transaction);
}
