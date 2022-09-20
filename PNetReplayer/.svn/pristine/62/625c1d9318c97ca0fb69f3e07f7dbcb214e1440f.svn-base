/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.tue.astar.AStarException;
import nl.tue.astar.AStarThread;
import nl.tue.astar.AStarThread.ASynchronousMoveSorting;
import nl.tue.astar.Record;
import nl.tue.astar.Trace;
import nl.tue.astar.impl.DijkstraTail;
import nl.tue.astar.impl.memefficient.MemoryEfficientAStarAlgorithm;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.astar.petrinet.impl.PDelegate;
import org.processmining.plugins.astar.petrinet.impl.PHead;
import org.processmining.plugins.astar.petrinet.impl.PRecord;
import org.processmining.plugins.petrinet.replayer.matchinstances.InfoObjectConst;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.AllSyncReplayResult;

/**
 * @author aadrians May 31, 2013
 * 
 */
public class AllKnotSamplingOptAlignmentsGraphAlg extends AllOptAlignmentsGraphAlg {
	public String toString() {
		return "Knot-based graph-based state space replay to obtain optimal alignment representatives of level-1";
	}

	public String getHTMLInfo() {
		return "<html>Returns representative alignments using graph-based state space. <br/>"
				+ "Assuming that the model does not allow loop/infinite firing sequences of cost 0. <br/>"
				+ "Reordering of sync moves is taken into account. <br/>"
				+ "NOTE: This algorithm is computationally expensive" + "</html>";
	};

	/**
	 * Since we only need samples, sorting may take place
	 * @throws AStarException 
	 */
	protected AllOptAlignmentsGraphThread<PHead, DijkstraTail> getThread(
			MemoryEfficientAStarAlgorithm<PHead, DijkstraTail> aStar, PHead initial, Trace trace, int maxNumOfStates) throws AStarException {
		AllOptAlignmentsGraphThread<PHead, DijkstraTail> thread = new AllOptAlignmentsGraphThread.MemoryEfficient<PHead, DijkstraTail>(
				aStar, initial, trace, maxNumOfStates);
		thread.setASynchronousMoveSorting(ASynchronousMoveSorting.MODELMOVEFIRST);
		return thread;
	}

	@Override
	protected AllSyncReplayResult recordToResult(PDelegate d, XTrace trace, Trace filteredTrace, Collection<PRecord> records,
			int traceIndex, int states, int queuedStates, boolean isReliable, long milliseconds,
			final List<Object> suffixNodeInstance, final List<StepTypes> suffixStepTypes,
			AllSyncReplayResult prevResult, Map<Record, List<Record>> mapToStatesWSameSuffix) {

		List<List<Object>> lstNodeInstanceLst = new ArrayList<List<Object>>(records.size());
		List<List<StepTypes>> lstStepTypesLst = new ArrayList<List<StepTypes>>(records.size());

		double cost = 0.00;

		int numOfOptAlignments = 0;
		int numRepresentatives = 0;
		List<Integer> numRepresented = null;
		if (isRepresentedCounted()) {
			numRepresented = new LinkedList<Integer>(); // record of number alignments represented
		}

		long extraTime = System.currentTimeMillis();

		// we'll have multiple records if there are more than one paths to reach termination
		// with cost 0 and each path does not share the same prefix
		for (PRecord r : records) {
			// uncomment this to get more optimal alignments
			// start block -----------------------------------
			// compute the number of represented alignments
			int numRepresentedBySample;
			if (isRepresentedCounted()) {
				PRecord pred = r.getPredecessor(); // the main state
				numRepresentedBySample = 1 + countOptimalAlignments(pred, mapToStatesWSameSuffix);
				numRepresented.add(numRepresentedBySample); // add number of alignment the sample represents
				numOfOptAlignments += numRepresentedBySample;
			}
			cost = constructResult(r, d, trace, filteredTrace, true, lstNodeInstanceLst, lstStepTypesLst);
			numRepresentatives++;

			// do this for the other represented
			List<Record> others = mapToStatesWSameSuffix.get(r);
			if (others != null) {
				for (Record rOther : others) {
					if (isRepresentedCounted()) {
						numRepresentedBySample = 1 + countOptimalAlignments(rOther, mapToStatesWSameSuffix);
						numRepresented.add(numRepresentedBySample); // add number of alignment the sample represents
						numOfOptAlignments += numRepresentedBySample;
					}
					cost = constructResult((PRecord) rOther, d, trace, filteredTrace, true, lstNodeInstanceLst,
							lstStepTypesLst);
					numRepresentatives++;
				}
			}
			// end block -----------------------------------

			// uncomment this if only one alignment is sufficient
			// start block -----------------------------------
			// compute the number of alignments represented
			//			int numRepresentedBySample = 1 + countOptimalAlignments(r, mapToStatesWSameSuffix);
			//			numRepresented.add(numRepresentedBySample); // add number of alignment the sample represents
			//			numOfOptAlignments += numRepresentedBySample;
			//
			//			cost = constructResult(r, d, trace, filteredTrace, true, lstNodeInstanceLst, lstStepTypesLst);
			// end block -----------------------------------
		}

		extraTime = System.currentTimeMillis() - extraTime;

		AllSyncReplayResult res = new AllSyncReplayResult(lstNodeInstanceLst, lstStepTypesLst, traceIndex, isReliable);
		if (isRepresentedCounted()) {
			res.addInfoObject(InfoObjectConst.NUMREPRESENTEDALIGNMENT, numRepresented);
		}

		// set infos
		res.addInfo(PNMatchInstancesRepResult.RAWFITNESSCOST, cost);
		res.addInfo(PNMatchInstancesRepResult.NUMSTATES, (double) states);
		res.addInfo(PNMatchInstancesRepResult.QUEUEDSTATE, (double) queuedStates);
		res.addInfo(PNMatchInstancesRepResult.ORIGTRACELENGTH, (double) trace.size());
		res.addInfo(PNMatchInstancesRepResult.TIME, (double) (extraTime + milliseconds));
		if (isRepresentedCounted()) {
			res.addInfo(PNMatchInstancesRepResult.NUMALIGNMENTS, (double) numOfOptAlignments);
		} else {
			res.addInfo(PNMatchInstancesRepResult.REPRESENTATIVES, (double) numRepresentatives);
		}

		return res;
	}

	/**
	 * True if the number of represented optimal alignments per alignments is
	 * also computed Setting this to true leads to much more expensive
	 * computation
	 * 
	 * @return
	 */
	protected boolean isRepresentedCounted() {
		return false;
	}

	protected double constructResult(PRecord r, PDelegate d, XTrace trace, Trace filteredTrace, boolean isFirst,
			List<List<Object>> lstNodeInstanceLst, List<List<StepTypes>> lstStepTypesLst) {
		double cost = 0.00;
		List<PRecord> history = PRecord.getHistory(r); // this is only a single history
		int eventInTrace = -1;
		List<StepTypes> stepTypes = new ArrayList<StepTypes>(history.size());
		List<Object> nodeInstance = new ArrayList<Object>();
		for (PRecord rec : history) {
			if (rec.getMovedEvent() == AStarThread.NOMOVE) {
				// move model only
				Transition t = d.getTransition((short) rec.getModelMove());
				if (t.isInvisible()) {
					stepTypes.add(StepTypes.MINVI);
				} else {
					stepTypes.add(StepTypes.MREAL);
				}
				nodeInstance.add(t);
				if (isFirst) {
					cost += (d.getCostForMoveModel((short) rec.getModelMove()) - d.getEpsilon()) / d.getDelta();
				}
			} else {
				// a move occurred in the log. Check if class aligns with class in trace
				short a = (short) filteredTrace.get(rec.getMovedEvent());
				eventInTrace++;
				XEventClass clsInTrace = d.getClassOf(trace.get(eventInTrace));
				while (d.getIndexOf(clsInTrace) != a) {
					// The next event in the trace is not of the same class as the next event in the A-star result.
					// This is caused by the class in the trace not being mapped to any transition.
					// move log only
					stepTypes.add(StepTypes.L);
					nodeInstance.add(clsInTrace);
					if (isFirst) {
						cost += mapEvClass2Cost.get(clsInTrace);
					}
					eventInTrace++;
					clsInTrace = d.getClassOf(trace.get(eventInTrace));
				}
				if (rec.getModelMove() == AStarThread.NOMOVE) {
					// move log only
					stepTypes.add(StepTypes.L);
					nodeInstance.add(d.getEventClass(a));
					if (isFirst) {
						cost += (d.getCostForMoveLog(a) - d.getEpsilon()) / d.getDelta();
					}
				} else {
					// sync move
					stepTypes.add(StepTypes.LMGOOD);
					nodeInstance.add(d.getTransition((short) rec.getModelMove()));
					if (isFirst) {
						cost += (d.getCostForMoveSync((short) rec.getModelMove()) - d.getEpsilon()) / d.getDelta();
					}
				}
			}
		}

		// add the rest of the trace
		eventInTrace++;
		while (eventInTrace < trace.size()) {
			// move log only
			XEventClass a = d.getClassOf(trace.get(eventInTrace++));
			stepTypes.add(StepTypes.L);
			nodeInstance.add(a);
			if (isFirst) {
				cost += mapEvClass2Cost.get(a);
			}
		}

		lstNodeInstanceLst.add(nodeInstance);
		lstStepTypesLst.add(stepTypes);
		return cost;
	}

	protected int countOptimalAlignments(Record r, final Map<Record, List<Record>> mapToStatesWSameSuffix) {
		// change this method to procedural
		//		if (r == null) {
		//			return 0;
		//		} else {
		//			int counter = 0;
		//
		//			List<Record> toBeExtended = new LinkedList<Record>();
		//			toBeExtended.add(r);
		//
		//			while (toBeExtended.size() > 0) {
		//				Record currR = toBeExtended.remove(0);
		//				if (currR != null) {
		//					List<Record> os = mapToStatesWSameSuffix.get(currR);
		//					if (os != null) {
		//						counter += os.size();
		//						for (Record sibling : os) {
		//							toBeExtended.add(sibling.getPredecessor());
		//						}
		//					}
		//					toBeExtended.add(currR.getPredecessor());
		//				}
		//			}
		//			return counter;
		//		}

		// recursive version
		if (r == null) {
			return 0;
		} else {
			int result = 0;
			List<Record> otherStates = mapToStatesWSameSuffix.get(r);
			if (otherStates != null) {
				for (Record rec : otherStates) {
					result += 1 + countOptimalAlignments(rec.getPredecessor(), mapToStatesWSameSuffix);
				}
			}
			result += countOptimalAlignments(r.getPredecessor(), mapToStatesWSameSuffix);
			return result;
		}
	}
}
