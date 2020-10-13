// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import com.lloseng.ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  String id;
  
  //Constructors ****************************************************
 
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
    public ChatClient(String host, int port, String loginID, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.id = loginID;
    openConnection();
    sendToServer("#login " + id);
  }

  
  //Instance methods ************************************************

  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  @Override
  protected void connectionClosed() {
    clientUI.display("connection has been closed. Use #login to log back in...");
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String msg)
  {
    if (msg.charAt(0) == '#') {
      if (strcmp(msg, "quit", 1, 0) == 0) {
        quit();
      } else if (strcmp(msg, "logoff", 1, 0) == 0) {
        try
        {
          closeConnection();
        }
        catch(IOException e) { }
      } else if (strcmp(msg.toString(), "sethost", 1, 0) == 0) {
        if (this.isConnected()) {
          clientUI.display("cannot #sethost, client is already connected somewhere.");
        } else {
          String h = msg.toString().substring(11);
          clientUI.display(h);
          this.setHost(h);
        }
      } else if (strcmp(msg, "setport", 1, 0) == 0) {
        try {
          this.setPort(Integer.parseInt(msg.substring(11)));
        } catch (Exception e) {
          clientUI.display(e.toString());
          clientUI.display("Badly formatted command! Format: #setport PORT");
        }
      } else if (strcmp(msg, "login", 1, 0) == 0) {
        try {
          if (msg.trim().length() <= 7) {
            System.out.println("Must provide ID.");
          } else {
            this.id = msg.substring(7);
            openConnection();
            try {
              sendToServer("#login " + id);
            } catch(IOException e) {
              clientUI.display
                ("Could not send message to server.  Terminating client.");
              quit();
            }
          }
        } catch (IOException e) { 
          clientUI.display(e.toString());
          clientUI.display("Could not log in.");
        }
      } else if (strcmp(msg, "gethost", 1, 0) == 0) {
        clientUI.display(this.getHost());
      } else if (strcmp(msg, "getport", 1, 0) == 0) {
        clientUI.display(String.format("%d", this.getPort()));
      } else {
        clientUI.display("Unknown command!");
      }
    } else {
      try
      {
        sendToServer(msg);
      }
      catch(IOException e)
      {
        clientUI.display
          ("Could not send message to server.  Terminating client.");
        quit();
      }
    }
  }

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
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
}
//End of ChatClient class
