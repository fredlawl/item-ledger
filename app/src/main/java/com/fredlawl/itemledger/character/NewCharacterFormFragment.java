package com.fredlawl.itemledger.character;

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
import com.fredlawl.itemledger.dao.AppDatabase;
import com.fredlawl.itemledger.dao.CharacterDao;
import com.fredlawl.itemledger.databinding.FragmentNewCharacterFormBinding;
import com.fredlawl.itemledger.entity.Character;

import java.util.Objects;

import static com.fredlawl.itemledger.SharedPrefConstants.FILE;

public class NewCharacterFormFragment extends Fragment {

    private CharacterCreatedListener characterCreatedListener;
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
            boolean hasErrors = false;
            String character = Objects.toString(binding.tCharacterName.getEditText().getText(), "").trim();
            String campaign = Objects.toString(binding.tCampaign.getEditText().getText(), "").trim();

            if (character.isEmpty()) {
                binding.tCharacterName.setError("Required");
                hasErrors = true;
            } else {
                binding.tCharacterName.setError(null);
            }

            if (campaign.isEmpty()) {
                binding.tCampaign.setError("Required");
                hasErrors = true;
            } else {
                binding.tCampaign.setError(null);
            }

            if (hasErrors) {
                return;
            }

            CharacterDao dao = db.characterDao();
            Character newCharacter = new Character();
            newCharacter.setCampaign(campaign);
            newCharacter.setCharacter(character);

            dao.insert(newCharacter);

            new ChangeCharacter(dao, preferences)
                .changeCharacter(newCharacter.getId());

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

    public void setOnCharacterCreatedListener(CharacterCreatedListener listener) {
        this.characterCreatedListener = listener;
    }

    public interface CharacterCreatedListener {
        void onCharacterCreated(Character character);
    }
}
