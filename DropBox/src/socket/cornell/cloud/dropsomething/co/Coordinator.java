package cornell.cloud.dropsomething.co;

/**
 * Co-ordinator keeps track of -
 * 1. All Primaries and their respective backups
 * 2. Assigns Primaries to Clients
 * 3. Load balancing
 */

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import socket.COHandler;
import socket.CoordinatorManager;
import socket.IConstants;
import socket.Logger;

public class Coordinator {

	  public static void main (String args[]) 
	  { 
		try{    
				Logger.Log("Coordinator.main() Coordinator Running");
				Scanner scan = new Scanner(System.in);
				System.out.println("Coordinator.main() Port");
				int port = scan.nextInt();
				ServerSocket listenSocket = new ServerSocket(port); 
				Logger.Log("Coordinator.main() Co-ordinator is up and running @ IP: " + listenSocket.getInetAddress() + " / Port:" +listenSocket.getLocalPort() +"    ........");
				CoordinatorManager.getInstance().setPort(port);
				Logger.Log("Coordinator.main() Coordinator IP"+InetAddress.getLocalHost().getHostAddress());
				while(true) { 
					System.out.println("Accepting new Servers\n");
					Socket newServerConn = listenSocket.accept(); 
					System.out.println("got request on Port:"+port);
					new  COHandler(newServerConn); 
				} 
		} 
		catch(IOException e) {
			System.out.println("Listen :"+e.getMessage());} 
	  }
	}

	




