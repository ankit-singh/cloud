package socket;

/**
 * Co-ordinator keeps track of -
 * 1. All Primaries and their respective backups
 * 2. Assigns Primaries to Clients
 * 3. Load balancing
 */

import java.net.*; 
import java.io.*; 

public class Coordinator {


	  public static void main (String args[]) 
	  { 
		try{    
				Logger.Log("Coordinator.main() Coordinator Running");
				ServerSocket listenSocket = new ServerSocket(IConstants.COOR_PORT); 
				Logger.Log("Coordinator.main() Co-ordinator is up and running @ IP: " + listenSocket.getInetAddress() + " / Port:" +listenSocket.getLocalPort() +"    ........");
		  
				Logger.Log("Coordinator.main() Coordinator IP"+InetAddress.getLocalHost().getHostAddress());
				while(true) { 
					System.out.println("Accepting new Servers\n");
					Socket newServerConn = listenSocket.accept(); 
					System.out.println("Accepting new Servers\n");
					new RequestHandler(newServerConn); 
				} 
		} 
		catch(IOException e) {
			System.out.println("Listen :"+e.getMessage());} 
	  }
	}

	




