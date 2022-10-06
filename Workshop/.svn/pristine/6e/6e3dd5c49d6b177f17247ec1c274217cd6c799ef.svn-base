package org.processmining.plugins.workshop.JoSwinnen;

import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;

public class Parameters {
	
	private XEventClassifier classifier;

	public Parameters() {
		classifier = new XEventAndClassifier(new XEventNameClassifier(), new XEventLifeTransClassifier());
	}
	
	public void setClassifier(XEventClassifier classifier) {
		if (classifier != null) {
			this.classifier = classifier;
		}
	}
	
	public XEventClassifier getClassifier() {
		return classifier;
	}

	public boolean equals(Object object) {
		if (object instanceof Parameters) {
			Parameters parameters = (Parameters) object;
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




