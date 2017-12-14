package hu.ait.android.dungeoncrawler.data;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import hu.ait.android.dungeoncrawler.imports.backend.Client;
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
    private ArrayList<Game> allGames;

    private ArrayList<Character> allCharacters;
    private Character activeCharacter;

    private ArrayList<Character> party;
    private Client client;
    private boolean dm;

    private Queue<String> messageQueue;

    private User(){
        allCharacters = new ArrayList<>();
        party = new ArrayList<>();
        dm = false;
        messageQueue = new LinkedList<>();
    }

    public static User getInstance(){
        if (user == null) user = new User();
        return user;
    }

    public void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Character findCharacter(String name){
        for (Character c : allCharacters){
            if (c.getStat(Character.StatTag.NAME).equals(name)){
                return c;
            }
        }
        return null;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setGames(String games){
        allGames = new ArrayList<>();
        Scanner s = new Scanner(games);
        s.useDelimiter(Constants.LAMBDA_DELIMINATOR);

        while (s.hasNext()){
            String tag = s.next();
            if (tag.equals("GAME")){
                String data = s.next();
                Scanner s2 = new Scanner(data);
                s2.useDelimiter("_");

                String name = s2.next();
                String host = s2.next();
                String password = s2.next();
                allGames.add(new Game(name, password, host));

                s2.close();
            }
        }

        s.close();
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
                allCharacters.get(allCharacters.size()-1).validate();
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

    public Character getActiveCharacter() {
        return activeCharacter;
    }

    public void setActiveCharacter(Character activeCharacter) {
        this.activeCharacter = activeCharacter;
    }

    public ArrayList<Game> getAllGames() {
        return allGames;
    }

    public void setActiveGame(Game activeGame) {
        currentGame = activeGame;
    }

    public Game getActiveGame() {
        return currentGame;
    }

    /*
	 * Sends a string to the server to be handled
	 * adds tag to the string
	 */
    public void send(String s) {
        String res = s;

        if (dm) {
            if (s.charAt(0) != '@') {
                res = "DM: " + s;
            }
        } else {
            if (s.charAt(0) != '$') {
                res = activeCharacter.getStat(Character.StatTag.NAME) +": " + s;
            }
        }

        client.send(res);
    }

    public String popMessage(){
        if (messageQueue.isEmpty()) return null;
        return messageQueue.remove();
    }

    public void close(){
        client.close();
    }

    /*
     * Handles an inputted string send from the
     * server
     */
    public void handle(String s) {
        if (Constants.DEBUG) {
            System.out.println("DM Status : " + dm);
            System.out.println("Handling : " + s);
        }

        if (s.charAt(0) == '#') {
            Scanner scan = new Scanner(s);
            switch(scan.next()) {

                //send character command if not dm, fetch character and send with code given
                case "#SENDCHAR":
                    if (Constants.DEBUG) System.out.println("Sending character");
                    sendCharacter(scan.next());
                    break;

                //set a given character id in the party with the information
                case "#SETCHAR":
                    updateCharacter(scan.next(), scan.nextLine());
                    break;

                //add a character to the party
                case "#ADDCHAR":
                    String character = scan.nextLine();
                    party.add(new Character(character));
                    break;

                case "#REMOVECHAR":
                    String id = scan.next();
                    for (int i = party.size()-1; i>= 0; i--) {
                        Character c = party.get(i);
                        if (c.getStat(Character.StatTag.ID).equals(id)) {
                            party.remove(i);
                        }
                    }
                    break;

                //if its not recognized, print out that it cnat be handles
                default:
                    System.err.println("Cannot handle Command in User.handle : " + s);
                    break;
            }
            scan.close();
        } else {
            messageQueue.add(s);
        }
    }

    public void updateCharacter(String id, String character) {
        if (activeCharacter != null && activeCharacter.getStat(Character.StatTag.ID).equals(id)){
            activeCharacter.setCharacter(character);
        }
        for (Character c : party) {
            if (c.getStat(Character.StatTag.ID).equals(id)){
                c.setCharacter(character);
            }
        }
    }

    /*
	 * Joins a particular game given the inputted name,
	 * password, and the character to join the game with
	 */
    public boolean joinGame(String name, String password) {
        messageQueue = new LinkedList<>();
        client = new Client();
        if (client.isActive()) {
            client.send("$JOINGAME " + name);
            dm = false;
            System.out.println(activeCharacter.toString());
        } else {
            System.err.println("Error Activiating Client in User.joinGame");
        }
        return client.isActive();
    }

    //sends the active character to server with the entered validation code
    public void sendCharacter(String code) {
        if (dm) return;

        String response = "$SETCHAR " + code + " " + activeCharacter.toString();
        client.send(response);
    }
}
