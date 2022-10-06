package org.processmining.models.workshop.fbetancor.dataqualityaspects;

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
 * This class implements the quality check for the quality dimension:
 * Consistency.
 * 
 * @author R. Verhulst
 *
 */
public class Consistency implements QualityCheck {
	/**
	 * Output for the consistency check.
	 */
	public Output output;

	/**
	 * Keeps track of number of elements in the input
	 */
	private int totals;

	/**
	 * Notable findings for the consistency check.
	 */
	private String faults;

	/**
	 * Advice related to the consistency check.
	 */
	private String advice;

	/**
	 * Score for the consistency check.
	 */
	private int score;

	/**
	 * Output table for a structured overview. HTML code will be placed in here.
	 */
	private String table;

	/**
	 * Initializes all the variables.
	 */
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

	/**
	 * Check consistency on XLog level.
	 */
	public void checkQuality(XLog eventlog) {
		// TODO Auto-generated method stub

	}

	/**
	 * Check consistency on XTrace level.
	 */
	public void checkQuality(XLog eventlog, XTrace trace) {
		// TODO Auto-generated method stub

	}

	/**
	 * Check consistency on Trace-Attribute level.
	 */
	public void checkQuality(XLog eventlog, XTrace trace, XAttribute att) {
		// TODO Auto-generated method stub

	}

	/**
	 * Check consistency on XEvent level.
	 */
	public void checkQuality(XLog eventlog, XTrace trace, XEvent event) {
		// TODO Auto-generated method stub

	}

	/**
	 * Check consistency on Event-Attribute level.
	 */
	public void checkQuality(XLog eventlog, XTrace trace, XEvent event, XAttribute att) {
		// TODO Auto-generated method stub

	}

	/**
	 * Check consistency with the use of a specified list.
	 */
	public void checkClear(CentralRegistryInterface list) {
		List<ArrayList<String>> attList = list.getAttributeList();
		for (int i = 0; i < attList.size(); i++) {
			calculate(attList.get(i));
		}
		totals = attList.size();
		table += "" + "</table>";
		faults = table;
	}

	/**
	 * Set the result of the consistency check.
	 */
	public Output getResult() {
		output.setName("Consistency Check");
		output.setScore("" + (double) (score / totals));
		output.setExplanation("For each attribute, the score is calculated in terms of looking at the length of "
				+ "the strings and if the string "
				+ "consists of only digits, only characters or only a mix of these. <br>"
				+ "The following score system is used: <br> " + "<ul>"
				+ "<li> 2 - Inconsistency in length together with a mix of only string, "
				+ "only digit and string/digit values. </li>"
				+ "<li> 4 - Inconsistency in length together with a mix of two out "
				+ "of three possible composition possibilities. </li>"
				+ "<li> 6 - Consistency in length together with a mix of only string, "
				+ "only digit and string/digit values. </li>"
				+ "<li> 8 - Inconsistency in length together with only one specific composition. </li>"
				+ "<li> 8 - Consistency in length together with a mix of two out of "
				+ "three possible composition possibilities </li>"
				+ "<li> 10 - Consistency in length together with only one specific composition. </li>" + "</ul>");
		output.setFaults(faults);
		giveAdvice();
		output.setAdvice(advice);
		return output;
	}

	/**
	 * Set the advice for the consistency check.
	 */
	private void giveAdvice() {
		if (score >= 8) {
			advice += "-";
		} else if (score >= 6) {
			advice += "Your data is fairly consistent. <br> "
					+ "Try to see for which attributes the consistency is not scoring high, "
					+ "and evaluate if it is necessary to fix this. It could be due to missing / incorrect values.";
		} else {
			advice += "Your data is inconsistent. <br> " + "Evaluate all the inconsistencies mentioned here, "
					+ "and decide on whether it is necessary to improve the consistency. Inconsistent "
					+ "values could mean missing / incorrect values.";
		}
	}

	/**
	 * Calculate the score and fill in the faults for the consistency check.
	 * 
	 * @param list
	 */
	private void calculate(List<String> list) {
		List<String> values = new ArrayList<String>();
		double longestString = list.get(1).length();
		double shortestString = list.get(1).length();

		int counter = 0;
		int totalLength = 0;
		double average = 0;

		int onlyString = 0;
		int onlyDigits = 0;
		int combinationStringDigit = 0;

		int attScore = 0;

		values.add(list.get(0));

		List<String> copyList = new ArrayList<String>(list);

		copyList.remove(0);

		for (String element : copyList) {
			counter++;
			totalLength = totalLength + element.length();
			average = totalLength / counter;

			if (element.length() > longestString) {
				longestString = element.length();
			} else if (element.length() < shortestString) {
				shortestString = element.length();
			}

			if (isAlphaSpace(element)) {
				onlyString++;
			} else if (isNumericSpace(element)) {
				onlyDigits++;
			} else {
				combinationStringDigit++;
			}
		}

		double temp = 0;

		for (String element2 : list) {
			temp = temp + ((average - element2.length()) * (average - element2.length()));
		}

		double variance = temp / counter;
		double stdDev = Math.sqrt(variance);

		if (onlyString > 0 && onlyDigits > 0 && combinationStringDigit > 0) {
			if (stdDev <= 2) {
				attScore = 6;
			} else {
				attScore = 2;
			}
		} else if (onlyString == 0 || onlyDigits == 0 || combinationStringDigit == 0) {
			if ((onlyString == 0 && onlyDigits == 0) || (onlyString == 0 && combinationStringDigit == 0)
					|| (onlyDigits == 0 && combinationStringDigit == 0)) {
				if (stdDev <= 2) {
					attScore = 10;
				} else {
					attScore = 8;
				}
			} else {
				if (stdDev <= 2) {
					attScore = 8;
				} else {
					attScore = 4;
				}
			}
		}

		score = score + attScore;

		table += "" + "<tr>";
		table += "" + "<td>" + list.get(0) + "</td>";
		table += "" + "<td>" + attScore + "</td>";
		table += "" + "<td> </td>";
		table += "" + "<td>" + list.size() + "</td>";
		table += "" + "<td>" + onlyString + "</td>";
		table += "" + "<td>" + onlyDigits + "</td>";
		table += "" + "<td>" + combinationStringDigit + "</td>";
		table += "" + "<td> </td>";
		table += "" + "<td>" + Double.toString(longestString) + "</td>";
		table += "" + "<td>" + Double.toString(shortestString) + "</td>";
		table += "" + "<td>" + Double.toString(average) + "</td>";
		table += "" + "<td>" + Double.toString(variance) + "</td>";
		table += "" + "<td>" + Double.toString(stdDev) + "</td>";
		table += "" + "</tr>";
	}

	/**
	 * Check whether the string is made of only characters.
	 * 
	 * @param str
	 * @return true if only chars, false if not.
	 */
	public static boolean isAlphaSpace(String str) {
		if (str == null) {
			return false;
		}

		int sz = str.length();

		for (int i = 0; i < sz; i++) {
			if ((Character.isLetter(str.charAt(i)) == false) && (str.charAt(i) != ' ') && str.charAt(i) != '-'
					&& str.charAt(i) != ':' && str.charAt(i) != '.' && str.charAt(i) != ';' && str.charAt(i) != '<'
					&& str.charAt(i) != '>' && str.charAt(i) != '(' && str.charAt(i) != ')' && str.charAt(i) != '@'
					&& str.charAt(i) != '!') {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check whether the string only consists of numeric values.
	 * 
	 * @param str
	 * @return true if only numeric values, false if not.
	 */
	public static boolean isNumericSpace(String str) {
		if (str == null) {
			return false;
		}

		int sz = str.length();

		for (int i = 0; i < sz; i++) {
			/*
			 * Check for Timestamps aswell which contains +, -, T, :
			 */
			if ((Character.isDigit(str.charAt(i)) == false) && (str.charAt(i) != ' ') && (str.charAt(i) != '-')
					&& str.charAt(i) != ':' && str.charAt(i) != '+' && str.charAt(i) != 'T') {
				return false;
			}
		}
		return true;
	}
}
