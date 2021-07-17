package com.fredlawl.itemledger.character;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.fredlawl.itemledger.AppStartActivity;
import com.fredlawl.itemledger.R;
import com.fredlawl.itemledger.SharedPrefConstants;
import com.fredlawl.itemledger.dao.AppDatabase;
import com.fredlawl.itemledger.dao.CharacterDao;
import com.fredlawl.itemledger.databinding.ActivityDeleteCharacterBinding;
import com.fredlawl.itemledger.entity.Character;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executor;

import static com.fredlawl.itemledger.SharedPrefConstants.FILE;

public class DeleteCharacterActivity extends AppCompatActivity {
    private ActivityDeleteCharacterBinding binding;
    private CharacterDao dao;
    private Optional<Character> currentCharacter = Optional.empty();
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeleteCharacterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppDatabase db = AppDatabase.getInstance(this);
        dao = db.characterDao();

        preferences = getSharedPreferences(FILE, Context.MODE_PRIVATE);
        String currentCharacterId = preferences.getString(SharedPrefConstants.SELECTED_CHARACTER_ID, "");
        if (!currentCharacterId.isEmpty()) {
            currentCharacter = dao.getById(UUID.fromString(currentCharacterId));
        }


        List<Character> characterSuggestions = dao.getAll();
        ChooseCharacterListAdapter adapter  = new ChooseCharacterListAdapter(this, characterSuggestions);
        binding.actvCharacter.setAdapter(adapter);
        binding.actvCharacter.setOnItemClickListener((adapterView, view, i, l) -> {
            Character selectedCharacter = (Character) adapterView.getItemAtPosition(i);
            binding.actvCharacter.setText(selectedCharacter.getNamePart().toString(), false);
            binding.bSubmit.setEnabled(true);
            binding.bSubmit.setOnClickListener((v) -> {
                onCharacterDelete(selectedCharacter);
            });
        });

        binding.bCancel.setOnClickListener((v) -> {
            transitionToStartActivity();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void onCharacterDelete(Character selectedCharacter) {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(DeleteCharacterActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                    R.string.delete_character_auth_error_message, Toast.LENGTH_SHORT)
                    .show();
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                    R.string.delete_character_auth_success_message, Toast.LENGTH_SHORT).show();

                /*
                    If we attempt to delete the currently logged in character, we need to clear preferences
                    so we force users to choose a character again.
                 */
                if (currentCharacter.isPresent()) {
                    if (currentCharacter.get().equals(selectedCharacter)) {
                        preferences.edit().clear().commit();
                    }
                }

                dao.delete(selectedCharacter.getId());
                transitionToStartActivity();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), R.string.delete_character_auth_error_message,
                    Toast.LENGTH_SHORT)
                    .show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.delete_character_auth_title))
            .setSubtitle(getString(R.string.delete_character_auth_subtitle))
            .setAllowedAuthenticators(BiometricManager.Authenticators.DEVICE_CREDENTIAL | BiometricManager.Authenticators.BIOMETRIC_WEAK)
            .build();

        biometricPrompt.authenticate(promptInfo);
    }

    public void transitionToStartActivity() {
        Intent k = new Intent(this, AppStartActivity.class);
        startActivity(k);
        finish();
    }
}
