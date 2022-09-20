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
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.markeq.MarkingEqConsultant;
import org.processmining.plugins.petrinet.replayer.util.codec.EncPNWSetFinalMarkings;
import org.processmining.plugins.petrinet.replayer.util.statespaces.CPNCostBasedTreeNodeEncFitnessWHeurCost;
import org.processmining.plugins.petrinet.replayresult.StepTypes;

/**
 * @author aadrians
 * Oct 23, 2011
 *
 */
public class CostBasedCompleteMarkEquation extends AbstractCostBasedCompleteMarkEqAlg {

	public String toString() {
		return "Best-First Search Cost-based Fitness (with marking equation analysis, require final marking)";
	}
	
	public String getHTMLInfo() {
		return "<html>This is an algorithm to calculate cost-based fitness between a log and a Petri net. <br/><br/>"
				+ "Given a trace and a Petri net (can also be reset/inhibitor net), this algorithm "
				+ "return a matching between the trace and an allowed firing sequence of the net with the"
				+ "least deviation cost using the Best-First Search technique. The firing sequence has to reach proper "
				+ "termination (possible final markings/dead markings) of the net. <br/><br/>"
				+ "If no final markings are provided, all dead markings of the net's reachability graph is considered as "
				+ "final markings. <br/><br/>"
				+ "To minimize the number of explored state spaces, the algorithm uses marking equation to "
				+ "calculate which transition needs to fire to reach proper termination.<br/><br/>"
				+ "Cost for skipping (move on model) and inserting (move on log) "
				+ "activities can be assigned uniquely for each move on model/log. <br/><br/> "
				+ "Marking equation is calculated using lp_solve tool (see <a href='http://lpsolve.sourceforge.net/5.5/'>"
				+ "http://lpsolve.sourceforge.net/5.5/</a>) via the javailp-1.2.a library "
				+ "(see <a href='http://javailp.sourceforge.net/'>http://javailp.sourceforge.net/</a>).</html>";
	}

	public Object[] replayLoop(List<XEventClass> lstEvtClass, EncPNWSetFinalMarkings encodedPN,
			Set<Integer> encInvisTransition, Map<Integer, Map<Integer, Integer>> mapArc2Weight,
			Map<Integer, Map<Integer, Integer>> mapInt2Marking, Map<Map<Integer, Integer>, Integer> mapMarking2Int,
			Integer encInitMarking, Set<Integer> encFinalMarkings, Random numGenerator, int maxNumOfStates,
			Map<XEventClass, Set<Integer>> mapEvClass2EncTrans, Map<XEventClass, Integer> mapEvClass2Cost,
			Map<Integer, Integer> mapEncTrans2Cost, Map<Integer, SortedSet<Integer>> mapMarking2Enabled,
			Map<Integer, Map<Integer, Integer>> mapFiringTransitions, MarkingEqConsultant costConsultant,
			Map<Integer, XEventClass> mapEncTrans2EvClass, Progress progress) {
		// control variables
		int lstLength = lstEvtClass.size();

		// create tree 
		CPNCostBasedTreeNodeEncFitnessWHeurCost stateSpaceRoot = new CPNCostBasedTreeNodeEncFitnessWHeurCost(0,
				encInitMarking, null, null, 0, 0, null);

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
						costConsultant.estimateCost(
								lstEvtClass.subList(currStateSpaceNode.getCurrIndexOnTrace() + 1, lstLength),
								currStateSpaceNode.getCurrEncMarking(), mapInt2Marking, mapEncTrans2EvClass),
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

				CPNCostBasedTreeNodeEncFitnessWHeurCost mvOnModelStateSpace = null;
				if (!encInvisTransition.contains(trans)) {
					// not invisible
					mvOnModelStateSpace = new CPNCostBasedTreeNodeEncFitnessWHeurCost(
							currStateSpaceNode.getCurrIndexOnTrace(), newMarkingIndex, StepTypes.MREAL, trans,
							currStateSpaceNode.getCost() + mapEncTrans2Cost.get(trans), costConsultant.estimateCost(
									lstEvtClass.subList(currStateSpaceNode.getCurrIndexOnTrace(), lstLength),
									newMarkingIndex, mapInt2Marking, mapEncTrans2EvClass), currStateSpaceNode);
				} else {
					// invisible
					mvOnModelStateSpace = new CPNCostBasedTreeNodeEncFitnessWHeurCost(
							currStateSpaceNode.getCurrIndexOnTrace(), newMarkingIndex, StepTypes.MINVI, trans,
							currStateSpaceNode.getCost() + mapEncTrans2Cost.get(trans), costConsultant.estimateCost(
									lstEvtClass.subList(currStateSpaceNode.getCurrIndexOnTrace(), lstLength),
									newMarkingIndex, mapInt2Marking, mapEncTrans2EvClass), currStateSpaceNode);
				}

				costBasedPNPQ.add(mvOnModelStateSpace);

				stateCounter++;

				// check for move synchronously
				if ((currStateSpaceNode.getCurrIndexOnTrace() < lstLength)
						&& (mapEvClass2EncTrans.get(lstEvtClass.get(currStateSpaceNode.getCurrIndexOnTrace()))
								.contains(trans))) {
					CPNCostBasedTreeNodeEncFitnessWHeurCost mvSynchronous = new CPNCostBasedTreeNodeEncFitnessWHeurCost(
							currStateSpaceNode.getCurrIndexOnTrace() + 1, newMarkingIndex, StepTypes.LMGOOD, trans,
							currStateSpaceNode.getCost(), costConsultant.estimateCost(
									lstEvtClass.subList(currStateSpaceNode.getCurrIndexOnTrace() + 1, lstLength),
									newMarkingIndex, mapInt2Marking, mapEncTrans2EvClass), currStateSpaceNode);
					costBasedPNPQ.add(mvSynchronous);
					stateCounter++;
				}
			}

			currStateSpaceNode = costBasedPNPQ.poll();
		}

		return new Object[] { currStateSpaceNode, stateCounter };
	}

}
