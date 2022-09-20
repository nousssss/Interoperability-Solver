/**
 * 
 */
package org.processmining.plugins.astar.petrinet.manifestreplay;

import java.util.Map;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;

/**
 * @author aadrians
 * Feb 21, 2012
 *
 */
public class CostBasedCompleteManifestParam extends CostBasedCompleteParam {
	/**
	 * Attribute
	 */
	private Set<Transition> restrictedTrans;
	
	/**
	 * Default constructor, assuming that the cost of move sync is 0
	 * 
	 * @param mapEvClass2Cost
	 * @param mapTrans2Cost
	 * @param initMarking
	 * @param finalMarkings
	 * @param maxNumOfStates
	 * @param moveModelTrans
	 * @param restrictedTrans
	 */
	public CostBasedCompleteManifestParam(Map<XEventClass, Integer> mapEvClass2Cost,
			Map<Transition, Integer> mapTrans2Cost, Marking initMarking, Marking[] finalMarkings, int maxNumOfStates,
			Set<Transition> restrictedTrans) {
		super(mapEvClass2Cost, mapTrans2Cost);
		setInitialMarking(initMarking);
		setFinalMarkings(finalMarkings);
		setMaxNumOfStates(maxNumOfStates);
		this.restrictedTrans = restrictedTrans;
	}
	
	/**
	 * Constructor with mapping from sync moves to cost
	 * 
	 * @param mapEvClass2Cost
	 * @param mapTrans2Cost
	 * @param initMarking
	 * @param finalMarkings
	 * @param maxNumOfStates
	 * @param moveModelTrans
	 * @param fragmentTrans
	 */
	public CostBasedCompleteManifestParam(Map<XEventClass, Integer> mapEvClass2Cost,
			Map<Transition, Integer> mapTrans2Cost, Map<Transition, Integer> mapSync2Cost, Marking initMarking, Marking[] finalMarkings, int maxNumOfStates,
			Set<Transition> fragmentTrans) {
		super(mapEvClass2Cost, mapTrans2Cost);
		setMapSync2Cost(mapSync2Cost);
		setInitialMarking(initMarking);
		setFinalMarkings(finalMarkings);
		setMaxNumOfStates(maxNumOfStates);
		this.restrictedTrans = fragmentTrans;
	}

	/**
	 * @return the notMoveModelTransitions
	 */
	public Set<Transition> getRestrictedTrans() {
		return restrictedTrans;
	}

	/**
	 * @param restrictedTrans the restrictedTrans to set
	 */
	public void setRestrictedTrans(Set<Transition> restrictedTrans) {
		this.restrictedTrans = restrictedTrans;
	}

	
	
		
}
