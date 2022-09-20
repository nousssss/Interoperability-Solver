/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.swapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;

/**
 * @author aadrians Sep 12, 2012
 * 
 */
public class CostBasedSwapParam extends CostBasedCompleteParam {
	// map from the swapped event class to event classes it is swapped with
	private Map<XEventClass, List<Pair<XEventClass, Integer>>> swapCost;

	// map from the replaced ev class to event classes it is replaced with
	private Map<XEventClass, List<Pair<XEventClass, Integer>>> replacementCost;

	public CostBasedSwapParam(Map<XEventClass, Integer> mapEvClass2Cost, Map<Transition, Integer> mapTrans2Cost,
			Map<Transition, Integer> mapSync2Cost, Marking initMarking, Marking[] finalMarkings, int maxNumOfStates,
			Map<XEventClass, List<Pair<XEventClass, Integer>>> swapCost,
			Map<XEventClass, List<Pair<XEventClass, Integer>>> replacementCost) {
		super(mapEvClass2Cost, mapTrans2Cost, mapSync2Cost);
		setInitialMarking(initMarking);
		setFinalMarkings(finalMarkings);
		setMaxNumOfStates(maxNumOfStates);

		// unique thing about this param: cost of swapping and replacing activities
		this.swapCost = swapCost == null ? new HashMap<XEventClass, List<Pair<XEventClass, Integer>>>(0) : swapCost;
		this.replacementCost = replacementCost == null ? new HashMap<XEventClass, List<Pair<XEventClass, Integer>>>(0)
				: replacementCost;
	}

	/**
	 * return cost of swapping one event class with another
	 * 
	 * @param ec
	 * @return
	 */
	public List<Pair<XEventClass, Integer>> getSwapCostFor(XEventClass ec) {
		return swapCost.get(ec);
	}

	/**
	 * return cost of replacing one event class with another (one occurrence
	 * replaced by other)
	 * 
	 * @param ec
	 * @return
	 */
	public List<Pair<XEventClass, Integer>> getReplacementCostFor(XEventClass ec) {
		return replacementCost.get(ec);
	}

	/**
	 * Get cost of swapping mapping
	 * 
	 * @return
	 */
	public Map<XEventClass, List<Pair<XEventClass, Integer>>> getSwapCostMap() {
		return swapCost;
	}

	/**
	 * Get replacement cost mapping
	 * 
	 * @return
	 */
	public Map<XEventClass, List<Pair<XEventClass, Integer>>> getReplacementCostMap() {
		return replacementCost;
	};
}
