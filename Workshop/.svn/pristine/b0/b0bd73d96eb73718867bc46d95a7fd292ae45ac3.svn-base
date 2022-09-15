package org.processmining.plugins.workshop.spoilers;

/**
 * Parameters for the conversion of a workshop model to a workshop graph.
 * 
 * @author hverbeek
 * 
 */
public class WorkshopConversionParameters {

	/**
	 * Minimal cardinality parameter: If the cardinality of an edge is below
	 * this threshold, it will be omitted from the graph.
	 */
	private int minCardinality;

	/**
	 * Create default parameter values.
	 */
	public WorkshopConversionParameters() {
		this.minCardinality = 1;
	}

	/**
	 * Sets the minimal cardinality to the given cardinality (max'ed with 1, as
	 * values below 1 do not make sense here).
	 * 
	 * @param minCardinality
	 *            The given cardinality.
	 */
	public void setMinCardinality(int minCardinality) {
		this.minCardinality = (minCardinality < 1 ? 1 : minCardinality);
	}

	/**
	 * Gets the minimal cardinality.
	 * 
	 * @return The minimal cardinality.
	 */
	public int getMinCardinality() {
		return minCardinality;
	}

	/**
	 * Returns whether these parameter values are equal to the given parameter
	 * values.
	 * 
	 * @param object
	 *            The given parameter values.
	 * @return Whether these parameter values are equal to the given parameter
	 *         values.
	 */
	public boolean equals(Object object) {
		if (object instanceof WorkshopConversionParameters) {
			WorkshopConversionParameters parameters = (WorkshopConversionParameters) object;
			if (minCardinality == parameters.minCardinality) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the hash code for these parameters.
	 */
	public int hashCode() {
		return minCardinality;
	}
}
