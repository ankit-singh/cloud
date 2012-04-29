package socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import cornell.cloud.dropsomething.server.handler.ServerHandler;

public class TCPServer {
	
	public static void main(String args[]) throws IOException {
		try {
			Scanner scan = new Scanner(System.in);
			System.out.print("Server to start on Port?: ");
			String port = scan.next();
			System.out
					.println(" Enter Server Dir : /users/ankitsingh/desktop/drop/");
			String dir = scan.next();
			dir = "/users/ankitsingh/desktop/drop/";
			Logger.Log("Contacting the Coordinator");
			Socket s = new Socket(IConstants.COORD_IP, IConstants.COOR_PORT);// USE
																				// TO
																				// AUTOMATE
			DataOutputStream requestStream = new DataOutputStream(s.getOutputStream());
			DataInputStream responseStream = new DataInputStream(s.getInputStream());
			String packet = IConstants.NEW_SERVER + IConstants.DELIMITER
					+ InetAddress.getLocalHost().getHostAddress().toString()
					+ IConstants.DELIMITER + port;
			requestStream.writeInt(packet.length());
			requestStream.writeBytes(packet);
			
			

			ServerSocket listenSocket = new ServerSocket(Integer.valueOf(port));


			while (true) {
				System.out.println("\n Now listening @ Port : "
						+ listenSocket.getLocalPort());
				Socket clientSocket = listenSocket.accept();
				new ServerHandler(clientSocket, dir);
			}
		} catch (IOException e) {
			System.out.println("Listen :" + e.getMessage());
		}
	}
}
