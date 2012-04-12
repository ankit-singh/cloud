package socket;

public class ServerMessage {
	
	private ServerDetails serverDetails;
	public ServerDetails getServerDetails() {
		return serverDetails;
	}
	public void setServerDetails(ServerDetails serverDetails) {
		this.serverDetails = serverDetails;
	}
	public int getMsgCode() {
		return msgCode;
	}
	public void setMsgCode(int msgCode) {
		this.msgCode = msgCode;
	}
	private int msgCode;
	
	public void deSerialize(String str){
		String[] arr = str.split(IConstants.DELIMITER);
		try{
			msgCode = Integer.parseInt(arr[0]);
			serverDetails = new ServerDetails();
			serverDetails.setIp(arr[1]);
			serverDetails.setPort(arr[2]);
		}catch(NumberFormatException nfe){
			System.err.println("Incorrect Opcode used for client message");
			
		}catch(ArrayIndexOutOfBoundsException nfe){
			System.err.println("Something worng in the user name of passward recieved in the ClientMessage");
		}
	}
	public String serialize(){
		
		return this.msgCode+IConstants.DELIMITER+this.serverDetails.toString();
		
	}
	
	
}
