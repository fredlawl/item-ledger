package com.fredlawl.itemledger.character;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fredlawl.itemledger.dao.CharacterDao;
import com.fredlawl.itemledger.entity.Character;

import java.util.Optional;
import java.util.UUID;

import static com.fredlawl.itemledger.SharedPrefConstants.CURRENT_SESSION;
import static com.fredlawl.itemledger.SharedPrefConstants.SELECTED_CHARACTER_ID;

public class PreferenceBasedChangeCharacter implements ChangeCharacter {
    private final CharacterDao dao;
    private final SharedPreferences preferences;

    public PreferenceBasedChangeCharacter(CharacterDao dao, SharedPreferences preferences) {
        this.dao = dao;
        this.preferences = preferences;
    }

    @Override
    public boolean changeCharacter(@NonNull Character oldCharacter, @NonNull Character newCharacter) {
        if (!oldCharacter.equals(newCharacter)) {
            return false;
        }

        return changeCharacter(newCharacter);
    }

    @Override
    public boolean changeCharacter(@NonNull Character newCharacter) {
        SharedPreferences.Editor preferencesEditor = preferences.edit();

        // Save current user state to DB
        Optional<String> hasCurrentCharacterUUID = Optional.ofNullable(preferences.getString(SELECTED_CHARACTER_ID, null));
        int currentSession = preferences.getInt(CURRENT_SESSION, 0);
        hasCurrentCharacterUUID.ifPresent(s -> dao.updateSession(UUID.fromString(s), currentSession));

        // Set new state
        preferencesEditor.putString(SELECTED_CHARACTER_ID, newCharacter.getId().toString());
        preferencesEditor.putInt(CURRENT_SESSION, newCharacter.getSavedSession());
        return preferencesEditor.commit();
    }
}
