package com.fredlawl.itemledger.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
@Entity
public class Transaction {
    @PrimaryKey
    @NonNull
    private UUID id;

    @ColumnInfo(name = "character_id")
    @NonNull
    private UUID characterId;

    @ColumnInfo(name = "session", defaultValue = "0")
    private int session;

    @ColumnInfo(name = "item")
    @NonNull
    private String item;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "transaction_on")
    @NonNull
    private Instant transactionOn;

    @ColumnInfo(name = "created_on")
    @NonNull
    private Instant createdOn;

    public Transaction() {
        id = UUID.randomUUID();
        Instant now = Instant.now();
        transactionOn = now;
        createdOn = now;
        quantity = 1;
    }
}
