package com.fredlawl.itemledger.dao;

import androidx.room.ColumnInfo;
import androidx.room.DatabaseView;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@DatabaseView("SELECT t.character_id, t.item, SUM(t.quantity) AS qty "
            + "FROM `Transaction` t "
            + "GROUP BY t.character_id, t.item")
public class InventoryItem {
    @ColumnInfo(name = "character_id")
    protected UUID characterId;

    @ColumnInfo(name = "item")
    protected String item;

    @ColumnInfo(name = "qty")
    protected int quantity;
}
