import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * This class is responsible for IO with server
 *
 * @Author
 * @Since 2019-04-29
 */

public class Client {
    private Socket mySocket; //socket for connection
    private BufferedReader input; //reader for network stream
    private PrintWriter output;  //printwriter for network output
    private boolean running = true; //thread status via boolean

    /** Go
     * Starts the client
     */
    public void go() {

        //Create a socket (try-catch required)
        System.out.println("Attempting to make a connection..");

        try {
            mySocket = new Socket("10.242.161.220",6666); //attempt socket connection (local address). This will wait until a connection is made

            InputStreamReader stream1= new InputStreamReader(mySocket.getInputStream()); //Stream for network input
            input = new BufferedReader(stream1);

            output = new PrintWriter(mySocket.getOutputStream()); //assign printwriter to network stream

        } catch (IOException e) {  //connection error occured
            System.out.println("Connection to Server Failed");
            e.printStackTrace();
        }

        System.out.println("Connection made.");
        boolean joinedRoom = false;
        while (!joinedRoom) {
            joinedRoom = enterRoom();
        }

        boolean nameChosen = false;
        while (!nameChosen) {
            nameChosen = chooseName();
        }

        while (running) {
            String msg = getServerMessage().trim();
            if (msg.contains("BOARD")) {
                ChineseCheckers.readGrid(msg);
            }
        }

        try {  //after leaving the main loop we need to close all the sockets
            input.close();
            output.close();
            mySocket.close();
        }catch (Exception e) {
            System.out.println("Failed to close socket");
        }
    }

    private boolean enterRoom () {
        Scanner sc = new Scanner(System.in);
        System.out.println ("What's the name of the room you want to join?");
        String roomName = sc.next();
        output.println("JOINROOM " + roomName);
        output.flush();
        String msg = getServerMessage();
        if (msg.contains("ERROR")) {
            System.out.println(msg);
            return false;
        }
        return true;
    }

    private boolean chooseName () {
        Scanner sc = new Scanner(System.in);
        System.out.println ("What's your name?");
        String name = sc.next();
        output.println("CHOOSENAME " + name);
        output.flush();
        String msg = getServerMessage();
        if (msg.contains("ERROR")) {
            System.out.println(msg);
            return false;
        }
        return true;
    }

    private String getServerMessage () {
        while(true) {  // loop unit a message is received
            try {
                if (input.ready()) { //check for an incoming messge
                    String msg = input.readLine(); //read the message
                    return msg;

                }
            }catch (IOException e) {
                System.out.println("Failed to receive msg from the server");
                e.printStackTrace();
            }
        }
    }

    public void sendMove (String move) {
        output.println(move);
        output.flush();
    }

    public void setRunning (boolean running) {
        this.running = running;
    }
}
