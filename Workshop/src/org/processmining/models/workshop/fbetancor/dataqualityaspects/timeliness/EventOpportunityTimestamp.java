package org.processmining.models.workshop.fbetancor.dataqualityaspects.timeliness;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
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
public class EventOpportunityTimestamp implements QualityCheck {
	
	public Output output;

	private String value;

	private Float score;

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
		// TODO Auto-generated method stub

	}

	public void checkQuality(XLog eventlog, XTrace trace, XAttribute att)  {
	}

	public void checkQuality(XLog eventlog, XTrace trace, XEvent event) {
	}

	public void checkQuality(XLog eventlog, XTrace trace, XEvent event, XAttribute att) {
		
	}

	public void checkClear(CentralRegistryInterface list) {
		List<List<XAttributeTimestamp>> lista = list.getTraceEventTimestamps();
		List<XAttributeTimestamp> currentTrace;
		Date first;
		Date last;
		Float desc = (float) 0; 
		List<List<String>> traceEvents;
		
		traceEvents = list.getTraceStructureList();
		desc = (float) 1 / (float) traceEvents.size();
		
		
		for (int i = 0; i < lista.size(); i++) {
			currentTrace = lista.get(i);
			first = currentTrace.get(0).getValue();
			last = currentTrace.get(currentTrace.size()-1).getValue();
			//System.out.println(currentTrace);
			//System.out.println();
			for(int j = 0; j < currentTrace.size(); j++) {	
				System.out.println(first + " <= " +  currentTrace.get(j) + " <= " + last);
				if(!(!first.after(currentTrace.get(j).getValue()) && !last.before(currentTrace.get(j).getValue()))){
					System.out.println("Entre!!");
					if (score > desc) {
						score -= desc;
						if(score < desc) {
							score = (float) 0;
						}
					}
					faults += "<li> The timestamp of event " + currentTrace.get(j) //+ " in trace " + currentTrace.get(j) 
							+ "is outside the range  the traces timestamp. </li>";
				}
			}
		}
		setFaults();
		table += "" + "</table>";
		
		faults += table;
	}

	public Output getResult() {
		output.setName("Event Opportunity Timestamp");

		setScore();
		output.setScore("" + totalScore);

		output.setExplanation("The score represents that the events are within the timestamp threshold of the trace to which they correspond.");

		output.setFaults(faults);

		giveAdvice();
		output.setAdvice(advice);
		return output;
	}

	private void giveAdvice() {
		if (totalScore >= 8) {
			advice += "-";
		} else {
			advice += "Your timestamps are not good.\r\n" + 
					"Check the log.";
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
