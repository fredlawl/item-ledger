package com.fredlawl.itemledger.character;

import android.content.SharedPreferences;

import com.fredlawl.itemledger.dao.CharacterDao;
import com.fredlawl.itemledger.entity.Character;

import java.util.Optional;
import java.util.UUID;

import static com.fredlawl.itemledger.SharedPrefConstants.CURRENT_SESSION;
import static com.fredlawl.itemledger.SharedPrefConstants.SELECTED_CHARACTER_ID;

public class ChangeCharacter {
    private final CharacterDao dao;
    private final SharedPreferences preferences;

    public ChangeCharacter(CharacterDao dao, SharedPreferences preferences) {
        this.dao = dao;
        this.preferences = preferences;
    }

    public boolean changeCharacter(UUID toCharacterUUID) {
        SharedPreferences.Editor preferencesEditor = preferences.edit();

        // Save current user session state to DB
        Optional<String> hasCurrentCharacterUUID = Optional.ofNullable(preferences.getString(SELECTED_CHARACTER_ID, null));
        int currentSession = preferences.getInt(CURRENT_SESSION, 0);
        hasCurrentCharacterUUID.ifPresent(s -> dao.updateSession(UUID.fromString(s), currentSession));

        // Swap current state with next user state
        Optional<Character> toCharacter = dao.getById(toCharacterUUID);
        if (!toCharacter.isPresent()) {
            return false;
        }

        preferencesEditor.putString(SELECTED_CHARACTER_ID, toCharacterUUID.toString());
        preferencesEditor.putInt(CURRENT_SESSION, toCharacter.get().getSavedSession());
        return preferencesEditor.commit();
    }
}
