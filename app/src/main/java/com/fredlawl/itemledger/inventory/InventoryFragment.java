package com.fredlawl.itemledger.inventory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fredlawl.itemledger.R;
import com.fredlawl.itemledger.dao.AppDatabase;
import com.fredlawl.itemledger.dao.CharacterDao;
import com.fredlawl.itemledger.entity.InventoryItem;
import com.fredlawl.itemledger.databinding.FragmentInventoryBinding;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.fredlawl.itemledger.SharedPrefConstants.FILE;
import static com.fredlawl.itemledger.SharedPrefConstants.SELECTED_CHARACTER_ID;

public class InventoryFragment extends Fragment {
    private FragmentInventoryBinding binding;
    protected RecyclerView recyclerView;
    protected InventoryAdapter recyclerViewAdapter;
    protected LinearLayoutManager layoutManager;
    protected List<InventoryItem> inventoryItems;
    protected UUID currentCharacter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentInventoryBinding.inflate(inflater, container, false);

        // todo: Consider kicking user back to the new character activity if this is not set cuz this is pretty important on app start
        SharedPreferences preferences = getContext().getSharedPreferences(FILE, Context.MODE_PRIVATE);
        currentCharacter = UUID.fromString(preferences.getString(SELECTED_CHARACTER_ID, UUID.randomUUID().toString()));

        AppDatabase db = AppDatabase.getInstance(getContext());
        CharacterDao dao = db.characterDao();
        inventoryItems = dao.getInventory(currentCharacter);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = binding.lvInventory;
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewAdapter = new InventoryAdapter(inventoryItems);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        recyclerViewAdapter.setClickListener((v, p) -> {
            InventoryItem item = inventoryItems.get(p);

            AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                .setView(R.layout.dialog_change_item_name)
                .setTitle(R.string.change_item_name_dialog_title)
                .setPositiveButton(R.string.change_item_name_dialog_success, (d, dp) -> {})
                .setNegativeButton(R.string.change_item_name_dialog_cancel, (d, dp) -> {
                    d.cancel();
                })
                .create();

            dialog.setOnShowListener((d) -> {
                TextInputLayout itemNameInputLayout = dialog.findViewById(R.id.tlItem);
                itemNameInputLayout.getEditText().setText(item.getItem());
            });

            dialog.show();

            dialog
                .getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener((btnView) -> {
                    TextInputLayout itemNameInputLayout = dialog.findViewById(R.id.tlItem);

                    String currentItemText = item.getItem();
                    String itemText = Objects.toString(itemNameInputLayout.getEditText().getText(), "");

                    if (itemText.isEmpty()) {
                        itemNameInputLayout.setError(getString(R.string.validation_required));
                        return;
                    }

                    if (itemText.equals(item.getItem())) {
                        dialog.cancel();
                    }

                    AppDatabase db = AppDatabase.getInstance(getContext());
                    db.inventoryDao().changeItemName(currentCharacter, currentItemText, itemText);

                    dialog.dismiss();
                });

            dialog.setOnDismissListener((d) -> {
                AppDatabase db = AppDatabase.getInstance(getContext());
                CharacterDao dao = db.characterDao();
                inventoryItems.clear();
                inventoryItems.addAll(dao.getInventory(currentCharacter));
                recyclerViewAdapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
