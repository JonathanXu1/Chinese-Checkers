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

  /**
   * Go
   * Starts the client
   */
  public void go() {

    //Create a socket (try-catch required)
    System.out.println("Attempting to make a connection..");

    try {
      mySocket = new Socket("10.242.161.220", 6666); //attempt socket connection (local address). This will wait until a connection is made

      InputStreamReader stream1 = new InputStreamReader(mySocket.getInputStream()); //Stream for network input
      input = new BufferedReader(stream1);

      output = new PrintWriter(mySocket.getOutputStream()); //assign printwriter to network stream

    } catch (IOException e) {  //connection error occured
      System.out.println("Connection to Server Failed");
      e.printStackTrace();
    }
    System.out.println("Connection made.");

    //Join a room
    boolean joinedRoom = false;
    while (!joinedRoom) {
      joinedRoom = enterRoom();
    }

    //Choose a name
    boolean nameChosen = false;
    while (!nameChosen) {
      nameChosen = chooseName();
    }

    //Get server messages sending board and send information to algorithm
    while (running) {
      String msg = getServerMessage().trim();
      if (msg.contains("BOARD")) {
        ChineseCheckers.readGrid(msg);
      }
    }

    //Close sockets and IO
    try {
      input.close();
      output.close();
      mySocket.close();
    } catch (Exception e) {
      System.out.println("Failed to close socket");
    }
  }

  private boolean enterRoom() {
    Scanner keyboardScanner = new Scanner(System.in);
    System.out.println("What's the name of the room you want to join?");
    String roomName = keyboardScanner.next();
    keyboardScanner.close();
    sendMessage("JOINROOM " + roomName);
    String msg = getServerMessage();
    if (msg.contains("ERROR")) {
      System.out.println(msg);
      return false;
    }
    System.out.println("Successfully joined " + roomName + ".");
    return true;
  }

  private boolean chooseName() {
    Scanner keyboardScanner = new Scanner(System.in);
    System.out.println("What's your name?");
    String name = keyboardScanner.next();
    keyboardScanner.close();
    sendMessage("CHOOSENAME " + name);
    String msg = getServerMessage();
    if (msg.contains("ERROR")) {
      System.out.println(msg);
      return false;
    }
    System.out.println("Successfully chosen name " + name + ".");
    return true;
  }

  private String getServerMessage() {
    try {
      if (input.ready()) { //check for an incoming message
        return input.readLine();
      }
    } catch (IOException e) {
      System.out.println("Failed to receive msg from the server");
      e.printStackTrace();
    }
    return "";
  }

  public void sendMessage(String msg) {
    output.println(msg);
    output.flush();
  }

  public void setRunning(boolean running) {
    this.running = running;
  }
}
