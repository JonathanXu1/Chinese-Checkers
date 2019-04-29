//imports for network communication
import java.io.*;
import java.net.*;

public class ChineseCheckers {


  /** Main
   * @param args parameters from command line
   */
  public static void main (String[] args) {
    Client client = new Client(); //start the client
    client.go(); //begin the connection
  }


}
