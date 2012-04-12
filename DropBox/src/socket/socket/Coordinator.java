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
				int ControllerPort = 5554; 
				System.out.println("HEY");
				ServerSocket listenSocket = new ServerSocket(ControllerPort); 
				System.out.println("Co-ordinator is up and running @ IP: " + listenSocket.getInetAddress() + " / Port:" +listenSocket.getLocalPort() +"    ........");
		  
				System.out.println("Coordinator IP"+InetAddress.getLocalHost().getHostAddress());
				while(true) { 
					System.out.println("Accepting new Servers\n");
					Socket newServerConn = listenSocket.accept(); 
					System.out.println("Accepting new Servers\n");
					RequestHandler req = new RequestHandler(newServerConn); 
				} 
		} 
		catch(IOException e) {
			System.out.println("Listen :"+e.getMessage());} 
	  }
	}

	




