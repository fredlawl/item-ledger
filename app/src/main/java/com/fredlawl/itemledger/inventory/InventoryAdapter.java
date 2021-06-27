package com.fredlawl.itemledger.inventory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fredlawl.itemledger.R;

import java.util.List;

import lombok.Getter;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryItem> {

    @Getter
    public static class InventoryItem extends RecyclerView.ViewHolder {
        private final TextView item;
        private final TextView quantity;

        public InventoryItem(View view) {
            super(view);

            item = (TextView) view.findViewById(R.id.tvItem);
            quantity = (TextView) view.findViewById(R.id.tvQuantity);
        }
    }

    private final List<com.fredlawl.itemledger.dao.InventoryItem> inventoryItems;

    public InventoryAdapter(List<com.fredlawl.itemledger.dao.InventoryItem> inventoryItems) {
        this.inventoryItems = inventoryItems;
    }

    @Override
    public InventoryAdapter.InventoryItem onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_inventory_item, viewGroup, false);

        return new InventoryAdapter.InventoryItem(view);
    }

    @Override
    public void onBindViewHolder(InventoryAdapter.InventoryItem viewHolder, final int position) {
        com.fredlawl.itemledger.dao.InventoryItem item = inventoryItems.get(position);

        viewHolder.getItem().setText(item.getItem());

        int quantity = item.getQuantity();
        String quantityFmt = String.valueOf(quantity);
        viewHolder.getQuantity().setText(quantityFmt);
    }

    @Override
    public int getItemCount() {
        return inventoryItems.size();
    }
}
