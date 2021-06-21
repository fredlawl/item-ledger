package com.fredlawl.itemledger.character;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fredlawl.itemledger.InAppActivity;
import com.fredlawl.itemledger.databinding.ActivityNewCharacterBinding;

import static com.fredlawl.itemledger.SharedPrefConstants.FILE;
import static com.fredlawl.itemledger.SharedPrefConstants.SELECTED_CHARACTER_ID;

public class NewCharacterActivity extends AppCompatActivity {
    private ActivityNewCharacterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Character is created/stored in app state, navigate to inventory automatically
        SharedPreferences preferences = getSharedPreferences(FILE, Context.MODE_PRIVATE);
        String characterId = preferences.getString(SELECTED_CHARACTER_ID, "");
        if (!characterId.isEmpty()) {
            Intent k = new Intent(NewCharacterActivity.this, InAppActivity.class);
            startActivity(k);
            return;
        }

        binding = ActivityNewCharacterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}