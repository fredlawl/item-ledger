package com.fredlawl.itemledger.ledger;

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
import com.fredlawl.itemledger.databinding.FragmentLedgerBinding;
import com.fredlawl.itemledger.entity.Transaction;

import java.util.List;
import java.util.UUID;

import static com.fredlawl.itemledger.SharedPrefConstants.CURRENT_SESSION;
import static com.fredlawl.itemledger.SharedPrefConstants.FILE;
import static com.fredlawl.itemledger.SharedPrefConstants.SELECTED_CHARACTER_ID;

public class LedgerFragment extends Fragment {
    private FragmentLedgerBinding binding;
    protected RecyclerView recyclerView;
    protected LedgerAdapter recyclerViewAdapter;
    protected LinearLayoutManager layoutManager;
    protected List<Transaction> transactions;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentLedgerBinding.inflate(inflater, container, false);

        // todo: Consider kicking user back to the new character activity if this is not set cuz this is pretty important on app start
        SharedPreferences preferences = getContext().getSharedPreferences(FILE, Context.MODE_PRIVATE);
        UUID currentCharacter = UUID.fromString(preferences.getString(SELECTED_CHARACTER_ID, UUID.randomUUID().toString()));

        AppDatabase db = AppDatabase.getInstance(getContext());
        CharacterDao dao = db.characterDao();
        transactions = dao.getLedger(currentCharacter);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = binding.lvLedger;
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewAdapter = new LedgerAdapter(transactions);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
