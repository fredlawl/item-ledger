package com.fredlawl.itemledger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fredlawl.itemledger.dao.AppDatabase;
import com.fredlawl.itemledger.dao.CharacterDao;
import com.fredlawl.itemledger.databinding.FragmentNewCharacterFormBinding;
import com.fredlawl.itemledger.entity.Character;

import java.util.Objects;

import static com.fredlawl.itemledger.SharedPrefConstants.FILE;
import static com.fredlawl.itemledger.SharedPrefConstants.SELECTED_CHARACTER_ID;

public class NewCharacter extends Fragment {

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
        SharedPreferences preferences = getActivity().getSharedPreferences(FILE, Context.MODE_PRIVATE);

        binding.bSave.setOnClickListener(view1 -> {
            SharedPreferences.Editor editor = preferences.edit();
            String character = Objects.toString(binding.tCharacterName.getText(), "").trim();
            String campaign = Objects.toString(binding.tCampaign.getText(), "").trim();

            if (character.isEmpty()) {
                binding.tlCharacterName.setError("Character name is required");
            } else {
                binding.tlCharacterName.setError(null);
            }

            if (campaign.isEmpty()) {
                binding.tlCampaign.setError("Campaign is required");
            } else {
                binding.tlCampaign.setError(null);
            }

            CharacterDao dao = db.characterDao();
            Character newCharacter = new Character();
            newCharacter.setCampaign(campaign);
            newCharacter.setCharacter(character);

            dao.insert(newCharacter);

            editor.putString(SELECTED_CHARACTER_ID, newCharacter.getId().toString());
            editor.commit();

            Intent k = new Intent(getActivity(), InAppActivity.class);
            startActivity(k);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
