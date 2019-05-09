package Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * This class is responsible for IO with server
 *
 * @Author Bryan Zhang, Jonathan Xu, Carol Chen
 * @Since 2019-04-29
 */

public class Client {
  private static Socket mySocket; //socket for connection
  private static BufferedReader input; //reader for network stream
  private static PrintWriter output; //printwriter for network output
  private static boolean running = false; //thread status via boolean
  //TODO: Delete running boolean probably
  private static ChineseCheckers algorithm = new ChineseCheckers();

  private static Scanner keyboardScanner = new Scanner(System.in);

  /**
   * Main
   */
  public static void main(String[] args) {

    //Create a socket (try-catch required)
    System.out.println("Attempting to make a connection..");

    try {
      mySocket = new Socket("localhost", 6666); //attempt socket connection (local address). This will wait until a connection is made

      InputStreamReader stream1 = new InputStreamReader(mySocket.getInputStream()); //Stream for network input
      input = new BufferedReader(stream1);

      output = new PrintWriter(mySocket.getOutputStream()); //assign printwriter to network stream
      System.out.println("Connection made.");
      running = true;
    } catch (IOException e) { //connection error occured
      System.out.println("Connection to Server Failed");
      e.printStackTrace();
    }

    if (running) {
      //Join a room
      enterRoom();
      //Choose a name
      chooseName();
      keyboardScanner.close();
    }

    //Get server messages sending board and send information to algorithm
    while (running) {
      String msg = getServerMessage();
      if (msg.contains("BOARD")) {
        algorithm.readGrid(msg);
        if (!algorithm.checkWin());
        String output = algorithm.makeMove();
        sendMessage(output);
        System.out.println("Post to server: " + output);
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

  private static void enterRoom() {
    boolean success = false;
    String roomName = "";
    while (!success) {
      System.out.println("What's the name of the room you want to join?");
      roomName = keyboardScanner.nextLine();
      sendMessage("JOINROOM " + roomName);
      String msg = getServerMessage();
      if (msg.contains("OK")) {
        success = true;
      }
    }
    System.out.println("Successfully joined " + roomName + ".");
  }

  private static void chooseName() {
    boolean success = false;
    String name = "";
    while (!success) {
      System.out.println("What's your name?");
      name = keyboardScanner.nextLine();
      sendMessage("CHOOSENAME " + name);
      String msg = getServerMessage();
      if (msg.contains("OK")) {
        success = true;
      }
    }
    System.out.println("Successfully chosen name " + name + ".");
  }

  private static String getServerMessage() {
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

  public static void sendMessage(String msg) {
    output.println(msg);
    output.flush();
  }

}