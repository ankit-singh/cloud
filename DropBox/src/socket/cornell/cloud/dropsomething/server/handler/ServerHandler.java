package cornell.cloud.dropsomething.server.handler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;

import cornell.cloud.dropsomething.common.IConstants;
import cornell.cloud.dropsomething.common.model.ServerDetails;
import cornell.cloud.dropsomething.common.service.FileDownloader;
import cornell.cloud.dropsomething.common.service.FileUploader;
import cornell.cloud.dropsomething.common.service.MessageService;
import cornell.cloud.dropsomething.common.util.Jarvis;
import cornell.cloud.dropsomething.common.util.Logger;
import cornell.cloud.dropsomething.common.util.MD5Checksum;
import cornell.cloud.dropsomething.common.util.Utilities;

public class ServerHandler extends Thread { 
	DataInputStream reqStream; 
	DataOutputStream respStream; 
	Socket clientSocket; 
	final static int BUFFER_SIZE = 65536;
	String rootDir = "/users/ankitsingh/desktop/drop/sdef/";

	public ServerHandler (Socket aClientSocket,String dir) { 
		try { 
			this.rootDir = dir;
			clientSocket = aClientSocket; 
			reqStream = new DataInputStream( clientSocket.getInputStream()); 
			respStream = new DataOutputStream( clientSocket.getOutputStream()); 
			this.start(); 
		} 
		catch(IOException e) {
			System.out.println("Connection:"+e.getMessage());
		} 
	} 

	public void reciveFile(String request){
		System.out.println("ServerHandler.reciveFile() Starting the recive");
		ArrayList<String> requestList = Utilities.getMessage(request);
		String filePath = "/"+requestList.get(2)+requestList.get(3);
		FileDownloader.recieveFile(rootDir, filePath, requestList.get(4), requestList.get(2), clientSocket);
		replicate(request);
	}
	public void reciveReplicaFile(String request){
		System.out.println("ServerHandler.reciveReplicaFile() Starting the recive:"+request);
		ArrayList<String> requestList = Utilities.getMessage(request);
		String filePath = "/"+requestList.get(3);
		FileDownloader.recieveFile(rootDir, filePath, requestList.get(3), requestList.get(4), clientSocket);
	}
	public void replicate(String request){
		System.out.println("ServerHandler.replicate() Starting the replication");
		System.out.println("ServerHandler.replicate() ServerList :"+ServerState.getServerList());
		ArrayList<ServerDetails> newList =new ArrayList<ServerDetails>();
		newList.addAll(ServerState.getServerList());
		for(ServerDetails dest : ServerState.getServerList()){
			if (!dest.equals(ServerState.MYIP)) {
				ArrayList<String> requestList = Utilities.getMessage(request);
				String filePath = "/" + requestList.get(2) + requestList.get(3);
				try {
					//HardCode the chain id to client name
					FileUploader.replicate(rootDir, filePath, ServerState.getChainId()+IConstants.DELIMITER+requestList.get(2),
							dest);
				} catch (ConnectException e) {
					newList.remove(dest);
					e.printStackTrace();
				}
			}
		}
		String updateServerlistToCO = IConstants.UPDATE+IConstants.DELIMITER
				+ServerState.getChainId()+IConstants.DELIMITER+
				ServerState.getItr()+
				getServerListAsString(newList);
		//Handle if Co has failed// or let the random co generator handle this case
		MessageService.send(updateServerlistToCO, ServerDetails.coOrdinator());
		
		
	}
	private String getServerListAsString(ArrayList<ServerDetails> list){
		String listAsString = new String();
		for(ServerDetails sd : list){
			listAsString +=IConstants.DELIMITER+sd.getIp()+IConstants.DELIMITER;
			listAsString+=sd.getPort();
		}
		return listAsString;
	}
	public String getMD5(String filePath){
		System.out.println("File Path :"+filePath);
		try {
			return MD5Checksum.getMD5Checksum(filePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}
	public void sendFile(String request){
		System.out.println("ServerHandler.sendFile()");
		ArrayList<String> requestList = Utilities.getMessage(request);
		String clientName = requestList.get(2);
		if(clientName.equals("root")){
			clientName = "";
		}
		String filePath = "/"+clientName+requestList.get(3).trim();
		FileUploader.sendFile(rootDir, filePath, requestList.get(1), clientSocket);

	}
	private String createFileList(String fileListString,String folderName,String clientName){
		System.out.println("ServerHandler.createFileList() fileListString :"+fileListString);
		File folder = new File(folderName);
		if (folder != null && folder.listFiles() != null) {
			Logger.Log("Client Foler :" + folder);
			File[] fileList = folder.listFiles();
			for (int j = 0; j < fileList.length; j++) {
				System.out.println("ServerHandler.createFileList()File Name :"
						+ fileList[j].getAbsolutePath());
				if (fileList[j].isFile() && !fileList[j].isHidden()) {
					File file = fileList[j];
					//Send only the name as this the absolute path for the client
					String md5 = getMD5(file.getAbsolutePath());
					String filePath = file.getName();
					String userDir;
					if (clientName == null) {
						userDir = rootDir;
					} else {
						userDir = rootDir + "/" + clientName;

					}
					if (folder.equals(userDir)) {
						fileListString += IConstants.DELIMITER + filePath
								+ IConstants.DELIMITER + md5;
						System.out
								.println("ServerHandler.createFileList() fileListString :"
										+ fileListString);
					} else {
						String absPath = file.getAbsolutePath();
						filePath = absPath.substring(userDir.length(),
								absPath.length());
						fileListString += IConstants.DELIMITER + filePath
								+ IConstants.DELIMITER + md5;
						System.out
								.println("ServerHandler.createFileList() fileListString :"
										+ fileListString);

					}
				} else if (fileList[j].isDirectory()) {
					fileListString += createFileList(fileListString,
							fileList[j].getAbsolutePath(), clientName);
					System.out
							.println("ServerHandler.createFileList() fileListString :"
									+ fileListString);
				}

			}
		}
		return fileListString;
	}
	public void sendFileList(String request){
		System.out.println("ConnectionHandler.sendFileList()"+request);
		try {
			ArrayList<String> response = Utilities.getMessage(request);
			String clientName = response.get(2).trim();
			Logger.Log("Opening Client Folder :"+rootDir+"/"+clientName);
			
			String fileListString = new String();
			//sending client name as white space
			fileListString = IConstants.FILELIST+IConstants.DELIMITER+ServerState.getChainId()+IConstants.DELIMITER+" "+createFileList(fileListString, rootDir+"/"+clientName,clientName);
			respStream.writeInt(fileListString.getBytes().length);
			respStream.writeBytes(fileListString);
			respStream.close();
			reqStream.close();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("ConnectionHandler.sendFile() Something went wrong :"+request);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	private void handleNewServerRequest(String request){
		try {
			ArrayList<String> response = Utilities.getMessage(request);
			String clientName = "root";
			Logger.Log("Opening Client Folder :"+rootDir);

			String fileListString = new String();
			fileListString = IConstants.FILELIST+createFileList(fileListString, rootDir,null);
			respStream.writeInt(fileListString.getBytes().length);
			respStream.writeBytes(fileListString);
			respStream.close();
			reqStream.close();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("ConnectionHandler.sendFile() Something went wrong :"+request);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	private static void updateServerState(ArrayList<String> responseList) {
		int chainId = Integer.valueOf(responseList.get(1));
		int itr = Integer.valueOf(responseList.get(2));
		System.out.println("ServerHandler.updateServerState() Old Chain Id :"+ServerState.getChainId());
		System.out.println("ServerHandler.updateServerState() Updating the Chaind to :"+chainId);
		ServerState.setChainId(chainId);
		ServerState.setItr(itr);
		ArrayList<ServerDetails> serverList = new ArrayList<ServerDetails>();
		for (int i = 3; i < responseList.size() - 1; i = i + 2) {
			String ip = responseList.get(i);
			int port = Integer.valueOf(responseList.get(i + 1));
			serverList.add(ServerDetails.create(ip, port));
		}
		System.out.println("ServerHandler.updateServerState() Old Server List "+ServerState.getServerList());
		ServerState.setServerList(serverList);
		System.out.println("ServerHandler.updateServerState() New Server List "+ServerState.getServerList());

	}
	@Override
	public void run() {
		//Close the sockets
		int size;
		try {
			size = reqStream.readInt();
			byte[] digit = new byte[size];
			for(int i = 0; i < size; i++){
				digit[i] = reqStream.readByte();
			}
			String request = new String(digit);
			System.out.println("ServerHandler.run() REQUEST AT SERVER :"+request);
			Jarvis jarvis = new Jarvis(request);
			int opcode = jarvis.getOPCode();
			
			if (jarvis.getChainID() == ServerState.getChainId()) {
				if (opcode == IConstants.PUSH) {
					reciveFile(request);
				} else if (opcode == IConstants.PULL) {
					sendFile(request);
				} else if (opcode == IConstants.FILELIST) {
					sendFileList(request);
				} else if (opcode == IConstants.PING) {
					String reply = String.valueOf(IConstants.OK);
					respStream.writeInt(reply.length());
					respStream.writeBytes(reply);
				} else if (opcode == IConstants.NEW_SERVER) {
					handleNewServerRequest(request);
				} else if (opcode == IConstants.INIT) {
					//
					String chainId = request.split(IConstants.DELIMITER)[1];
					int itr = Integer.valueOf(request
							.split(IConstants.DELIMITER)[2]);
					if (chainId.equals(ServerState.getChainId())
							&& itr == ServerState.getItr()) {
						//Set init only if its in read state
						ServerState.setState(ServerState.INIT);
					}
				} else if (opcode == IConstants.UPDATE) {
					updateServerState(Utilities.getMessage(request));
					String reply = String.valueOf(IConstants.OK);
					respStream.writeInt(reply.length());
					respStream.writeBytes(reply);
				} else if (opcode == IConstants.REPLICATE) {
					reciveReplicaFile(request);
				}
			}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally{
				if(clientSocket != null){
					try {

						clientSocket.close();

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
