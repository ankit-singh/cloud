package socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;



public class RequestHandler extends Thread implements IConstants{

	DataInputStream reqStream; 
	DataOutputStream respStream; 
	Socket clientSocket; 
	String clientName;
	public RequestHandler (Socket aClientSocket) { 
		try { 
			clientSocket = aClientSocket; 
			reqStream = new DataInputStream( clientSocket.getInputStream()); 
			respStream = new DataOutputStream( clientSocket.getOutputStream()); 
			this.start(); 
		} 
		catch(IOException e) {
			System.out.println("Connection:"+e.getMessage());
		} 
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
					String respString = null;
					String request = new String(digit);
					int opcode = Integer.parseInt(request.split(IConstants.DELIMITER)[0]);
					Logger.Log("RequestHandler.run() Opcode :"+opcode);
					if(opcode == IConstants.UPLOAD){
						respString = handleUploadRequest(request);
					}else if(opcode == IConstants.CREATE_NEW_USER){
						respString = handleNewUserRequest(request);
					}else if(opcode == IConstants.NEW_SERVER){
						handleNewServer(request);
					}else if(opcode == IConstants.AUTHENTICATE){
						respString = handleAuthentication(request);
					}else if(opcode == IConstants.DOWNLOAD){
						respString = handleDownloadRequest(request);
					}
					if(respString != null){
						respStream.writeInt(respString.getBytes().length);
						respStream.write(respString.getBytes());
					}
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					if(clientSocket != null){
						try {
							clientSocket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

	}
	private String handleAuthentication(String request) {
		String response = null;
		String[] arr = request.split(IConstants.DELIMITER);
		try{
			String uname = arr[1];
			String pwd = arr[2];
			CoordinatorManager cm = CoordinatorManager.getInstance();
			if(cm.getPwd(uname) == null){
				System.out.println("RequestHandler.handleAuthentication() User Not found");
			}else{
				if(cm.getPwd(uname).equals(pwd)){
					
					response = String.valueOf(IConstants.USER_AUTHENTICATED);
				}else{
					response = String.valueOf(IConstants.INVALID_PWD);
				}
			}
		}catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			System.out.println("RequestHandler.handleAuthentication() Error Request"+request);
		}
				
		return response;
	}
	private String handleDownloadRequest(String request){
		String response = null;
		String[] arr = request.split(IConstants.DELIMITER);
		try{
			String clientName = arr[1];
			//get file list
			ArrayList<FileDetails> fileList = CoordinatorManager.getInstance().getClientFileTable().getFileList(clientName);
			//file doesnt exits then , no down load
			if(fileList == null){
				
			}
			else if(fileList.size() >0){	
				//for every file in fileList get the server address
				response = String.valueOf(FILELIST);
				for(FileDetails fd : fileList){
					try {
						response+=DELIMITER+fd.getClientName()+"_"+fd.getFileName()+DELIMITER+CoordinatorManager.getInstance().getFileServerTable().getServerDetails(fd).toString();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			
		}catch (ArrayIndexOutOfBoundsException e) {
			Logger.Log("RequestHandler.handleDownloadRequest() Something is wrong in the request :"+request);
		}
		return response;
	}
	private String handleUploadRequest(String request){
		String response = null;
		String[] arr = request.split(IConstants.DELIMITER);
		try{
			String clientName = arr[1];
			String fileName = arr[2];
			Long fileSize = Long.valueOf(arr[3]);
			ServerTable serverTable = CoordinatorManager.getInstance().getServerTable();
			ServerDetails potentialServer = serverTable.getPotentialServer(fileSize);
			if(potentialServer != null){
				//Get the potential server
				//Update the server size
				//store in file server table
				//TODO Version matching??
				response = String.valueOf(SERVER)+DELIMITER+potentialServer.toString();
				Long newSize = serverTable.getServerSpace(potentialServer) - fileSize;
				serverTable.addServer(potentialServer, newSize);
				FileServerTable fsTable = CoordinatorManager.getInstance().getFileServerTable();
				FileDetails fileDetails = new FileDetails();
				fileDetails.setClientName(clientName);
				fileDetails.setFileName(fileName);
				//FIXME get the version no
				fileDetails.setVersion(0);
				fsTable.addFileDetails(fileDetails, potentialServer);
				//Add the file to client file table
				CoordinatorManager.getInstance().getClientFileTable().addFile(clientName, fileDetails);
			}else{
				response = String.valueOf(NO_SPACE);
			}
			
		}catch (ArrayIndexOutOfBoundsException e) {
			Logger.Log("RequestHandler.handleUploadRequest() Something is wrong in the request :"+request);
		}
		return response;
	}
	private void handleNewServer(String request){
		String[] arr = request.split(IConstants.DELIMITER);
		try{
			String ip = arr[1];
			int port  = Integer.parseInt(arr[2]);
			Long size = Long.parseLong(arr[3]);
			//Using fixed server size for now
//			Long size = IConstants.SERVER_SIZE;
			ServerTable stable = CoordinatorManager.getInstance().getServerTable();
			stable.addServer(ServerDetails.creat(ip, port),size);
		}catch (ArrayIndexOutOfBoundsException e) {
			Logger.Log("RequestHandler.handleNewServer() Something is wrong in the request :"+request);
		}
	}
	public String handleNewUserRequest(String request){
		String response = null;
		String[] arr = request.split(IConstants.DELIMITER);
		try{
			String uname = arr[1];
			String pwd = arr[2];
			CoordinatorManager cm = CoordinatorManager.getInstance();
			if(cm.getPwd(uname) == null){
				cm.addUser(uname, pwd);
				response =String.valueOf(USER_CREATED);
			}else{
				response =String.valueOf(USER_EXISTS);
			}
		}catch (ArrayIndexOutOfBoundsException e) {
			Logger.Log("RequestHandler.handleNewClient() Something is wrong in the request :"+request);
		}
		return response;

	}

	public boolean isValidRequest(String request){
		if(request != null && !request.trim().equals("")){
			return true;
		}
		else{
			return false;
		}
	}
}