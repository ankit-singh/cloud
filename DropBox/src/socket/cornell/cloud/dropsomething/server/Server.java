package cornell.cloud.dropsomething.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import cornell.cloud.dropsomething.common.IConstants;
import cornell.cloud.dropsomething.common.model.ServerDetails;
import cornell.cloud.dropsomething.common.service.FileDownloader;
import cornell.cloud.dropsomething.common.service.MessageService;
import cornell.cloud.dropsomething.common.util.Logger;
import cornell.cloud.dropsomething.common.util.Utilities;
import cornell.cloud.dropsomething.server.handler.ServerHandler;
import cornell.cloud.dropsomething.server.handler.ServerState;

public class Server {
	private static String rootDir;

	public static void main(String args[]) throws IOException {
		try {
			Scanner scan = new Scanner(System.in);
			System.out.print("Server to start on Port?: ");
			String port = scan.next();
			System.out
					.println(" Enter Server Dir :");
			rootDir = scan.next();
			rootDir = Utilities.getFormattedDir(rootDir);
			Logger.Log("Contacting the Coordinator");
			String packet = IConstants.NEW_SERVER + IConstants.DELIMITER
					+ InetAddress.getLocalHost().getHostAddress().toString()
					+ IConstants.DELIMITER + port;
			String response = MessageService.send(packet,
					ServerDetails.coOrdinator());
			System.out.println("Server.main()"+ServerDetails.coOrdinator());
			if (Utilities.isValidMessage(response)) {
				System.out.println("Registration response  = " + response);
				ArrayList<String> responseList = Utilities.getMessage(response);
				int opcode = Integer.parseInt(responseList.get(0));
				ServerState.MYIP = ServerDetails
						.create(InetAddress.getLocalHost().getHostAddress(),
								Integer.valueOf(port));
				System.out.println("My IP :" + ServerState.MYIP);
				if (opcode == IConstants.OK) {
					updateServerState(responseList);
				} else if (opcode == IConstants.CHAIN) {
					updateServerState(responseList);
					pullFilesFromChain();
				}
				ServerSocket listenSocket = new ServerSocket(
						Integer.valueOf(port));
				while (true) {
					System.out.println("\n Now listening @ Port : "
							+ listenSocket.getLocalPort());
					Socket clientSocket = listenSocket.accept();
					new ServerHandler(clientSocket, rootDir);
				}
			}else{
				System.out.println("Server Start Failure , CO failed to respond");
			}
		} catch (IOException e) {
			System.out.println("Listen :" + e.getMessage());
		}
	}

	private static void updateServerState(ArrayList<String> responseList) {
		int chainId = Integer.valueOf(responseList.get(1));
		int itr = Integer.valueOf(responseList.get(2));
		ServerState.setChainId(chainId);
		ServerState.setItr(itr);
		ArrayList<ServerDetails> serverList = new ArrayList<ServerDetails>();
		for (int i = 3; i < responseList.size() - 1; i = i + 2) {
			String ip = responseList.get(i);
			int port = Integer.valueOf(responseList.get(i + 1));
			serverList.add(ServerDetails.create(ip, port));
		}
		ServerState.setServerList(serverList);

	}

	private static void pullFilesFromChain() {
		ServerState.setState(ServerState.INIT);
		ArrayList<ServerDetails> serverList = ServerState.getServerList();
		// Tell all the servers to stop accpeting all the request they have been
		// handling
		// add a while loop till the server repiles
		for (ServerDetails dest : serverList) {
			if (!ServerState.MYIP.equals(dest)) {
				MessageService.send(IConstants.INIT + IConstants.DELIMITER
						+ ServerState.getChainId() + IConstants.DELIMITER
						+ ServerState.getItr(), dest);
			}
		}
		for (ServerDetails dest : serverList) {
			if (!ServerState.MYIP.equals(dest)) {
				String fileListRequest = IConstants.NEW_SERVER
						+ IConstants.DELIMITER + ServerState.getChainId()
						+ IConstants.DELIMITER + ServerState.getItr();
				String message = MessageService.send(fileListRequest, dest);
				if (Utilities.isValidMessage(message)) {
					ArrayList<String> fileList = Utilities.getMessage(message);
					if (fileList.get(0).equals("" + IConstants.FILELIST)) {
						for (int i = 1; i < fileList.size(); i = i + 2) {

							String filePath = fileList.get(i);
							String md5 = fileList.get(i + 1);
							FileDownloader.pullFIle(rootDir, filePath, md5, ServerState.getChainId()+IConstants.DELIMITER+"root",
									dest);
						}
					}
				}

			}

		}
	}
}
