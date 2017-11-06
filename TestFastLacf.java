 
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestFastLacf {

	static ArrayList<Long> inputList = new ArrayList<Long>();
	static ArrayList<String> lookupList = new ArrayList<String>();
	static ArrayList<Integer> inputPrefixLengthList = new ArrayList<Integer>();
	static ArrayList<Integer> lookupPrefixLengthList = new ArrayList<Integer>();

	/**
	 * Method to read input file and store it in arraylist
	 * 
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public void readinputFile() throws NumberFormatException, IOException {

		BufferedReader buf = new BufferedReader(new FileReader(new File(ConfigFastLACF.Input_FileName)));
		String line = null;

		while ((line = buf.readLine()) != null) {

			String ip = line.replaceAll("\n", "");

			int ipPrefix = 0;

			int index = 0;

			index = ip.indexOf('/');

			ipPrefix = Integer.parseInt(ip.substring(index + 1));
			ip = ip.substring(0, index);

			long prefix = Integer.parseInt(ip);

			inputList.add(prefix);

			// if unpopular element
			if (ipPrefix < 14 || ipPrefix > 24) {
				inputPrefixLengthList.add(1);
			}
			// if popular element
			else {
				inputPrefixLengthList.add(0);
			}

		}

		buf.close();
	
		System.out.println("input file over -----------------------");

	}

	/**
	 * Method to read lookup file and store it in arraylist
	 * 
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public void readLookupFile() throws NumberFormatException, IOException {

		BufferedReader buf = new BufferedReader(new FileReader(new File(ConfigFastLACF.LookUp_FileName)));

		String line = null;

		while ((line = buf.readLine()) != null) {

			String ip = line.replaceAll("\n", "");

			int ipPrefix = 0;

			int index = 0;

			index = ip.indexOf('/');

			ipPrefix = Integer.parseInt(ip.substring(index + 1));
			ip = ip.substring(0, index);

			lookupList.add(ip);

			// if unpopular element
			if (ipPrefix < 14 || ipPrefix > 24) {
				lookupPrefixLengthList.add(1);
			}
			// if popular element
			else {
				lookupPrefixLengthList.add(0);
			}


		}
		buf.close();
		
		System.out.println("lookup file over ----------------------");

	}

	/**
	 * Method to execute the filter and store the result into arraylist.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void executeFilter() throws IOException, InterruptedException {

		// number of times filter has to be executed.
		int loopCount = 10;

		List<Integer> countList = new ArrayList<Integer>();
		List<Integer> fprList = new ArrayList<Integer>();

		// for(int i=0;i<loopCount;i++)
		{
			FastLacf obj = new FastLacf();

			int[] result = obj.executeFilter(obj, inputList, inputPrefixLengthList, lookupList, lookupPrefixLengthList);
			countList.add(result[0]);
			fprList.add(result[1]);

			// obj = null;

		}
		synchronized (inputList) {
			BufferedWriter bw = new BufferedWriter(new FileWriter("C:/Users/Hari/Desktop/aaaaa.txt", true));

			for (int i = 0; i < countList.size(); i++) {
				bw.write(countList.get(i) + " (" + fprList.get(i) + ")                ");

				if (i % 5 == 0)
					bw.newLine();
			}

			bw.flush();
			bw.close();
		}
	}

	/**
	 * Main method starts here
	 * 
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub

		TestFastLacf p1 = new TestFastLacf();
		p1.readinputFile();
		p1.readLookupFile();
		p1.executeFilter();

	}

}
