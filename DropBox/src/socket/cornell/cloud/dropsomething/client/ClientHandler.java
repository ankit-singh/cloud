package cornell.cloud.dropsomething.client;

import java.io.File;
import java.util.ArrayList;

import socket.ClientDetails;
import socket.FileDownloader;
import socket.FileUploader;
import socket.FileVersionTable;
import socket.IConstants;
import socket.Logger;
import socket.MessageService;
import cornell.cloud.dropsomething.common.model.ServerDetails;
import cornell.cloud.dropsomething.common.util.Utilities;



public class ClientHandler implements IConstants{
	private ClientDetails client;

	private static final ServerDetails co = ServerDetails.coOrdinator();

	private static FileVersionTable versionTable = new FileVersionTable();

	private ClientView view;

	String rootDir = "";
	public ClientHandler(){
		view = new ClientView(this);
		view.open();
	}

	public void createNewUser(){
		client = getClientDetails();
		String request = new String();
		request = CREATE_NEW_USER+DELIMITER+client.getUserName()+DELIMITER+client.getPassWord();
		String response = MessageService.send(request, co);
		if(Utilities.isValidMessage(response)){
			String[] arr = response.split(DELIMITER);
			int opcode = Integer.parseInt(arr[0]);
			if(opcode == USER_EXISTS){
				System.out.println("ClientHandler.createNewUser()");
			}
			else if(opcode == USER_CREATED){
				upload();
			}
		}
	}
	public ClientDetails getClientDetails(){
		String userName = view.getClientName();
		String pwd = view.getPassword();
		ClientDetails client = new ClientDetails();
		client.setUserName(userName);
		client.setPassWord(pwd);
		rootDir = view.getDirectoryPath();
		return client;
	}
	public void authenticateUser(){
		client = getClientDetails();
		String request = new String();
		request = AUTHENTICATE+DELIMITER+client.getUserName()+DELIMITER+client.getPassWord();
		String response = MessageService.send(request, co);
		if(Utilities.isValidMessage(response)){
			String[] arr = response.split(DELIMITER);
			int opcode = Integer.parseInt(arr[0]);
			if(opcode == USER_AUTHENTICATED){
				download();
				upload();
			}
			else if(opcode == INVALID_PWD){
				Logger.Log("ClientHandler.authenticateUser() Reenter password");
				//				authenticateUser();
			}
		}
	}
	private void uploadDir(String dirPath,ServerDetails dest){
		Logger.Log("Starting upload for DIR:"+dirPath);
		File folder = new File(dirPath);
		File[] fileList = folder.listFiles();
		for (int j = 0; j < fileList.length; j++) {
			File file = fileList[j];
			if (file.isFile() && !file.isHidden()) {
				String absPath = file.getAbsolutePath();
				String filePath = absPath.substring(
						rootDir.length(), absPath.length());
				//Now Let file uploader do the magic
				FileUploader.pushFile(rootDir, filePath,
						client.getUserName(), dest);
			} else if (file.isDirectory()) {
				uploadDir(file.getAbsolutePath(), dest);
			}
		}
	}
	private void uploadDir(String dirPath,ArrayList<ServerDetails> destList){
		try{
			boolean isServerAlive = false;
			for(int i= 0; i<destList.size();i++){
				if(!isServerAlive){
					ServerDetails dest = destList.get(i);
					System.out.println("ClientHandler.uploadDir()");
					String pingServer = MessageService.send(""+IConstants.PING, dest);
					Logger.Log("Server :"+dest+" ALIVE :"+pingServer.equals(""+IConstants.OK));
					if (pingServer.equals(""+IConstants.OK)) {
						isServerAlive = true;
						uploadDir(dirPath, dest);
					}
				}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}
	/** gets the server details from the coordinator
	 * @return<ServerDetails>
	 */
	public ArrayList<ServerDetails> sendServerRequest(){
		//FIXME
		String serverRequest = IConstants.DOWNLOAD+IConstants.DELIMITER+client.getUserName();
		String message = MessageService.send(serverRequest, ServerDetails.coOrdinator());
		ArrayList<ServerDetails> serverList = new ArrayList<ServerDetails>();
		if (Utilities.isValidMessage(message)) {
			ArrayList<String> responseFromCO = Utilities.getMessage(message);
			Logger.Log("Server Details Reponse :"+message);
			Logger.Log("Server Details Reponse OPCODE:"+responseFromCO.get(0).equals(""+IConstants.SERVER));
			if (responseFromCO.get(0).equals(""+IConstants.SERVER)) {
				for(int i=1;i<responseFromCO.size() -1;i=i+2){
					System.out.println("ClientHandler.sendServerRequest() ip :+"+responseFromCO.get(i));
					System.out.println("ClientHandler.sendServerRequest() port :+"+responseFromCO.get(i+1));
					serverList.add(ServerDetails.create(responseFromCO.get(i), Integer.parseInt(responseFromCO.get(i+1))));
				}
			}
		}
		return serverList;
	}
	/**
	 * Uploads the file to server
	 */
	public void upload(){
		ArrayList<ServerDetails> serverList = sendServerRequest();
		System.out.println("ClientHandler.upload() ServerList :"+serverList);
		if(serverList == null){
			//TODO
		}
		else{
			uploadDir(rootDir, serverList);
		}

	}
	/**
	 * Download the file from server
	 */
	public void download(){
		ArrayList<ServerDetails> serverList = sendServerRequest();
		if(serverList == null){
			//TODO
		}
		else{
			downloadDir(serverList);

		}
	}
	private void downloadDir(ServerDetails dest){
		String fileListRequest = IConstants.FILELIST+IConstants.DELIMITER+client.getUserName();
		String message = MessageService.send(fileListRequest, dest);
		if(Utilities.isValidMessage(message)){
			ArrayList<String> fileList = Utilities.getMessage(message);
			if(fileList.get(0).equals(""+IConstants.FILELIST)){
				for(int i=1 ; i <fileList.size() ; i=i+2){
					
					String filePath = fileList.get(i);
					String md5 = fileList.get(i+1);
					FileDownloader.pullFIle(rootDir, filePath, md5, client.getUserName(), dest);
				}
			}
		}

	}
	private void downloadDir(ArrayList<ServerDetails> destList){
		try{
			boolean isServerAlive = false;
			for(int i= 0; i<destList.size();i++){
				if(!isServerAlive){
					ServerDetails dest = destList.get(i);
					String pingServer = MessageService.send(""+IConstants.PING, dest);
					if (pingServer.equals(""+IConstants.OK)) {
						isServerAlive = true;
						downloadDir(dest);
					}
				}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}
	public static void main(String[] args){
		new ClientHandler();
	}

}

