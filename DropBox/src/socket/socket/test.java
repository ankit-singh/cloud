package socket;

import javax.swing.Icon;





public class test {
	public static void testClients(){
		ClientHandler ch1 = new ClientHandler();
		ch1.createNewUser();
		ClientHandler ch2 = new ClientHandler();
		ch2.authenticateUser();
	}
	public static void main(String[] args) {
		System.out.println("test.main()"+IConstants.SERVER_SIZE);
		testClients();
		//		ClientHandler ch = new ClientHandler();
		//		//		ch.createNewUser();
		//		final String largeFile = "/users/ankitsingh/desktop/sl.mp4"; // REPLACE
		//		final int BUFFER_SIZE = 65536;
		//		new Thread(new Runnable() {
		//			public void run() {
		//				try {
		//					ServerSocket serverSocket = new ServerSocket(12345);
		//					Socket clientSocket = serverSocket.accept();
		//					long startTime = System.currentTimeMillis();
		//					byte[] buffer = new byte[BUFFER_SIZE];
		//					int read;
		//					int totalRead = 0;
		//					DataInputStream clientInputStream = new DataInputStream (clientSocket.getInputStream());
		//					DataOutputStream clientOS = new DataOutputStream (clientSocket.getOutputStream());
		//					int size = clientInputStream.readInt();
		//					
		//					byte[] digit = new byte[size];
		//
		//					for(int i = 0; i < size; i++)
		//						digit[i] = clientInputStream.readByte(); 
		//					String request = new String(digit);
		//					File f = new File("/users/ankitsingh/desktop/drop/"+request.split("__")[1]);
		//					
		//					clientOS.writeInt(333);
		//					FileOutputStream fileStream  = new FileOutputStream(f);
		//					System.out
		//							.println("test.main(...).new Runnable() {...}.run()");
		//					while ((read = clientInputStream.read(buffer)) != -1) {
		//						fileStream.write(buffer,0,read);
		//						totalRead += read;
		//					}
		//					long endTime = System.currentTimeMillis();
		//					System.out.println(totalRead + " bytes read in " + (endTime - startTime) + " ms.");
		//				} catch (IOException e) {
		//				}
		//			}
		//		}).start();
		//		new Thread(new Runnable() {
		//			public void run() {
		//				try {
		//					Thread.sleep(1000);
		//					Socket socket = new Socket("localhost", 12345);
		//					File file = new File(largeFile);
		//					FileInputStream fileInputStream = new FileInputStream(file);
		//					DataOutputStream socketOutputStream = new DataOutputStream (socket.getOutputStream());
		//					DataInputStream socketInputStream  = new DataInputStream ( socket.getInputStream());
		//					long startTime = System.currentTimeMillis();
		//					String re = "222__"+file.getName()+"__"+file.getTotalSpace();
		//					socketOutputStream.writeInt(re.getBytes().length);
		//					socketOutputStream.write(re.getBytes());
		//					 int opcode = socketInputStream.readInt();
		//					 if(opcode == 333){
		//					byte[] buffer = new byte[BUFFER_SIZE];
		//					int read;
		//					int readTotal = 0;
		//					while ((read = fileInputStream.read(buffer)) != -1) {
		//						System.out
		//								.println("Uploading");
		//						socketOutputStream.write(buffer, 0, read);
		//						readTotal += read;
		//					}
		//					socketOutputStream.close();
		//					fileInputStream.close();
		//					socket.close();
		//					long endTime = System.currentTimeMillis();
		//					System.out.println(readTotal + " bytes written in " + (endTime - startTime) + " ms.");
		//					 }
		//				} catch (Exception e) {
		//				}
		//			}
		//		}).start();
		//	}
	}
}

