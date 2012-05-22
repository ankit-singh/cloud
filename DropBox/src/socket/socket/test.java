package socket;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;





public class test {
	public static void testClients(){
	}
	public static void main(String[] args){



		try{


			Socket tcpSocket = new Socket(IConstants.COORD_IP, 6565);
			
		}catch (ConnectException e) {
			// TODO: handle exception
			System.out.println("test.main() No connection");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

