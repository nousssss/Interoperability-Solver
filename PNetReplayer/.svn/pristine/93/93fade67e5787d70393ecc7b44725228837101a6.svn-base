/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express;

import java.util.ArrayList;
import java.util.Collection;
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
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.annotations.KeepInProMCache;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.impl.PDelegate;
import org.processmining.plugins.astar.petrinet.impl.PHead;
import org.processmining.plugins.astar.petrinet.impl.PRecord;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.annotations.PNReplayMultipleAlignmentAlgorithm;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.AllSyncReplayResult;

/**
 * Return all samples of prefix optimal alignments, given a log and a process
 * model
 * 
 * @author aadrians Mar 12, 2013
 * 
 */
@PNReplayMultipleAlignmentAlgorithm
@KeepInProMCache
public class AllPrefixOptAlignmentsGraphGuessMarkingAlg extends AllPrefixOptAlignmentsGraphAlg {
	public String toString() {
		return "Graph-based state space replay to obtain all prefix optimal alignments that leads to different markings";
	}

	public String getHTMLInfo() {
		return "<html>Returns all prefix optimal alignments that yields to different markings using graph-based state space. <br/>"
				+ "The number of all prefix optimal alignments is computed as part of provided results</html>";
	};

	@Override
	protected AllOptAlignmentsGraphThread<PHead, DijkstraTail> getThread(
			MemoryEfficientAStarAlgorithm<PHead, DijkstraTail> aStar, PHead initial, Trace trace, int maxNumOfStates)
			throws AStarException {
		AllOptAlignmentsGraphThread<PHead, DijkstraTail> thread = new AllOptAlignmentsGraphThread.MemoryEfficient<PHead, DijkstraTail>(
				aStar, initial, trace, maxNumOfStates);
		thread.setASynchronousMoveSorting(ASynchronousMoveSorting.MODELMOVEFIRST);
		return thread;
	}

	/**
	 * Recursive call that investigate the same map
	 * 
	 * @param d
	 * @param trace
	 * @param filteredTrace
	 * @param records
	 * @param traceIndex
	 * @param states
	 * @param queuedStates
	 * @param isReliable
	 * @param milliseconds
	 * @param suffixNodeInstance
	 * @param suffixStepTypes
	 * @param prevResult
	 * @param mapToStatesWSameSuffix
	 * @return
	 */
	@Override
	protected AllSyncReplayResult recordToResult(PDelegate d, XTrace trace, Trace filteredTrace, Collection<PRecord> records,
			int traceIndex, int states, int queuedStates, boolean isReliable, long milliseconds,
			final List<Object> suffixNodeInstance, final List<StepTypes> suffixStepTypes,
			AllSyncReplayResult prevResult, Map<Record, List<Record>> mapToStatesWSameSuffix) {

		List<List<Object>> lstNodeInstanceLst = new ArrayList<List<Object>>(records.size());
		List<List<StepTypes>> lstStepTypesLst = new ArrayList<List<StepTypes>>(records.size());

		double cost = 0;
		boolean isFirst = true;
		int numOfOptAlignments = 0;

		long extraTime = System.currentTimeMillis();

		for (PRecord r : records) {
			List<PRecord> history = PRecord.getHistory(r); // this is only a single history
			numOfOptAlignments += 1 + countOptimalAlignments(r, mapToStatesWSameSuffix);
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
						cost += (d.getCostForMoveModel((short) rec.getModelMove()) - 1) / d.getDelta();
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
							cost += (d.getCostForMoveLog(a) - 1) / d.getDelta();
						}
					} else {
						// sync move
						stepTypes.add(StepTypes.LMGOOD);
						nodeInstance.add(d.getTransition((short) rec.getModelMove()));
						if (isFirst) {
							cost += (d.getCostForMoveSync((short) rec.getModelMove()) - 1) / d.getDelta();
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
			isFirst = false;
		}

		extraTime = System.currentTimeMillis() - extraTime;

		AllSyncReplayResult res = new AllSyncReplayResult(lstNodeInstanceLst, lstStepTypesLst, traceIndex, isReliable);

		// set infos
		res.addInfo(PNMatchInstancesRepResult.RAWFITNESSCOST, cost);
		res.addInfo(PNMatchInstancesRepResult.NUMSTATES, (double) states);
		res.addInfo(PNMatchInstancesRepResult.QUEUEDSTATE, (double) queuedStates);
		res.addInfo(PNMatchInstancesRepResult.ORIGTRACELENGTH, (double) trace.size());
		res.addInfo(PNMatchInstancesRepResult.TIME, (double) (extraTime + milliseconds));
		res.addInfo(PNMatchInstancesRepResult.NUMALIGNMENTS, (double) numOfOptAlignments);

		return res;
	}

	/**
	 * recursive method to count the number of optimal alignments
	 * 
	 * @param r
	 * @param mapToStatesWSameSuffix
	 * @return
	 */
	protected int countOptimalAlignments(Record r, Map<Record, List<Record>> mapToStatesWSameSuffix) {
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
			;
			return result;
		}
	}

	@Override
	protected PDelegate getDelegate(PetrinetGraph net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost, int delta,
			boolean allMarkingsAreFinal, Marking[] finalMarkings) {
		if (net instanceof ResetInhibitorNet) {
			return new PDelegate((ResetInhibitorNet) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, delta,
					allMarkingsAreFinal, finalMarkings);
		} else if (net instanceof ResetNet) {
			return new PDelegate((ResetNet) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, delta,
					allMarkingsAreFinal, finalMarkings);
		} else if (net instanceof InhibitorNet) {
			return new PDelegate((InhibitorNet) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, delta,
					allMarkingsAreFinal, finalMarkings);
		} else if (net instanceof Petrinet) {
			return new PDelegate((Petrinet) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, delta,
					allMarkingsAreFinal, finalMarkings);
		}
		return null;
	}
}
