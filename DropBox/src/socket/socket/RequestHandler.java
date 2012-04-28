package socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.Icon;

import cornell.cloud.dropsomething.co.model.ServerBlockTable;
import cornell.cloud.dropsomething.co.model.ServerListTable;



public class RequestHandler extends Thread implements IConstants{

	DataInputStream reqStream; 
	DataOutputStream respStream; 
	Socket clientSocket; 
	String clientName;
	CoordinatorManager manager;
	public RequestHandler (Socket aClientSocket) { 
		try { 
			clientSocket = aClientSocket; 
			reqStream = new DataInputStream( clientSocket.getInputStream()); 
			respStream = new DataOutputStream( clientSocket.getOutputStream());
			manager = CoordinatorManager.getInstance();
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
						respString = handleNewServer(request);
					}else if(opcode == IConstants.AUTHENTICATE){
						respString = handleAuthentication(request);
					}else if(opcode == IConstants.DOWNLOAD){
						respString = handleDownloadRequest(request);
					}else if(opcode == IConstants.STABLE){
						handleStableRequest(request);
					}
					if(respString != null){
						respStream.writeInt(respString.getBytes().length);
						respStream.write(respString.getBytes());
					}
				}
				catch (IOException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}finally{
					if(clientSocket != null){
						try {
							clientSocket.close();
						} catch (IOException e) {
							System.out.println(e.getMessage());
							e.printStackTrace();
						}
					}
				}

	}
	
	/**This method handles the STABLE messages from server after chaining or whenever it pleases them
	 * @param request
	 */
	private void handleStableRequest(String request){
		String[] arr = request.split(IConstants.DELIMITER);
		try{
			String chainId = arr[1];
			String serverList = arr[2];
//			manager.addChain(chainId, serverList);
		}catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			System.out.println("RequestHandler.handleAuthentication() Error Request"+request);
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
	//According to the new implementation download all the files from the server
	private String handleDownloadRequest(String request){
		return handleUploadRequest(request);
	}
	private String handleUploadRequest(String request){
		String response = null;
		String[] arr = request.split(IConstants.DELIMITER);
		try{
			String clientName = arr[1];
			//TODO make sure the manager refresh the chain before giving it to client
			//TODO make sure the client gets the 
//			Servrli
////			String serverlist = manager.getServerList(clientName);
//			response = IConstants.SERVER+serverlist;
			
		}catch (ArrayIndexOutOfBoundsException e) {
			Logger.Log("RequestHandler.handleUploadRequest() Something is wrong in the request :"+request);
		}
		return response;
	}
	private static int id = 0;
	private String generateNewChainId(){
		return String.valueOf(id++);
	}
	/**This method will handle the registration of new servers
	 * The server will still not be added to the pool till they send a stable message
	 * @param request
	 */
	private String handleNewServer(String request){
		String[] arr = request.split(IConstants.DELIMITER);
		String response = null;
		try{
			String ip = arr[1];
			int port  = Integer.parseInt(arr[2]);
			//get a potenial chain from co , if null then create a new chain and store it back in
			ServerListTable slTable = manager.getSlTable();
			
			String chainId = slTable.newServerChain();
			if(chainId == null){
				chainId = generateNewChainId();
				ServerBlockTable  sbTable = manager.getSbTable();
				slTable.addServerList(chainId, ip+IConstants.DELIMITER+port);
				sbTable.addServer(chainId, IConstants.SERVER_SIZE);
				//Create new chain add store it and just send ok to the server
			}else{
				String serverList = slTable.getServerList(chainId);
				serverList += IConstants.DELIMITER+ip+IConstants.DELIMITER+port;
				slTable.addServerList(chainId, serverList);
			}
			response = IConstants.CHAIN+IConstants.DELIMITER+chainId+IConstants.DELIMITER+slTable.getServerList(chainId);
		}catch (ArrayIndexOutOfBoundsException e) {
			Logger.Log("RequestHandler.handleNewServer() Something is wrong in the request :"+request);
		}
		return response;
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