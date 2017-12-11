
/**
 * Java program which contains variables to configure Fast-LACF.
 * 
 *
 */
public class ConfigFastLACF {


	// total number of buckets
	public static Integer NumberofBuckets = 500;
	//2000, 4080
	

	// Kick Off limit variable
	public static Integer MAX_KICK_OFF = NumberofBuckets;

	// Hash algorithm to choose. "MD5" or "SHA1"
	public static String HASH_ALGORITHM = "SHA1";

	// input data file
	public static String Input_FileName = "C:/Users/Hari/Desktop/fin/4500.txt";

	// lookup file
	public static String LookUp_FileName = "C:/Users/Hari/Desktop/fin/1M.txt";

}
