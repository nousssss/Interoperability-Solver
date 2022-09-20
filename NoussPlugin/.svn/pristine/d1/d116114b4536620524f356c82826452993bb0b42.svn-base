package org.processmining.plugins.workshop.sebas;

import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;

public class SebasModelParameters {
	/**
	 * Classifier parameter. Determines which classifier will be used during the mining.
	 */
	private XEventClassifier classifier;
	
	/**
	 * Create default parameter values
	 */
	public SebasModelParameters() {
		classifier = new XEventAndClassifier(new XEventNameClassifier(), new XEventLifeTransClassifier());
	}
	
	/**
	 * Classifier setter
	 */
	public void setClassifier(XEventClassifier classifier){
		if (classifier != null){
			this.classifier = classifier;
		}
	}
	
	/**
	 * Classifier getter
	 */
	public XEventClassifier getClassifier(){
		return classifier;
	}
	
	/**
	 * Returns whether these parameter values are equal to the given parameter values
	 */
	public boolean equals (Object object){
		if (object instanceof SebasModelParameters){
			SebasModelParameters parameters = (SebasModelParameters) object;
			if (classifier.equals(parameters.classifier)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the hash code for these parameters
	 */
	public int hashCode(){
		return classifier.hashCode();
	}
	
}
