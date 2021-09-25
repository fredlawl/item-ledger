package com.fredlawl.itemledger.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.Data;
import lombok.Getter;

@Data
@Entity
public class Transaction {
    public static final BigDecimal MIN_SESSION = new BigDecimal("0");
    public static final BigDecimal MAX_SESSION = new BigDecimal("9999");

    public static final BigDecimal MIN_QUANTITY = new BigDecimal("-999999");
    public static final BigDecimal MAX_QUANTITY = new BigDecimal("999999");

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
    private BigDecimal quantity;

    @ColumnInfo(name = "transaction_on")
    @NonNull
    private Instant transactionOn;

    @ColumnInfo(name = "memo")
    @NonNull
    private String memo;

    @ColumnInfo(name = "created_on")
    @NonNull
    private Instant createdOn;

    public Transaction() {
        id = UUID.randomUUID();
        Instant now = Instant.now();
        transactionOn = now;
        createdOn = now;
        quantity = BigDecimal.ONE;
    }
}
