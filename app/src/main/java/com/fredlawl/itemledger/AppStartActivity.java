package com.fredlawl.itemledger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fredlawl.itemledger.databinding.ActivityAppStartBinding;

import static com.fredlawl.itemledger.SharedPrefConstants.FILE;
import static com.fredlawl.itemledger.SharedPrefConstants.SELECTED_CHARACTER_ID;

public class AppStartActivity extends AppCompatActivity {
    private ActivityAppStartBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Character is created/stored in app state, navigate to inventory automatically
        SharedPreferences preferences = getSharedPreferences(FILE, Context.MODE_PRIVATE);
        String characterId = preferences.getString(SELECTED_CHARACTER_ID, "");
        if (!characterId.isEmpty()) {
            Intent k = new Intent(AppStartActivity.this, InAppActivity.class);
            startActivity(k);
            finish();
            return;
        }

        binding = ActivityAppStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}