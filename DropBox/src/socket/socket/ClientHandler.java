package socket;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;



public class ClientHandler implements IConstants{
	private ClientDetails client;

	private static final ServerDetails co = ServerDetails.coOrdinator();

	private ClientView view = new ClientView();
	
	public void createNewUser(){
		client = getClientDetails();
		String request = new String();
		request = CREATE_NEW_USER+DELIMITER+client.getUserName()+DELIMITER+client.getPassWord();
		String response = TCPConncetion.send(request, co);
		if(isValidResponse(response)){
			String[] arr = response.split(DELIMITER);
			int opcode = Integer.parseInt(arr[0]);
			if(opcode == USER_EXISTS){
				createNewUser();
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
		String response = TCPConncetion.send(request, co);
		if(isValidResponse(response)){
			String[] arr = response.split(DELIMITER);
			int opcode = Integer.parseInt(arr[0]);
			if(opcode == USER_AUTHENTICATED){
				download();
				upload();
			}
			else if(opcode == INVALID_PWD){
				Logger.Log("ClientHandler.authenticateUser() Reenter password");
				authenticateUser();
			}
		}
	}
	public boolean isValidResponse(String response){
		if(response != null && !response.trim().equals("")){
			return true;
		}
		else{
			return false;
		}
	}

	public void upload(){
		String dirPath = view.getDirectoryPath();
		Logger.Log("ClientHandler.upload() Directory Path :"+dirPath);
		try{
			File folder = new File(dirPath);
			File[] fileList = folder.listFiles();
			Logger.Log("ClientHandler.upload() No of Files"+fileList.length);

			for (int j = 0; j < fileList.length; j++) {
				//FIXME folder inside folder problem
				if (fileList[j].isFile()) {
					File file =  fileList[j];
					String fileName = file.getName();
					Logger.Log("ClientHandler.upload() File Name :"+fileName);
					Long fileSize = file.length();
					Logger.Log("ClientHandler.sendUploadRequest() File Size : "+fileSize);
					String request =UPLOAD+DELIMITER+client.getUserName()+DELIMITER+fileName+DELIMITER+fileSize;
					String response = TCPConncetion.send(request, co);
					if(isValidResponse(response)){
						String[] arr = response.split(DELIMITER);
						int opcode = Integer.parseInt(arr[0]);
						if(opcode == SERVER){
							ServerDetails dest = ServerDetails.creat(arr[1],Integer.parseInt(arr[2]));
							FileUploader.pushFile(file, dest,view.getClientName());
						}else if(opcode == NO_SPACE){
							Logger.Log("ClientHandler.upload() File could not be uploaded : "+file.getName());

						}

					}
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	public void download(){
		String request = DOWNLOAD+DELIMITER+client.getUserName();
		String response = TCPConncetion.send(request, co);
		if(isValidResponse(response)){
			String[] arr = response.split(DELIMITER);
			int opcode = Integer.parseInt(arr[0]);
			//retreive the file list
			if(opcode == FILELIST){
				Hashtable<String, ServerDetails> fileList = new Hashtable<String, ServerDetails>();
				for(int i=1;i< arr.length; i =i+3){
					System.out.println("ClientHandler.download() i:"+i+"  value: "+arr[i]);
					System.out.println("ClientHandler.download() i+1:"+(i+1)+"  value: "+arr[i+1]);
					System.out.println("ClientHandler.download() i+2:"+(i+2)+"  value: "+arr[i+2]);
					fileList.put(arr[i],ServerDetails.creat(arr[i+1], Integer.parseInt(arr[i+2])));
				}
				//pull the files
				Enumeration<String> files = fileList.keys();
				while(files.hasMoreElements()){
					String file = files.nextElement();
						FileDownloader.pullFile(file, fileList.get(file),view.getDirectoryPath());
				}
			}
		}
	}
}

