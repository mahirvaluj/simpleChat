// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import com.lloseng.ocsf.server.*;

import common.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  /**
   * The default port to listen on.
   */

  final public static int DEFAULT_PORT = 5555;
  
  ChatIF serverUI; 

  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) {
    super(port);
  }

  public EchoServer(int port, ChatIF serverUI) {
    super(port);
    this.serverUI = serverUI;
  }

  
  //Instance methods ************************************************

  @Override
  protected void clientConnected(ConnectionToClient client) {
    System.out.println("Client " + client.toString() + " has connected");
  }
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
    System.out.println("Client " + client.toString() + " has disconnected");
  }

  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
    String s = (String) msg;
    String idx = (String) client.getInfo("id");
    if (idx != null) {
      serverUI.display("Message recieved: '" + msg + "' + from " + idx);
    } else {
      serverUI.display("Message recieved: '" + msg + "' + from " + client.toString());
    }
    if (s.charAt(0) == '#') {
      if (strcmp(s, "login", 1, 0) == 0) {
        Boolean typed = (Boolean) client.getInfo("typed");
        if ( typed != null && typed.booleanValue() == true) {
          System.out.println("Attempted to login after sending messages");
          try {
            client.close();
          } catch (IOException e) { }
        } else {
          String id = s.substring(7);
          client.setInfo("id", id);
          serverUI.display("<" + id + "> has logged in.");
          client.setInfo("logged_in", new Boolean(true));
        }
      } else {
        System.out.println("Bad command from client");
      }
    } else {
      client.setInfo("typed", new Boolean(true));
      // System.out.println("Message received: " + s + " from " + client);
      String clientID = (String) client.getInfo("id");
      if (clientID == null) {
        clientID = "anonymous";
      }
      this.sendToAllClients(clientID + ":> " + s.trim());
    }
  }

  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the CLI.
   */
  public void handleMessageFromServerUI(String msg)
  {
    if (msg.charAt(0) == '#') {
      if (strcmp(msg, "quit", 1, 0) == 0) {
        try {
          this.close();
        } catch (IOException e) { }
        System.exit(0);
      } else if (strcmp(msg, "stop", 1, 0) == 0) {
        this.stopListening();
      } else if (strcmp(msg, "close", 1, 0) == 0) {
        try {
          this.close();
        } catch (IOException e) { }
      } else if (strcmp(msg, "setport", 1, 0) == 0) {
        if (this.isListening()) {
          serverUI.display("Cannot setport while running.");
        } else {
          try {
            this.close();
            this.setPort(Integer.parseInt(msg.substring(9)));
          } catch (Exception e) { serverUI.display("could not set port"); }
        }
      } else if (strcmp(msg, "start", 1, 0) == 0) {
        try {
          this.listen();
        } catch (Exception e) { serverUI.display("could not Start server"); }
      } else if (strcmp(msg, "getport", 1, 0) == 0) {
        serverUI.display(String.format("port: %d", this.getPort()));
      } else {
        serverUI.display("Unknown command .");
      }
    } else {
      String s = "SERVER MSG> " + msg;
      System.out.println(s);
      this.sendToAllClients(s);
    }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
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
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
  
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
//End of EchoServer class
