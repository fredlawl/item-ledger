package com.fredlawl.itemledger.entity;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;
import java.util.UUID;
import java.util.jar.Attributes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Character {
    public static final String ENCODING_SEPARATOR = " - ";
    public static final int ENCODING_SEPARATOR_LENGTH = ENCODING_SEPARATOR.length();

    @PrimaryKey
    @NonNull
    @EqualsAndHashCode.Include
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

    public static NamePart extractNamePart(String encodedCharacter) {
        int splitIndex = encodedCharacter.indexOf(" - ", 0);

        return new NamePart(
            encodedCharacter.substring(0, splitIndex),
            encodedCharacter.substring(splitIndex + ENCODING_SEPARATOR_LENGTH)
        );
    }

    public Character() {
        id = UUID.randomUUID();
        createdOn = Instant.now();
        savedSession = 0;
    }

    public Character(UUID id) {
        this();
        this.id = id;
    }

    public String encode() {
        return getNamePart().toString();
    }

    public NamePart getNamePart() {
        return new NamePart(getCharacter(), getCampaign());
    }

    @EqualsAndHashCode
    @Getter
    @AllArgsConstructor
    public static class NamePart {
        private final String character;
        private final String campaign;

        @Override
        public String toString() {
            return character + ENCODING_SEPARATOR + campaign;
        }
    }
}
