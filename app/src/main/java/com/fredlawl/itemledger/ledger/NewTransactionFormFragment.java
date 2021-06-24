package com.fredlawl.itemledger.ledger;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.fredlawl.itemledger.R;
import com.fredlawl.itemledger.dao.AppDatabase;
import com.fredlawl.itemledger.dao.CharacterDao;
import com.fredlawl.itemledger.dao.TransactionDao;
import com.fredlawl.itemledger.databinding.FragmentNewTransactionFormBinding;
import com.fredlawl.itemledger.entity.Character;
import com.fredlawl.itemledger.entity.Transaction;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Objects;
import java.util.SimpleTimeZone;
import java.util.UUID;

import static com.fredlawl.itemledger.SharedPrefConstants.CURRENT_SESSION;
import static com.fredlawl.itemledger.SharedPrefConstants.FILE;
import static com.fredlawl.itemledger.SharedPrefConstants.SELECTED_CHARACTER_ID;

public class NewTransactionFormFragment extends Fragment {
    private FragmentNewTransactionFormBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentNewTransactionFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat dateFmt = new SimpleDateFormat("MM/dd/yyyy");
        TextInputLayout transactionDateTextLayout = binding.tTransactionDate;
        TextInputLayout sessionTextLayout = binding.tSession;
        TextInputLayout quantityTextLayout = binding.tQuantity;
        TextInputLayout itemTextLayout = binding.tItem;
        TextInputLayout memoTextLayout = binding.tMemo;
        AppDatabase db = AppDatabase.getInstance(getContext());

        SharedPreferences preferences = getContext().getSharedPreferences(FILE, Context.MODE_PRIVATE);

        // Get the previous session used from the last transaction
        int setSessionId = preferences.getInt(CURRENT_SESSION, 0);
        sessionTextLayout.getEditText().setText(String.valueOf(setSessionId));

        // todo: Consider kicking user back to the new character activity if this is not set cuz this is pretty important on app start
        UUID currentCharacter = UUID.fromString(preferences.getString(SELECTED_CHARACTER_ID, UUID.randomUUID().toString()));

        transactionDateTextLayout.getEditText().setText(dateFmt.format(calendar.getTime()));

        DatePickerDialog.OnDateSetListener date = (DatePicker dp, int year, int monthOfYear, int dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            transactionDateTextLayout.getEditText().setText(dateFmt.format(calendar.getTime()));
        };

        transactionDateTextLayout.setOnClickListener(v -> {
            new DatePickerDialog(
                getContext(),
                date,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.inapp_nav_host);
        NavController navController = navHostFragment.getNavController();
        binding.bCancel.setOnClickListener((v) -> {
            navController.popBackStack();
        });

        binding.bSubmit.setOnClickListener(v -> {
            Transaction newTransaction = new Transaction();
            SharedPreferences.Editor editor = preferences.edit();
            boolean hasErrors = false;

            String session = Objects.toString(sessionTextLayout.getEditText().getText(), "");
            if (session.isEmpty()) {
                sessionTextLayout.setError("Required");
                hasErrors = true;
            } else {
                try {
                    int parsedSession = Integer.parseInt(session);
                    if (parsedSession < 0) {
                        sessionTextLayout.setError("Required");
                        hasErrors = true;
                    } else {
                        newTransaction.setSession(parsedSession);
                        editor.putInt(CURRENT_SESSION, parsedSession);
                    }
                } catch (NumberFormatException nfe) {
                    sessionTextLayout.setError("Must be a integer");
                    hasErrors = true;
                }
            }

            String quantity = Objects.toString(quantityTextLayout.getEditText().getText(), "");
            if (quantity.isEmpty()) {
                quantityTextLayout.setError("Required");
                hasErrors = true;
            } else {
                try {
                    int parsedQuantity = Integer.parseInt(quantity);
                    newTransaction.setQuantity(parsedQuantity);
                } catch (NumberFormatException nfe) {
                    quantityTextLayout.setError("Must be a integer");
                    hasErrors = true;
                }
            }

            String item = Objects.toString(itemTextLayout.getEditText().getText(), "");
            if (item.isEmpty()) {
                itemTextLayout.setError("Required");
                hasErrors = true;
            }

            String memo = Objects.toString(memoTextLayout.getEditText().getText(), "");
            if (item.isEmpty()) {
                memoTextLayout.setError("Required");
                hasErrors = true;
            }

            if (hasErrors) {
                return;
            }

            TransactionDao dao = db.transactionDao();
            newTransaction.setCharacterId(currentCharacter);
            newTransaction.setItem(item);
            newTransaction.setMemo(memo);
            newTransaction.setTransactionOn(calendar.toInstant());

            dao.insert(newTransaction);

            editor.commit();
            navController.popBackStack();
        });
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
