/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express;

import gnu.trove.map.TIntIntMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.tue.astar.AStarThread;
import nl.tue.astar.Record;
import nl.tue.astar.Tail;
import nl.tue.astar.Trace;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.astar.petrinet.impl.AbstractPDelegate;
import org.processmining.plugins.astar.petrinet.impl.PRecord;
import org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.AbstractAllOptAlignmentsAlg;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.AllSyncReplayResult;

/**
 * @author aadrians
 * Mar 2, 2013
 *
 */
public abstract class AbstractAllOptAlignmentsGraphAlg<D extends AbstractPDelegate<T>, T extends Tail> extends AbstractAllOptAlignmentsAlg<D,T>{
	protected int threads = 1;

	public static class MatchInstancesGraphRes {
		List<PRecord> records = new LinkedList<PRecord>();
		Map<Record, List<Record>> mapRecordToSameSuffix = new HashMap<Record, List<Record>>();
		int states;
		long milliseconds;
		int trace;
		Trace filteredTrace;
		boolean reliable;
		int queuedStates;

		public void addRecord(PRecord record) {
			records.add(record);
		}
	}
	
	protected int addReplayResults(D delegate, XTrace trace, MatchInstancesGraphRes r, TIntIntMap doneMap,
			XLog log, List<AllSyncReplayResult> col, int traceIndex, Map<Integer, AllSyncReplayResult> mapRes,
			final List<Object> suffixNodeInstance, final List<StepTypes> suffixStepTypes) {
		AllSyncReplayResult repResult = recordToResult(delegate, trace, r.filteredTrace, r.records, traceIndex,
				r.states, r.queuedStates, r.reliable, r.milliseconds, suffixNodeInstance, suffixStepTypes, new AllSyncReplayResult(),
				r.mapRecordToSameSuffix);
		if (mapRes == null) {
			mapRes = new HashMap<Integer, AllSyncReplayResult>();
		}
		mapRes.put(traceIndex, repResult);

		boolean done = false;
		forLoop: for (int key : doneMap.keys()) {
			if (doneMap.get(key) == r.trace) {
				// This should only be done for similar traces.
				XTrace keyTrace = log.get(key);
				// check if trace == keyTrace
				for (Integer keyMapRes : mapRes.keySet()) {
					if (compareEventClassList(delegate, log.get(keyMapRes), keyTrace)) {
						mapRes.get(keyMapRes).addNewCase(key);
						doneMap.put(key, -2);
						continue forLoop;
					}
				}
				if (!done) {
					// Now they are not the same.
					addReplayResults(delegate, keyTrace, r, doneMap, log, col, key, mapRes, suffixNodeInstance,
							suffixStepTypes);
					done = true;
				}
			}
		}
		col.add(repResult);

		return r.states;
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
	protected AllSyncReplayResult recordToResult(D d, XTrace trace, Trace filteredTrace, Collection<PRecord> records,
			int traceIndex, int states, int queuedStates, boolean isReliable, long milliseconds, final List<Object> suffixNodeInstance,
			final List<StepTypes> suffixStepTypes, AllSyncReplayResult prevResult,
			Map<Record, List<Record>> mapToStatesWSameSuffix) {

		List<List<Object>> lstNodeInstanceLst = new ArrayList<List<Object>>(records.size());
		List<List<StepTypes>> lstStepTypesLst = new ArrayList<List<StepTypes>>(records.size());

		double cost = 0.000;
		boolean isFirst = true;
//
//		for (Entry<Record, List<Record>> e : mapToStatesWSameSuffix.entrySet()){
//			System.out.print(e.getKey().toString());
//			System.out.print("---");
//			System.out.print("[");
//			System.out.print(e.getValue().size());
//			System.out.print("]");
//			System.out.print("---");
//			System.out.println(e.getValue().toString());
//			System.out.println();
//		}
		
		long extraTime = System.currentTimeMillis();
		
		for (PRecord r : records) {
			// List<PRecord> history = PRecord.getHistory(r); // this is only a single history
			List<List<PRecord>> allHistory = new LinkedList<List<PRecord>>();
			List<PRecord> suffix = new LinkedList<PRecord>();
			extractAllHistory(r, mapToStatesWSameSuffix, allHistory, suffix);
			
			for (List<PRecord> history : allHistory) {

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
							if (isFirst){
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
				isFirst = false;
			}
		}
		
		extraTime = System.currentTimeMillis() - extraTime;
		
		AllSyncReplayResult res = new AllSyncReplayResult(lstNodeInstanceLst, lstStepTypesLst, traceIndex, isReliable);

		// set infos
		res.addInfo(PNMatchInstancesRepResult.RAWFITNESSCOST, cost);
		res.addInfo(PNMatchInstancesRepResult.NUMSTATES, (double) states);
		res.addInfo(PNMatchInstancesRepResult.QUEUEDSTATE, (double) queuedStates);
		res.addInfo(PNMatchInstancesRepResult.ORIGTRACELENGTH, (double) trace.size());
		res.addInfo(PNMatchInstancesRepResult.TIME, (double) (extraTime + milliseconds));
		res.addInfo(PNMatchInstancesRepResult.NUMALIGNMENTS, (double) lstNodeInstanceLst.size());

		return res;
	}

	/**
	 * Recursively extract all history
	 * @param r
	 * @param mapRecordToSameSuffix
	 * @param result
	 * @param suffix
	 */
	public static void extractAllHistory(PRecord r, Map<Record, List<Record>> mapRecordToSameSuffix,
			List<List<PRecord>> result, List<PRecord> suffix) {
		if (r == null || r.getBacktraceSize() < 0) {
			result.add(suffix);
			return;
		}

		List<Record> sameSuffix = mapRecordToSameSuffix.get(r);
		if (sameSuffix != null){
			for (Record newRec : sameSuffix){
				// copy list suffix
				List<PRecord> newSuffix = new ArrayList<PRecord>(((PRecord) newRec).getBacktraceSize() + suffix.size());
				newSuffix.add((PRecord) newRec);
				newSuffix.addAll(new ArrayList<PRecord>(suffix));
				
				extractAllHistory((PRecord) newRec.getPredecessor(), mapRecordToSameSuffix, result, newSuffix);			
			}
		}
		suffix.add(0, r);
		r = r.getPredecessor();
		
		extractAllHistory(r, mapRecordToSameSuffix, result, suffix);
	}
}
