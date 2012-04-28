package socket;

import java.io.*;

import quicktime.app.spaces.Space;
class Directory 
{

	public static void print(String[] arr){
		System.out.println("Directory.print()");
		for(String s: arr){
			System.out.println(s);
		}
	}

	public static void main(String args[])
	{
		try{
			String[] strDirectoy ="users/ankitsingh/desktop/drop".split("/");
			String[] strDirectoy1 ="users/ankitsingh/desktop/drop".split("/");
			String[] strPath="/users/ankitsingh/desktop/drop/".split("/");
			print(strDirectoy);
			print(strDirectoy1);
			print(strPath);
			
			
//			if(strPath.trim().length() != strDirectoy.trim().length()){
//				int diff = strDirectoy.trim().length() -strPath.trim().length(); 
//				System.out.println("Directory.main() Diff :"+diff);
//				System.out.println("Directory.main() filepath :"+strDirectoy.length());
//				System.out.println("Directory.main() DirPath :"+strPath.length());
//				String[] arr = strDirectoy.split("/");
//				String fileName = arr[arr.length -1];
//				String dir = "/users/ankitsingh/desktop/ankit/ankit/ankit/";
//				System.out.println(new File(dir).mkdirs());
//				System.out.println("Directory.main() "+strDirectoy.substring(0,strDirectoy.length()-fileName.length()-1));
//			}
			
			
			// Create one directory
//			boolean success = (
//					new File(strDirectoy)).mkdir();
//			if (success) {
//				System.out.println("Directory: " 
//						+ strDirectoy + " created");
//			}  
			// Create multiple directories
//			success = (new File(strManyDirectories)).mkdirs();
//			if (success) {
//				System.out.println("Directories: " 
//						+ strManyDirectories + " created");
//			}

		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
}