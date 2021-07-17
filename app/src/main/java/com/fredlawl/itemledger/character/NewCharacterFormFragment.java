package com.fredlawl.itemledger.character;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fredlawl.itemledger.InAppActivity;
import com.fredlawl.itemledger.R;
import com.fredlawl.itemledger.dao.AppDatabase;
import com.fredlawl.itemledger.dao.CharacterDao;
import com.fredlawl.itemledger.databinding.FragmentNewCharacterFormBinding;
import com.fredlawl.itemledger.entity.Character;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import static com.fredlawl.itemledger.SharedPrefConstants.FILE;

public class NewCharacterFormFragment extends Fragment {
    private FragmentNewCharacterFormBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentNewCharacterFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppDatabase db = AppDatabase.getInstance(getContext());
        CharacterDao dao = db.characterDao();
        SharedPreferences preferences = getActivity().getSharedPreferences(FILE, Context.MODE_PRIVATE);

        int totalCharacters = dao.hasCharacters();
        if (totalCharacters > 0) {
            binding.hasCharacters.setVisibility(View.VISIBLE);
        }

        binding.actionChooseCharacter.setOnClickListener((v) -> {
            AlertDialog dialog = ChooseCharacterDialog
                .builder()
                .setChangeCharacterService(new PreferenceBasedChangeCharacter(dao, preferences))
                .setOnCharacterChosenListener((c) -> {
                    Intent k = new Intent(getActivity(), InAppActivity.class);
                    startActivity(k);
                    getActivity().finish();
                })
                .setOnCharacterNotChosenListener(() -> {
                    Snackbar.make(
                        getActivity().findViewById(R.id.app_start_layout),
                        R.string.choose_character_dialog_error_message,
                        Snackbar.LENGTH_SHORT)
                        .show();
                })
                .create(getActivity());

            dialog.show();
        });

        binding.bSave.setOnClickListener(view1 -> {
            boolean hasErrors = false;
            String character = Objects.toString(binding.tCharacterName.getEditText().getText(), "").trim();
            String campaign = Objects.toString(binding.tCampaign.getEditText().getText(), "").trim();

            if (character.isEmpty()) {
                binding.tCharacterName.setError(getString(R.string.validation_required));
                hasErrors = true;
            } else {
                binding.tCharacterName.setError(null);
            }

            if (campaign.isEmpty()) {
                binding.tCampaign.setError(getString(R.string.validation_required));
                hasErrors = true;
            } else {
                binding.tCampaign.setError(null);
            }

            if (hasErrors) {
                return;
            }

            Character newCharacter = new Character();
            newCharacter.setCampaign(campaign);
            newCharacter.setCharacter(character);

            dao.insert(newCharacter);

            new PreferenceBasedChangeCharacter(dao, preferences)
                .changeCharacter(newCharacter);

            Intent k = new Intent(getActivity(), InAppActivity.class);
            startActivity(k);
            getActivity().finish();
        });
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
