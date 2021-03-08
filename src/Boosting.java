import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner; // Import the Scanner class to read text files


public class Boosting {

	static ArrayList<double[]> trainingMatrix;
	static ArrayList<String> dictionaryMatrix;
	static ArrayList<double[]> testMatrix;
	static ArrayList<Double> aList = new ArrayList<>();
	static ArrayList<Integer> hClassifiers = new ArrayList<>();
	static ArrayList<Integer> dictionaryHIndices = new ArrayList<>();;
	public static void main(String []args){
        System.out.println("Hello World");
        Scanner trainingData = readFile("C:\\Users\\Michael\\eclipse-workspace\\CSE 151A Project4\\src\\pa5train.txt");
        Scanner testData = readFile("C:\\Users\\Michael\\eclipse-workspace\\CSE 151A Project4\\src\\pa5test.txt");
        Scanner dictionaryData = readFile("C:\\Users\\Michael\\eclipse-workspace\\CSE 151A Project4\\src\\pa5dictionary.txt");
        trainingMatrix = getMatrixDouble(trainingData);
        testMatrix = getMatrixDouble(testData);
        dictionaryMatrix = getDictionary(dictionaryData);
        ArrayList<Integer> iterations = new ArrayList<Integer>();
        iterations.add(3);
        iterations.add(4);
        iterations.add(7);
        iterations.add(10);iterations.add(15);iterations.add(20);
        
        boosting(iterations);

        
        
        
	}
	
	public static void getWords() {
		System.out.println("WORDS");
		for(int i = 0; i < dictionaryHIndices.size();i++) {
			System.out.println(dictionaryMatrix.get(dictionaryHIndices.get(i)));
		}
	}
	
	public static double getError(ArrayList<double[]> matrix) {
		if(aList.size() != hClassifiers.size() || aList.size() != dictionaryHIndices.size()) {
			System.out.println("ERROR");
			return 0;
		}
		double errorCount = 0;
		for(int i = 0; i < matrix.size(); i++) {
			double sign = 0;

			for(int j = 0; j < aList.size(); j++) {
				
				sign += (aList.get(j) * getHLabel(hClassifiers.get(j),dictionaryHIndices.get(j),matrix.get(i)));
			}
			double actualLabel = matrix.get(i)[matrix.get(i).length - 1];
			
			if((sign > 0.0 && 1.0 != actualLabel) || (sign < 0.0 && -1.0 != actualLabel) ) {
				errorCount++;
			}else {
				
			}
		}
		return errorCount / matrix.size();
	}
	
	public static void boosting(ArrayList<Integer> iterations) {
		//hPlus == 1 
		aList = new ArrayList<>();
		hClassifiers = new ArrayList<>();
		dictionaryHIndices = new ArrayList<>();
		ArrayList<Double> weights = new ArrayList<>();
		for(int i = 0; i < trainingMatrix.size();i++) {
			weights.add(1.0/(double)trainingMatrix.size());
		}
		for(int i = 0; i < iterations.get(iterations.size() - 1);i++) {
			double Et = getWeakLearner(weights);
			int h = Et >= 0 ? 1 : -1;
			//System.out.println(Et);
			Et = h * Et;
			double temp = Math.floor(Et);
			int dictionaryIndex = (int)temp;
			Et = Et - temp;
//			System.out.println(Et);
			
			double At = 0.5 * Math.log((1-Et)/Et);//closer to 0, bigger At is 
			double Z = 0.0;
			for(int j = 0; j < weights.size();j++) {
				int hLabel = getHLabel(h,dictionaryIndex,trainingMatrix.get(j));
				double newWeight = weights.get(j) * Math.exp(-1 * At * trainingMatrix.get(j)[trainingMatrix.get(0).length - 1] * hLabel );
				weights.set(j, newWeight);
				Z += newWeight;
			}
			for(int j = 0; j < weights.size();j++) {
				weights.set(j, weights.get(j) / Z);
			}
			aList.add(At);
			hClassifiers.add(h);
			dictionaryHIndices.add(dictionaryIndex);
			if(iterations.contains(i+1)) {
	        	System.out.println("iteration " + (i+1) + " training E: " + getError(trainingMatrix) + " test E: " + getError(testMatrix));
	        	if( i + 1== 10) {
	        		getWords();
	        	}
			}
		
		}
		
		
		
	}
	
	public static int getHLabel(int h,int dictionaryIndex,double[] row) {
		if(h == 1) {
			return row[dictionaryIndex] == 1.0 ? 1 : -1;
		}else {
			//System.out.print("hlabel " + (row[dictionaryIndex] == 0.0 ? 1 : -1));
			return row[dictionaryIndex] == 0.0 ? 1 : -1;
		}
	}

	public static double getWeakLearner(ArrayList<Double> weights) {
		double leastError = 1.0;
		int dictionaryIndex = 0;
		int hPlus = 1;
		for(int i = 0; i < trainingMatrix.get(0).length - 1;i++) {
			ArrayList<Double> h1PlusOutput = new ArrayList<>();
			ArrayList<Double> h1MinusOutput = new ArrayList<>();
			for(int j = 0; j < trainingMatrix.size() ;j++) {
				if(trainingMatrix.get(j)[i] == 1.0) {
					h1PlusOutput.add(1.0);
					h1MinusOutput.add(-1.0);
				}else {
					h1PlusOutput.add(-1.0);
					h1MinusOutput.add(1.0);
				}
			}

			
			//error_w(h+)
			double errorHPlus = 0.0,errorHMinus = 0.0;
			for(int k = 0; k < h1PlusOutput.size();k++) {
				double label = trainingMatrix.get(k)[trainingMatrix.get(0).length - 1];
				errorHPlus += weights.get(k) * (h1PlusOutput.get(k) != label ? 1.0 : 0.0);
				errorHMinus += weights.get(k) * (h1MinusOutput.get(k) != label ? 1.0 : 0.0);
			}
			if(leastError >= errorHPlus || leastError >= errorHMinus) {
				dictionaryIndex = i;
				leastError = Math.min(errorHMinus, errorHPlus);
				hPlus = leastError == errorHPlus ? 1 : -1;
			}
		}
		if(leastError >= 0.5) {
		System.out.println("leastError " + leastError);
		}
		return hPlus * (dictionaryIndex + leastError);
	}
	
	public static ArrayList<String> getDictionary(Scanner data){
		ArrayList<String> matrix = new ArrayList<>();
		while(data.hasNextLine()) {
			String dataString = data.nextLine();
			matrix.add(dataString);
			
		}
		return matrix;
	}
	
 	public static ArrayList<double[]> getMatrixDouble(Scanner data) {
		ArrayList<double[]> matrix = new ArrayList<>();
		while(data.hasNextLine()) {
			String dataString = data.nextLine();
			String[] dataArray = dataString.split(" ");
			double[] n1 = new double[dataArray.length];
			
			for(int i = 0; i < dataArray.length; i++) {
			   n1[i] = Double.parseDouble(dataArray[i]);
			}
			matrix.add(n1);
			
		}
		return matrix;
	}
	
	public static ArrayList<int[]> getMatrixInt(Scanner data) {
		ArrayList<int[]> matrix = new ArrayList<>();
		while(data.hasNextLine()) {
			String dataString = data.nextLine();
			String[] dataArray = dataString.split(" ");
			int[] n1 = new int[dataArray.length];
			for(int i = 0; i < dataArray.length; i++) {
			   n1[i] = Integer.parseInt(dataArray[i]);
			}
			matrix.add(n1);
			
		}
		return matrix;
	}
	
	
	public static Scanner readFile(String fileName) {
		try {
		      File myObj = new File(fileName);
		      Scanner myReader = new Scanner(myObj);
		      return myReader;
		}
		catch(FileNotFoundException e) {
			System.out.println("An error occured.");
			e.printStackTrace();
			return null;
		}
	}
}