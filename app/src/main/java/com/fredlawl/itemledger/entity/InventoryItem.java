package com.fredlawl.itemledger.entity;

import androidx.room.ColumnInfo;
import androidx.room.DatabaseView;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@DatabaseView("SELECT t.character_id, t.item, SUM(t.quantity) AS qty "
            + "FROM `Transaction` t "
            + "GROUP BY t.character_id, t.item")
public class InventoryItem {
    public static final BigDecimal MAX_QUANTITY = Transaction.MAX_QUANTITY;

    @ColumnInfo(name = "character_id")
    private UUID characterId;

    @ColumnInfo(name = "item")
    private String item;

    @ColumnInfo(name = "qty")
    private int quantity;
}
