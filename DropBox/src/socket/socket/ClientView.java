package socket;

import java.util.Scanner;

public class ClientView {
	Scanner scan;
	String dir = null;
	String cname = null;
	String pwd = null;
	public ClientView(){
		creatUI();
	}
	public  static void main(String args[]){
		
	}
	public  void creatUI(){
		 scan = new Scanner(System.in);
	}
	public String getClientName() {
		if (cname == null) {
			System.out.println("ClientView.getClientName()");
			cname = scan.next();
		}
		return cname;
	}
	public String getPassword() {
		if (pwd == null) {
			System.out.println("ClientView.getPassword()");
			pwd = scan.next();
		}
			return pwd;
	}
	public String getDirectoryPath() {
		if(dir == null)
			{
		System.out.println("ClientView.getDirectoryPath()");
		 dir = scan.next();
		}
		return dir;
	}
	public void destrutUI(){
		scan.close();
	}
}
