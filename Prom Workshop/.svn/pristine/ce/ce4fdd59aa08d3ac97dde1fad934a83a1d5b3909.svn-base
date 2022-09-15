package org.processmining.models.workshop.sjjleemans.ProcessTree.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.processmining.framework.util.HTMLToString;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class ProcessTreeModel implements HTMLToString{
	public enum Operator {
		ACTIVITY,
		SEQUENTIAL,
		LOOP,
		LOOP_FLOWER,
		EXCLUSIVE_CHOICE,
		PARALLEL
	}
	
	private Map<XEventClass, Map<XEventClass, Integer>> successionMap;
	int minCardinality = Integer.MAX_VALUE, maxCardinality = 0;
	public String debug = "";
	
	public Node root;
	
	public ProcessTreeModel(InputStream input) throws IOException {
		successionMap = new HashMap<XEventClass, Map<XEventClass, Integer>>();
		importFromStream(input);
	}
	
	public ProcessTreeModel(XEventClasses eventClasses) {
		successionMap = new HashMap<XEventClass, Map<XEventClass, Integer>>();
		for (XEventClass fromEventClass : eventClasses.getClasses()) {
			Map<XEventClass, Integer> successorMap = new HashMap<XEventClass, Integer>();
			for (XEventClass toEventClass : eventClasses.getClasses()) {
				successorMap.put(toEventClass, 0);
			}
			successionMap.put(fromEventClass, successorMap);
		}
	}
	
	public void addDirectSuccession(XEventClass fromEventClass, XEventClass toEventClass, int cardinality) {
		Map<XEventClass, Integer> successorMap = successionMap.get(fromEventClass);
		assert (successorMap != null);
		Integer oldCardinality = successorMap.get(toEventClass);
		assert (oldCardinality != null);
		successorMap.put(toEventClass, oldCardinality + cardinality);
		updateCardinality(oldCardinality + cardinality);
	}
	
	public int getDirectSuccession(XEventClass fromEventClass, XEventClass toEventClass) {
		return successionMap.get(fromEventClass).get(toEventClass);
	}
	
	public Set<XEventClass> getEventClasses() {
		return successionMap.keySet();
	}
	
	private void importFromStream(InputStream input) throws IOException {
		Reader streamReader = new InputStreamReader(input);
		CsvReader csvReader = new CsvReader(streamReader);
		Map<String, XEventClass> map = new HashMap<String, XEventClass>();
		List<XEventClass> array = new ArrayList<XEventClass>();
		successionMap = new HashMap<XEventClass, Map<XEventClass, Integer>>();
		if (csvReader.readRecord()) {
			for (int i = 1; i < csvReader.getColumnCount(); i++) {
				String s = csvReader.get(i);
				XEventClass eventClass = new XEventClass(s, i - 1);
				map.put(s, eventClass);
				array.add(i - 1, eventClass);
				successionMap.put(eventClass, new HashMap<XEventClass, Integer>());
			}
		}
		while (csvReader.readRecord()) {
			XEventClass fromEventClass = map.get(csvReader.get(0));
			assert (fromEventClass != null);
			Map<XEventClass, Integer> successorMap = successionMap.get(fromEventClass);
			for (int i = 1; i < csvReader.getColumnCount(); i++) {
				Integer cardinality = Integer.valueOf(csvReader.get(i));
				assert (cardinality != null);
				XEventClass toEventClass = array.get(i - 1);
				assert (toEventClass != null);
				successorMap.put(toEventClass, cardinality);
				updateCardinality(cardinality);
			}
		}
		csvReader.close();
	}
	
	public void exportToFile(File file) throws IOException {
		Writer fileWriter = new FileWriter(file);
		CsvWriter csvWriter = new CsvWriter(fileWriter, ',');
		csvWriter.write("");
		for (XEventClass eventClass : successionMap.keySet()) {
			csvWriter.write(eventClass.getId());
		}
		csvWriter.endRecord();
		for (XEventClass fromEventClass : successionMap.keySet()) {
			csvWriter.write(fromEventClass.getId());
			for (XEventClass toEventClass : successionMap.keySet()) {
				csvWriter.write(successionMap.get(fromEventClass).get(toEventClass).toString());
			}
			csvWriter.endRecord();
		}
		csvWriter.close();
	}
	
	public String toHTMLString(boolean includeHTMLTags) {
		StringBuffer buffer = new StringBuffer();
		if (includeHTMLTags) {
			buffer.append("<html>");
		}
		
		//print the model
		if (root != null) {
			buffer.append(root.toString());
		}
		
		if (includeHTMLTags) {
			buffer.append("</html>");
		}
		return buffer.toString();
	}
	
	private void updateCardinality(int cardinality) {
		if (cardinality > 0 && cardinality < minCardinality) {
			minCardinality = cardinality;
		}
		if (cardinality > maxCardinality) {
			maxCardinality = cardinality;
		}
	}
	
	public int getMinCardinality() {
		return minCardinality;
	}
	
	public int getMaxCardinality() {
		return maxCardinality;
	}
}
