package socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import cornell.cloud.dropsomething.common.model.ServerDetails;
import cornell.cloud.dropsomething.common.util.Utilities;
import cornell.cloud.dropsomething.server.handler.ServerHandler;
import cornell.cloud.dropsomething.server.handler.ServerState;

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
			String packet = IConstants.NEW_SERVER + IConstants.DELIMITER
					+ InetAddress.getLocalHost().getHostAddress().toString()
					+ IConstants.DELIMITER + port;
			String response = MessageService.send(packet, ServerDetails.coOrdinator());
			ArrayList<String> responseList = Utilities.getMessage(response);
			int opcode = Integer.parseInt(responseList.get(0));
			if(opcode == IConstants.OK)
			{
			 //Start the server
			}
			else if(opcode == IConstants.CHAIN){
				updateServerState(responseList);
			}

			ServerSocket listenSocket = new 
					ServerSocket(Integer.valueOf(port));


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

	private static void updateServerState(ArrayList<String> responseList) {
		int chainId =Integer.valueOf(responseList.get(1));
		int itr = Integer.valueOf(responseList.get(2));
		ServerState.setChainId(chainId);
		ServerState.setItr(itr);
		ArrayList<ServerDetails> serverList = new ArrayList<ServerDetails>();
		for(int i=3 ; i <responseList.size()-1;i=i+2){
			String ip = responseList.get(i);
			int port = Integer.valueOf(responseList.get(i+1));
			serverList.add(ServerDetails.create(ip, port));
		}
		ServerState.setServerList(serverList);
		
	}
	private static void pullFilesFromChain(){
		ServerState.setState(ServerState.INIT);
		String
	}
}
