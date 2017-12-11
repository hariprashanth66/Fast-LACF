 
/**
 * Java program class for each bucket in the filter
 * 
 * 
 * @author
 *
 */
public class Bucket {
	char[] fingerprint;
	long[] strprint;
	int size;

	public Bucket() {
		fingerprint = new char[10];
		strprint = new long[10];
		size = 0;
	}
}