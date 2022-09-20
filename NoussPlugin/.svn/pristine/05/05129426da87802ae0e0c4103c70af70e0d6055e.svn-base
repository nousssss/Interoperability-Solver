package org.processmining.models.workshop.klunnel;

public class KlunnelConversionParameters {
	private int minCardinality;
	
	public KlunnelConversionParameters() {
		minCardinality = 1;
	}

	/**
	 * @return the minCardinality
	 */
	public int getMinCardinality() {
		return minCardinality;
	}

	/**
	 * @param minCardinality the minCardinality to set
	 */
	public void setMinCardinality(int minCardinality) {
		//Negative is a problem.
		this.minCardinality = minCardinality;
	}

	public boolean equals(Object object) {
		if (object instanceof KlunnelConversionParameters) {
			KlunnelConversionParameters parameters = (KlunnelConversionParameters) object;
			return (minCardinality == parameters.minCardinality);
		}
		return false;
	}

	public int hashCode() {
		return minCardinality;
	}
}
