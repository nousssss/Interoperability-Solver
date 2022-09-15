/**
 * 
 */
package org.processmining.plugins.multietc.automaton;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * @author jmunoz
 *
 */
public abstract class AutomatonNode {
	
	public static final String WEIGHT = "weight";
	public static final String MARKING = "marking";
	public static final String AVAIL_TASKS = "available_tasks";
	public static final String ESCAPING_TASKS = "escaping_tasks";
	
	public static final String NUM_AVAIL_TASKS = "num_available_tasks";
	public static final String NUM_ESCAPING_TASKS = "num_escaping_tasks";
	public static final String NUM_NON_ESCAPING_TASKS = "num_non_escaping_tasks";

	private Map<Object,Object> attr;
	
	
	protected AutomatonNode(){
		attr = new HashMap<Object,Object>();

		//Default attributes
		putAttribute(WEIGHT, 0.0);
	}
	
 
	public Object putAttribute(Object key, Object value){
		return attr.put(key, value);
	}
	
	public Object getAttribute(Object key){
		return attr.get(key);
	}
	
	public double getWeight(){
		return (Double) attr.get(WEIGHT);
	}
	
	public Object setWeight(double w){
		return putAttribute(WEIGHT, w);
	}
	
	public Marking getMarking(){
		return (Marking) attr.get(MARKING);
	}
	
	public Object setMarking(Marking m){
		return  putAttribute(MARKING, m);
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Transition> getAvailableTasks(){
		return (Collection<Transition>) attr.get(AVAIL_TASKS);
	}
	
	public Object setAvailableTasks(Collection<Transition> a){
		return putAttribute(AVAIL_TASKS, a);
	}
	
	/**
	 * Compute and set the marking of the node, considering the provided net and the provided initial marking.
	 * The marking set refers to elements in the original Petri net (not the one provided).
	 * @param net Net used to compute the marking (it could not be the original net)
	 * @param iniM Initial marking for the provided net (could not be the original initial marking)
	 * @param orig2new Map between Petrinet nodes of the original net to the one provived
	 * @param new2orig Map between Petrinet nodes in the provided net to the original one.
	 */
	public abstract void computeMarking(Petrinet net, Marking iniM, Map<PetrinetNode, PetrinetNode> orig2new, Map<PetrinetNode, PetrinetNode> new2orig);
	

}
