/**
 * 
 */
package org.processmining.plugins.connectionfactories.logpetrinet;

import java.util.HashMap;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

/**
 * This class stores mapping between transitions and event classes
 * @author aadrians
 *
 */
public class TransEvClassMapping extends HashMap<Transition, XEventClass>{
	private static final long serialVersionUID = -4344051692440782096L;
	private XEventClassifier eventClassifier;
	private XEventClass dummyEventClass;
	
	@SuppressWarnings("unused")
	private TransEvClassMapping(){}; // this constructor is not allowed
	
	/**
	 * Allowed constructor
	 * @param eventClassifier
	 * @param dummyEventClass
	 */
	public TransEvClassMapping(XEventClassifier eventClassifier, XEventClass dummyEventClass){
		this.eventClassifier = eventClassifier;
		this.dummyEventClass = dummyEventClass;
	}
	
	/**
	 * get the classifier
	 * @return
	 */
	public XEventClassifier getEventClassifier(){
		return this.eventClassifier;
	}
	
	/**
	 * Get event class that is used to represent transition (not invisible ones) that is not mapped to 
	 * any activity
	 * 
	 * @return
	 */
	public XEventClass getDummyEventClass(){
		return this.dummyEventClass;
	}
}
