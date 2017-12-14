package hu.ait.android.dungeoncrawler;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunctionException;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;

import org.w3c.dom.Text;

import java.util.ArrayList;

import hu.ait.android.dungeoncrawler.activities.CreateCharacterActivity;
import hu.ait.android.dungeoncrawler.data.User;
import hu.ait.android.dungeoncrawler.imports.backend.Character;
import hu.ait.android.dungeoncrawler.imports.backend.Weapon;
import hu.ait.android.dungeoncrawler.imports.callers.FetchAllGamesInput;
import hu.ait.android.dungeoncrawler.imports.callers.FetchAllGamesService;
import hu.ait.android.dungeoncrawler.imports.callers.FetchCharacterService;
import hu.ait.android.dungeoncrawler.imports.callers.Output;
import hu.ait.android.dungeoncrawler.imports.util.Messages;

public class CharacterViewActivity extends AppCompatActivity {

    private Character active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_view);

        if (getIntent().hasExtra(GameActivity.IN_GAME)){
            Button btnJoinGame = (Button) findViewById(R.id.btnJoinGame);
            btnJoinGame.setVisibility(View.GONE);
        }

        active = User.getInstance().getActiveCharacter();
        if (active != null){
            setFields();
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.error_unexpected),
                    Toast.LENGTH_LONG).show();
        }

        initJoinGame();
    }

    private void initJoinGame() {
        Button btnJoinGame = (Button) findViewById(R.id.btnJoinGame);
        btnJoinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        R.string.fetch_string,
                        Toast.LENGTH_LONG).show();

                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                        getApplicationContext(),
                        "us-east-2:4afff1a5-f1a5-49fb-99c3-d7167ff61afe", // Identity pool ID
                        Regions.US_EAST_2 // Region
                );
                LambdaInvokerFactory factory = new LambdaInvokerFactory(getApplicationContext(),
                        Regions.US_EAST_2, credentialsProvider);

                final FetchAllGamesInput input = new FetchAllGamesInput();
                final FetchAllGamesService fetchGames = factory.build(FetchAllGamesService.class);

                new AsyncTask<FetchAllGamesInput, Void, Output>(){

                    @Override
                    protected Output doInBackground(FetchAllGamesInput... fetchAllGamesInputs) {
                        try{
                            Output out = fetchGames.fetchAllGames(input);
                            return out;
                        } catch (LambdaFunctionException e){
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(final Output success){
                        if (success != null && !success.getMessage().contains(Messages.ERROR_TAG)){
                            if (success.getData().equals("__NO_GAMES__")){
                                Toast.makeText(CharacterViewActivity.this,
                                        R.string.no_games,
                                        Toast.LENGTH_LONG).show();
                            } else {
                                User.getInstance().setGames(success.getData());
                                Intent intent = new Intent();
                                intent.setClass(CharacterViewActivity.this, GameListActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(CharacterViewActivity.this,
                                    getString(R.string.error_unexpected),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                }.execute(input);
            }
        });
    }

    private void setFields() {
        setStats();

        setLists();

        setWeapons();

        setSpells();
    }

    private void setStats() {
        TextView tvCVDescription = (TextView) findViewById(R.id.tvCVDescription);
        StringBuilder sb = new StringBuilder();
        sb.append(active.getStat(Character.StatTag.NAME)).append(" the Level ");
        sb.append(active.getStat(Character.StatTag.LEVEL)).append(" ");
        sb.append(active.getStat(Character.StatTag.RACE)).append(" ");
        sb.append(active.getStat(Character.StatTag.CLASS));
        tvCVDescription.setText(sb.toString());

        TextView tvCVAlignmentBackground = (TextView) findViewById(R.id.tvCVAlignmentBackground);
        sb = new StringBuilder();
        sb.append(active.getStat(Character.StatTag.ALIGNMENT)).append("\t");
        sb.append(active.getStat(Character.StatTag.BACKGROUND));
        tvCVAlignmentBackground.setText(sb.toString());

        TextView tvCVAC = (TextView) findViewById(R.id.tvCVAC);
        tvCVAC.setText(String.format("Armor class : %s", active.getStat(Character.StatTag.AC)));

        TextView tvCVHP = (TextView) findViewById(R.id.tvCVHP);
        tvCVHP.setText(String.format("Hitpoints : %s/%s", active.getStat(Character.StatTag.CURRENT_HP),
                active.getStat(Character.StatTag.TOTAL_HP)));

        TextView tvCVStr = (TextView) findViewById(R.id.tvCVStr);
        tvCVStr.setText(String.format("%s(+%s)",
                active.getStat(Character.StatTag.STR),
                active.getStat(Character.StatTag.STR_BON)));

        TextView tvCVDex = (TextView) findViewById(R.id.tvCVDex);
        tvCVDex.setText(String.format("%s(+%s)",
                active.getStat(Character.StatTag.DEX),
                active.getStat(Character.StatTag.DEX_BON)));

        TextView tvCVCon = (TextView) findViewById(R.id.tvCVCon);
        tvCVCon.setText(String.format("%s(+%s)",
                active.getStat(Character.StatTag.CON),
                active.getStat(Character.StatTag.CON_BON)));

        TextView tvCVInt  = (TextView) findViewById(R.id.tvCVInt);
        tvCVInt.setText(String.format("%s(+%s)",
                active.getStat(Character.StatTag.INT),
                active.getStat(Character.StatTag.INT_BON)));

        TextView tvCVWis = (TextView) findViewById(R.id.tvCVWis);
        tvCVWis.setText(String.format("%s(+%s)",
                active.getStat(Character.StatTag.WIS),
                active.getStat(Character.StatTag.WIS_BON)));

        TextView tvCVCha = (TextView) findViewById(R.id.tvCVCha);
        tvCVCha.setText(String.format("%s(+%s)",
                active.getStat(Character.StatTag.CHA),
                active.getStat(Character.StatTag.CHA_BON)));
    }

    private void setLists() {
        StringBuilder sb;
        sb = new StringBuilder();
        for (int i = 0; i < active.getFeats().size(); i++){
            sb.append(active.getFeats().get(i));
            if (i != active.getFeats().size()-1){
                sb.append(", ");
            }
        }
        if (sb.toString().isEmpty()) sb.append("No Feats");
        TextView tvCVFeatList = (TextView) findViewById(R.id.tvCVFeatList);
        tvCVFeatList.setText(sb.toString());

        sb = new StringBuilder();
        for (int i = 0; i < active.getItems().size(); i++){
            sb.append(active.getItems().get(i));
            if (i != active.getItems().size()-1){
                sb.append(", ");
            }
        }
        if (sb.toString().isEmpty()) sb.append("No Items");
        TextView tvCVItemList = (TextView) findViewById(R.id.tvCVItemList);
        tvCVItemList.setText(sb.toString());

        sb = new StringBuilder();
        for (int i = 0; i < active.getProfs().size(); i++){
            sb.append(active.getProfs().get(i));
            if (i != active.getProfs().size()-1){
                sb.append(", ");
            }
        }
        if (sb.toString().isEmpty()) sb.append("No Proficiencies");
        TextView tcCVProfList = (TextView) findViewById(R.id.tvCVProfList);
        tcCVProfList.setText(sb.toString());
    }

    private void setWeapons() {
        StringBuilder sb;LinearLayout llWeaponLayout = (LinearLayout) findViewById(R.id.llWeaponLayout);
        if (!active.getWeapons().isEmpty()){
            TextView tv = new TextView(llWeaponLayout.getContext());
            tv.setText("Weapons");
            tv.setTextSize(15);
            llWeaponLayout.addView(tv);
            llWeaponLayout.setVisibility(View.VISIBLE);
        }

        for (Weapon w : active.getWeapons()){
            sb = new StringBuilder();
            sb.append(w.name).append("   ");
            sb.append(w.dice).append("+").append(w.bonus);
            if (w.finess.equals( "TRUE")){
                sb.append("   Finesse");
            }
            TextView tv = new TextView(llWeaponLayout.getContext());
            tv.setText(sb.toString());
            llWeaponLayout.addView(tv);
        }
    }

    private void setSpells() {
        StringBuilder sb;LinearLayout llSpellLayout = (LinearLayout) findViewById(R.id.llSpellLayout);
        for (int i = 0; i < 10; i++){
            ArrayList<String> spells = active.getSpellList().get(i);
            if (!spells.isEmpty()){
                TextView tv = new TextView(llSpellLayout.getContext());
                if (i == 0){
                    tv.setText("Cantrips");
                } else {
                    tv.setText("Level " + i + " Spells");
                }
                tv.setTextSize(15);
                llSpellLayout.setVisibility(View.VISIBLE);
                llSpellLayout.addView(tv);
                TextView stv = new TextView(llSpellLayout.getContext());
                sb = new StringBuilder();
                for (int j = 0; j < spells.size(); j++){
                    sb.append(spells.get(j));
                    if (j != spells.size()-1){
                        sb.append(", ");
                    }
                }
                stv.setText(sb.toString());
                llSpellLayout.addView(stv);
            }
        }
    }
}
