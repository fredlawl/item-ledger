package com.fredlawl.itemledger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.fredlawl.itemledger.databinding.ActivityAppstartBinding;

import static com.fredlawl.itemledger.SharedPrefConstants.FILE;
import static com.fredlawl.itemledger.SharedPrefConstants.SELECTED_CHARACTER_ID;

public class AppStartActivity extends AppCompatActivity {
    private ActivityAppstartBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAppstartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment nav = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.appstart_nav_host);
        NavController navController = nav.getNavController();

        // Character is created/stored in app state, navigate to inventory automatically
        SharedPreferences preferences = getSharedPreferences(FILE, Context.MODE_PRIVATE);
        String characterId = preferences.getString(SELECTED_CHARACTER_ID, "");
        if (!characterId.isEmpty()) {
            Intent k = new Intent(AppStartActivity.this, InAppActivity.class);
            startActivity(k);
        }
    }
}