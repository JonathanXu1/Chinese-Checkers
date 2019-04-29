import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    Socket mySocket; //socket for connection
    BufferedReader input; //reader for network stream
    PrintWriter output;  //printwriter for network output
    boolean running = true; //thread status via boolean

    /** Go
     * Starts the client
     */
    public void go() {

        //Create a socket (try-catch required)
        System.out.println("Attempting to make a connection..");

        try {
            mySocket = new Socket("10.242.161.220",5530); //attempt socket connection (local address). This will wait until a connection is made

            InputStreamReader stream1= new InputStreamReader(mySocket.getInputStream()); //Stream for network input
            input = new BufferedReader(stream1);

            output = new PrintWriter(mySocket.getOutputStream()); //assign printwriter to network stream

        } catch (IOException e) {  //connection error occured
            System.out.println("Connection to Server Failed");
            e.printStackTrace();
        }

        System.out.println("Connection made.");

        // Send a message to the server (Once)
        output.println("Hi. I am a client!");

        output.flush();  //flush (very important)

        while(running) {  // loop unit a message is received
            try {

                if (input.ready()) { //check for an incoming messge
                    String msg;
                    msg = input.readLine(); //read the message
                    System.out.println("msg from server: " + msg);
                }

            }catch (IOException e) {
                System.out.println("Failed to receive msg from the server");
                e.printStackTrace();
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
}
