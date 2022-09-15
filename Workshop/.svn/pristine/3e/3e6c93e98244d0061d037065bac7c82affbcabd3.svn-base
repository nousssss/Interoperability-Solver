package org.processmining.plugins.workshop.Tatiana;

import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;

public class Parameters {
	//new XEventAndClassifier (new XEventNameClassifier(), new XEventLifeTransClassifier())
	
	private XEventClassifier xeclassifier;

	public Parameters() {
		xeclassifier = new XEventAndClassifier(new XEventNameClassifier(), new XEventLifeTransClassifier());
	}

	public void setCs(XEventClassifier classifier) {
		if (classifier != null) {
			this.xeclassifier = classifier;
		}
	}

	public XEventClassifier getCs() {
		return xeclassifier;
	}


	public boolean equals(Object object) {
		if (object instanceof Parameters) {
			Parameters parameters = (Parameters) object;
			if (xeclassifier.equals(parameters.xeclassifier)) {
				return true;
			}
		}
		return false;
	}
	
	public int hashCode() {
		return xeclassifier.hashCode();
	}
}
