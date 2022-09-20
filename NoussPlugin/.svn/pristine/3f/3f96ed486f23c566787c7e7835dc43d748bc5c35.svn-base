package org.processmining.models.workshop.fbetancor.dataqualityaspects.duplicationFree;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.workshop.fbetancor.constructors.Output;
import org.processmining.models.workshop.fbetancor.interfaces.CentralRegistryInterface;
import org.processmining.models.workshop.fbetancor.interfaces.QualityCheck;

public class DuplicatedAttributes implements QualityCheck {
	public Output output;
	
	private String faults;
	
	private String advice;

	private double score;

	private int counter;

	private int total;
	
	public void initialize() {
		faults = "";
		advice = "";

		score = 10;
		counter = 0;
		total = 0;

		output = new Output("", "", "", faults, advice);
	}

	public void checkQuality(XLog eventlog) {
		// TODO Auto-generated method stub
		
	}

	public void checkQuality(XLog eventlog, XTrace trace) {
		// TODO Auto-generated method stub
		
	}

	public void checkQuality(XLog eventlog, XTrace trace, XAttribute att) {
		// TODO Auto-generated method stub
		
	}

	public void checkQuality(XLog eventlog, XTrace trace, XEvent event) {
		XAttributeMap eventAttributesMap = event.getAttributes();
		int eventAttributesCount = eventAttributesMap.size();
		Set<String> eventAttributesSet = eventAttributesMap.keySet();
		int duplicates = eventAttributesCount - eventAttributesSet.size();
		
		if (duplicates > 0) {
			faults += "" + "The event " + event + " has: " + duplicates + " duplicate attributes.<br>";
			counter += duplicates;
//			getDupCounts(eventsBag, uniqueSetLog);
		}	
	}

	public void checkQuality(XLog eventlog, XTrace trace, XEvent event, XAttribute att) {
		// TODO Auto-generated method stub
		
	}

	public void checkClear(CentralRegistryInterface list) {
		List<ArrayList<String>> attributes = list.getAttributeList();
		total = attributes.size();
	}

	public Output getResult() {
		output.setName("Duplicate Attributes.");

		setScore();
		output.setScore("" + score);
		output.setExplanation("This checks for duplicated attributes in an event. <br>"
				+ " The score is calculated by the percentage of duplicated attributes over the log. <br>");
		
		output.setFaults(faults);
		giveAdvice();
		output.setAdvice(advice);
		return output;
	}
	
	private void setScore() {
		if (counter != 0) {
			double percentage = ((double) counter / (double) total) * 100;
			BigDecimal bd = new BigDecimal(percentage).setScale(1, RoundingMode.HALF_UP);
			double decimalPerc = bd.doubleValue();
			score = score - decimalPerc;
			if (score < 1) {
				score = 1;
			}
		}
	}
	
	private void giveAdvice() {
		if (score >= 8) {
			advice += "-";
		} else if (score >= 6) {
//			advice += "There are a fair amount of duplicate events. <br> "
//					+ "This could give a bias to the analysis results. <br>"
//					+ "Check whether this is as intended.";
		} else {
//			advice += "Lots of duplicate events over the log have been found. <br> "
//					+ "This could have a big influence on the results of analysis, since it can "
//					+ "create bias towards the duplicate events. <br>"
//					+ "Go through all of these events and check if this is as intended, "
//					+ "and try to avoid having duplicates if not intended.";
		}
	}
	

}
