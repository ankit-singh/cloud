package cornell.cloud.dropsomething.common.util;

import java.io.*;
import java.util.*;

public class ReadProperty{
	String str, key;
	public static void main(String[] args) {
		ReadProperty r = new ReadProperty();
	}
	public ReadProperty(){
		try{
			File f = new File("/Users/ankitsingh/Desktop/server.properties");
			if(f.exists()){
				Properties pro = new Properties();
				FileInputStream in = new FileInputStream(f);
				pro.load(in);
				System.out.println("All key are given: " + pro.keySet());
			}
			else{
				System.out.println("File not found!");
			}
		}
		catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
	
}