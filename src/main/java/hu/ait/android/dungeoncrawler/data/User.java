package hu.ait.android.dungeoncrawler.data;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;

import java.util.ArrayList;
import java.util.Scanner;

import hu.ait.android.dungeoncrawler.imports.backend.Character;
import hu.ait.android.dungeoncrawler.imports.backend.Game;
import hu.ait.android.dungeoncrawler.imports.callers.FetchCharacterInput;
import hu.ait.android.dungeoncrawler.imports.callers.FetchCharacterService;
import hu.ait.android.dungeoncrawler.imports.callers.Output;
import hu.ait.android.dungeoncrawler.imports.util.Constants;
import hu.ait.android.dungeoncrawler.imports.util.Messages;

public class User {
    private static User user = null;

    private String username;
    private String password;

    private Game currentGame;
    private boolean dm;

    private ArrayList<Character> allCharacters;
    private Character activeCharacter;
    private ArrayList<Character> party;

    private User(){
        allCharacters = new ArrayList<>();
        party = new ArrayList<>();
    }

    public static User getInstance(){
        if (user == null) user = new User();
        return user;
    }

    public void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean setCharacters(String data, LambdaInvokerFactory factory) {
        allCharacters = new ArrayList<>();
        Scanner s = new Scanner(data);
        s.useDelimiter(Constants.LAMBDA_DELIMINATOR);

        while(s.hasNext()){
            String tag = s.next();

            if (tag.equals(Constants.CHAR_ID_TAG)){
                String id = s.next();
                FetchCharacterInput in = new FetchCharacterInput();
                in.setId(id);

                FetchCharacterService fetchCharacter = factory.build(FetchCharacterService.class);
                Output out = fetchCharacter.fetchCharacter(in);
                if (scanOutput(out)) return false;
            }
        }

        s.close();

        return true;
    }

    private boolean scanOutput(Output out) {
        Scanner s2 = new Scanner(out.getMessage());
        s2.useDelimiter(Constants.LAMBDA_DELIMINATOR);

        switch (s2.next()) {
            case Messages.ERROR_TAG:
                return true;
            case Messages.SUCCESS_TAG:
                allCharacters.add(new Character(out.getData()));
                break;
        }
        s2.close();
        return false;
    }

    public ArrayList<Character> getAllCharacters() {
        return allCharacters;
    }

    public void reset(){
        user = null;
    }
}
