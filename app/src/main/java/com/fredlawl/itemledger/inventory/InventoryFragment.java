package com.fredlawl.itemledger.inventory;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fredlawl.itemledger.dao.AppDatabase;
import com.fredlawl.itemledger.dao.CharacterDao;
import com.fredlawl.itemledger.dao.InventoryItem;
import com.fredlawl.itemledger.databinding.FragmentInventoryBinding;

import java.util.List;
import java.util.UUID;

import static com.fredlawl.itemledger.SharedPrefConstants.FILE;
import static com.fredlawl.itemledger.SharedPrefConstants.SELECTED_CHARACTER_ID;

public class InventoryFragment extends Fragment {
    private FragmentInventoryBinding binding;
    protected RecyclerView recyclerView;
    protected InventoryAdapter recyclerViewAdapter;
    protected LinearLayoutManager layoutManager;
    protected List<InventoryItem> inventoryItems;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentInventoryBinding.inflate(inflater, container, false);

        // todo: Consider kicking user back to the new character activity if this is not set cuz this is pretty important on app start
        SharedPreferences preferences = getContext().getSharedPreferences(FILE, Context.MODE_PRIVATE);
        UUID currentCharacter = UUID.fromString(preferences.getString(SELECTED_CHARACTER_ID, UUID.randomUUID().toString()));

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
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
