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
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.rpstwrapper.RPSTConsultant;
import org.processmining.plugins.petrinet.replayer.util.codec.EncPNWSetFinalMarkings;
import org.processmining.plugins.petrinet.replayer.util.statespaces.CPNCostBasedTreeNodeEncFitnessWHeurCost;
import org.processmining.plugins.petrinet.replayresult.StepTypes;

/**
 * @author aadrians
 * Oct 24, 2011
 *
 */
public class CostBasedCompleteRPST extends AbstractCostBasedCompleteRPSTAlg {

	public String toString() {
		return "Best-First Search Cost-based Fitness (with RPST analysis optimization)";
	}
	
	public String getHTMLInfo() {
		return "<html>This is an algorithm to calculate cost-based fitness between a log and a Petri net. <br/><br/>"
				+ "Given a trace and a Petri net (can also be reset/inhibitor net), this algorithm "
				+ "return a matching between the trace and an allowed firing sequence of the net with the"
				+ "least deviation cost using the Best-First Search technique. The firing sequence has to reach proper "
				+ "termination (possible final markings/dead markings) of the net. <br/><br/>"
				+ "To minimize the number of explored state spaces, the algorithm uses RPST to "
				+ "calculate which transition needs to fire to reach proper termination. <br/><br/>"
				+ "Cost for skipping (move on model) and inserting (move on log) "
				+ "activities can be assigned uniquely for each move on model/log. <br/><br/> "
				+ "RPST calculation is performed using the jbpt library version 0.2.77 "
				+ "(see <a href='http://code.google.com/p/jbpt/'>http://code.google.com/p/jbpt/</a>). "
				+ "Since the library require a net with a single entry and a single exit node, only nets that satisfy "
				+ "such requirements are supported. </html>";
	}

	public Object[] replayLoop(List<XEventClass> lstEvtClass, EncPNWSetFinalMarkings encodedPN,
			Set<Integer> encInvisTransition, Map<Integer, Map<Integer, Integer>> mapArc2Weight,
			Map<Integer, Map<Integer, Integer>> mapInt2Marking, Map<Map<Integer, Integer>, Integer> mapMarking2Int,
			Integer encInitMarking, Set<Integer> encFinalMarkings, Random numGenerator, int maxNumOfStates,
			Map<XEventClass, Set<Integer>> mapEvClass2EncTrans, Map<XEventClass, Integer> mapEvClass2Cost,
			Map<Integer, Integer> mapEncTrans2Cost, Map<Integer, SortedSet<Integer>> mapMarking2Enabled,
			Map<Integer, Map<Integer, Integer>> mapFiringTransitions, RPSTConsultant costConsultant,
			Map<Integer, XEventClass> mapEncTrans2EvClass, Progress progress) {
		// control variables
		int lstLength = lstEvtClass.size();

		// create tree 
		CPNCostBasedTreeNodeEncFitnessWHeurCost stateSpaceRoot = new CPNCostBasedTreeNodeEncFitnessWHeurCost(0,
				encInitMarking, null, null, 0, estimateCost(lstEvtClass, mapEvClass2EncTrans, 0, lstLength,
						costConsultant, encInitMarking, mapInt2Marking, encodedPN, mapEncTrans2EvClass), null);

		// explore state space
		int stateCounter = 1;
		CPNCostBasedTreeNodeEncFitnessWHeurCost currStateSpaceNode = stateSpaceRoot;

		PriorityQueue<CPNCostBasedTreeNodeEncFitnessWHeurCost> costBasedPNPQ = new PriorityQueue<CPNCostBasedTreeNodeEncFitnessWHeurCost>();

		while ((currStateSpaceNode != null)
				&& ((!isEndOfModel(currStateSpaceNode.getCurrEncMarking(), encFinalMarkings, encodedPN, mapInt2Marking,
						mapMarking2Enabled, mapArc2Weight)) || (currStateSpaceNode.getCurrIndexOnTrace() < lstLength))
				&& (stateCounter < maxNumOfStates) && (!progress.isCancelled())) {
			if (currStateSpaceNode.getCurrIndexOnTrace() < lstLength) {
				// do move on log
				CPNCostBasedTreeNodeEncFitnessWHeurCost mvOnLogState = new CPNCostBasedTreeNodeEncFitnessWHeurCost(
						currStateSpaceNode.getCurrIndexOnTrace() + 1, currStateSpaceNode.getCurrEncMarking(),
						StepTypes.L, null, currStateSpaceNode.getCost()
								+ mapEvClass2Cost.get(lstEvtClass.get(currStateSpaceNode.getCurrIndexOnTrace())),
						estimateCost(lstEvtClass, mapEvClass2EncTrans, currStateSpaceNode.getCurrIndexOnTrace() + 1,
								lstLength, costConsultant, currStateSpaceNode.getCurrEncMarking(), mapInt2Marking,
								encodedPN, mapEncTrans2EvClass), currStateSpaceNode);
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

				CPNCostBasedTreeNodeEncFitnessWHeurCost mvOnModelStateSpace = null;
				if (!encInvisTransition.contains(trans)) {
					// not invisible
					mvOnModelStateSpace = new CPNCostBasedTreeNodeEncFitnessWHeurCost(
							currStateSpaceNode.getCurrIndexOnTrace(), newMarkingIndex, StepTypes.MREAL, trans,
							currStateSpaceNode.getCost() + mapEncTrans2Cost.get(trans), estimateCost(lstEvtClass,
									mapEvClass2EncTrans, currStateSpaceNode.getCurrIndexOnTrace(), lstLength,
									costConsultant, newMarkingIndex, mapInt2Marking, encodedPN, mapEncTrans2EvClass),
							currStateSpaceNode);
				} else {
					// invisible
					mvOnModelStateSpace = new CPNCostBasedTreeNodeEncFitnessWHeurCost(
							currStateSpaceNode.getCurrIndexOnTrace(), newMarkingIndex, StepTypes.MINVI, trans,
							currStateSpaceNode.getCost() + mapEncTrans2Cost.get(trans), estimateCost(lstEvtClass,
									mapEvClass2EncTrans, currStateSpaceNode.getCurrIndexOnTrace(), lstLength,
									costConsultant, newMarkingIndex, mapInt2Marking, encodedPN, mapEncTrans2EvClass),
							currStateSpaceNode);
				}

				costBasedPNPQ.add(mvOnModelStateSpace);

				stateCounter++;

				// check for move synchronously
				if ((currStateSpaceNode.getCurrIndexOnTrace() < lstLength)
						&& (mapEvClass2EncTrans.get(lstEvtClass.get(currStateSpaceNode.getCurrIndexOnTrace()))
								.contains(trans))) {
					CPNCostBasedTreeNodeEncFitnessWHeurCost mvSynchronous = new CPNCostBasedTreeNodeEncFitnessWHeurCost(
							currStateSpaceNode.getCurrIndexOnTrace() + 1, newMarkingIndex, StepTypes.LMGOOD, trans,
							currStateSpaceNode.getCost(), estimateCost(lstEvtClass, mapEvClass2EncTrans,
									currStateSpaceNode.getCurrIndexOnTrace() + 1, lstLength, costConsultant,
									newMarkingIndex, mapInt2Marking, encodedPN, mapEncTrans2EvClass),
							currStateSpaceNode);
					costBasedPNPQ.add(mvSynchronous);
					stateCounter++;
				}
			}

			currStateSpaceNode = costBasedPNPQ.poll();
		}

		return new Object[] { currStateSpaceNode, stateCounter };
	}

}
