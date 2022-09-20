/**
 * 
 */
package org.processmining.plugins.petrinet.manifestreplayer.transclassifier;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

/**
 * @author aadrians
 * Feb 19, 2012
 *
 */
public class DefTransClassifier implements ITransClassifier{

	public String getClassIdentity(Transition transition) {
		return transition.getLabel();
	}
	
	public String toString(){
		return "Label-based Classifier";
	}
}
