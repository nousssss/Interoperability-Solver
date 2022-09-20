package org.processmining.plugins.astar.petrinet;

import java.util.Set;

import nl.tue.astar.AStarException;

import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.impl.PILPDelegate;
import org.processmining.plugins.astar.petrinet.impl.PILPTail;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

import com.google.common.collect.ImmutableSet;

// dont show in UI
// @KeepInProMCache
// @PNReplayAlgorithm
/**
 * 
 * @author F. Mannhardt
 * 
 */
public class PetrinetReplayerMovePreferenceAwareWithILP extends
		AbstractMovePreferenceAwarePetrinetReplayer<PILPTail, PILPDelegate> {

	private Set<Transition> preferredTransitions = ImmutableSet.<Transition>of();

	/**
	 * Return true if all replay inputs are correct
	 */
	public boolean isAllReqSatisfied(PluginContext context, PetrinetGraph net, XLog log, TransEvClassMapping mapping,
			IPNReplayParameter parameter) {
		if (super.isAllReqSatisfied(context, net, log, mapping, parameter)) {
			Marking[] finalMarking = ((CostBasedCompleteParam) parameter).getFinalMarkings();
			if ((finalMarking != null) && (finalMarking.length > 0)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return true if input of replay without parameters are correct
	 */
	public boolean isReqWOParameterSatisfied(PluginContext context, PetrinetGraph net, XLog log,
			TransEvClassMapping mapping) {
		return super.isReqWOParameterSatisfied(context, net, log, mapping);
	}

	public String toString() {
		return "A* Cost-based Fitness Express with ILP, assuming at most " + Short.MAX_VALUE + " tokens in each place.";
	}

	protected PILPDelegate getDelegate(PetrinetGraph net, XLog log, XEventClasses classes, TransEvClassMapping mapping,
			int delta, int threads) {
		if (net instanceof ResetInhibitorNet) {
			return new PILPDelegate((ResetInhibitorNet) net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost,
					mapSync2Cost, delta, threads, true, true, finalMarkings);
		} else if (net instanceof ResetNet) {
			return new PILPDelegate((ResetNet) net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost,
					mapSync2Cost, delta, threads, true, true, finalMarkings);
		} else if (net instanceof InhibitorNet) {
			return new PILPDelegate((InhibitorNet) net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost,
					mapSync2Cost, delta, threads, true, true, finalMarkings);
		} else if (net instanceof Petrinet) {
			return new PILPDelegate((Petrinet) net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost,
					mapSync2Cost, delta, threads, true, true, finalMarkings);
		}

		return null;
	}

	public PNRepResult replayLog(PluginContext context, PetrinetGraph net, XLog log, TransEvClassMapping mapping,
			IPNReplayParameter parameter) throws AStarException {
		return super.replayLog(context, net, log, mapping, parameter, preferredTransitions);
	}

	public void setPreferredTransitions(Set<Transition> preferredTransitions) {
		this.preferredTransitions = preferredTransitions;
	}

}