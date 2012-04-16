package socket;

public interface IConstants {

	
	
	public static final int CREATE_NEW_USER =101;
	public static final int USER_CREATED = 102;
	public static final int USER_EXISTS = 103;
	public static final int AUTHENTICATE = 104;
	public static final int USER_AUTHENTICATED = 105;
	public static final int INVALID_PWD = 106;
	public static final int UPLOAD =107;
	public static final int SERVER = 108;
	public static final int NO_SPACE =109;
	public static final int DOWNLOAD =110;
	public static final int PUSH =111;
	public static final int PULL =112;
	public static final int FILELIST=113;
	
	
	
	
	public static final String DELIMITER = "__";
	
	public static String COORD_IP ="128.84.213.188";
	
	public static final int COOR_PORT=5555;
	
	public static final long SERVER_SIZE = 10*1024*1024*1024;
	
	
}
