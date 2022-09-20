package org.processmining.models.workshop.fbetancor.dataqualityaspects.format;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
public class Format implements QualityCheck {
	
	public Output output;

	private String value;

	private int score;

	private double totalScore;

	private List<String> attKeyNames;

	private List<Integer> attKeyScore;

	private String faults;

	private String advice;

	private String table;
	
	SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	public void initialize() {
		table = "";
		table += "" + "<html><table> <tr> <th>Attribute</th> <th>Score</th></tr>";

		totalScore = 0;

		attKeyNames = new ArrayList<String>();
		attKeyScore = new ArrayList<Integer>();

		faults = "";
		advice = "";

		output = new Output("", "", "", faults, advice);
	}

	private void initTimes() {
		value = "";
		score = 0;
	}

	public void checkQuality(XLog eventlog) {
		// TODO Auto-generated method stub

	}

	public void checkQuality(XLog eventlog, XTrace trace) {
		// TODO Auto-generated method stub

	}

	public void checkQuality(XLog eventlog, XTrace trace, XAttribute att)  {
		setScores(att);
	}

	public void checkQuality(XLog eventlog, XTrace trace, XEvent event) {
		// TODO Auto-generated method stub

	}

	public void checkQuality(XLog eventlog, XTrace trace, XEvent event, XAttribute att) {
		setScores(att);
	}

	public void checkClear(CentralRegistryInterface list) {
		setFaults();
		table += "" + "</table>";
		faults = table;
	}

	public Output getResult() {
		output.setName("Timestamp Formats");

		setScore();
		output.setScore("" + totalScore);

		output.setExplanation("The score is an average of all the attributes that are of a timestamp-format.");

		output.setFaults(faults);

		giveAdvice();
		output.setAdvice(advice);
		return output;
	}

	private void giveAdvice() {
		if (totalScore >= 8) {
			advice += "-";
		} else if (totalScore >= 6) {
			advice += "The format of your timestamps is not optimal.\r\n" + 
					"For more accurate analysis results, try to improve these timestamp formats.";
		} else {
			advice += "The formats of your timestamps are very bad. \r\n" + 
					"This influences the results of analysis, since event ordering is not always possible anymore to identify.\r\n" + 
					"So evaluate whether this is intended. If not, try to fix your data.";
		}
	}

	private void setScores(XAttribute att) {
		if (att instanceof XAttributeTimestamp) {
			int currentKeyPosition = 0;

			if (!attKeyNames.contains(att.getKey())) {
				attKeyNames.add(att.getKey());
				attKeyScore.add(0);
				currentKeyPosition = attKeyNames.size() - 1;
			} else {
				currentKeyPosition = attKeyNames.indexOf(att.getKey());
			}

			int currentScore = checkTimestampFormat(att);
			int oldScore = attKeyScore.get(currentKeyPosition);

			if (currentScore > oldScore) {
				attKeyScore.set(currentKeyPosition, currentScore);
			}
		}
	}

	private int checkTimestampFormat(XAttribute att) {
		initTimes();
		value = att.toString();
		try {
			timestamp.parse(value.substring(0, 19));
			score = 10;
			return score;
		}catch (ParseException ignored) {
			return score;
		}		
	}

	private void setScore() {
		double total = 0;
		double avg = 0;

		for (int attScore : attKeyScore) {
			total = total + attScore;
		}

		avg = (total / attKeyScore.size());
		BigDecimal bd = new BigDecimal(avg).setScale(1, RoundingMode.HALF_UP);
		totalScore = bd.doubleValue();
	}

	private void setFaults() {
		for (String attKey : attKeyNames) {
			table += "" + "<tr><td>" + attKey + "</td><td>" + attKeyScore.get(attKeyNames.indexOf(attKey))
					+ "</td></tr>";
		}
	}

}
