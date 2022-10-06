package org.processmining.models.workshop.fbetancor.dataqualityaspects.domainConsistency;

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

public class ValuesByCompression implements QualityCheck {
	public Output output;

	private int totals;

	private String faults;

	private String advice;

	private double score;

	private String table;

	public void initialize() {
		table = "";
		table += "" + "<table> <tr> <th>Attribute</th> <th>Score</th> <th></th> <th>#Values</th> "
				+ "<th>Only Strings</th>  <th>Only Digits</th>    <th>String/Digit</th>	    "
				+ "<th></th>    <th>Max. Length</th>    <th>Min. Length</th>    <th>Average</th>    "
				+ "<th>Variance</th>    <th>Standard Deviasion</th> </tr>";
		score = 0;
		faults = "";
		advice = "";
		output = new Output("", "", "", faults, advice);
		totals = 0;
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
		
	}

	public void checkQuality(XLog eventlog, XTrace trace, XEvent event, XAttribute att) {
		// TODO Auto-generated method stub
		
	}

	public void checkClear(CentralRegistryInterface list) {
		// TODO Auto-generated method stub
		List<ArrayList<String>> attTypes = list.getAttributeTypes();
		List<ArrayList<String>> attValues = list.getAttributeList();
		
		for (int i = 0; i<attTypes.size(); i++) {
			calculate(attValues.get(i), attTypes.get(i));
		}
		
		totals = attTypes.size();
		table += "" + "</table>";
		faults = table;
	}

	public Output getResult() {
		output.setName("Possible values by compression Check");
		BigDecimal totalScore = new BigDecimal(score / totals).setScale(1, RoundingMode.HALF_UP);
		output.setScore("" + score / totals);
		output.setExplanation("For each attribute checks if it belongs to a");
		output.setFaults(faults);
		giveAdvice();
		output.setAdvice(advice);
		return output;
	}
	
	
	private void giveAdvice() {
		if (score >= 8) {
			advice += "-";
		} else if (score >= 6) {
//			advice += "Your data is fairly consistent. <br> "
//					+ "Try to see for which attributes the consistency is not scoring high, "
//					+ "and evaluate if it is necessary to fix this. It could be due to missing / incorrect values.";
		} else {
//			advice += "Your data is inconsistent. <br> " + "Evaluate all the inconsistencies mentioned here, "
//					+ "and decide on whether it is necessary to improve the consistency. Inconsistent "
//					+ "values could mean missing / incorrect values.";
		}
	}

	
	/**
	 * Calculate the score and fill in the faults for the consistency check.
	 * 
	 * @param list
	 */
	private void calculate(ArrayList<String> listValues,ArrayList<String> listTypes) {
		double attScore = 0;
		int count = 0;
		for (int i = 0; i<listValues.size(); i++) {
			if (i < listTypes.size()) {
				if (checkType(listValues.get(i),listTypes.get(i))) {
					count += 10;
				}
			}
		}
		attScore = (double) count / (double) listValues.size(); 

		score += attScore;
	}
	
	private boolean checkType(String value, String type) {
		try {switch (type) {
			case "String": { return true;}
			case "Boolean": {
				return value.toLowerCase() == "true" || value.toLowerCase() == "false"; 
			}
			case "Double": {
				double res = Double.parseDouble(value);
				return true;
			}
			case "Integer": {
				int res = Integer.parseInt(value);
				return true;
			}
			case "Timestamp": {return true;}
			case "ID": {return true;}
			case "Collection": {return true;}
			case "List": {return true;}
			case "Container": {return true;}
			default: { return false; }
		} } catch(Error e) {
			return false;
		}
	}
	
	private static boolean valueByCompression() {
		return false;
	}

}
