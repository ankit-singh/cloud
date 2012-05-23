package cornell.cloud.dropsomething.common.service;

import java.io.File;

import cornell.cloud.dropsomething.common.IConstants;

/**
 * @author ankitsingh
 *
 */
public class FileDeleter{

	
	
	public static void forceDeleteFile(String rootDir, String filePath){
		
		File folder = new File(rootDir);
		if(folder.isDirectory() ){
			//Open the directory , if file exists delete it
			File file = new File(rootDir+filePath);
			if(file.delete()){
				System.out.println("FileDeleter.deleteFile() File deleted ");
			}else{
				System.out.println("FileDeleter.deleteFile() Delete failed");
			}
		}else{
			System.out.println("FileDeleter.deleteFile() root Path given is not a directory please check");
		}
	}
	public static void deleteFile(String rootDir, String filePath,String clientName){
		
		File folder = new File(rootDir);
		if(folder.isDirectory() ){
			// File (or directory) to be moved
			File file = new File(rootDir+filePath);

			// Destination directory
			File dir = new File(rootDir+IConstants.DIRSEP+clientName+"__del");
			dir.mkdirs();

			// Move file to new directory
			boolean success = file.renameTo(new File(dir, file.getName()));
			if (success) {
			    System.out.println("File Moved");
			}else{
				System.out.println("FileDeleter.deleteFile() Opps monkey have taken over the system again");
			}
		}else{
			System.out.println("FileDeleter.deleteFile() root Path given is not a directory please check");
		}
	}
	public static void main(String[] args){
		String rootDir = "/users/ankitsingh/desktop/drop/c1";
		String filePath ="/ijodl2000.pdf";
		deleteFile(rootDir, filePath,"ankit");
		
	}

}
