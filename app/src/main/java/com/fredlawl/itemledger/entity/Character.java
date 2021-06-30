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
public class Character {
    @PrimaryKey
    @NonNull
    private UUID id;

    @ColumnInfo(name = "character")
    @NonNull
    private String character;

    @ColumnInfo(name = "campaign")
    @NonNull
    private String campaign;

    @ColumnInfo(name = "created_on")
    @NonNull
    private Instant createdOn;

    @ColumnInfo(name = "saved_session")
    @NonNull
    private int savedSession;

    public Character() {
        id = UUID.randomUUID();
        createdOn = Instant.now();
        savedSession = 0;
    }
}
