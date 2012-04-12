package socket;

public class ClientMessage {
	private ClientDetails clientDetails;
	private int msgCode;
	public ClientDetails getClientDetails() {
		return clientDetails;
	}
	public int getMsgCode() {
		return msgCode;
	}

	public void setMsgCode(int msgCode) {
		this.msgCode = msgCode;
	}
	public void setClientDetails(ClientDetails clientDetails) {
		this.clientDetails = clientDetails;
	}
	public void deSerialize(String str){
		String[] arr = str.split(IConstants.DELIMITER);
		try{
			msgCode = Integer.parseInt(arr[0]);
			clientDetails = new ClientDetails();
			clientDetails.setUserName(arr[1]);
			clientDetails.setPassWord(arr[2]);
		}catch(NumberFormatException nfe){
			System.err.println("Incorrect Opcode used for client message");
			
		}catch(ArrayIndexOutOfBoundsException nfe){
			System.err.println("Something worng in the user name of passward recieved in the ClientMessage");
		}
	}
	public String serialize(){
		
		return this.msgCode+IConstants.DELIMITER+this.clientDetails.toString();
		
	}
	
	
}
