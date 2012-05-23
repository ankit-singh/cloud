package cornell.cloud.dropsomething.co.handler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import cornell.cloud.dropsomething.co.CoordinatorManager;
import cornell.cloud.dropsomething.co.model.ClientServerTable;
import cornell.cloud.dropsomething.co.model.ServerBlockTable;
import cornell.cloud.dropsomething.co.model.ServerListTable;
import cornell.cloud.dropsomething.common.IConstants;
import cornell.cloud.dropsomething.common.model.ServerDetails;
import cornell.cloud.dropsomething.common.service.MessageService;
import cornell.cloud.dropsomething.common.util.Logger;
import cornell.cloud.dropsomething.common.util.Utilities;

public class COHandler extends Thread implements IConstants {

	DataInputStream reqStream;
	DataOutputStream respStream;
	Socket clientSocket;
	String clientName;
	CoordinatorManager manager;

	public COHandler(Socket aClientSocket) {
		try {
			clientSocket = aClientSocket;
			reqStream = new DataInputStream(clientSocket.getInputStream());
			respStream = new DataOutputStream(clientSocket.getOutputStream());
			manager = CoordinatorManager.getInstance();
			this.start();
		} catch (IOException e) {
			System.out.println("Connection:" + e.getMessage());
		}
	}

	@Override
	public void run() {
		// Close the sockets
		int size;
		try {
			size = reqStream.readInt();
			byte[] digit = new byte[size];
			for (int i = 0; i < size; i++) {
				digit[i] = reqStream.readByte();
			}
			String respString = null;
			String request = new String(digit);
			System.out.println("RequestHandler.run() Request" + request);
			int opcode = Integer
					.parseInt(request.split(IConstants.DELIMITER)[0]);
			Logger.Log("RequestHandler.run() Opcode :" + opcode);
			if (opcode == IConstants.UPLOAD) {
				respString = handleUploadRequest(request);
			} else if (opcode == IConstants.CREATE_NEW_USER) {
				respString = handleNewUserRequest(request);
			} else if (opcode == IConstants.NEW_SERVER) {
				respString = handleNewServer(request);
			} else if (opcode == IConstants.AUTHENTICATE) {
				respString = handleAuthentication(request);
			} else if (opcode == IConstants.DOWNLOAD) {
				respString = handleDownloadRequest(request);
			} else if (opcode == IConstants.STABLE) {
				handleStableRequest(request);
			}else if(opcode == IConstants.UPDATE){
				updateServerChain(Utilities.getMessage(request));
			}
			 else if (opcode == IConstants.PING) {
					String reply = String.valueOf(IConstants.OK);
					respStream.writeInt(reply.length());
					respStream.writeBytes(reply);
			 }
			if (respString == null) {
				respString = ""+IConstants.NO_ACTION;
			}
			System.out.println("RequestHandler.run() Response string :"
					+ respString);
			respStream.writeInt(respString.getBytes().length);
			respStream.write(respString.getBytes());
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			if (clientSocket != null) {
				try {
					clientSocket.close();
				} catch (IOException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * This method handles the STABLE messages from server after chaining or
	 * whenever it pleases them
	 * 
	 * @param request
	 */
	private void handleStableRequest(String request) {
		String[] arr = request.split(IConstants.DELIMITER);
		try {
			String chainId = arr[1];
			String serverList = arr[2];
			// manager.addChain(chainId, serverList);
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			System.out
					.println("RequestHandler.handleAuthentication() Error Request"
							+ request);
		}

	}

	private String handleAuthentication(String request) {
		String response = null;
		String[] arr = request.split(IConstants.DELIMITER);
		try {
			String uname = arr[1];
			String pwd = arr[2];
			CoordinatorManager cm = CoordinatorManager.getInstance();
			if (cm.getPwd(uname) == null) {
				System.out
						.println("RequestHandler.handleAuthentication() User Not found");
			} else {
				if (cm.getPwd(uname).equals(pwd)) {

					response = String.valueOf(IConstants.USER_AUTHENTICATED);
				} else {
					response = String.valueOf(IConstants.INVALID_PWD);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			System.out
					.println("RequestHandler.handleAuthentication() Error Request"
							+ request);
		}

		return response;
	}

	// According to the new implementation download all the files from the
	// server
	private String handleDownloadRequest(String request) {
		return handleUploadRequest(request);
	}

	private String handleUploadRequest(String request) {
		String response = null;
		String[] arr = request.split(IConstants.DELIMITER);
		try {
			String clientName = arr[1];
			ClientServerTable csTable = manager.getCsTable();
			String chainId = csTable.getServer(clientName);
			Logger.Log("Client Name :" + clientName);
			Logger.Log("Chain Id :" + chainId);
			if (chainId == null) {
				ServerBlockTable sbTable = manager.getSbTable();
				chainId = sbTable.getNextServer();
				
				if (chainId == null) {
					// NO Server Available //TODO
					System.out.println("No Servers Available");
				} else {
					int oldBlockSize = sbTable.getAvailableBlocks(chainId);
					sbTable.addServer(chainId, oldBlockSize - 1);
				}
			}
			if (chainId != null) {
				csTable.addClient(clientName, chainId);
				response = IConstants.SERVER + IConstants.DELIMITER+chainId+IConstants.DELIMITER
						+ manager.getSlTable().getServerList(chainId);
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			Logger.Log("RequestHandler.handleUploadRequest() Something is wrong in the request :"
					+ request);
		}
		return response;
	}

	private static int id = CoordinatorManager.getInstance().getPOrt();

	private String generateNewChainId() {
		return String.valueOf(id++);
	}
	private  void updateServerChain(ArrayList<String> responseList) {
		int chainId = Integer.valueOf(responseList.get(1));
		int itr = Integer.valueOf(responseList.get(2));
		String serverList = new String();
		for (int i = 3; i < responseList.size() - 1; i = i + 2) {
			String ip = responseList.get(i);
			int port = Integer.valueOf(responseList.get(i + 1));
			if(i == 3){
				serverList +=ip+IConstants.DELIMITER+port;
			}
			else{
				serverList +=IConstants.DELIMITER+ip+IConstants.DELIMITER+port;
			}
		}
		System.out.println("COHandler.updateServerChain() Group Id :"+chainId);
		System.out.println("COHandler.updateServerChain() Server List :"+serverList);
		manager.getSlTable().addServerList(String.valueOf(chainId), serverList);
		

	}
	/**
	 * This method will handle the registration of new servers The server will
	 * still not be added to the pool till they send a stable message
	 * 
	 * @param request
	 */
	private String handleNewServer(String request) {
		String[] arr = request.split(IConstants.DELIMITER);
		String response = null;
		try {
			String ip = arr[1];
			int port = Integer.parseInt(arr[2]);
			// get a potenial chain from co , if null then create a new chain
			// and store it back in
			ServerListTable slTable = manager.getSlTable();
			int itr = 0;
			String chainId = slTable.newServerChain();
			if (chainId == null) {
				chainId = generateNewChainId();
				ServerBlockTable sbTable = manager.getSbTable();
				slTable.addServerList(chainId, ip + IConstants.DELIMITER + port);
				sbTable.addServer(chainId, IConstants.SERVER_SIZE);
				// Create new chain add store it and just send ok to the server
				response = IConstants.OK + IConstants.DELIMITER + chainId
						+ IConstants.DELIMITER + itr + IConstants.DELIMITER
						+ slTable.getServerList(chainId);
			} else {
				String serverList = slTable.getServerList(chainId);
				serverList += IConstants.DELIMITER + ip + IConstants.DELIMITER
						+ port;
				slTable.addServerList(chainId, serverList);
				// Tell all the servers about the new chain
				ArrayList<String> slArr = Utilities.getMessage(serverList);
				System.out.println("RequestHandler.handleNewServer() Servlist :"+serverList);
				System.out.println("RequestHandler.handleNewServer() ServerList Size :"+serverList.length());
				for (int i = 0; i < slArr.size()-1; i = i + 2) {
					System.out.println("RequestHandler.handleNewServer() i :"+i);
					String newip = slArr.get(i);
					System.out.println("RequestHandler.handleNewServer() new IP :"+newip);
					int newport = Integer.valueOf(slArr.get(i+1));
					System.out.println("RequestHandler.handleNewServer() Port :"+newport);
					if (!ServerDetails.create(newip, newport).equals(
							ServerDetails.create(ip, port))) {
						String updateRequest = IConstants.UPDATE
								+ IConstants.DELIMITER + chainId
								+ IConstants.DELIMITER + itr
								+ IConstants.DELIMITER
								+ serverList;
						System.out.println("COHandler.handleNewServer() Sending List Update Message"+slTable.getServerList(chainId));
						MessageService.send(updateRequest, ServerDetails.create(newip, newport));
					}
				}
				response = IConstants.CHAIN + IConstants.DELIMITER + chainId
						+ IConstants.DELIMITER + itr + IConstants.DELIMITER
						+ serverList;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			Logger.Log("RequestHandler.handleNewServer() Something is wrong in the request :"
					+ request);
			e.printStackTrace();
			System.out.println("RequestHandler.handleNewServer() AIOFB"+e.getMessage());
		}
		return response;
	}

	public String handleNewUserRequest(String request) {
		String response = null;
		String[] arr = request.split(IConstants.DELIMITER);
		try {
			String uname = arr[1];
			String pwd = arr[2];
			CoordinatorManager cm = CoordinatorManager.getInstance();
			if (cm.getPwd(uname) == null) {
				cm.addUser(uname, pwd);
				response = String.valueOf(USER_CREATED);
			} else {
				response = String.valueOf(USER_EXISTS);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			Logger.Log("RequestHandler.handleNewClient() Something is wrong in the request :"
					+ request);
		}
		return response;

	}

	public boolean isValidRequest(String request) {
		if (request != null && !request.trim().equals("")) {
			return true;
		} else {
			return false;
		}
	}
}