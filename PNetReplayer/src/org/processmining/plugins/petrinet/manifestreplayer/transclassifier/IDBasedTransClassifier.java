/**
 * 
 */
package org.processmining.plugins.petrinet.manifestreplayer.transclassifier;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

/**
 * @author aadrians
 * Feb 29, 2012
 *
 */
public class IDBasedTransClassifier implements ITransClassifier{

	public String getClassIdentity(Transition transition) {
		return transition.getId().toString();
	}
	
	public String toString(){
		return "ID-based Classifier";
	}
}