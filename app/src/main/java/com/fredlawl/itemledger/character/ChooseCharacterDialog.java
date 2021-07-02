package com.fredlawl.itemledger.character;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import com.fredlawl.itemledger.dao.AppDatabase;
import com.fredlawl.itemledger.dao.CharacterDao;
import com.fredlawl.itemledger.entity.Character;

import java.util.Optional;

public class ChooseCharacterDialog {
    private SharedPreferences preferences;
    private CharacterChosenListener chosenListener;
    private CharacterNotChosenListener notChosenListener;

    public static ChooseCharacterDialog builder(SharedPreferences preferences) {
        return new ChooseCharacterDialog(preferences);
    }

    protected ChooseCharacterDialog(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public ChooseCharacterDialog setOnCharacterChosenListener(CharacterChosenListener listener) {
        this.chosenListener = listener;
        return this;
    }

    public ChooseCharacterDialog setOnCharacterNotChosenListener(CharacterNotChosenListener listener) {
        this.notChosenListener = listener;
        return this;
    }

    public AlertDialog create(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        CharacterDao dao = db.characterDao();

        // TODO: I don't want to deal with a full list adapter right now, so we'll encode the characters
        //  this will mean a huge performance boost too cus we don't need to perform an extra character
        //  lookup to get the UUID, and don't have to worry about character names or campaigns with
        //  the pattern <space><hyphen><space> in them.
        String[] characters = dao.getAll().stream()
                .map(Character::encode)
                .toArray(String[]::new);

        AlertDialog dialog = new AlertDialog.Builder(context)
            .setTitle("Choose a character")
            .setItems(characters, (DialogInterface.OnClickListener) (d, which) -> {
                String selectedCharacter = characters[which];
                Character.NamePart namePart = Character.extractNamePart(selectedCharacter);
                Optional<Character> foundCharacter = dao.getByNameAndCampaign(namePart.getCharacter(), namePart.getCampaign());
                if (!foundCharacter.isPresent()) {
                    if (this.notChosenListener != null) {
                        this.notChosenListener.onNotChooseCharacter();
                    }
                    d.cancel();
                    return;
                }

                Character character = foundCharacter.get();
                boolean characterChanged = new ChangeCharacter(dao, preferences)
                        .changeCharacter(character.getId());

                if (!characterChanged) {
                    if (this.notChosenListener != null) {
                        this.notChosenListener.onNotChooseCharacter();
                    }
                } else {
                    if (this.chosenListener != null) {
                        this.chosenListener.onChooseCharacter(character);
                    }
                }
            })
            .create();

        return dialog;
    }

    public interface CharacterChosenListener {
        void onChooseCharacter(Character character);
    }

    public interface CharacterNotChosenListener {
        void onNotChooseCharacter();
    }
}
