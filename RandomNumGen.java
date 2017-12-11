import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class RandomNumGen {

	HashMap<Integer, Boolean> map = new HashMap<>();

	public void addOld() throws NumberFormatException, IOException {

		BufferedReader buf = new BufferedReader(
				new FileReader(new File("C:/Users/Hari/Desktop/fin/5.txt")));

		String line = null;

		while ((line = buf.readLine()) != null) {

			String ip = line.replaceAll("\n", "");

			int index = ip.indexOf('\\');

			ip = ip.substring(0, index);

			map.put(Integer.parseInt(ip), true);

		}
		buf.close();

		System.out.println("lookup file reading over");
	}

	public static void main(String[] args) throws IOException {
		//

		RandomNumGen obj = new RandomNumGen();

		//obj.addOld();

		int min = 1000000;

		int max = 2000000000;

		Random random = new Random();

		int randomNumber = 0;

		int[] prefixLengthArray = { 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
									24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 
									24, 24, 24, 24, 24, 24, 24, 24, 24, 24,	24, 24, 24, 24, 24,
									15, 15, 15, 15, 15, 16, 16, 16, 16, 16, 17, 17, 17, 17, 18, 18, 18, 18, 18, 19,
									19, 19, 19, 20, 20, 20, 20, 21, 21, 21, 21, 22, 22, 22, 22, 22, 22, 19, 19, 2 ,
									1 , 4 , 5 , 25, 99};

		System.out.println(prefixLengthArray.length);
		int randomPrefix = 0;
		int index = 0;

		int count =0;
		BufferedWriter bw = new BufferedWriter(new FileWriter("C:/Users/Hari/Desktop/fin/4500.txt", false));

		for (int i = 0; i < 4500; i++) {
			randomNumber = random.nextInt(max + 1 - min) + min;

			// System.out.println(index);
			//if (1==1){
				//if (!obj.map.containsKey(random)) {

				index = random.nextInt(prefixLengthArray.length);
				//index = random.nextInt(10) + 90;
				
				if(index >=95)
					count++;
				randomPrefix = prefixLengthArray[index];
				bw.write(randomNumber + "\\" + randomPrefix + "\n");

			/*}
			else
			{
				i--;
			}*/

			// System.out.println(randomPrefix);


		}

		bw.flush();
		bw.close();
		
		System.out.println(count);

	}

}
