package org.processmining.plugins.workshop.spoilers;

import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;

/**
 * Parameters for the mining of a workshop model from an event log.
 * 
 * @author hverbeek
 * 
 */
public class WorkshopMiningParameters {

	/**
	 * Classifier parameter. This determines which classifier will be used
	 * during the mining.
	 */
	private XEventClassifier classifier;

	/**
	 * Create default parameter values.
	 */
	public WorkshopMiningParameters() {
		classifier = new XEventAndClassifier(new XEventNameClassifier(), new XEventLifeTransClassifier());
	}

	/**
	 * Set the classifier to the given classifier.
	 * 
	 * @param classifier
	 *            The given classifier.
	 */
	public void setClassifier(XEventClassifier classifier) {
		if (classifier != null) {
			this.classifier = classifier;
		}
	}

	/**
	 * Gets the classifier.
	 * 
	 * @return The classifier.
	 */
	public XEventClassifier getClassifier() {
		return classifier;
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
		if (object instanceof WorkshopMiningParameters) {
			WorkshopMiningParameters parameters = (WorkshopMiningParameters) object;
			if (classifier.equals(parameters.classifier)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the hash code for these parameters.
	 */
	public int hashCode() {
		return classifier.hashCode();
	}
}
