package socket;

import java.io.File;
import java.io.IOException;

import cornell.cloud.dropsomething.common.util.Utilities;





public class test {
	public static void testClients(){
	}
	public static void main(String[] args) throws IOException {
		
		
		
		
		
		
			  File file=new File("/users/ankitsingh/desktop/drop/c1/cloud");
			  boolean exists = file.exists();
			  if (!exists) {
			  // It returns false if File or directory does not exist
			  System.out.println("the file or directory			  you are searching does not exist : " + exists);
			  
			  }else{
			  // It returns true if File or directory exists
			  System.out.println("the file or 			  directory you are searching does exist : " + exists);
			  }
		
		
//		String absPath = "/users/ankitsingh/desktop/drop/c1/mr.txt";
//		String absPath2 = "folder2/file1.txt";
//		String rootDir = "/users/ankitsingh/1/2/3";
//		String rootDir2 = "/users/ankitsingh/1/4/5/";
//		boolean filexist = new File(absPath).exists();
//		System.out.println("File Exist :"+filexist);
////		System.out.println("test.main() "+filePath);
////		new File(rootDir).mkdirs();
////		new File(rootDir2).mkdirs();
		
//		System.out.println("1 :"+Utilities.getFormattedDir(rootDir));
//		System.out.println("2 :"+Utilities.getFormattedDir(rootDir2));
//		System.out.println(" File Path 1:"+Utilities.getFormattedFilePath(absPath));
//		System.out.println(" File Path 2:"+Utilities.getFormattedFilePath(absPath2));
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

