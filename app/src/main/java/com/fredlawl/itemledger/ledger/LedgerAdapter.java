package com.fredlawl.itemledger.ledger;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fredlawl.itemledger.R;
import com.fredlawl.itemledger.entity.Transaction;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import lombok.Getter;

import static androidx.core.content.ContextCompat.getColor;

public class LedgerAdapter extends RecyclerView.Adapter<LedgerAdapter.LedgerItem> {

    @Getter
    public static class LedgerItem extends RecyclerView.ViewHolder {
        private final TextView transactionDate;
        private final TextView item;
        private final TextView memo;
        private final TextView quantity;
        private final TextView session;

        public LedgerItem(View view) {
            super(view);

            transactionDate = (TextView) view.findViewById(R.id.tvTransactionDate);
            item = (TextView) view.findViewById(R.id.tvItem);
            memo = (TextView) view.findViewById(R.id.tvMemo);
            quantity = (TextView) view.findViewById(R.id.tvQuantity);
            session = (TextView) view.findViewById(R.id.tvSession);
        }
    }

    private final List<Transaction> transactions;

    public LedgerAdapter(List<Transaction> transactions) {
         this.transactions = transactions;
    }

    @Override
    public LedgerItem onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_ledger_item, viewGroup, false);

        return new LedgerItem(view);
    }

    @Override
    public void onBindViewHolder(LedgerItem viewHolder, final int position) {
        Transaction transaction = transactions.get(position);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        viewHolder.getTransactionDate().setText(dateFormat.format(Date.from(transaction.getTransactionOn())));
        viewHolder.getItem().setText(transaction.getItem());
        viewHolder.getMemo().setText(transaction.getMemo());
        viewHolder.getSession().setText(String.valueOf(transaction.getSession()));

        BigDecimal quantity = transaction.getQuantity();
        String quantityFmt = String.valueOf(quantity);
        if (quantity.compareTo(BigDecimal.ZERO) < 0) {
            quantityFmt = "(" + (quantity.abs()) + ")";
            viewHolder.getQuantity().setTextColor(getColor(viewHolder.getQuantity().getContext(), R.color.ledgerDebt));
        } else {
            viewHolder.getQuantity().setTextColor(getColor(viewHolder.getQuantity().getContext(), R.color.ledgerCredit));
        }

        viewHolder.getQuantity().setText(quantityFmt);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }
}
