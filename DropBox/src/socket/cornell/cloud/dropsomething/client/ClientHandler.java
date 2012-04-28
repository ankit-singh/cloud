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
	private void updloadDir(String dirPath,ServerDetails dest){
		Logger.Log("ClientHandler.upload() Directory Path :"+dirPath);
		try{
			File folder = new File(dirPath);
			File[] fileList = folder.listFiles();
			Logger.Log("ClientHandler.upload() No of Files"+fileList.length);
			for (int j = 0; j < fileList.length; j++) {
				File file =  fileList[j];
				if (file.isFile() && !file.isHidden()) {
					String absPath  =	file.getAbsolutePath();
					String filePath = absPath.substring(dirPath.length()-1,absPath.length());
					//Now Let file uploader do the magic
					FileUploader.pushFile(rootDir, filePath, client.getUserName(), dest);
				}
				else if(file.isDirectory()){
					updloadDir(file.getAbsolutePath(), dest);
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
	public ServerDetails sendServerRequest(){
		String serverRequest = IConstants.SERVER+client.getUserName();
		String message = MessageService.send(serverRequest, ServerDetails.coOrdinator());
		if (Utilities.isValidMessage(message)) {
			ArrayList<String> responseFromCO = Utilities.getMessage(message);
			if (responseFromCO.get(0).equals(IConstants.SERVER)) {
				return ServerDetails.create(responseFromCO.get(1),
						Integer.parseInt(responseFromCO.get(0)));
			}
		}
		return null;
	}
	/**
	 * Uploads the file to server
	 */
	public void upload(){
		ServerDetails dest = sendServerRequest();
		if(dest == null){
			//TODO
		}
		else{
			updloadDir(rootDir, dest);
		}

	}
	/**
	 * Download the file from server
	 */
	public void download(){
		ServerDetails dest = sendServerRequest();
		if(dest == null){
			//TODO
		}
		else{
			String fileListRequest = IConstants.FILELIST+client.getUserName();
			String message = MessageService.send(fileListRequest, dest);
			if(Utilities.isValidMessage(message)){
				ArrayList<String> fileList = Utilities.getMessage(message);
				if(fileList.get(0).equals( IConstants.FILELIST)){
					for(int i=1 ; i <fileList.size() ; i=i+2){
						String filePath = fileList.get(i);
						String md5 = fileList.get(i+1);
						FileDownloader.pullFIle(rootDir, filePath, md5, client.getUserName(), dest);
					}
				}
			}


		}
	}


}

