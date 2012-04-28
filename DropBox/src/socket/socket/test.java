package socket;

import java.io.File;
import java.io.IOException;





public class test {
	public static void testClients(){
	}
	public static void main(String[] args) throws IOException {
		
	}
	public static boolean isValidRequest(String request){
		if(request != null && !request.trim().equals("")){
			return true;
		}
		else{
			return false;
		}
	}
}

