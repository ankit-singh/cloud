package socket;

import java.io.*;
class Directory 
{
	
	
	
 public static void main(String args[])
  {
  try{
  String strDirectoy ="test";
  String strManyDirectories="dir1/dir2/dir3";

  // Create one directory
  boolean success = (
  new File(strDirectoy)).mkdir();
  if (success) {
  System.out.println("Directory: " 
   + strDirectoy + " created");
  }  
  // Create multiple directories
  success = (new File(strManyDirectories)).mkdirs();
  if (success) {
  System.out.println("Directories: " 
   + strManyDirectories + " created");
  }

  }catch (Exception e){//Catch exception if any
  System.err.println("Error: " + e.getMessage());
  }
  }
}