package hu.ait.android.dungeoncrawler.imports.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import hu.ait.android.dungeoncrawler.data.User;
import hu.ait.android.dungeoncrawler.imports.util.Constants;
import hu.ait.android.dungeoncrawler.imports.util.Secret;

public class Client {
    //socket for communication
    private Socket socket;
    //sender and listening thread for communication
    private PrintWriter sender;
    private ReadingThread listener;

    //whether the client is active
    private boolean active;

    public Client() {
        active = false;
        if (Constants.DEBUG) {
            connect(Secret.DEBUGSERVER, Secret.PORT);
        } else
            connect(Secret.SERVER, Secret.PORT);
    }

    /*
    Connect to a server with the given port
     */
    public void connect(String serverName, int serverPort){
        System.out.println("Establishing connection to DM. Please wait...");
        try {
            //create the socket
            socket = new Socket(serverName, serverPort);
            System.out.println("Connected Successfully.");

            //start reading thread and sender
            active = true;
            listener = new ReadingThread(socket);
            sender = new PrintWriter(socket.getOutputStream());
        } catch (IOException e){
            e.printStackTrace();
            System.err.println("Could not connect to Server : "
                    + serverName + " : " + serverPort);
        }
    }

    /*
    Send a message to the server
     */
    public void send(String s){
        if (Constants.DEBUG) {
            System.out.println("Sent : " + s);
        }
        sender.print(s + '\n');
        sender.flush();
    }

    /*
     * Caled whenever a message is recieved to handle it
     */
    public void recieve(String s) {
        User.getInstance().handle(s);
    }

    /*
    Close the thread
     */
    public void close(){
        //close the sending thread
        sender.close();

        //attempt to close the socket
        try {
            socket.close();
        } catch (IOException e){
            System.err.println("Error closing Client Thread.");
        }

        //if listener is open, close it
        if (listener != null)
            listener.closeReader();
        listener = null;
        active = false;
    }

    /*
    Reading thread for reading in messages from server
     */
    private class ReadingThread extends Thread{
        //socket for sending and recieving messages
        private Socket socket;
        //reader for inputted messages
        private BufferedReader reader;

        /*
        Creates the reading thread
         */
        public ReadingThread(Socket socket){
            this.socket = socket;

            //open and start the thread
            open();
            start();
        }

        /*
        Settup the input reader
         */
        public void open(){
            try{
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e){
                System.err.println("Error getting input stream in client.");
                close();
            }
        }

        /*
        Close the reader
         */
        public void closeReader(){
            try{
                reader.close();
            } catch (IOException e){
                System.err.println("Error closing input stream in client.");
            }
        }

        /*
        Run the message reciever
         */
        public void run(){
            while(active){
                try {
                    //attempt to read a line and send to client code to handle
                    recieve(reader.readLine());
                } catch (IOException e){
                    System.err.println("Listening error in Client.");
                    close();
                }
            }
        }
    }

    public boolean isActive() {
        return active;
    }
}
