package org.processmining.models.workshop.fbetancor.constructors;

public class Output {
	/**
	 * Name for the quality check output-element.
	 */
	private String Name;

	/**
	 * Score for the quality check output-element.
	 */
	private String Score;

	/**
	 * Explanation for the score of the quality check output-element.
	 */
	private String Explanation;

	/**
	 * Faults for the quality check output-element.
	 */
	private String Faults;

	/**
	 * Advice for the quality check output-element.
	 */
	private String Advice;

	/**
	 * Constructor for the IOutput.
	 * 
	 * @param name
	 * @param score
	 * @param explanation
	 * @param faults
	 * @param advice
	 */
	public Output(String name, String score, String explanation, String faults, String advice) {
		Name = name;
		Score = score;
		Explanation = explanation;
		Faults = faults;
		Advice = advice;
	}

	/**
	 * Set the name for the output-element.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		Name = name;
	}

	/**
	 * Set the score for the output-element.
	 * 
	 * @param score
	 */
	public void setScore(String score) {
		Score = score;
	}

	/**
	 * Set the explanation for the output-element.
	 * 
	 * @param explanation
	 */
	public void setExplanation(String explanation) {
		Explanation = explanation;
	}

	/**
	 * Set the faults for the output-element.
	 * 
	 * @param faults
	 */
	public void setFaults(String faults) {
		Faults = faults;
	}

	/**
	 * Set the advice for the output-element.
	 * 
	 * @param advice
	 */
	public void setAdvice(String advice) {
		Advice = advice;
	}

	/**
	 * Get method for the name of the output-element.
	 * 
	 * @return
	 */
	public String getName() {
		return Name;
	}

	/**
	 * Get method for the score of the output-element.
	 * 
	 * @return
	 */
	public String getScore() {
		return Score;
	}

	/**
	 * Get method for the explanation of the output-element.
	 * 
	 * @return
	 */
	public String getExplanation() {
		return Explanation;
	}

	/**
	 * Get method for the faults of the output-element.
	 * 
	 * @return
	 */
	public String getFaults() {
		return Faults;
	}

	/**
	 * Get method for the advice of the output-element.
	 * 
	 * @return
	 */
	public String getAdvice() {
		return Advice;
	}

}