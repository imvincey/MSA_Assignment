//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Name: 	Lim Bing Shun																							//
// GUID: 	2228131L																								//
// Title:	Multimedia Systems and Applications 4/M Coursework														//
//																													//
// COPYRIGHT     																									//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TagOccurenceMatrix {
	
	// populate the CSV HashMap from specific CSV file location
	public static HashMap<String, ArrayList<String>> csvToHM(String csvFile) throws Exception {
		BufferedReader br = null;																		// initialise the variables
		String line = "", token = ",", tempKey = "";
		HashMap<String, ArrayList<String>> tagMap = new HashMap<String, ArrayList<String>>();

		br = new BufferedReader(new FileReader(csvFile));												// read csvfile
		while ((line = br.readLine()) != null) {														// read per line in a csv file, delimit the token
			String data[] = line.split(token);															// place them in the hashmap, return hashmap
			ArrayList<String> alValue = new ArrayList<String>();
			if (!tempKey.equals(data[0])) {
				tempKey = data[0];
				alValue.add(data[1]);
				tagMap.put(tempKey, alValue);
			} else {
				if (tagMap.get(tempKey) == null) {
					alValue.add(data[1]);
					tagMap.put(tempKey, alValue);
				} else {
					alValue = tagMap.get(tempKey);
					alValue.add(data[1]);
					tagMap.put(tempKey, alValue);
				}
			}
		}
		br.close();
		return tagMap;
	}

	// compute the value in the hashmap to find the co-occurence matrix
	public static Map<String, HashMap<String, Double>> hashMapData(HashMap<String, ArrayList<String>> csvToHM)
			throws Exception {
		Map<String, HashMap<String, Double>> outerMap = new HashMap<String, HashMap<String, Double>>();
		HashMap<String, Double> innerMap = new HashMap<String, Double>();

		for (String i : csvToHM.keySet()) {										// populate all the unique value in csv hashmap as a key to co-occurence hashmap
			ArrayList<String> tagValue = new ArrayList<String>();
			tagValue = csvToHM.get(i);
			for (String tag : tagValue) {
				if (!outerMap.containsKey(tag)) {
					outerMap.put(tag, innerMap);
				}
			}
		}

		for (String outerKey : outerMap.keySet()) {								// populate the inner hashmap that has association with the outerkey
			innerMap = new HashMap<String, Double>();							// if innerkey does not exist, add innerkey into the keyset
			for (String tmKey : csvToHM.keySet()) {								// if innerkey exist, then value in the innerkey + 1
				ArrayList<String> tmValue = new ArrayList<String>();
				tmValue = csvToHM.get(tmKey);
				if (tmValue.contains(outerKey)) {
					for (String tval : tmValue) {
						innerMap = outerMap.get(outerKey);
						if (innerMap.isEmpty() && !tval.equals(outerKey)) {
							double counter = 1;
							innerMap.put(tval, counter);
						} else {
							if (innerMap.containsKey(tval)) {
								double counter = innerMap.get(tval);
								counter = counter + 1;
								innerMap.put(tval, counter);
							} else {
								if (!tval.equals(outerKey)) {
									double counter = 1;
									innerMap.put(tval, counter);
								}
							}
						}
					}
				}
				outerMap.put(outerKey, innerMap);
			}
		}
		return outerMap;
	}
	
	// populate the co-occurrence hashtable into matrix for printing
	public static String[][] hashToMatrix(Map<String, HashMap<String, Double>> hashMapData) throws Exception {
		String[][] matrix = new String[hashMapData.size() + 1][hashMapData.size() + 1];
		int matCounter = 1;
		for (String outerKey : hashMapData.keySet()) {								// set up the row and column of the matrix with the tags
			matrix[matCounter][0] = outerKey;
			matrix[0][matCounter] = outerKey;
			matCounter++;
		}
		for (int i = 1; i < matrix.length; i++) {									// do a matrix with the counter, if tag combination does not exist = 0
			for (int j = 1; j < matrix.length; j++) {								// if similar tag = 0
				if (matrix[i][0].equalsIgnoreCase(matrix[0][j])) {
					matrix[i][j] = Double.toString(0);
				}
				if (hashMapData.keySet().contains(matrix[i][0])) {
					if (hashMapData.get(matrix[i][0]).containsKey(matrix[0][j])) {
						matrix[i][j] = Double.toString(hashMapData.get(matrix[i][0]).get(matrix[0][j]));
					} else {
						matrix[i][j] = "0";
					}
				} else {
					matrix[i][j] = "0";
				}
			}
		}
		return matrix;
	}

	//printing of the matrix to csvfile
	public static void printMatrixToCSV(String[][] matrix) throws Exception {
		PrintWriter pw = new PrintWriter(new File("tag_occurence.csv"));						// initialise the printwrite method with csv file name
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < matrix.length; i++) {												// add the value and token back into the stringbuilder and
			for (int j = 0; j < matrix.length; j++) {											// append next line if go to next row
				if (j != (matrix.length - 1)) {													// print into csv file after finishing the for loop
					sb.append(matrix[i][j]);
					sb.append(",");
				} else {
					sb.append(matrix[i][j]);
				}
			}
			sb.append("\n");
		}
		pw.write(sb.toString());
		pw.close();
		System.out.println("CSV has generated successfully. CSV file name : tag_occurence.csv");
	}

	// sort the Map by value, with asc (true) /desc (false) order
	private static Map<String, Double> sortByComparator(Map<String, Double> unsortMap, final boolean order) {

		List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(unsortMap.entrySet());
		
		Collections.sort(list, new Comparator<Entry<String, Double>>() {					// place hashmap data into LinkedList
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {		// compare the list based on values, swap if the value is higher / lower
				if (order) {
					return o1.getValue().compareTo(o2.getValue());
				} else {
					return o2.getValue().compareTo(o1.getValue());
				}
			}
		});

		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();				// Use LinkedHashMap to maintain the insertion order
		for (Entry<String, Double> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}
	
	// print top 5 TF tags
	public static void printTop5Tags(Map<String, HashMap<String, Double>> outerMap, String tags) {
		Map<String, Double> sortedMap = sortByComparator(outerMap.get(tags), false);		// use the sorting method and print out the top 5 tags of the specify outerkey
		int sortedMapCounter = 0;
		
		for (String x : sortedMap.keySet()) {
			System.out.println(x + ": " + sortedMap.get(x));
			if (sortedMapCounter < 4) {
				sortedMapCounter++;
			} else {
				break;
			}
		}
	}
	
	// compute the IDF score of each tag
	public static HashMap<String, Double> tagMapWithIDF (String tagURL) throws Exception{
		HashMap<String, Double> tagHM = new HashMap<String, Double> ();				
		BufferedReader br = new BufferedReader(new FileReader(tagURL));						// read the tags.csv file for the total no of tags per photo collection
		String line = "";																	// then compute the IDF score = log10 (I / I(X))
		while ((line = br.readLine()) != null){
			String[] tag = line.split(",");
			String key = tag[0];
			double value = Math.log10(10000 / Integer.parseInt(tag[1]));
			tagHM.put(key, value);
		}
		return tagHM;
	}
	
	// populate out the TFIDF with IDF and the value of each counter, returning top 5 TFIDF tags (top 5 popular tags)
	public static void populateTop5TagsWithTFIDF (HashMap<String, Double> tagHashMap, Map<String, HashMap<String, Double>> hashMapData, String outerKey){
		HashMap<String, Double> innerMap = hashMapData.get(outerKey);
		int testCounter = 0;
		
		for (String x : innerMap.keySet()){
			DecimalFormat df = new DecimalFormat("#.###");												// using the outerkey to retrieve the inner hashmap
			innerMap.put(x, Double.valueOf(df.format(innerMap.get(x) * tagHashMap.get(x))));			// update TFIDF score into the inner hashmap (3.d.p.)
		}																								// sort the hashmap and print out top 5 TFIDF per outerkey specify
		
		Map<String, Double> tmpMap = sortByComparator(innerMap, false); 
		for (String x : tmpMap.keySet()){
			System.out.println(x + ": " + tmpMap.get(x));
			if (testCounter < 4){
				testCounter++;
			} else {
				break;
			}
		}
	}

	public static void main(String[] args) throws Exception {
		// Task 1
		System.out.println("Start of Task 01: ");
		String csvFile = "photos_tags.csv";
		String tagFile = "tags.csv";
		System.out.println("Copying csv file to hashmap...");
		HashMap<String, ArrayList<String>> csvToHM = csvToHM(csvFile);
		System.out.println("Copying complete... Populating matrix map...");
		Map<String, HashMap<String, Double>> hashMapData = hashMapData(csvToHM);
		System.out.println("Matrix map populated. Printing map to CSV... ");
		String[][] hashToMatrix = hashToMatrix(hashMapData);
		printMatrixToCSV(hashToMatrix);
		System.out.println("Task 01 Complete...");
		// end of Task 1
		System.out.println("------------------------------------------------------------------");
		// Task 2
		System.out.println("Start of Task 02: ");
		System.out.println("Water Tags: ");
		printTop5Tags(hashMapData, "water");
		System.out.println("People Tags: ");
		printTop5Tags(hashMapData, "people");
		System.out.println("London Tags: ");
		printTop5Tags(hashMapData, "london");
		System.out.println("Task 02 Complete...");
		// end of Task 2
		System.out.println("------------------------------------------------------------------");
		// Task 3
		System.out.println("Start of Task 03: ");
		HashMap <String, Double> tagHashMap = tagMapWithIDF(tagFile);
		System.out.println("Water Tags: ");
		populateTop5TagsWithTFIDF(tagHashMap, hashMapData, "water");
		System.out.println("People Tags: ");
		populateTop5TagsWithTFIDF(tagHashMap, hashMapData, "people");
		System.out.println("London Tags: ");
		populateTop5TagsWithTFIDF(tagHashMap, hashMapData, "london");
		System.out.println("Task 03 Complete...");
		// end of Task 3
	}
}
