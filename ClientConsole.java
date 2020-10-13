// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.Scanner;

import client.*;
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
public class ClientConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************

  String host;
  int port;
  String id;
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;
  
  
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 

  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientConsole(String host, int port, String id) {
    this.host = host;
    this.port = port;
    this.id = id;
      
    try {
      client= new ChatClient(this.host, this.port, this.id, this);
    } 
    catch(IOException exception) {
      System.out.println("Error: Can't setup connection! Awaiting command");
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
    try
    {
      String msg;
      while (true) 
      {
        msg = fromConsole.nextLine();
        if (msg.charAt(0) == '#') {
          if (strcmp(msg, "quit", 1, 0) == 0) {
            if (client != null) {
              client.quit();
            };
            System.exit(0);
          } else if (strcmp(msg, "logoff", 1, 0) == 0) {
            try
            {
              client.closeConnection();
            }
            catch(IOException e) { }
          } else if (strcmp(msg.toString(), "sethost", 1, 0) == 0) {
            if (client != null && client.isConnected()) {
              this.display("cannot #sethost, client is already connected somewhere.");
            } else {
              String h = msg.toString().substring(8).trim();
              this.display(h);
              host = h;
            }
          } else if (strcmp(msg, "setport", 1, 0) == 0) {
            try {
              port = Integer.parseInt(msg.substring(8).trim());
            } catch (Exception e) {
              this.display(e.toString());
              this.display("Badly formatted command! Format: #setport PORT");
            }
          } else if (strcmp(msg, "login", 1, 0) == 0) {
            if (msg.trim().length() <= 7) {
              System.out.println("Must provide ID.");
            } else {
              this.id = msg.substring(7);
              try {
                client = new ChatClient(this.host, this.port, this.id, this);
              } catch(IOException exception) {
                System.out.println("Error: Can't setup connection! Awaiting command");
                return;
              }
              this.display("<" + id + "> has logged in.");
            }
          } else if (strcmp(msg, "gethost", 1, 0) == 0) {
            if (client != null) {
              this.display(client.getHost());
            } else {
              System.out.println(String.format("not logged in, but saved host is %s", host));
            }
          } else if (strcmp(msg, "getport", 1, 0) == 0) {
            if (client != null) {
              this.display(String.format("%d", client.getPort()));
            } else {
              System.out.println(String.format("not logged in, but saved port is %d", port));
            }
          } else {
            this.display("Unknown command!");
          }
        } else {
          if (client == null) {
            System.out.println("Not logged in, and command not entered.");
          } else {
            client.handleMessageFromClientUI(msg);
          }
        }
      }
    } 
    catch (Exception ex) 
    {
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
    String host = "";
    int port = DEFAULT_PORT;
    String id = "";

    try {
      id = args[0];
    } catch(ArrayIndexOutOfBoundsException e) {
      System.out.println("ERROR - No login ID specified.  Connection aborted.");
      System.exit(1);
    }
    System.out.println("ID: " + id);

    try {
      host = args[1];
    } catch(ArrayIndexOutOfBoundsException e) {
      host = "localhost";
    } 
    System.out.println("host: " + host);

    try {
      port = Integer.parseInt(args[2]);
    } catch(ArrayIndexOutOfBoundsException e) { }
    System.out.println("port: " + port);
        
    ClientConsole chat = new ClientConsole(host, port, id);
    chat.accept();  //Wait for console data
  }
}
//End of ConsoleChat class
