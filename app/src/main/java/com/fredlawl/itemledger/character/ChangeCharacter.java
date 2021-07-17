package com.fredlawl.itemledger.character;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fredlawl.itemledger.entity.Character;

public interface ChangeCharacter {
    boolean changeCharacter(@NonNull Character oldCharacter, @NonNull Character newCharacter);
    boolean changeCharacter(@NonNull Character newCharacter);
}
