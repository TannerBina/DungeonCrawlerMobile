package hu.ait.android.dungeoncrawler.imports.callers;

import hu.ait.android.dungeoncrawler.data.User;
import hu.ait.android.dungeoncrawler.imports.backend.Character;
import hu.ait.android.dungeoncrawler.imports.backend.Weapon;

public class CreateCharacterInput {
    private String username;
    private String name;
    private String race;
    private String classID;
    private String background;
    private String alignment;
    private String baseStats;
    private String baseHP;
    private String level;
    private String ac;
    private String featList;
    private String spellList;
    private String weaponList;
    private String itemList;
    private String profList;

    public CreateCharacterInput(Character c){
        username = User.getInstance().getUsername();
        name = c.getStat(Character.StatTag.NAME);
        race = c.getStat(Character.StatTag.RACE);
        classID = c.getStat(Character.StatTag.CLASS);
        background = c.getStat(Character.StatTag.BACKGROUND);
        alignment = c.getStat(Character.StatTag.ALIGNMENT);
        baseStats = String.format("__STR__%s__DEX__%s__CON__%s" +
                "__INT__%s__WIS__%s__CHA__%s",
                c.getStat(Character.StatTag.STR),
                c.getStat(Character.StatTag.DEX),
                c.getStat(Character.StatTag.CON),
                c.getStat(Character.StatTag.INT),
                c.getStat(Character.StatTag.WIS),
                c.getStat(Character.StatTag.CHA));
        baseHP = c.getStat(Character.StatTag.BASE_HP);
        level = c.getStat(Character.StatTag.LEVEL);
        ac = c.getStat(Character.StatTag.AC);

        StringBuilder sb = new StringBuilder();
        for (String f : c.getFeats()){
            sb.append("__FEAT__");
            sb.append(f);
        }
        featList = sb.toString();
        if (featList.isEmpty()){
            featList = "__NULL";
        }

        sb = new StringBuilder();
        for (int i = 0; i < 10; i++){
            for (String s : c.getSpellList().get(i)){
                sb.append("__SPELL__");
                sb.append(i);
                sb.append("__");
                sb.append(s);
            }
        }
        spellList = sb.toString();
        if (spellList.isEmpty()){
            spellList = "__NULL";
        }

        sb = new StringBuilder();
        for (Weapon w : c.getWeapons()){
            sb.append(w.toString());
        }
        weaponList = sb.toString();
        if (weaponList.isEmpty()){
            weaponList = "__NULL";
        }

        sb = new StringBuilder();
        for (String i : c.getItems()){
            sb.append("__ITEM__");
            sb.append(i);
        }
        itemList = sb.toString();
        if (itemList.isEmpty()){
            itemList = "__NULL";
        }

        sb = new StringBuilder();
        for (String p : c.getProfs()){
            sb.append("__PROF__");
            sb.append(p);
        }
        profList = sb.toString();
        if (profList.isEmpty()){
            profList = "__NULL";
        }
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getRace() {
        return race;
    }

    public String getClassID() {
        return classID;
    }

    public String getBackground() {
        return background;
    }

    public String getAlignment() {
        return alignment;
    }

    public String getBaseStats() {
        return baseStats;
    }

    public String getBaseHP() {
        return baseHP;
    }

    public String getLevel() {
        return level;
    }

    public String getAc() {
        return ac;
    }

    public String getFeatList() {
        return featList;
    }

    public String getSpellList() {
        return spellList;
    }

    public String getWeaponList() {
        return weaponList;
    }

    public String getItemList() {
        return itemList;
    }

    public String getProfList() {
        return profList;
    }
}
