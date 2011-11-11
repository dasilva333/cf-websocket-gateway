package org.riaforge.websocketgateway;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.tootallnate.websocket.WebSocket;

import org.webbitserver.WebServer;
import org.webbitserver.WebServers;
import org.webbitserver.WebSocketConnection;
import org.webbitserver.WebSocketHandler;
import org.webbitserver.handler.StaticFileHandler;

public class CustomWebSocketServer implements Runnable, WebSocketHandler {

  /**
   * The port number that this WebSocket server should listen on. Default is 80
   * (HTTP).
   */
  private int port;
  
  int connectionCount;
  
  /**
   * Holds the list of active WebSocket connections. "Active" means WebSocket
   * handshake is complete and socket can be written to, or read from.
   */
  private CopyOnWriteArraySet<WebSocketConnection> connections;
  
  /**
   * The socket channel for this WebSocket server.
   */
  private WebServer server;
  
  public void onOpen(WebSocketConnection connection) {
    connection.send("Hello! There are " + connectionCount + " other connections active");
    connectionCount++;
  }
  
  public void onClose(WebSocketConnection connection) {
    connectionCount--;
  }
  
  public void onMessage(WebSocketConnection connection, String message) {
    connection.send(message.toUpperCase()); // echo back message in upper case
  }

  public void onMessage(WebSocketConnection connection, byte[] message) {
  }

  public void onPong(WebSocketConnection connection, String message) {
  }

  /**
   * Closes all connected clients sockets, then closes the underlying
   * ServerSocketChannel, effectively killing the server socket thread and
   * freeing the port the server was bound to.
   * 
   * @throws IOException
   *           When socket related I/O errors occur.
   */
  public void stop() throws IOException {
    for (WebSocketConnection ws : connections) {
      ws.close();
    }
    ((AbstractInterruptibleChannel) this.server).close();
  }
  
  /**
   * Sets the port that this WebSocketServer should listen on.
   * 
   * @param port
   *          The port number to listen on.
   */
  public void setPort(int port) {
    this.port = port;
  }

  /**
   * Gets the port number that this server listens on.
   * 
   * @return The port number.
   */
	public int getPort() {
	    return port;
	}  
  
	
	  /**
	   * Sends <var>text</var> to connected WebSocket client specified by
	   * <var>connection</var>.
	   * 
	   * @param connection
	   *          The {@link WebSocket} connection to send to.
	   * @param text
	   *          The String to send to <var>connection</var>.
	   * @throws IOException
	   *           When socket related I/O errors occur.
	   */
	  public void sendTo(WebSocketConnection connection, String text) throws IOException {
	    if (connection == null) {
	      throw new NullPointerException("'connection' cannot be null");
	    }
	    connection.send(text);
	  }

	  /**
	   * Sends <var>text</var> to all currently connected WebSocket clients found in
	   * the Set <var>connections</var>.
	   * 
	   * @param connections
	   * @param text
	   * @throws IOException
	   *           When socket related I/O errors occur.
	   */
	  public void sendTo(Set<WebSocketConnection> connections, String text)
	      throws IOException {
	    if (connections == null) {
	      throw new NullPointerException("'connections' cannot be null");
	    }

	    for (WebSocketConnection c : this.connections) {
	      if (connections.contains(c)) {
	        c.send(text);
	      }
	    }
	  }
  /**
   * Sends <var>text</var> to all currently connected WebSocket clients.
   * 
   * @param text
   *          The String to send across the network.
   * @throws IOException
   *           When socket related I/O errors occur.
   */
  public void sendToAll(String text) throws IOException {
    for (WebSocketConnection c : this.connections) {
      c.send(text);
    }
  } 

  /**
   * Starts the server thread that binds to the currently set port number and
   * listeners for WebSocket connection requests.
   */
  public void start() {
	  //THIS IS WHERE I GET CONFUSED
	  try {
			server = WebServers.createWebServer(this.port)
			  .add("/", new CustomWebSocketServer())
			  .start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  (new Thread(this)).start();
  }
	  
	@Override
	public void run() {
		  //THIS IS WHERE I GET CONFUSED
		try {
			server = WebServers.createWebServer(this.port)
			  .add("/", new CustomWebSocketServer())
			  .start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
    