package com.fredlawl.itemledger.ledger;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.fredlawl.itemledger.R;
import com.fredlawl.itemledger.dao.AppDatabase;
import com.fredlawl.itemledger.dao.CharacterDao;
import com.fredlawl.itemledger.dao.InventoryDao;
import com.fredlawl.itemledger.entity.Character;
import com.fredlawl.itemledger.entity.InventoryItem;
import com.fredlawl.itemledger.dao.TransactionDao;
import com.fredlawl.itemledger.databinding.FragmentNewTransactionFormBinding;
import com.fredlawl.itemledger.entity.Transaction;
import com.fredlawl.itemledger.util.InputTextRangeFilter;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.fredlawl.itemledger.SharedPrefConstants.CURRENT_SESSION;
import static com.fredlawl.itemledger.SharedPrefConstants.FILE;
import static com.fredlawl.itemledger.SharedPrefConstants.SELECTED_CHARACTER_ID;

public class NewTransactionFormFragment extends Fragment {
    private FragmentNewTransactionFormBinding binding;
    private TextInputLayout transactionDateTextLayout;
    private TextInputLayout sessionTextLayout;
    private TextInputLayout quantityTextLayout;
    private TextInputLayout itemTextLayout;
    private TextInputLayout memoTextLayout;
    private SharedPreferences preferences;
    private UUID currentCharacterId;
    private Character currentCharacter;
    private AppDatabase db;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        db = AppDatabase.getInstance(getContext());
        InventoryDao inventoryDao = db.inventoryDao();
        CharacterDao characterDao = db.characterDao();

        binding = FragmentNewTransactionFormBinding.inflate(inflater, container, false);
        preferences = getContext().getSharedPreferences(FILE, Context.MODE_PRIVATE);

        // todo: Figure out how to pass current character state down to the fragment from the parent activity
        currentCharacterId = UUID.fromString(preferences.getString(SELECTED_CHARACTER_ID, UUID.randomUUID().toString()));
        Optional<Character> foundCharacter = characterDao.getById(currentCharacterId);
        foundCharacter.ifPresent(character -> currentCharacter = character);

        transactionDateTextLayout = binding.tTransactionDate;
        sessionTextLayout = binding.tSession;
        quantityTextLayout = binding.tQuantity;
        itemTextLayout = binding.tItem;
        memoTextLayout = binding.tMemo;


        String[] inventorySuggestions = inventoryDao.getNames(currentCharacter.getCampaign()).toArray(new String[0]);

        ArrayAdapter<String> adapter  = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_item, inventorySuggestions);
        ((AutoCompleteTextView) itemTextLayout.getEditText()).setAdapter(adapter);
        ((AutoCompleteTextView) itemTextLayout.getEditText()).setThreshold(1);

        List<InputFilter> sessionInputFilters = new ArrayList<>(Arrays.asList(sessionTextLayout.getEditText().getFilters()));
        sessionInputFilters.add(new InputTextRangeFilter(Transaction.MIN_SESSION, Transaction.MAX_SESSION));
        InputFilter[] sessionFiltersArray = new InputFilter[sessionInputFilters.size()];
        sessionTextLayout.getEditText().setFilters(sessionInputFilters.toArray(sessionFiltersArray));

        List<InputFilter> quantityInputFilters = new ArrayList<>(Arrays.asList(quantityTextLayout.getEditText().getFilters()));
        quantityInputFilters.add(new InputTextRangeFilter(Transaction.MIN_QUANTITY, Transaction.MAX_QUANTITY));
        InputFilter[] quantityFiltersArray = new InputFilter[quantityInputFilters.size()];
        quantityTextLayout.getEditText().setFilters(quantityInputFilters.toArray(quantityFiltersArray));

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemTextLayout.getEditText().requestFocus();

        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat dateFmt = new SimpleDateFormat("MM/dd/yyyy");

        // Get the previous session used from the last transaction
        int setSessionId = preferences.getInt(CURRENT_SESSION, 0);
        sessionTextLayout.getEditText().setText(String.valueOf(setSessionId));

        transactionDateTextLayout.getEditText().setText(dateFmt.format(calendar.getTime()));

        DatePickerDialog.OnDateSetListener date = (DatePicker dp, int year, int monthOfYear, int dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            transactionDateTextLayout.getEditText().setText(dateFmt.format(calendar.getTime()));
        };

        transactionDateTextLayout.getEditText().setOnClickListener(v -> {
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
                sessionTextLayout.setError(getString(R.string.validation_required));
                hasErrors = true;
            } else {
                try {
                    int parsedSession = Integer.parseInt(session);
                    if (parsedSession < 0) {
                        sessionTextLayout.setError(getString(R.string.validation_required));
                        hasErrors = true;
                    } else {
                        newTransaction.setSession(parsedSession);
                        editor.putInt(CURRENT_SESSION, parsedSession);
                    }
                } catch (NumberFormatException nfe) {
                    sessionTextLayout.setError(getString(R.string.validation_integer));
                    hasErrors = true;
                }
            }

            String item = Objects.toString(itemTextLayout.getEditText().getText(), "");
            if (item.isEmpty()) {
                itemTextLayout.setError(getString(R.string.validation_required));
                hasErrors = true;
            }

            String memo = Objects.toString(memoTextLayout.getEditText().getText(), "");
            if (memo.isEmpty()) {
                memoTextLayout.setError(getString(R.string.validation_required));
                hasErrors = true;
            }

            if (hasErrors) {
                return;
            }

            TransactionDao dao = db.transactionDao();
            CharacterDao characterDao = db.characterDao();

            newTransaction.setCharacterId(currentCharacterId);
            newTransaction.setItem(item);
            newTransaction.setMemo(memo);
            newTransaction.setTransactionOn(calendar.toInstant());

            Optional<InventoryItem> foundItem = characterDao.getItemByName(currentCharacterId, item);
            String quantity = Objects.toString(quantityTextLayout.getEditText().getText(), "");
            if (quantity.isEmpty()) {
                quantityTextLayout.setError(getString(R.string.validation_required));
                return;
            } else {
                try {
                    BigDecimal parsedQuantity = new BigDecimal(quantity);

                    // Handle the case where we're trying to withdrawal more than we have available
                    if (parsedQuantity.compareTo(BigDecimal.ZERO) < 0) {
                        if (!foundItem.isPresent() && parsedQuantity.compareTo(BigDecimal.ZERO) < 0) {
                            Snackbar.make(
                                getActivity().findViewById(R.id.inapp_layout),
                                R.string.new_transaction_form_item_error_message,
                                Snackbar.LENGTH_SHORT)
                                .setAnchorView(R.id.fbNewTransaction)
                                .show();
                            return;
                        }

                        BigDecimal currentQuantity = foundItem.get().getQuantity();
                        BigDecimal diff = currentQuantity.add(parsedQuantity);
                        if (diff.compareTo(BigDecimal.ZERO) < 0) {
                            String error = String.format(
                                getString(R.string.new_transaction_form_quantity_insufficient_funds),
                                diff.multiply(new BigDecimal("-1")).toPlainString());
                            quantityTextLayout.setError(error);
                            return;
                        }
                    }

                    // Handle the case where we prevent a overflow
                    if (foundItem.isPresent()) {
                        BigDecimal currentQuantity = foundItem.get().getQuantity();
                        BigDecimal diff = currentQuantity.add(parsedQuantity);
                        if (diff.compareTo(InventoryItem.MAX_QUANTITY) > 0) {
                            String error = String.format(
                                getString(R.string.new_transaction_form_quantity_max_inventory),
                                parsedQuantity.toPlainString());
                            quantityTextLayout.setError(error);
                            return;
                        }
                    }

                    newTransaction.setQuantity(parsedQuantity);
                } catch (NumberFormatException nfe) {
                    quantityTextLayout.setError(getString(R.string.validation_integer));
                    return;
                }
            }


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
