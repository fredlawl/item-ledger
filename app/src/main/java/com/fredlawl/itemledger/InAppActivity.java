package com.fredlawl.itemledger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.fredlawl.itemledger.character.ChangeCharacter;
import com.fredlawl.itemledger.dao.AppDatabase;
import com.fredlawl.itemledger.dao.CharacterDao;
import com.fredlawl.itemledger.entity.Character;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Optional;

import static com.fredlawl.itemledger.SharedPrefConstants.FILE;

public class InAppActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private NavController navController;
    Toolbar mainNavigation;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inapp);

        mainNavigation = findViewById(R.id.inapp_toolbar);
        setSupportActionBar(mainNavigation);

        // Even though "inventory" is selected on app startup on first destination, it gets overwritten somewhere.
        mainNavigation.setTitle("Inventory");

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
            .findFragmentById(R.id.inapp_nav_host);
        navController = navHostFragment.getNavController();
        BottomNavigationView bottomNav = findViewById(R.id.inapp_bottomAppBar_view);
        NavigationUI.setupWithNavController(bottomNav, navController);

        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph())
            .build();

        // Update the title whenever navigation happens
        navController.addOnDestinationChangedListener((c, d, a) -> {
            mainNavigation.setTitle(d.getLabel());
        });

        FloatingActionButton fab = findViewById(R.id.fbNewTransaction);
        fab.setOnClickListener(v -> {
            navController.navigate(R.id.NewTransaction);
        });

        preferences = getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Even though "inventory" is selected on app startup on first destination, it gets overwritten on resume
        mainNavigation.setTitle("Inventory");
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_choose_character) {
            handleOnChooseCharacter();
        }

        if (id == R.id.action_new_character) {
            handleOnNewCharacter();
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleOnChooseCharacter() {
        AppDatabase db = AppDatabase.getInstance(this);
        CharacterDao dao = db.characterDao();

        // TODO: I don't want to deal with a full list adapter right now, so we'll encode the characters
        //  this will mean a huge performance boost too cus we don't need to perform an extra character
        //  lookup to get the UUID, and don't have to worry about character names or campaigns with
        //  the pattern <space><hyphen><space> in them.
        String[] characters = dao.getAll().stream()
            .map(c -> String.format("%s - %s", c.getCharacter(), c.getCampaign()))
            .toArray(String[]::new);

        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("Choose a character")
            .setItems(characters, (DialogInterface.OnClickListener) (d, which) -> {
                String selectedCharacter = characters[which];
                int splitIndex = selectedCharacter.indexOf(" - ", 0);
                String characterName = selectedCharacter.substring(0, splitIndex);
                String campaignName = selectedCharacter.substring(splitIndex + 3);
                Optional<Character> foundCharacter = dao.getByNameAndCampaign(characterName, campaignName);
                if (!foundCharacter.isPresent()) {
                    d.cancel();
                }

                boolean characterChanged = new ChangeCharacter(dao, preferences)
                    .changeCharacter(foundCharacter.get().getId());

                if (!characterChanged) {
                    Snackbar.make(
                        findViewById(R.id.inapp_layout),
                        "Could not change character",
                        Snackbar.LENGTH_SHORT)
                        .show();
                } else {
                    finish();
                    startActivity(getIntent());
                }
            })
            .create();

        dialog.show();
    }

    private void handleOnNewCharacter() {
        preferences.edit().clear().commit();
        Intent k = new Intent(this, AppStartActivity.class);
        startActivity(k);
        finish();
    }
}
