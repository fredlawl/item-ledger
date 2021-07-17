package com.fredlawl.itemledger.character;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.fredlawl.itemledger.R;
import com.fredlawl.itemledger.dao.AppDatabase;
import com.fredlawl.itemledger.dao.CharacterDao;
import com.fredlawl.itemledger.entity.Character;

import java.util.List;

public class ChooseCharacterDialog {
    private CharacterChosenListener chosenListener;
    private CharacterNotChosenListener notChosenListener;
    private ChangeCharacter characterChanger;

    public static ChooseCharacterDialog builder() {
        return new ChooseCharacterDialog();
    }

    protected ChooseCharacterDialog() {
    }

    public ChooseCharacterDialog setOnCharacterChosenListener(CharacterChosenListener listener) {
        this.chosenListener = listener;
        return this;
    }

    public ChooseCharacterDialog setOnCharacterNotChosenListener(CharacterNotChosenListener listener) {
        this.notChosenListener = listener;
        return this;
    }

    public ChooseCharacterDialog setChangeCharacterService(ChangeCharacter characterChanger) {
        this.characterChanger = characterChanger;
        return this;
    }

    public AlertDialog create(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        CharacterDao dao = db.characterDao();

        List<Character> allCharacters = dao.getAll();
        ChooseCharacterDialogListAdapter adapter =
            new ChooseCharacterDialogListAdapter(context, allCharacters);

        AlertDialog dialog = new AlertDialog.Builder(context)
            .setTitle(R.string.choose_character_dialog_title)
            .setAdapter(adapter, (DialogInterface.OnClickListener) (d, which) -> {
                Character selectedCharacter = allCharacters.get(which);

                boolean characterChanged = false;
                if (characterChanger != null) {
                    characterChanged = characterChanger.changeCharacter(selectedCharacter);
                }

                if (!characterChanged) {
                    if (this.notChosenListener != null) {
                        this.notChosenListener.onNotChooseCharacter();
                    }
                } else {
                    if (this.chosenListener != null) {
                        this.chosenListener.onChooseCharacter(selectedCharacter);
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
