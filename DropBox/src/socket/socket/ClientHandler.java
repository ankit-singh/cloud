package socket;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;



public class ClientHandler implements IConstants{
	private ClientDetails client;

	private static final ServerDetails co = ServerDetails.coOrdinator();
	
	private static FileVersionTable versionTable = new FileVersionTable();

	private ClientView view;
	public ClientHandler(){
		view = new ClientView(this);
		view.open();
	}
	
	public void createNewUser(){
		client = getClientDetails();
		String request = new String();
		request = CREATE_NEW_USER+DELIMITER+client.getUserName()+DELIMITER+client.getPassWord();
		String response = TCPConncetion.send(request, co);
		if(isValidResponse(response)){
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
//				authenticateUser();
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
	private int getVersionNumber(String fileName){
		return -1;
	}
	private void updloadDir(String dirPath){
		Logger.Log("ClientHandler.upload() Directory Path :"+dirPath);
		try{
			File folder = new File(dirPath);
			File[] fileList = folder.listFiles();
			Logger.Log("ClientHandler.upload() No of Files"+fileList.length);

			for (int j = 0; j < fileList.length; j++) {
				//FIXME folder inside folder problem
				if (fileList[j].isFile() && !fileList[j].isHidden()) {
					
					File file =  fileList[j];
					String fileName = file.getAbsolutePath();
					
					
					int versionNumber = getVersionNumber(fileName);
					Logger.Log("ClientHandler.upload() File Name :"+fileName);
					Logger.Log("ClientHandler.updloadDir() Absoulte Path:"+file.getAbsolutePath());
					Long fileSize = file.length();
					Logger.Log("ClientHandler.sendUploadRequest() File Size : "+fileSize);
					String request =UPLOAD+DELIMITER+client.getUserName()+DELIMITER+fileName+DELIMITER+fileSize+DELIMITER+versionNumber;
					String response = TCPConncetion.send(request, co);
					if(isValidResponse(response)){
						String[] arr = response.split(DELIMITER);
						int opcode = Integer.parseInt(arr[0]);
						if(opcode == SERVER){
							ServerDetails dest = ServerDetails.create(arr[1],Integer.parseInt(arr[2]));
							if(FileUploader.pushFile(file, dest,view.getClientName())){
								
								//if file is successfully uploaded check if it exists in version table
								// TODO if so then increment the version number
								// 
								
							}
							
						}else if(opcode == NO_SPACE){
							Logger.Log("ClientHandler.upload() File could not be uploaded : "+file.getName());

						}else if(opcode == NO_ACTION){
							Logger.Log("File Exist Version :"+versionNumber);
						}else if(opcode == CONFLICT){
							
						}

					}
				}else if(fileList[j].isDirectory()){
					updloadDir(fileList[j].getAbsolutePath());
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	public void upload(){
		String dirPath = view.getDirectoryPath();
		updloadDir(dirPath);
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
				for(int i=1;i< arr.length; i =i+4){
					System.out.println("ClientHandler.download() i:"+i+"  value: "+arr[i]);
					System.out.println("ClientHandler.download() i+1:"+(i+1)+"  value: "+arr[i+1]);
					System.out.println("ClientHandler.download() i+2:"+(i+2)+"  value: "+arr[i+2]);
					System.out.println("ClientHandler.download() i+2:"+(i+3)+"  value: "+arr[i+2]);
					fileList.put(arr[i]+"__"+arr[i+1],ServerDetails.create(arr[i+2], Integer.parseInt(arr[i+3])));
				}
				//pull the files
				Enumeration<String> files = fileList.keys();
				while(files.hasMoreElements()){
					String fileData = files.nextElement();
					String fileName = fileData.split(DELIMITER)[0];
					int newVesrion = Integer.parseInt(fileData.split(DELIMITER)[1]);
					int currentVersion = getVersionNumber(fileName);
					if(currentVersion <= newVesrion){
						FileDownloader.pullFile(fileName, fileList.get(fileName),view.getDirectoryPath());
					}
				}
			}
		}
	}
	public static void main(String[] args){
		new ClientHandler();
		
		
		
	}
	public void handleDirectoryUpdate(int eventType , String fileName){
		
		
	}
}

