package org.processmining.plugins.ghzbue;

/**
 * configuration Parameters for the conversion of a ghz model to a ghz graph.
 * 
 * @author Ghzzz
 * 
 */
public class GhzConversionConfiguration {
	/**
	 * Minimal cardinality parameter: If the cardinality of an edge is below
	 * this threshold, it will be omitted from the graph.
	 */
	private int minCardinality;

	/**
	 * Create default parameter values.
	 */
	public GhzConversionConfiguration() {
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

	public int hashCode() {
		return minCardinality;
	}

	public boolean equals(Object obj) {
		if (obj instanceof GhzConversionConfiguration) {
			GhzConversionConfiguration parameters = (GhzConversionConfiguration) obj;
			if (minCardinality == parameters.minCardinality) {
				return true;
			}
		}
		return false;
	}

}
