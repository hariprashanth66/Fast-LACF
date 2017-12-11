import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class sacav {
	static ArrayList<Long> inputList = new ArrayList<Long>();
	static ArrayList<String> lookupList = new ArrayList<String>();
	static ArrayList<Integer> inputPrefixLengthList = new ArrayList<Integer>();
	
	
	public static void main(String args[]) throws NumberFormatException, IOException
	{
		BufferedReader buf = new BufferedReader(new FileReader(new File("C:/Users/Hari/Desktop/JavaWorkSpace/OneMillionInput.txt")));
		String line = null;

		/*while ((line = buf.readLine()) != null) {

			String ip = line.replaceAll("\n", "");
			
			long prefix = Integer.parseInt(ip);

			inputList.add(prefix);

		

		}*/

		buf.close();
	
		//System.out.println("input file over -----------------------");
		
		
		Collections.sort(inputList);
		Collections.reverse(inputList);
		//System.out.println(inputList);
		
		char a = (char)3016;
		
		char b = (char)3016;
		if(a == b)		
		System.out.println("yesssss");
	}

}
