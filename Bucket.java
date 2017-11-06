
/**
 * Java program class for each bucket in the filter
 * 
 * 
 * @author
 *
 */
public class Bucket {
	char[] fingerprint;
	int size;

	public Bucket() {
		fingerprint = new char[5];
		size = 0;

		fingerprint[4] = (char) 0;
	}
}