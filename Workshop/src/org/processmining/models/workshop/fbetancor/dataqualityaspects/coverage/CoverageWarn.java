package org.processmining.models.workshop.fbetancor.dataqualityaspects.coverage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.workshop.fbetancor.constructors.Output;
import org.processmining.models.workshop.fbetancor.interfaces.CentralRegistryInterface;
import org.processmining.models.workshop.fbetancor.interfaces.QualityCheck;



/**
 * This class implements the quality check for the quality metric:
 * Format
 */
public class CoverageWarn implements QualityCheck {
	
	public Output output;

	private String value;

	private Float score;
	
	private List<List<String>> traceEvents2;
	
	private Integer repeated = 0;

	private double totalScore;

	private List<String> attKeyNames;

	private List<Integer> attKeyScore;

	private String faults;

	private String advice;

	private String table;
	
	public void initialize() {
		table = "";
		table += "" + "<html><table> <tr> <th>Attribute</th> <th>Score</th></tr>";

		totalScore = 0;

		attKeyNames = new ArrayList<String>();
		attKeyScore = new ArrayList<Integer>();

		faults = "";
		advice = "";
		
		initTimes();

		output = new Output("", "", "", faults, advice);
	}

	private void initTimes() {
		value = "";
		score = (float) 10;
	}

	public void checkQuality(XLog eventlog) {
		// TODO Auto-generated method stub

	}

	public void checkQuality(XLog eventlog, XTrace trace) {
	
	}

	public void checkQuality(XLog eventlog, XTrace trace, XAttribute att)  {
		
	}

	public void checkQuality(XLog eventlog, XTrace trace, XEvent event) {
		// TODO Auto-generated method stub

	}

	public void checkQuality(XLog eventlog, XTrace trace, XEvent event, XAttribute att) {
		
	}
	
	public void checkClear(CentralRegistryInterface list) {
		Integer umbral = 3;
		List<String> currentTrace;
		List<String> currentTrace2;
		Float eventsPerTrace = (float) 0;
		Float desc = (float) 0; 
		Integer cantEvents = 0;
		List<List<String>> traceEvents;
		
		traceEvents = list.getTraceStructureList();
		for (int i = 0; i < traceEvents.size(); i++) {
			currentTrace = traceEvents.get(i);
			cantEvents += currentTrace.size();
		}
		eventsPerTrace = (float)cantEvents /(float)traceEvents.size();
		Integer diff;
		traceEvents2 = list.getTraceStructureList();
		desc = (float) 1 / (float) traceEvents2.size();
		for (int i = 0; i < traceEvents2.size(); i++) {
			currentTrace2 = traceEvents2.get(i);
			diff = Math.abs(currentTrace2.size()-(int) Math.ceil(eventsPerTrace));
			if(diff > umbral) {
				if (score > desc) {
					score -= desc;
					if(score < desc) {
						score = (float) 0;
					}
				}
				faults += "<li> The number of events on trace "+ currentTrace2.get(0) + 
						" is outside the threshold range relative to the average. </li>";
				repeated++;
			}
		}
		setFaults();
		table += "" + "</table>";
		
		faults += table;
	}

	public Output getResult() {
		output.setName("Coverage Warn");

		setScore();
		output.setScore("" + totalScore);

		output.setExplanation("The score is an average of all the traces and their corresponding number of events.");

		output.setFaults(faults);

		giveAdvice();
		output.setAdvice(advice);
		return output;
	}
	
	private void giveAdvice() {
		if (totalScore >= 8) {
			advice += "-";
		} else if (totalScore >= 6) {
			advice += "The number of events per trace is not homogeneous, there are traces with the "
					+ "number of events outside the established threshold.";
		} else {
			advice += "The number of events per trace is irregular, the traces have a number of "
					+ "events without an established pattern.";
		}
	}

	private void setScore() {
		BigDecimal sc = new BigDecimal(score).setScale(1, RoundingMode.HALF_UP);
		totalScore = sc.doubleValue();
	}

	private void setFaults() {
		for (String attKey : attKeyNames) {
			table += "" + "<tr><td>" + attKey + "</td><td>" + attKeyScore.get(attKeyNames.indexOf(attKey))
					+ "</td></tr>";
		}
	}

}
