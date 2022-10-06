package org.processmining.models.workshop.XixiLu;

import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;

public class XixiModelParameters {
	private XEventClassifier classifier;
	
	public XixiModelParameters() {
		classifier = new XEventAndClassifier(new XEventNameClassifier(), new XEventLifeTransClassifier());
	}
	
	public void setClassifier(XEventClassifier c){
		if (this.classifier != null) {
			this.classifier = c;
		}
	}
	
	public XEventClassifier getClassifier() {
		return classifier;
	}
	
	public boolean equals(Object object) {
		if (object instanceof XixiModelParameters) {
			XixiModelParameters parameters = (XixiModelParameters) object;
			if (classifier.equals(parameters.classifier)) {
				return true;
			}
		}
		return false;
	}
	
	public int hashCode() {
		return classifier.hashCode();
	}
	

}
