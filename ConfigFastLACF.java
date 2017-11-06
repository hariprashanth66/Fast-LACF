 
/**
 * Java program which contains variables  to configure filter as well as 
 * to run as  LA Cuckoo Filter and Fast-LACF.
 * 
 * 
 *
 */
public class ConfigFastLACF {
   //Kick Off limit variable
	public static Integer MAX_KICK_OFF = 10;
	//total number of buckets. 0 ... 1999 = >total = 2000.
	public static Integer NumberofBuckets = 50;
	//Hash algorithm to choose. "MD5" or "SHA1".
	public static String HASH_ALGORITHM = "SHA1";
	
	//input data file.
	public static String Input_FileName = "C:/Users/Hari/Desktop/OneMillionInput.txt";
	
	
	
	//lookup file
	public static String LookUp_FileName = "C:/Users/Hari/Desktop/OneMillionLookup.txt";
	
	
	//variable to test both standard Length Aware Cuckoo Filter and Fast-LACF.
	//if it is TRUE, then Fast-LACF else, LACF.
	public static boolean isLAModeOn  = true ;
	
	
}

