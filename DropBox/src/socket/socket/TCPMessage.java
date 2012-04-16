package socket;

public class TCPMessage {
	
	private int opCode;
	private String message;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getOpCode() {
		return opCode;
	}
	public void setOpCode(int opCode) {
		this.opCode = opCode;
	}
}
