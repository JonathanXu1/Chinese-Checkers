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
  private boolean running = false; //thread status via boolean

  private Scanner keyboardScanner = new Scanner(System.in);


  /**
   * Go
   * Starts the client
   */
  public void go() {

    //Create a socket (try-catch required)
    System.out.println("Attempting to make a connection..");

    try {
      mySocket = new Socket("localhost", 6666); //attempt socket connection (local address). This will wait until a connection is made

      InputStreamReader stream1 = new InputStreamReader(mySocket.getInputStream()); //Stream for network input
      input = new BufferedReader(stream1);

      output = new PrintWriter(mySocket.getOutputStream()); //assign printwriter to network stream
      System.out.println("Connection made.");
      running = true;
    } catch (IOException e) {  //connection error occured
      System.out.println("Connection to Server Failed");
      e.printStackTrace();
    }


    if(running){
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

      keyboardScanner.close();
    }

    //Get server messages sending board and send information to algorithm
    while (running) {
      String msg = getServerMessage();
      if (msg.contains("BOARD")) {
        ChineseCheckers.readGrid(msg);
      }
      String output = "";
      sendMessage(output);
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
    System.out.println("What's the name of the room you want to join?");
    String roomName = keyboardScanner.nextLine();
    sendMessage("JOINROOM " + roomName);
    String msg = getServerMessage();
    if (msg.contains("ERROR")) {
      return false;
    }
    System.out.println("Successfully joined " + roomName + ".");
    return true;
  }

  private boolean chooseName() {
    System.out.println("What's your name?");
    String name = keyboardScanner.nextLine();
    sendMessage("CHOOSENAME " + name);
    String msg = getServerMessage();
    if (msg.contains("ERROR")) {
      return false;
    }
    System.out.println("Successfully chosen name " + name + ".");
    return true;
  }

  private String getServerMessage() {
    while (true) {
      try {
        if (input.ready()) { //check for an incoming message
          String message = input.readLine().trim();
          System.out.println("Server response: " + message);
          return message;
        }
      } catch (IOException e) {
        System.out.println("Failed to receive msg from the server");
        e.printStackTrace();
      }
    }
  }

  public void sendMessage(String msg) {
    output.println(msg);
    output.flush();
  }

  public void setRunning(boolean running) {
    this.running = running;
  }
}
