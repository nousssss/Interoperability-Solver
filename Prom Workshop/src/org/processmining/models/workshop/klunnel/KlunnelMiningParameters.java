package org.processmining.models.workshop.klunnel;

import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;

/**
 * Parameters for the mining of a workshop model from an event log.
 * 
 * @author M.D. Brunings
 *
 */
public class KlunnelMiningParameters {

	/**
	 * Classifier parameter. This determines which classifier will be used during
	 * the mining.
	 */
	private XEventClassifier classifier;

	/**
	 * @return the classifier
	 */
	public XEventClassifier getClassifier() {
		return classifier;
	}

	/**
	 * @param classifier
	 *            the classifier to set
	 */
	public void setClassifier(XEventClassifier classifier) throws IllegalArgumentException {
		if (classifier == null) {
			throw new IllegalArgumentException(this.getClass().getName() + ".setClassifier was given a null argument.");
		}
		this.classifier = classifier;
	}

	/**
	 * Create default parameter values.
	 */
	public KlunnelMiningParameters() {
		classifier = new XEventAndClassifier(new XEventNameClassifier(), new XEventLifeTransClassifier());
	}

	public boolean equals(Object object) {
		if (object instanceof KlunnelMiningParameters) {
			KlunnelMiningParameters parameters = (KlunnelMiningParameters) object;
			return classifier.equals(parameters.classifier);
		}
		return false;
	}

	public int hashCode() {
		return classifier.hashCode();
	}

}
