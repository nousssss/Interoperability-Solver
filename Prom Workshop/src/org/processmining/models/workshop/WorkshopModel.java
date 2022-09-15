package org.processmining.models.workshop;

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

/**
 * The workshop model. Allows to keep track of direct succession relation with
 * cardinalities.
 * 
 * @author hverbeek
 * 
 */
public class WorkshopModel implements HTMLToString {

	/**
	 * The map from source nodes to target nodes to cardinalities.
	 */
	private Map<XEventClass, Map<XEventClass, Integer>> successionMap;
	/**
	 * The minimal and maximal (non-zero) cardinalities found in the map.
	 */
	int minCardinality = Integer.MAX_VALUE, maxCardinality = 0;

	/**
	 * Creates a workshop model from the given CSV stream.
	 * 
	 * @param input
	 *            The given CSV stream. The first row contains the target event
	 *            classes. The first column contains the source event classes.
	 *            Other fields contain cardinalities from the source event class
	 *            to the target event class. If no direct succession relation is
	 *            there fro the former to the latter, this field should contain
	 *            0.
	 * @throws IOException
	 */
	public WorkshopModel(InputStream input) throws IOException {
		successionMap = new HashMap<XEventClass, Map<XEventClass, Integer>>();
		importFromStream(input);
	}

	/**
	 * Creates an empty workshop model for the given collection of event
	 * classes.
	 * 
	 * @param eventClasses
	 *            The given collection of event classes.
	 */
	public WorkshopModel(XEventClasses eventClasses) {
		successionMap = new HashMap<XEventClass, Map<XEventClass, Integer>>();
		for (XEventClass fromEventClass : eventClasses.getClasses()) {
			Map<XEventClass, Integer> successorMap = new HashMap<XEventClass, Integer>();
			for (XEventClass toEventClass : eventClasses.getClasses()) {
				successorMap.put(toEventClass, 0);
			}
			successionMap.put(fromEventClass, successorMap);
		}
	}

	/**
	 * Adds as many direct successions from the given source event class to the
	 * given target event class as the given cardinality dictates.
	 * 
	 * @param fromEventClass
	 *            The given source event class.
	 * @param toEventClass
	 *            The given target event class.
	 * @param cardinality
	 *            The given cardinality.
	 */
	public void addDirectSuccession(XEventClass fromEventClass, XEventClass toEventClass, int cardinality) {
		Map<XEventClass, Integer> successorMap = successionMap.get(fromEventClass);
		assert (successorMap != null);
		Integer oldCardinality = successorMap.get(toEventClass);
		assert (oldCardinality != null);
		successorMap.put(toEventClass, oldCardinality + cardinality);
		updateCardinality(oldCardinality + cardinality);
	}

	/**
	 * Gets the cardinality of the direct succession from a given source event
	 * class to a given target event class.
	 * 
	 * @param fromEventClass
	 *            The given source event class.
	 * @param toEventClass
	 *            The given target event class.
	 * @return The cardinality of the direct succession relation from the source
	 *         event class to the target event class. 0 if no direct succession
	 *         exists.
	 */
	public int getDirectSuccession(XEventClass fromEventClass, XEventClass toEventClass) {
		return successionMap.get(fromEventClass).get(toEventClass);
	}

	/**
	 * Returns the event classes in the workshop model.
	 * 
	 * @return
	 */
	public Set<XEventClass> getEventClasses() {
		return successionMap.keySet();
	}

	/*
	 * Imports a workshop model from a CSV stream.
	 */
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

	/**
	 * Exports a workshop model to a given CSV file.
	 * 
	 * @param file
	 *            The given CSV file.
	 * @throws IOException
	 */
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

	/**
	 * Creates an HTML formatted string from the workshop model. Used for the
	 * basic visualization of the workshop model.
	 * 
	 * @param includeHTMLTags
	 *            Whether to include the <html> and </html> tags.
	 * @returns An HTML formatted string from the workshop model.
	 */
	public String toHTMLString(boolean includeHTMLTags) {
		StringBuffer buffer = new StringBuffer();
		if (includeHTMLTags) {
			buffer.append("<html>");
		}
		buffer.append("<table>");
		buffer.append("<tr>");
		buffer.append("<td></td>");
		for (XEventClass eventClass : getEventClasses()) {
			buffer.append("<td>" + eventClass.getId() + "</td>");
		}
		buffer.append("</tr>");
		for (XEventClass fromEventClass : getEventClasses()) {
			buffer.append("<tr>");
			buffer.append("<td>" + fromEventClass.getId() + "</td>");
			for (XEventClass toEventClass : getEventClasses()) {
				buffer.append("<td>" + getDirectSuccession(fromEventClass, toEventClass) + "</td>");
			}
			buffer.append("</tr>");
		}
		buffer.append("</table>");
		if (includeHTMLTags) {
			buffer.append("</html>");
		}
		return buffer.toString();
	}

	/*
	 * Updates the minimal and maximal cardinalities, given that the given
	 * cardinality has just been added to model.
	 */
	private void updateCardinality(int cardinality) {
		if (cardinality > 0 && cardinality < minCardinality) {
			minCardinality = cardinality;
		}
		if (cardinality > maxCardinality) {
			maxCardinality = cardinality;
		}
	}

	/**
	 * Gets the minimal (non-zero) cardinality found in the model.
	 * 
	 * @return The minimal (non-zero) cardinality found in the model.
	 */
	public int getMinCardinality() {
		return minCardinality;
	}

	/**
	 * Gets the maximal cardinality found in the model.
	 * 
	 * @return The maximal cardinality found in the model.
	 */
	public int getMaxCardinality() {
		return maxCardinality;
	}
}
