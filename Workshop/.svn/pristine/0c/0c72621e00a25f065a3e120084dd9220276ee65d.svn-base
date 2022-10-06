package org.processmining.models.workshop.fbetancor.plugins;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Takes all the scores from the different quality checks and calculates an
 * overall score.
 * 
 * @author R. Verhulst
 *
 */
public class ScoreCalculator {

	/**
	 * Calculates the overall score.
	 * 
	 * @param scoreList
	 * @return overallScore
	 */
	public String getTotalScore(List<String> scoreList) {
		double totalScore = 0;
		int actualCounter = 0;
		for (String element : scoreList) {
			double currentScore = Double.parseDouble(element);

			if (!element.equals("0.0")) {
				totalScore = totalScore + currentScore;
				actualCounter++;
			}

			if (currentScore >= 8.0) {

			} else if (currentScore >= 6.0 && currentScore < 8.0) {

			} else {

			}
		}

		return "" + round(totalScore / actualCounter, 1);
	}

	/**
	 * Method that rounds up the value of the score up to [@param places] behind
	 * the comma.
	 * 
	 * @param value
	 * @param places
	 * @return rounded value
	 */
	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
}
