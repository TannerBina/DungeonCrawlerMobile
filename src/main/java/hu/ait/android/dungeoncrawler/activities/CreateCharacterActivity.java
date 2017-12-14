package hu.ait.android.dungeoncrawler.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunctionException;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;

import java.util.ArrayList;
import java.util.Scanner;

import hu.ait.android.dungeoncrawler.R;
import hu.ait.android.dungeoncrawler.adapters.CharacterAdapter;
import hu.ait.android.dungeoncrawler.data.User;
import hu.ait.android.dungeoncrawler.imports.backend.Character;
import hu.ait.android.dungeoncrawler.imports.backend.Weapon;
import hu.ait.android.dungeoncrawler.imports.callers.CreateCharacterInput;
import hu.ait.android.dungeoncrawler.imports.callers.CreateCharacterService;
import hu.ait.android.dungeoncrawler.imports.callers.Output;
import hu.ait.android.dungeoncrawler.imports.util.Messages;

import static hu.ait.android.dungeoncrawler.imports.util.Constants.CASTING_CLASSES;

public class CreateCharacterActivity extends AppCompatActivity {

    EditText etCCName;
    EditText etCCLevel;
    Spinner spnCCClass;
    Spinner spnCCRace;
    Spinner spnCCBackground;
    Spinner spnCCAlignment;

    EditText etCCStr;
    EditText etCCDex;
    EditText etCCCon;
    EditText etCCInt;
    EditText etCCWis;
    EditText etCCCha;

    EditText etCCAC;
    EditText etCCBaseHP;

    EditText etCCFeatList;
    EditText etCCItemList;
    EditText etCCProfList;

    TextView tvCCSpellbook;
    EditText etCCLvl0Spells;
    EditText etCCLvl1Spells;
    EditText etCCLvl2Spells;
    EditText etCCLvl3Spells;
    EditText etCCLvl4Spells;
    EditText etCCLvl5Spells;
    EditText etCCLvl6Spells;
    EditText etCCLvl7Spells;
    EditText etCCLvl8Spells;
    EditText etCCLvl9Spells;

    private ArrayList<Weapon> weaponList;
    private String deliminator = ",";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_character);
        weaponList = new ArrayList<>();
        initFields();
        initClassSpinner();
        initAddWeaponButton();
        initFinishButton();
    }

    private void initAddWeaponButton() {
        final Button btnAddWeapon = (Button) findViewById(R.id.btnCCAddWeapon);
        btnAddWeapon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildAddWeaponDialog(btnAddWeapon);
            }
        });
    }

    private void buildAddWeaponDialog(final Button btnAddWeapon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                CreateCharacterActivity.this);
        builder.setTitle("Add Weapon");
        final EditText name = new EditText(CreateCharacterActivity.this);
        name.setHint("Name");
        final EditText numDie = new EditText(CreateCharacterActivity.this);
        numDie.setHint("Num Die");
        numDie.setInputType(InputType.TYPE_CLASS_NUMBER);
        final EditText die = new EditText(CreateCharacterActivity.this);
        die.setHint("Die");
        die.setInputType(InputType.TYPE_CLASS_NUMBER);
        final EditText bonus = new EditText(CreateCharacterActivity.this);
        bonus.setHint("Bonus");
        bonus.setInputType(InputType.TYPE_CLASS_NUMBER);
        final CheckBox finesse = new CheckBox(CreateCharacterActivity.this);
        finesse.setText("Finesse");

        LinearLayout layout = new LinearLayout(CreateCharacterActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(name);
        layout.addView(numDie);
        layout.addView(die);
        layout.addView(bonus);
        layout.addView(finesse);

        builder.setView(layout);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (name.getText().toString().isEmpty()
                        || numDie.getText().toString().isEmpty()
                        || die.getText().toString().isEmpty()
                        || bonus.getText().toString().isEmpty()){
                    Toast.makeText(CreateCharacterActivity.this,
                            R.string.error_weapon_not_specified,
                            Toast.LENGTH_LONG).show();
                } else {
                    Weapon weapon = new Weapon();
                    weapon.name = name.getText().toString();
                    weapon.dice = String.format("%sd%s",
                            numDie.getText().toString(),
                            die.getText().toString());
                    weapon.bonus = Integer.parseInt(bonus.getText().toString());
                    if (finesse.isChecked()){
                        weapon.finess = "TRUE";
                    } else weapon.finess = "FALSE";

                    weaponList.add(weapon);
                    if (weaponList.size() == 3){
                        btnAddWeapon.setEnabled(false);
                    }
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

    private void initFinishButton() {
        Button btnFinish = (Button) findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidity()){
                    createCharacter();
                }
            }
        });
    }

    private void createCharacter() {
        final Character newChar = new Character();
        setIndividualStats(newChar);

        setFeats(newChar);
        setItems(newChar);
        setProfs(newChar);
        setWeapons(newChar);

        if (CASTING_CLASSES.contains((String)spnCCClass.getSelectedItem())){
            setSpells(newChar);
        }

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-2:4afff1a5-f1a5-49fb-99c3-d7167ff61afe", // Identity pool ID
                Regions.US_EAST_2 // Region
        );
        LambdaInvokerFactory factory = new LambdaInvokerFactory(getApplicationContext(),
                Regions.US_EAST_2, credentialsProvider);

        final CreateCharacterInput input = new CreateCharacterInput(newChar);
        final CreateCharacterService createCharacter = factory.build(CreateCharacterService.class);

        new AsyncTask<CreateCharacterInput, Void, Output>(){
            @Override
            protected Output doInBackground(CreateCharacterInput... createCharacterInputs) {
                try {
                    Output out = createCharacter.createCharacter(input);
                    return out;
                } catch (LambdaFunctionException e){
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final Output success){
                if (success != null && !success.getMessage().contains(Messages.ERROR_TAG)){
                    newChar.setStat(Character.StatTag.ID, success.getData());
                } else {
                    Toast.makeText(CreateCharacterActivity.this,
                            getString(R.string.error_unexpected),
                            Toast.LENGTH_LONG).show();
                }
            }
        }.execute(input);

        newChar.validate();
        User.getInstance().getAllCharacters().add(newChar);
        Intent intent = new Intent();
        intent.setClass(CreateCharacterActivity.this, CharacterListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void setSpells(Character newChar) {
        setSpells0(newChar);
        setSpells1(newChar);
        setSpells2(newChar);
        setSpells3(newChar);
        setSpells4(newChar);
        setSpells5(newChar);
        setSpells6(newChar);
        setSpells7(newChar);
        setSpells8(newChar);
        setSpells9(newChar);
    }

    private void setSpells9(Character newChar) {
        Scanner s;
        String spells9 = etCCLvl9Spells.getText().toString();
        s = new Scanner(spells9);
        s.useDelimiter(deliminator);
        while(s.hasNext()){
            newChar.setStat(Character.StatTag.SPELL_9, s.next());
        }
        s.close();
    }

    private void setSpells8(Character newChar) {
        Scanner s;
        String spells8 = etCCLvl8Spells.getText().toString();
        s = new Scanner(spells8);
        s.useDelimiter(deliminator);
        while(s.hasNext()){
            newChar.setStat(Character.StatTag.SPELL_8, s.next());
        }
        s.close();
    }

    private void setSpells7(Character newChar) {
        Scanner s;
        String spells7 = etCCLvl7Spells.getText().toString();
        s = new Scanner(spells7);
        s.useDelimiter(deliminator);
        while(s.hasNext()){
            newChar.setStat(Character.StatTag.SPELL_7, s.next());
        }
        s.close();
    }

    private void setSpells6(Character newChar) {
        Scanner s;
        String spells6 = etCCLvl6Spells.getText().toString();
        s = new Scanner(spells6);
        s.useDelimiter(deliminator);
        while(s.hasNext()){
            newChar.setStat(Character.StatTag.SPELL_6, s.next());
        }
        s.close();
    }

    private void setSpells5(Character newChar) {
        Scanner s;
        String spells5 = etCCLvl5Spells.getText().toString();
        s = new Scanner(spells5);
        s.useDelimiter(deliminator);
        while(s.hasNext()){
            newChar.setStat(Character.StatTag.SPELL_5, s.next());
        }
        s.close();
    }

    private void setSpells4(Character newChar) {
        Scanner s;
        String spells4 = etCCLvl4Spells.getText().toString();
        s = new Scanner(spells4);
        s.useDelimiter(deliminator);
        while(s.hasNext()){
            newChar.setStat(Character.StatTag.SPELL_4, s.next());
        }
        s.close();
    }

    private void setSpells3(Character newChar) {
        Scanner s;
        String spells3 = etCCLvl3Spells.getText().toString();
        s = new Scanner(spells3);
        s.useDelimiter(deliminator);
        while(s.hasNext()){
            newChar.setStat(Character.StatTag.SPELL_3, s.next());
        }
        s.close();
    }

    private void setSpells2(Character newChar) {
        Scanner s;
        String spells2 = etCCLvl2Spells.getText().toString();
        s = new Scanner(spells2);
        s.useDelimiter(deliminator);
        while(s.hasNext()){
            newChar.setStat(Character.StatTag.SPELL_2, s.next());
        }
        s.close();
    }

    private void setSpells1(Character newChar) {
        Scanner s;
        String spells1 = etCCLvl1Spells.getText().toString();
        s = new Scanner(spells1);
        s.useDelimiter(deliminator);
        while(s.hasNext()){
            newChar.setStat(Character.StatTag.SPELL_1, s.next());
        }
        s.close();
    }

    private void setSpells0(Character newChar) {
        Scanner s;

        String spells0 = etCCLvl0Spells.getText().toString();
        s = new Scanner(spells0);
        s.useDelimiter(deliminator);
        while(s.hasNext()){
            newChar.setStat(Character.StatTag.SPELL_0, s.next());
        }
        s.close();
    }

    private void setWeapons(Character newChar) {
        for (Weapon w : weaponList){
            String weaponString = w.toString().substring("__WEAPON__".length(),
                    w.toString().length());
            newChar.setStat(Character.StatTag.WEAPON, weaponString);
        }
    }

    private void setProfs(Character newChar) {
        Scanner s;

        String profs = etCCProfList.getText().toString();
        s = new Scanner(profs);
        s.useDelimiter(deliminator);
        while(s.hasNext()){
            newChar.setStat(Character.StatTag.PROF, s.next());
        }
        s.close();
    }

    private void setItems(Character newChar) {
        Scanner s;

        String items = etCCItemList.getText().toString();
        s = new Scanner(items);
        s.useDelimiter(deliminator);
        while(s.hasNext()){
            newChar.setStat(Character.StatTag.ITEM, s.next());
        }
        s.close();
    }

    private void setFeats(Character newChar) {
        String feats = etCCFeatList.getText().toString();
        Scanner s = new Scanner(feats);
        s.useDelimiter(deliminator);
        while(s.hasNext()){
            newChar.setStat(Character.StatTag.FEAT, s.next());
        }
        s.close();
    }

    private void setIndividualStats(Character newChar) {
        newChar.setStat(Character.StatTag.NAME, etCCName.getText().toString());
        newChar.setStat(Character.StatTag.LEVEL, etCCLevel.getText().toString());
        newChar.setStat(Character.StatTag.CLASS, spnCCClass.getSelectedItem().toString());
        newChar.setStat(Character.StatTag.RACE, spnCCRace.getSelectedItem().toString());
        newChar.setStat(Character.StatTag.BACKGROUND, spnCCBackground.getSelectedItem().toString());
        newChar.setStat(Character.StatTag.ALIGNMENT, spnCCAlignment.getSelectedItem().toString());
        newChar.setStat(Character.StatTag.STR, etCCStr.getText().toString());
        newChar.setStat(Character.StatTag.DEX, etCCDex.getText().toString());
        newChar.setStat(Character.StatTag.CON, etCCCon.getText().toString());
        newChar.setStat(Character.StatTag.INT, etCCInt.getText().toString());
        newChar.setStat(Character.StatTag.WIS, etCCWis.getText().toString());
        newChar.setStat(Character.StatTag.CHA, etCCCha.getText().toString());
        newChar.setStat(Character.StatTag.AC, etCCAC.getText().toString());
        newChar.setStat(Character.StatTag.BASE_HP, etCCBaseHP.getText().toString());
    }

    private boolean checkValidity() {
        EditText focusView = null;
        boolean result = true;

        if (etCCBaseHP.getText().toString().isEmpty()){
            etCCBaseHP.setError(getString(R.string.error_field_required));
            focusView = etCCBaseHP;
            result = false;
        }
        if (etCCAC.getText().toString().isEmpty()){
            etCCAC.setError(getString(R.string.error_field_required));
            focusView = etCCAC;
            result = false;
        }
        if (etCCCha.getText().toString().isEmpty()){
            etCCCha.setError(getString(R.string.error_field_required));
            focusView = etCCCha;
            result = false;
        }
        if (etCCWis.getText().toString().isEmpty()){
            etCCWis.setError(getString(R.string.error_field_required));
            focusView = etCCWis;
            result = false;
        }
        if (etCCInt.getText().toString().isEmpty()){
            etCCInt.setError(getString(R.string.error_field_required));
            focusView = etCCInt;
            result = false;
        }
        if (etCCCon.getText().toString().isEmpty()){
            etCCCon.setError(getString(R.string.error_field_required));
            focusView = etCCCon;
            result = false;
        }
        if (etCCDex.getText().toString().isEmpty()){
            etCCDex.setError(getString(R.string.error_field_required));
            focusView = etCCDex;
            result = false;
        }
        if (etCCStr.getText().toString().isEmpty()){
            etCCStr.setError(getString(R.string.error_field_required));
            focusView = etCCStr;
            result = false;
        }
        if (etCCLevel.getText().toString().isEmpty()){
            etCCLevel.setError(getString(R.string.error_field_required));
            focusView = etCCLevel;
            result = false;
        }
        if (etCCName.getText().toString().isEmpty()){
            etCCName.setError(getString(R.string.error_field_required));
            focusView = etCCName;
            result = false;
        }
        if (User.getInstance().findCharacter(etCCName.getText().toString()) != null){
            etCCName.setError(getString(R.string.error_name_duplicate));
            focusView = etCCName;
            result = false;
        }

        if (focusView != null) focusView.requestFocus();
        return result;
    }

    private void initClassSpinner() {
        spnCCClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = (String) adapterView.getItemAtPosition(i);
                if (CASTING_CLASSES.contains(selected)){
                    setSpellFields(true);
                } else {
                    setSpellFields(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setSpellFields(boolean b) {
        int value = View.GONE;
        if (b) value = View.VISIBLE;

        tvCCSpellbook.setVisibility(value);
        etCCLvl0Spells.setVisibility(value);
        etCCLvl1Spells.setVisibility(value);
        etCCLvl2Spells.setVisibility(value);
        etCCLvl3Spells.setVisibility(value);
        etCCLvl4Spells.setVisibility(value);
        etCCLvl5Spells.setVisibility(value);
        etCCLvl6Spells.setVisibility(value);
        etCCLvl7Spells.setVisibility(value);
        etCCLvl8Spells.setVisibility(value);
        etCCLvl9Spells.setVisibility(value);
    }

    private void initFields(){
        etCCName = (EditText) findViewById(R.id.etCCName);
        etCCLevel = (EditText) findViewById(R.id.etCCLevel);
        spnCCClass = (Spinner) findViewById(R.id.spnCCClass);
        spnCCRace = (Spinner) findViewById(R.id.spnCCRace);
        spnCCBackground = (Spinner) findViewById(R.id.spnCCBackground);
        spnCCAlignment = (Spinner) findViewById(R.id.spnCCAlignment);
        etCCStr = (EditText) findViewById(R.id.etCCStr);
        etCCDex = (EditText) findViewById(R.id.etCCDex);
        etCCCon = (EditText) findViewById(R.id.etCCCon);
        etCCInt = (EditText) findViewById(R.id.etCCInt);
        etCCWis = (EditText) findViewById(R.id.etCCWis);
        etCCCha = (EditText) findViewById(R.id.etCCCha);
        etCCAC = (EditText) findViewById(R.id.etCCAC);
        etCCBaseHP = (EditText) findViewById(R.id.etCCBaseHP);
        etCCFeatList = (EditText) findViewById(R.id.etCCFeatList);
        etCCItemList = (EditText) findViewById(R.id.etCCItemList);
        etCCProfList = (EditText) findViewById(R.id.etCCProfList);
        tvCCSpellbook = (TextView) findViewById(R.id.tvCCSpellbook);
        etCCLvl0Spells = (EditText) findViewById(R.id.etCCLvl0Spells);
        etCCLvl1Spells = (EditText) findViewById(R.id.etCCLvl1Spells);
        etCCLvl2Spells = (EditText) findViewById(R.id.etCCLvl2Spells);
        etCCLvl3Spells = (EditText) findViewById(R.id.etCCLvl3Spells);
        etCCLvl4Spells = (EditText) findViewById(R.id.etCCLvl4Spells);
        etCCLvl5Spells = (EditText) findViewById(R.id.etCCLvl5Spells);
        etCCLvl6Spells = (EditText) findViewById(R.id.etCCLvl6Spells);
        etCCLvl7Spells = (EditText) findViewById(R.id.etCCLvl7Spells);
        etCCLvl8Spells = (EditText) findViewById(R.id.etCCLvl8Spells);
        etCCLvl9Spells = (EditText) findViewById(R.id.etCCLvl9Spells);
    }
}
