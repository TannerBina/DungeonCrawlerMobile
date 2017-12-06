package hu.ait.android.dungeoncrawler.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunctionException;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;

import hu.ait.android.dungeoncrawler.R;
import hu.ait.android.dungeoncrawler.adapters.CharacterAdapter;
import hu.ait.android.dungeoncrawler.data.User;
import hu.ait.android.dungeoncrawler.imports.callers.DeleteCharacterInput;
import hu.ait.android.dungeoncrawler.imports.callers.DeleteCharacterService;
import hu.ait.android.dungeoncrawler.imports.callers.Output;

public class CharacterListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CharacterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_list);
        initNavigationView();

        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new CharacterAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);
    }

    private void initNavigationView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.about:
                Toast.makeText(getApplicationContext(),
                        R.string.about_character_list, Toast.LENGTH_LONG).show();
                break;
            case R.id.createCharacter:
                Intent intent = new Intent();
                intent.setClass(CharacterListActivity.this, CreateCharacterActivity.class);
                startActivity(intent);
                break;
            case R.id.deleteCharacter:
                deleteCharacter();
                break;
            case R.id.logOut:
                returnToLogin();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void deleteCharacter() {
        AlertDialog.Builder builder = new AlertDialog.Builder
                (CharacterListActivity.this);
        builder.setTitle("Delete Character");
        final EditText input = new EditText(CharacterListActivity.this);
        builder.setView(input);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DeleteCharacterInput in = new DeleteCharacterInput();
                in.setUsername(User.getInstance().getUsername());
                String name = adapter.deleteCharacter(input.getText().toString());
                if (name == null){
                    dialogInterface.cancel();
                    Toast.makeText(getApplicationContext(),
                            "No Character with that name found.",
                            Toast.LENGTH_LONG).show();
                } else {
                    in.setId(name);

                    CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                            getApplicationContext(),
                            "us-east-2:4afff1a5-f1a5-49fb-99c3-d7167ff61afe", // Identity pool ID
                            Regions.US_EAST_2 // Region
                    );
                    LambdaInvokerFactory factory = new LambdaInvokerFactory(getApplicationContext(),
                            Regions.US_EAST_2, credentialsProvider);
                    final DeleteCharacterService deleteCharacter = factory.build(DeleteCharacterService.class);

                    new AsyncTask<DeleteCharacterInput, Void, Output>(){
                        @Override
                        protected Output doInBackground(DeleteCharacterInput... deleteCharacterInputs) {
                            try {
                                return deleteCharacter.deleteCharacter(deleteCharacterInputs[0]);
                            } catch (LambdaFunctionException e){
                                return null;
                            }
                        }

                        @Override
                        protected void onPostExecute(final Output success){
                            if (success == null){
                                Toast.makeText(CharacterListActivity.this,
                                        getString(R.string.error_unexpected),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }.execute(in);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    private void returnToLogin() {
        User.getInstance().reset();
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(LoginActivity.USERNAME_KEY, null);
        editor.putString(LoginActivity.PASSWORD_KEY, null);
        editor.commit();
        Intent intent = new Intent();
        intent.setClass(CharacterListActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
