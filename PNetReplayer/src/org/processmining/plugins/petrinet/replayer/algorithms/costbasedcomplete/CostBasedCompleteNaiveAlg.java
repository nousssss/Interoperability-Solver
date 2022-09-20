/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.framework.plugin.Progress;
import org.processmining.plugins.petrinet.replayer.util.codec.EncPNWSetFinalMarkings;
import org.processmining.plugins.petrinet.replayer.util.statespaces.CPNCostBasedTreeNodeEncFitness;
import org.processmining.plugins.petrinet.replayresult.StepTypes;

/**
 * @author aadrians
 * Oct 22, 2011
 *
 */
public class CostBasedCompleteNaiveAlg extends AbstractCostBasedCompleteNaiveAlg {
	public String toString() {
		return "Best-First Search Cost-based Fitness";
	}

	public String getHTMLInfo() {
		return "<html>This is an algorithm to calculate cost-based fitness between a log and a Petri net. <br/><br/>"
				+ "Given a trace and a Petri net (can also be reset/inhibitor net), this algorithm "
				+ "return a matching between the trace and an allowed firing sequence of the net with the "
				+ "least deviation cost using the Best-First Search algorithm technique. The firing sequence has "
				+ "to reach proper termination (possible final markings/dead markings) of the net. " + "<br/>"
				+ "<br/> Cost for skipping (move on model) and inserting (move on log) "
				+ "activities can be assigned uniquely for each move on model/log. </html>";
	}

	/**
	 * Replay loop
	 * 
	 * @param lstEvtClass
	 * @param encodedPN
	 * @param encInvisTransition
	 * @param mapArc2Weight
	 * @param mapInt2Marking
	 * @param mapMarking2Int
	 * @param encInitMarking
	 * @param encFinalMarkings
	 * @param numGenerator
	 * @param maxNumOfStates
	 * @param mapEvClass2EncTrans
	 * @param mapEvClass2Cost
	 * @param mapEncTrans2Cost
	 * @param mapMarking2Enabled
	 * @param progress
	 * @return array of objects, contains [0] CPNCostBasedTreeNodeEncFitness
	 *         solution node, and [1] num of states generated
	 */
	public Object[] replayLoop(List<XEventClass> lstEvtClass, EncPNWSetFinalMarkings encodedPN,
			Set<Integer> encInvisTransition, Map<Integer, Map<Integer, Integer>> mapArc2Weight,
			Map<Integer, Map<Integer, Integer>> mapInt2Marking, Map<Map<Integer, Integer>, Integer> mapMarking2Int,
			Integer encInitMarking, Set<Integer> encFinalMarkings, Random numGenerator, int maxNumOfStates,
			Map<XEventClass, Set<Integer>> mapEvClass2EncTrans, Map<XEventClass, Integer> mapEvClass2Cost,
			Map<Integer, Integer> mapEncTrans2Cost, Map<Integer, SortedSet<Integer>> mapMarking2Enabled,
			Map<Integer, Map<Integer, Integer>> mapFiringTransitions, Progress progress) {
		// control variables
		int lstLength = lstEvtClass.size();
	
		// create tree 
		CPNCostBasedTreeNodeEncFitness stateSpaceRoot = new CPNCostBasedTreeNodeEncFitness(0, encInitMarking, null,
				null, 0, null);
	
		// explore state space
		int stateCounter = 1;
		CPNCostBasedTreeNodeEncFitness currStateSpaceNode = stateSpaceRoot;
	
		PriorityQueue<CPNCostBasedTreeNodeEncFitness> costBasedPNPQ = new PriorityQueue<CPNCostBasedTreeNodeEncFitness>();
	
		while ((currStateSpaceNode != null)
				&& ((!isEndOfModel(currStateSpaceNode.getCurrEncMarking(), encFinalMarkings, encodedPN, mapInt2Marking,
						mapMarking2Enabled, mapArc2Weight)) || (currStateSpaceNode.getCurrIndexOnTrace() < lstLength))
				&& (stateCounter < maxNumOfStates) && (!progress.isCancelled())) {
			if (currStateSpaceNode.getCurrIndexOnTrace() < lstLength) {
				// do move on log
				CPNCostBasedTreeNodeEncFitness mvOnLogState = new CPNCostBasedTreeNodeEncFitness(
						currStateSpaceNode.getCurrIndexOnTrace() + 1, currStateSpaceNode.getCurrEncMarking(),
						StepTypes.L, null, currStateSpaceNode.getCost()
								+ mapEvClass2Cost.get(lstEvtClass.get(currStateSpaceNode.getCurrIndexOnTrace())),
						currStateSpaceNode);
				costBasedPNPQ.add(mvOnLogState);
	
				stateCounter++;
			}
	
			// do move on model
			SortedSet<Integer> enabledTransitions = identifyEnabledTransitions(currStateSpaceNode.getCurrEncMarking(),
					mapInt2Marking, mapMarking2Enabled, encodedPN, mapArc2Weight);
	
			for (Integer trans : enabledTransitions) {
				// execute the enabled transitions and change current marking
	
				Integer newMarkingIndex = fireTransition(currStateSpaceNode.getCurrEncMarking(), trans, mapInt2Marking,
						mapMarking2Int, numGenerator, mapFiringTransitions, encodedPN, mapArc2Weight);
	
				CPNCostBasedTreeNodeEncFitness mvOnModelStateSpace = null;
				if (!encInvisTransition.contains(trans)) {
					// not invisible
					mvOnModelStateSpace = new CPNCostBasedTreeNodeEncFitness(currStateSpaceNode.getCurrIndexOnTrace(),
							newMarkingIndex, StepTypes.MREAL, trans, currStateSpaceNode.getCost()
									+ mapEncTrans2Cost.get(trans), currStateSpaceNode);
				} else {
					// invisible
					mvOnModelStateSpace = new CPNCostBasedTreeNodeEncFitness(currStateSpaceNode.getCurrIndexOnTrace(),
							newMarkingIndex, StepTypes.MINVI, trans, currStateSpaceNode.getCost()
									+ mapEncTrans2Cost.get(trans), currStateSpaceNode);
				}
	
				costBasedPNPQ.add(mvOnModelStateSpace);
	
				stateCounter++;
	
				// check for move synchronously
				if ((currStateSpaceNode.getCurrIndexOnTrace() < lstLength)
						&& (mapEvClass2EncTrans.get(lstEvtClass.get(currStateSpaceNode.getCurrIndexOnTrace()))
								.contains(trans))) {
					CPNCostBasedTreeNodeEncFitness mvSynchronous = new CPNCostBasedTreeNodeEncFitness(
							currStateSpaceNode.getCurrIndexOnTrace() + 1, newMarkingIndex, StepTypes.LMGOOD, trans,
							currStateSpaceNode.getCost(), currStateSpaceNode);
					costBasedPNPQ.add(mvSynchronous);
					stateCounter++;
				}
			}
	
			currStateSpaceNode = costBasedPNPQ.poll();
		}
	
		return new Object[] { currStateSpaceNode, stateCounter };
	}

}
