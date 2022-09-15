package org.processmining.plugins.workshop.nouss;

import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;

/* 
   Paramètres pour l'extraction d'un modèle à partir d'un journal d'événements.
 */

public class MiningParameters {

	// Paramètre du classificateur. Il détermine quel classificateur sera utilisé pendant l'extraction.
	private XEventClassifier classifier;
	
	public MiningParameters() {
		 this.classifier = new XEventAndClassifier(new XEventNameClassifier(), new XEventLifeTransClassifier()); 
		
	}
	
	// le getter
	public XEventClassifier getClassifier() {
		return this.classifier;
	}
	
	//le setter
	public void setClassifier(XEventClassifier classifier) {
		if (classifier != null) {
			this.classifier = classifier;
		}
	}
	

	
	public boolean equals(Object obj) {
		if (obj instanceof MiningParameters == true) {
			MiningParameters param = (MiningParameters) obj; //faire un cast
			if (this.classifier.equals(param.getClassifier())) {
				return true;
			}
		}
		return false;
	}
	
	public int hashCode() {
		return this.classifier.hashCode();
	}
	
	
	
}
