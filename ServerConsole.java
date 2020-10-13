// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.Scanner;

import common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 * @version September 2020
 */
public class ServerConsole implements ChatIF
{
  //Class variables *************************************************
    final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  EchoServer server;
  
  
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 

  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ServerConsole UI.
   *
   * @param host The host to listen to.
   * @param port The port to listen on.
   */
  public ServerConsole(int port) 
  {
    server = new EchoServer(port, this);
    try {
      server.listen(); //Start listening for connections
    } catch (Exception ex) {
      System.out.println("ERROR - Could not listen for clients!");
    }
    
    // Create scanner object to read from console
    fromConsole = new Scanner(System.in); 
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept() 
  {
    try {
      String msg;
      while (true) 
      {
        msg = fromConsole.nextLine();
        if (msg.charAt(0) == '#') {
          if (strcmp(msg, "quit", 1, 0) == 0) {
            try {
              server.close();
            } catch (IOException e) { }
            System.exit(0);
          } else if (strcmp(msg, "stop", 1, 0) == 0) {
            server.stopListening();
          } else if (strcmp(msg, "close", 1, 0) == 0) {
            try {
              server.close();
            } catch (IOException e) { }
          } else if (strcmp(msg, "setport", 1, 0) == 0) {
            if (server.isListening()) {
              this.display("Cannot setport while running.");
            } else {
              try {
                server.close();
                server.setPort(Integer.parseInt(msg.substring(9)));
              } catch (Exception e) { this.display("could not set port"); }
            }
          } else if (strcmp(msg, "start", 1, 0) == 0) {
            try {
              server.listen();
            } catch (Exception e) { this.display("could not Start server"); }
          } else if (strcmp(msg, "getport", 1, 0) == 0) {
            this.display(String.format("port: %d", server.getPort()));
          } else {
            this.display("Unknown command .");
          }
        } else {
          server.handleMessageFromServerUI(msg);
        }
      }
    } 
    catch (Exception ex) {
      System.out.println(ex);
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println("> " + message);
  }

  
  //Class methods ***************************************************
  
  /**
   * This method is used earlier in the file to handle string comparisons
   */
  private int strcmp(String s1, String s2, int start1, int start2) {
    for (int i = 0; i < java.lang.Math.min(s1.length() - start1, s2.length() - start2); ++i) {
      if (s1.charAt(i + start1) != s2.charAt(i + start2)) {
        return s1.charAt(i + start1) - s2.charAt(i + start2);
      }
    }
    return 0;
  }

  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The host to connect to.
   */
  public static void main(String[] args) 
  {
    int port = DEFAULT_PORT;

    try {
      port = Integer.parseInt(args[0]);
    } catch(ArrayIndexOutOfBoundsException e) { }
    System.out.println("port: " + port);
        
    ServerConsole server = new ServerConsole(port);
    server.accept();
  }
}
//End of ConsoleChat class
