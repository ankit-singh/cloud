package socket;

import java.util.Scanner;

public class ClientView {
	Scanner scan;
	public  static void main(String args[]){
		
	}
	public  void creatUI(){
		 scan = new Scanner(System.in);
	}
	public String getClientName() {
		System.out.println("ClientView.getClientName()");
		String user = scan.next();
		return user;
	}
	public String getPassword() {
		System.out.println("ClientView.getPassword()");
			String pwd = scan.next();
			return pwd;
	}
	public String getDirectoryPath() {
		System.out.println("ClientView.getDirectoryPath()");
		String dp = scan.next();
		return dp;
	}
	public void destrutUI(){
		scan.close();
	}
}
