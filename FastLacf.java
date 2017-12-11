
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * This program implements Fast Length-Aware Cuckoo Filter
 * 
 */

public class FastLacf {

	Bucket filter[];
	int elementsInsertFailCount;

	int popInsertedCount;
	int unpopInsertedCount;

	int unpopFalsePositiveElements;
	int popFalsePositiveElements;

	int secondPosCount;
	int secondPosLookupCount;

	HashMap<Integer, Character> map = new HashMap<>();

	static ArrayList<Long> lookupMissedUnpop = new ArrayList<Long>();
	static ArrayList<Long> unpopElements = new ArrayList<Long>();
	static ArrayList<Integer> pos1List = new ArrayList<Integer>();
	static ArrayList<Integer> pos2List = new ArrayList<Integer>();
	static ArrayList<Integer> posCheck = new ArrayList<Integer>();
	static ArrayList<Integer> kicks = new ArrayList<Integer>();

	static HashMap<Long, Boolean> unpopInsertedElements = new HashMap<>();
	static ArrayList<Long> unpopPositiveLookupElements = new ArrayList<Long>();

	static HashMap<Long, Boolean> popInsertedElements = new HashMap<>();
	static ArrayList<Long> popPositiveLookupElements = new ArrayList<Long>();

	private final Integer Max_Kicks = ConfigFastLACF.MAX_KICK_OFF;
	private int filterSize = ConfigFastLACF.NumberofBuckets;
	String hashAlgorithm = ConfigFastLACF.HASH_ALGORITHM;

	/**
	 * constructor instantiating array of objects to store buckets of the filter
	 * 
	 */
	public FastLacf() {

		// creating 2000 filter buckets [ 0 ...1999]
		filter = new Bucket[filterSize + 1];

		// creating objects for each bucket.
		for (int i = 0; i <= filterSize; i++)
			filter[i] = new Bucket();

		popInsertedCount = 0;
		unpopInsertedCount = 0;
		unpopFalsePositiveElements = 0;
		elementsInsertFailCount = 0;
		popFalsePositiveElements = 0;
		secondPosCount = 0;
		secondPosLookupCount = 0;

	}

	/**
	 * Each bucket contains 5 cells range from 0 to 4. cells 0 to 3 store
	 * fingerprints of elements
	 * 
	 * 
	 * <P>
	 * Method to initialize first 4 cells (0,1,2,3) of each bucket with value
	 * "128". The value "128" is chosen because in java, MD5 Hash value can
	 * range from 0 to 127. So, inorder to detect empty cell or not, value 128
	 * is chosen.
	 * 
	 */
	private void initializeFilter() {
		for (int i = 0; i < filterSize; i++) {
			for (int j = 0; j <= 9; j++) {
				filter[i].fingerprint[j] = (char) 4095;
				// initializing with 4095 to mark empty cell for a fingerprint
				// size of 12 bits.

			}

		}

	}

	/**
	 * Method to invoke hash algorithm and calculate the hash value
	 * 
	 * @param prefix
	 *            - number for which fingerprint is to be calculated.
	 * @param algorithm
	 *            - MD5 or SHA1 algorithm to calculate hash value.
	 * 
	 * @return - string hash value.
	 * 
	 */
	private String calculateHash(String prefix, String algorithm) {
		// System.out.println(prefix);
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance(algorithm);

			byte[] hashedBytes = digest.digest(prefix.getBytes("UTF-8"));
			return convertByteArrtoString(hashedBytes);

		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Method to convert byte array of the hash value into string value
	 * 
	 * @param hashedVal
	 *            - byte array of hash value calculated from Hash algorithm
	 * @return - string hash value.
	 * 
	 */
	private String convertByteArrtoString(byte[] hashedVal) {

		StringBuilder build = new StringBuilder();
		for (int i = 0; i < hashedVal.length; i++) {
			build.append(Integer.toString((hashedVal[i] & 0xFF) + 0x100, 16).substring(1));
		}
		return build.toString();

	}

	/**
	 * Method which takes input number and convert it into fingerprint of char
	 * type.
	 * 
	 * @param prefix
	 *            - number to be inserted into the filter.
	 * @return - fingerprint character value.
	 */
	private char generateFingerprint(long prefix) {
		String hashStr = calculateHash(String.valueOf(prefix), hashAlgorithm);
		BigInteger val = new BigInteger(hashStr, 16);
		int mappedValue = val.intValue();
		// System.out.println(mappedValue + " !!!!");
		//// mappedValue = (mappedValue) % 253;
		mappedValue = (mappedValue & 0xFFF) % 4094;

		// & 0xFFFF
		if (mappedValue < 0) {
			mappedValue = mappedValue * -1;
		}
		// mappedValue += 1;

		// if (mappedValue == 0)
		// System.out.println(mappedValue + " ****");
		char fp = (char) mappedValue;

		// fpList.add(prefix + "");
		return fp;

	}

	/**
	 * Method to calculate the position of the fingerprint where it can be
	 * inserted in the filter
	 * 
	 * @param prefix
	 *            - String value for which fingerprint is to be calculated.
	 * @param algorithm
	 *            - MD5 or SHA1 algorithm to calculate hash value.
	 * 
	 * @return - bucker number in the filter.
	 */

	private int calculatePosition(String prefix, String algorithm) {
		String hash = calculateHash((prefix), algorithm);
		BigInteger val = new BigInteger(hash, 16);
		int mappedValue = val.intValue();

		mappedValue = mappedValue % filterSize;
		if (mappedValue < 0) {
			mappedValue = mappedValue * -1;
		}
		return mappedValue;

	}

	/**
	 * Method to check whether element is successfully inserted in the filter in
	 * either of the two positions .
	 * 
	 * <P>
	 * In each bucket, elements for position 1 are inserted in the following
	 * order indices: 0,1,2,3. Elements for position 2 are inserted in the
	 * following order indices : 3,2,1,0. So, position cell( cell 4) is
	 * initialized with value 3 and it decreases to 2,1,0 when the elements are
	 * inserted in position 2.
	 * 
	 * @param fp
	 *            - fingerprint of the number
	 * @param position
	 *            - position value in the filter
	 * @param hashPosition
	 *            - position 1 or position 2 of the fingerprint.
	 * 
	 * @return true - insert success. false - insert fail.
	 */
	private boolean isInsertSucess(char fp, int position, int prefixLength, long prefix) {
		// System.out.println(" in isinsertsuccess");
		// unpopular element
		if (prefixLength == 1) {

			int posVal = (int) (filter[position].fingerprint[0]);
			// System.out.println("check empty cell "+posVal);
			// checking whether it is empty cell .
			if (posVal == 4095) { // changed for 12 bits
				filter[position].fingerprint[0] = fp;
				filter[position].strprint[0] = prefix;
				return true;

			}

		}

		else {
			// System.out.println(" popular element");
			for (int i = 1; i <= 9; i++) {
				int posVal = (int) (filter[position].fingerprint[i]);
				// System.out.println("check empty cell "+posVal);
				// checking whether it is empty cell .
				if (posVal == 4095) { // changed for 12 bits
					filter[position].fingerprint[i] = fp;
					// System.out.println(" element inserted at " + i + " in
					// bucket " + position);
					filter[position].strprint[i] = prefix;
					return true;
				}

			}
		}

		return false;

	}

	/**
	 * Method to check whether element is inserted into the filter. If not, it
	 * will do kickOff and try to insert the element
	 * 
	 * @param fp
	 *            - fingerprint of the element
	 * @param pos1
	 *            - position 1 of the fingerprint in the filter
	 * @param pos2
	 *            - position 2 of the fingerprint in the filter
	 * 
	 * @return boolean - Insert success or failure.
	 */
	private boolean insertItem(char fp, int pos1, int pos2, int prefixLength, long prefix) {
		unpopElements.add(Long.valueOf(pos1));
		unpopElements.add(Long.valueOf(pos2));

		// Try to insert element in either of the two positions.
		if (isInsertSucess(fp, pos1, prefixLength, prefix)) {
			if (prefixLength == 1) {
				unpopElements.add(Long.valueOf(1));
			}

			return true;
		}

		if (isInsertSucess(fp, pos2, prefixLength, prefix)) {
			if (prefixLength == 1) {
				unpopElements.add(Long.valueOf(2));
			}

			return true;
		}
		/*
		 * Since element insertion is full, it will start KickingOFF elements.
		 * It will be done as, element from position 1 is removed ,say element
		 * "a" and current element is inserted in position 1. Position 2 of
		 * element "a" is calculated and try to insert it in the bucket. If that
		 * fails, then position 1 element from the same bucket as element "a"'s
		 * second position is removed and element "a" is inserted and its
		 * position cell is updated. Then this cycle follows till it reaches
		 * KickOFF limit.
		 */

		if (prefixLength == 1)
			++secondPosCount;
		int elementKickedOutPos = 0;
		char tempFp = '\0';
		long tempstr = 0;
		// choosing random position to start kick off.
		Random rand = new Random();
		int pickPos = rand.nextInt(2);
		if (pickPos == 0) {
			elementKickedOutPos = pos1;

		} else {
			elementKickedOutPos = pos2;

		}

		for (int i = 0; i < Max_Kicks; i++) {

			// choosing random position to insert the fingerprint
			// System.out.println("Kicking off at " + elementKickedOutPos + " "
			// + filter[elementKickedOutPos].strprint[randPos]);
			// kicks.add(elementKickedOutPos);

			int randPos = 0;

			if (prefixLength == 0) {
				randPos = rand.nextInt(9) + 1;
			}

			tempFp = filter[elementKickedOutPos].fingerprint[randPos];
			filter[elementKickedOutPos].fingerprint[randPos] = fp;

			// System.out.println(
			// "Kicking off at " + elementKickedOutPos + " " +
			// filter[elementKickedOutPos].strprint[randPos]);
			tempstr = filter[elementKickedOutPos].strprint[randPos];
			filter[elementKickedOutPos].strprint[randPos] = prefix;

			// if (prefixLength == 1)
			// System.out.println("kicking an unpop " + tempFp);

			if (map.containsKey(elementKickedOutPos)) {
				if (map.get(elementKickedOutPos) == (tempFp)) {
					elementKickedOutPos += filterSize;
				}
			}

			int nextPosOfKickedoutElement = (calculatePosition(tempFp + "", hashAlgorithm) ^ elementKickedOutPos);

			nextPosOfKickedoutElement = nextPosOfKickedoutElement % filterSize;
			// System.out.println("xors " + elementKickedOutPos + " to " +
			// nextPosOfKickedoutElement);
			if (isInsertSucess(tempFp, nextPosOfKickedoutElement, prefixLength, tempstr)) {
				kicks.add(elementKickedOutPos);
				kicks.add(nextPosOfKickedoutElement);
				unpopElements.add(Long.valueOf(nextPosOfKickedoutElement));
				return true;
			}

			else {
				fp = tempFp;
				elementKickedOutPos = nextPosOfKickedoutElement;

				prefix = tempstr;
				// System.out.println("successivee kicking");
			}

		}

		System.out.println("Max Kicks reached");
		return false;

	}

	/**
	 * Method to calculate two positions in the filter for the number .
	 * 
	 * @param prefix
	 *            - number to be inserted into the filter.
	 * 
	 */
	private boolean insertIntoFilter(long prefix, int prefixLength) {
		char fingerprint = generateFingerprint(prefix);
		// System.out.println(" inserting prefix " + prefix + " with fp = " +
		// fingerprint + " length " + prefixLength);
		int pos1 = -1, pos2 = -1;

		pos1 = calculatePosition(String.valueOf(prefix), hashAlgorithm);
		pos2 = (calculatePosition(fingerprint + "", hashAlgorithm)) ^ pos1;
		// calculating pos2 by XOR.

		if (pos2 > filterSize) {
			pos2 = pos2 % filterSize;
			map.put(pos2, fingerprint);

		}
		// System.out.println(" at pos = " + pos1 + " & " + pos2);

		if (insertItem(fingerprint, pos1, pos2, prefixLength, prefix)) {
			if (prefixLength == 1) {
				unpopInsertedCount++;
				unpopElements.add(prefix);
				unpopInsertedElements.put(prefix, true);
			} else {
				popInsertedCount++;
				popInsertedElements.put(prefix, true);
			}

			return true;

		}

		System.out.println(prefix + " not inserted at " + prefixLength);
		return false;

	}

	/**
	 * Method to read input file containing random numbers and insert it into
	 * the filter
	 * 
	 * @param ipList
	 *            - List containing random numbers
	 * 
	 * 
	 */

	public void insertInputFile(List<Long> ipList, List<Integer> ipPrefixList) throws IOException {

		// Random r = new Random();
		int start = 0; // r.nextInt(10);
		for (int i = start; i < ipList.size(); i++) {
			boolean status = insertIntoFilter(ipList.get(i), ipPrefixList.get(i));
			if (!status)
				break;
		}

	}

	/**
	 * Method to check whether particular fingerprint is found in the bucket
	 * 
	 * @param fp
	 *            - fingerprint of the number
	 * @param position
	 *            - position value in the filter
	 * @param hashPosition
	 *            - position 1 or position 2 of the fingerprint.
	 * 
	 * @return true - insert success. false - insert fail.
	 */

	private boolean isIpFound(char fp, int position, int prefixLength) {

		// checking the bucket corresponding to position 1 of the element..

		if (prefixLength == 1) {
			if (filter[position].fingerprint[0] == fp)
				return true;

		}

		else {
			for (int i = 1; i <= 9; i++) {
				if (filter[position].fingerprint[i] == fp)
					return true;
			}

		}

		return false;

	}

	/**
	 * Method to search whether the number is present in the filter
	 * 
	 * 
	 * @param prefix
	 *            - number to be searched
	 * @return boolean - True- number present, False - number not present
	 */
	private boolean searchInFilter(long prefix, int prefixLength) {
		int pos1 = -1;
		int pos2 = -1;

		char fp = generateFingerprint(prefix);
		// calculate position 1 of the element.
		pos1 = calculatePosition(String.valueOf(prefix), hashAlgorithm);

		// calculate position 2 of the element.
		pos2 = (calculatePosition(fp + "", hashAlgorithm)) ^ pos1;

		pos2 = pos2 % filterSize;

		if (isIpFound(fp, pos1, prefixLength)) {

			return true;
		}

		if (isIpFound(fp, pos2, prefixLength)) {

			++secondPosLookupCount;
			return true;
		}

		if (prefixLength == 1) {
			posCheck.add(pos1);
			posCheck.add(pos2);
		}
		return false;
	}

	private void lookup(List<Long> lookupList, List<Integer> prefixList) throws IOException {

		for (int i = 0; i < lookupList.size(); i++) {

			long prefix = lookupList.get(i);
			int prefixLength = prefixList.get(i);
			if (searchInFilter(prefix, prefixLength)) {
				if (prefixLength == 1) {
					unpopFalsePositiveElements++;
					unpopPositiveLookupElements.add(prefix);
				} else {
					popFalsePositiveElements++;
					popPositiveLookupElements.add(prefix);
				}

			}

			else {
				elementsInsertFailCount++;
				// System.out.println(prefix);
				if (prefixLength == 1) {
					lookupMissedUnpop.add(prefix);
				}

			}

		}

	}

	public void printbucket() {
		for (int i = 0; i < filter.length; i++) {
			for (int j = 0; j <= 9; j++)
				System.out.print(filter[i].fingerprint[j] + "\t");

			System.out.println();

		}

	}

	/**
	 * Main method execution begins here. Note : Use PrintInsertCount.java file
	 * to run either PAFilter or standard filter. Avoid running from this main
	 * method.
	 *
	 * @throws IOException
	 *             - if the input file is not found.
	 */
	public static void main(String[] args) throws IOException {
		FastLacf filterObj = new FastLacf();

		filterObj.initializeFilter();
		System.out.println("Total inserted entries " + filterObj.popInsertedCount);
		System.out.println("False positive count is: " + filterObj.unpopFalsePositiveElements);
		System.out.println("Failure Count is: " + filterObj.elementsInsertFailCount);

	}

	public int[] executeFilter(FastLacf filterObj, List<Long> ipList, List<Integer> ipPrefixList, List<Long> lookupList,
			List<Integer> lookupPrefixList) throws IOException {
		filterObj.initializeFilter();
		// filterObj.printbucket();

		filterObj.insertInputFile(ipList, ipPrefixList);
		filterObj.lookup(lookupList, lookupPrefixList);

		for (long unpopPositivePrefix : unpopPositiveLookupElements) {
			if (unpopInsertedElements.containsKey(unpopPositivePrefix)) {
				unpopFalsePositiveElements--;
			}

		}

		for (long popPositivePrefix : popPositiveLookupElements) {
			if (popInsertedElements.containsKey(popPositivePrefix)) {
				popFalsePositiveElements--;
			}

		}

		int[] result = new int[4];
		result[0] = filterObj.unpopFalsePositiveElements;
		result[1] = filterObj.popFalsePositiveElements;
		result[2] = filterObj.popInsertedCount;
		result[3] = filterObj.unpopInsertedCount;

		BufferedWriter bw = new BufferedWriter(new FileWriter("C:/Users/Hari/Desktop/fp1235.txt", false));

		for (int i = 1; i < ConfigFastLACF.NumberofBuckets; i++) {
			for (int j = 0; j <= 9; j++) {

				bw.write(filter[i].strprint[j] + "\t");

			}
			bw.write("\n");
		}

		for (int i = 1; i <= unpopElements.size(); i++) {
			bw.write(unpopElements.get(i - 1) + "\t");
			if (i % 4 == 0)
				bw.write("\n");
		}

		// if (result[0] < result[3])
		// result[0] = result[3];
		
		 System.out.println(result[0] + " fpr " + result[1]);
		 System.out.println(result[3] + " result " + result[2]);

		bw.write("check ----------------------------------------------------\n");
		for (int i = 0; i < lookupMissedUnpop.size(); i++) {
			bw.write(lookupMissedUnpop.get(i) + "\n");

		}

		bw.write("insert ----------------------------------------------------\n");
		for (int i = 0; i < posCheck.size(); i++) {
			bw.write(posCheck.get(i) + "\n");
		}

		bw.write("kickoff ----------------------------------------------------\n");
		for (int i = 0; i < kicks.size(); i++) {
			bw.write(kicks.get(i) + "\t");
			if (i % 2 == 1)
				bw.write("\n");
		}

		bw.write("prefixess ----------------------------------------------------\n");

		for (Integer name : map.keySet()) {

			int key = name;
			char value = map.get(name);
			// System.out.println(key + " " + value);
			bw.write("\n " + key + " " + value);

		}
		bw.close();
		return result;

	}
}
