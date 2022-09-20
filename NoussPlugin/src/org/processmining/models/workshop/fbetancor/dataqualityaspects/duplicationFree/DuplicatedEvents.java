package org.processmining.models.workshop.fbetancor.dataqualityaspects.duplicationFree;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.Bag;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.workshop.fbetancor.constructors.Output;
import org.processmining.models.workshop.fbetancor.interfaces.CentralRegistryInterface;
import org.processmining.models.workshop.fbetancor.interfaces.QualityCheck;

public class DuplicatedEvents implements QualityCheck {
	/**
	 * Output for the duplicatesLog check.
	 */
	public Output output;

	/**
	 * Notable findings for the duplicatesLog check.
	 */
	private String faults;

	/**
	 * Advice regarding the duplicatesLog check.
	 */
	private String advice;

	/**
	 * Score for the duplicatesLog check.
	 */
	private double score;

	/**
	 * Counter of all the duplicate events over the log.
	 */
	private int counter;

	/**
	 * Total number of events over the log.
	 */
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
		// TODO Auto-generated method stub
		XAttributeMap eventAttributesMap = event.getAttributes();
		int eventAttributesCount = eventAttributesMap.size();
		Set<String> eventAttributesSet = eventAttributesMap.keySet();
		int duplicates = eventAttributesCount - eventAttributesSet.size();
		//System.out.print("hola");
	}

	public void checkQuality(XLog eventlog, XTrace trace, XEvent event, XAttribute att) {
		// TODO Auto-generated method stub
		
	}

	public void checkClear(CentralRegistryInterface list) {
		List<Bag<List<String>>> tracesBag = list.getTraceBag();
		
		for (Bag<List<String>> eventsBag : tracesBag) {
			int eventsBagSize = eventsBag.size();

			Set<List<String>> uniqueSetLog = eventsBag.uniqueSet();
			int eventsCount = uniqueSetLog.size();

			total = eventsBagSize;

			int duplicates = eventsBagSize - eventsCount;

			if (duplicates > 0) {
				faults += "" + "The trace " + eventsBag + " has: " + duplicates + " duplicate events.<br>";
				getDupCounts(eventsBag, uniqueSetLog);
			}	
		}
		
	}

	public Output getResult() {
		output.setName("Duplicate Events.");

		setScore();
		output.setScore("" + score);
		output.setExplanation("This checks for duplicated events in a trace. <br>"+""
				+ " The score is calculated by the percentage of duplicated events over the total events of the log. <br>" 
		+ "This means that 0 Duplicates (0.0%) means a 10.0 out of 10.0");
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
	
	private void getDupCounts(Bag<List<String>> logBag, Set<List<String>> uniqueSet) {
		ArrayList<List<String>> toRemove = new ArrayList<List<String>>();

		for (List<String> element : uniqueSet) {
			toRemove.add(element);
		}

		logBag.removeAll(toRemove);

		Set<List<String>> uniqueTraceBag = logBag.uniqueSet();
		faults += "" + "<ul>";
		for (List<String> ele : uniqueTraceBag) {
			int currentCount = logBag.getCount(ele) + 1;

			counter = counter + (currentCount - 1);
			faults += "" + "<li>Event with attributes " + ele + " occurs " + currentCount + " times.</li>";
		}
		faults += "" + "</ul>";
	}

}
