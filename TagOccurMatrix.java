import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TagOccurMatrix {

	public static void main(String[] args) {
		String phototag = "photos_tags.csv", line = "", token = ",";
		BufferedReader br = null;
		PrintWriter pw;
		StringBuilder sb;
		BufferedReader br1 = null;
		String line2 = "";

		HashMap<String, ArrayList<String>> tagMap = new HashMap<String, ArrayList<String>>();

		Map<String, HashMap<String, Double>> outerMap = new HashMap<String, HashMap<String, Double>>();
		HashMap<String, Double> innerMap = new HashMap<String, Double>();

		String tempKey = "";
		try {
			br = new BufferedReader(new FileReader(phototag));
			while ((line = br.readLine()) != null) {
				String data[] = line.split(token);
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
			// store all different value into outer map
			for (String i : tagMap.keySet()) {
				ArrayList<String> tagValue = new ArrayList<String>();
				tagValue = tagMap.get(i);
				for (String tag : tagValue) {
					if (!outerMap.containsKey(tag)) {
						outerMap.put(tag, innerMap);
					}
				}
			}

			for (String outerKey : outerMap.keySet()) {
				innerMap = new HashMap<String, Double>();
				for (String tmKey : tagMap.keySet()) {
					ArrayList<String> tmValue = new ArrayList<String>();
					tmValue = tagMap.get(tmKey);
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

			////////////////////// PRINTING OF CSV
			////////////////////// FILE///////////////////////////
			String[][] matrix = new String[outerMap.size() + 1][outerMap.size() + 1];
			int matCounter = 1;
			for (String outerKey : outerMap.keySet()) {
				matrix[matCounter][0] = outerKey;
				matrix[0][matCounter] = outerKey;
				matCounter++;
			}
			for (int i = 1; i < matrix.length; i++) {
				for (int j = 1; j < matrix.length; j++) {
					if (matrix[i][0].equalsIgnoreCase(matrix[0][j])) {
						matrix[i][j] = Integer.toString(0);
					}
					if (outerMap.keySet().contains(matrix[i][0])) {
						if (outerMap.get(matrix[i][0]).containsKey(matrix[0][j])) {
							matrix[i][j] = Double.toString(outerMap.get(matrix[i][0]).get(matrix[0][j]));
						} else {
							matrix[i][j] = "0";
						}
						// System.out.println("key : " + matrix[i][0] + " ,
						// elements : " + outerMap.get(matrix[i][0]));
					} else {
						matrix[i][j] = "0";
					}
				}
			}
			// System.out.println(Arrays.deepToString(matrix));

			pw = new PrintWriter(new File("hello.csv"));
			sb = new StringBuilder();

			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix.length; j++) {
					if (j != (matrix.length - 1)) {
						sb.append(matrix[i][j]);
						sb.append(",");
					} else {
						sb.append(matrix[i][j]);
					}
				}
				sb.append("\n");
				// System.out.println(sb.toString());
			}

			pw.write(sb.toString());
			pw.close();
			
			
			
			// HashMap<String, Double> hm =
			// sortedByValue(outerMap.get("water"));
			
			
			
			//sort map into top 5
//			Map<String, Double> waterHM = sortByComparator(outerMap.get("water"), false);
//			int waterHMCounter = 0;
//			System.out.println("Water Tags: ");
//			for (String x : waterHM.keySet()){
//				System.out.println("Key: " + x + " ; Value: " + waterHM.get(x));
//				if (waterHMCounter < 4){
//					waterHMCounter++;
//				} else {
//					break;
//				}
//			}
//			
//			Map<String, Double> peopleHM = sortByComparator(outerMap.get("people"), false);
//			int peopleHMCounter = 0;
//			System.out.println("People Tags: ");
//			for (String x : peopleHM.keySet()){
//				System.out.println("Key: " + x + " ; Value: " + peopleHM.get(x));
//				if (peopleHMCounter < 4){
//					peopleHMCounter++;
//				} else {
//					break;
//				}
//			}
//			
//			Map<String, Double> londonHM = sortByComparator(outerMap.get("london"), false);
//			int londonHMCounter = 0;
//			System.out.println("London Tags: ");
//			for (String x : londonHM.keySet()){
//				System.out.println("Key: " + x + " ; Value: " + londonHM.get(x));
//				if (londonHMCounter < 4){
//					londonHMCounter++;
//				} else {
//					break;
//				}
//			}
			
			
			String tagUrl = "tags.csv";
			HashMap<String, Double> tagHM = new HashMap<String, Double> ();
			br1 = new BufferedReader(new FileReader(tagUrl));
			while ((line2 = br1.readLine()) != null){
				String[] tag = line2.split(",");
				String key = tag[0];
				double value = Math.log10(10000 / Integer.parseInt(tag[1]));
				System.out.println(value);
				tagHM.put(key, value);
			}
			
			HashMap<String, Double> londonMap = outerMap.get("london");
			
			for (String x : londonMap.keySet()){
				double value = londonMap.get(x);
				value = value * tagHM.get(x);
//				long factor = (long) Math.pow(10, 3);
//			    value = value * factor;
//			    long tmp = Math.round(value);
				DecimalFormat df = new DecimalFormat("#.###");  
				value = Double.valueOf(df.format(value));
			    londonMap.put(x, value);
			}
			Map<String, Double> tmpMap = new HashMap<String, Double>();
			tmpMap = sortByComparator(londonMap, false);
			int testCounter = 0;
			for (String x : tmpMap.keySet()){
				System.out.println("Key: " + x + " ; Value: " + tmpMap.get(x));
				if (testCounter < 4){
					testCounter++;
				} else {
					break;
				}
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			

//			for (int i = 0; i < matrix.length; i++) {
//				if (matrix[i][0] != null) {
//					if (matrix[i][0].equalsIgnoreCase("water")) {
//						System.out.println(Arrays.toString(matrix[i]));
//					}
//					if (matrix[i][0].contains("people")) {
//						System.out.println(Arrays.toString(matrix[i]));
//					}
//					if (matrix[i][0].contains("london")) {
//						System.out.println(Arrays.toString(matrix[i]));
//					}
//				}
//			}

		} catch (FileNotFoundException fe) {
			System.out.println(fe);
		} catch (IOException ioe) {
			System.out.println(ioe);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					System.out.println(e);
				}
			}
		}

	}
	
	private static Map<String, Double> sortByComparator(Map<String, Double> unsortMap, final boolean order) {

        List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Double>>() {
            public int compare(Entry<String, Double> o1, Entry<String, Double> o2)  {
                if (order)  {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        for (Entry<String, Double> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }



}
