/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.KeepInProMCache;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.impl.PHead;
import org.processmining.plugins.astar.petrinet.impl.PHeadUnique;
import org.processmining.plugins.astar.petrinet.impl.PRecord;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.annotations.PNReplayMultipleAlignmentAlgorithm;
import org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.AbstractAllOptAlignmentsAlg;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.AllSyncReplayResult;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import nl.tue.astar.AStarException;
import nl.tue.astar.AStarThread;
import nl.tue.astar.AStarThread.Canceller;
import nl.tue.astar.Trace;
import nl.tue.astar.impl.DijkstraTail;
import nl.tue.astar.impl.memefficient.MemoryEfficientAStarAlgorithm;

/**
 * @author aadrians Mar 9, 2012
 * 
 */
@PNReplayMultipleAlignmentAlgorithm
@KeepInProMCache
public class AllOptAlignmentsTreeAlg extends AbstractAllOptAlignmentsAlg<AllOptAlignmentsTreeDelegate, DijkstraTail> {
	public static class MatchInstancesRes {
		Set<PRecord> records = new HashSet<PRecord>();
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

	public String toString() {
		return "Tree-based state space replay for all optimal alignments";
	}

	public String getHTMLInfo() {
		return "<html>Returns all optimal alignments using tree-based state space.</html>";
	};

	@SuppressWarnings("unchecked")
	public PNMatchInstancesRepResult replayLog(final PluginContext context, PetrinetGraph net, Marking initMarking,
			Marking finalMarking, final XLog log, final TransEvClassMapping mapping, Object[] parameters)
			throws AStarException {
		// Collection<AllSyncReplayResult> result = null;
		this.initMarking = initMarking;
		this.finalMarkings = new Marking[] { finalMarking };

		mapTrans2Cost = (Map<Transition, Integer>) parameters[MAPTRANSTOCOST];
		mapEvClass2Cost = (Map<XEventClass, Integer>) parameters[MAPXEVENTCLASSTOCOST];
		maxNumOfStates = (Integer) parameters[MAXEXPLOREDINSTANCES];

		classifier = mapping.getEventClassifier();

		if (context != null) {
			if (maxNumOfStates != Integer.MAX_VALUE) {
				context.log("Starting replay with max state " + maxNumOfStates + "...");
			} else {
				context.log("Starting replay with no limit for max explored state...");
			}
		}

		final XLogInfo summary = XLogInfoFactory.createLogInfo(log, classifier);
		final XEventClasses classes = summary.getEventClasses();

		final int delta = 1;
		final int threads = 1;

		// for sake of compatibility, insert cost of dummy xevent class
		mapEvClass2Cost.put(mapping.getDummyEventClass(), 0);

		final AllOptAlignmentsTreeDelegate delegate = getDelegate(net, log, classes, mapping, mapTrans2Cost,
				mapEvClass2Cost, delta, false, finalMarkings);

		final MemoryEfficientAStarAlgorithm<PHead, DijkstraTail> aStar = new MemoryEfficientAStarAlgorithm<PHead, DijkstraTail>(
				delegate);

		ExecutorService pool = Executors.newFixedThreadPool(threads);

		final List<Future<MatchInstancesRes>> result = new ArrayList<Future<MatchInstancesRes>>();

		final TIntIntMap doneMap = new TIntIntHashMap();

		long start = System.currentTimeMillis();

		if (context != null) {
			context.getProgress().setMaximum(log.size() + 1);
		}

		TObjectIntMap<Trace> traces = new TObjectIntHashMap<Trace>(log.size() / 2, 0.5f, -1);

		final List<AllSyncReplayResult> col = new ArrayList<AllSyncReplayResult>();

		for (int i = 0; i < log.size(); i++) {
			if (context != null) {
				if (context.getProgress().isCancelled()) {
					break;
				}
			}
			PHeadUnique initial = new PHeadUnique(delegate, initMarking, log.get(i));
			//TODO: ALlow for partially ordered traces
			final Trace trace = getLinearTrace(log, i, delegate);
			int first = traces.get(trace);
			if (first >= 0) {
				doneMap.put(i, first);
				//System.out.println(i + "/" + log.size() + "-is the same as " + first);
				continue;
			} else {
				traces.put(trace, i);
			}
			final AllOptAlignmentsTreeThread<PHead, DijkstraTail> thread = new AllOptAlignmentsTreeThread.MemoryEfficient<PHead, DijkstraTail>(
					aStar, initial, trace, maxNumOfStates);
			//			final MemoryEfficientAStarTreeStateSpaceThread<PHead, PILPTail> thread = new MemoryEfficientAStarTreeStateSpaceThread<PHead, PILPTail>(
			//					aStar, initial, trace, maxNumOfStates);
			// To output dot files for each graph, use:
			//------------------
			//						String traceID = XConceptExtension.instance().extractName(log.get(i));
			//						if (traceID == null || traceID.isEmpty()) {
			//							traceID = "" + i;
			//						}
			//						thread.addObserver(new DotGraphAStarObserver(new java.io.File("D:/temp/trace_" + traceID + "_graph.txt")));
			//						thread.addObserver(new DotSpanningTreeObserver(new java.io.File("D:/temp/trace_" + traceID + "_sptree.txt")));
			//------------------
			// To use a fast implementation rather than a memory-efficient,use:
			//				TObjectIntMap<PHead> head2int = new TObjectIntHashMap<PHead>(10000);
			//				List<State<PHead, T>> stateList = new ArrayList<State<PHead, T>>(10000);
			//				thread = new FastAStarThread<PHead, T>(delegate,
			//			

			final int j = i;
			result.add(pool.submit(new Callable<MatchInstancesRes>() {

				public MatchInstancesRes call() throws Exception {
					MatchInstancesRes result = new MatchInstancesRes();
					result.trace = j;
					result.filteredTrace = trace;

					Canceller c = new Canceller() {
						public boolean isCancelled() {
							if (context != null) {
								return context.getProgress().isCancelled();
							}
							return false;
						}
					};

					long start = System.nanoTime();
					PRecord record = (PRecord) thread.getOptimalRecord(c);
					result.reliable = thread.wasReliable();
					result.addRecord(record);
					//record.printRecord(delegate, j, record);
					if (result.reliable) {
						record = (PRecord) thread.getOptimalRecord(c, (int) record.getTotalCost(), -1.0);
						while (thread.wasReliable()) {
							result.addRecord(record);
							//record.printRecord(delegate, j, record);
							record = (PRecord) thread.getOptimalRecord(c, (int) record.getTotalCost(), -1.0);
						}
						result.reliable = thread.getVisitedStateCount() < maxNumOfStates;
					}

					long end = System.nanoTime();

					// uncomment if the observers are used
					// thread.closeObservers();

					if (context != null) {
						synchronized (context) {
							if ((context != null) && (j % 100 == 0)) {
								context.log(j + "/" + log.size() + " queueing " + thread.getQueuedStateCount()
										+ " states, visiting " + thread.getVisitedStateCount() + " states took "
										+ (end - start) / 1000000000.0 + " seconds.");
							}
							context.getProgress().inc();
						}
					}
					visitedStates += thread.getVisitedStateCount();
					traversedArcs += thread.getTraversedArcCount();
					queuedStates += thread.getQueuedStateCount();

					result.queuedStates = thread.getQueuedStateCount();
					result.states = thread.getVisitedStateCount();
					result.milliseconds = (long) ((end - start) / 1000000.0);

					return result;

				}
			}));
		}
		if (context != null) {
			context.getProgress().inc();
		}
		pool.shutdown();
		while (!pool.isTerminated()) {
			try {
				pool.awaitTermination(10, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		long maxStateCount = 0;
		long time = 0;
		long ui = System.currentTimeMillis();
		for (Future<MatchInstancesRes> f : result) {
			MatchInstancesRes r = null;
			try {
				while (r == null) {
					try {
						r = f.get();
					} catch (InterruptedException e) {
					}
				}
				XTrace trace = log.get(r.trace);
				int states = addReplayResults(delegate, trace, r, doneMap, log, col, r.trace, null, 0.0);
				maxStateCount = Math.max(maxStateCount, states);
				time += r.milliseconds;
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		long end = System.currentTimeMillis();
		// each PRecord uses 56 bytes in memory

		maxStateCount *= 56;
		if (context != null) {
			context.log("Total time : " + (end - start) / 1000.0 + " seconds");
			context.log("Time for Best First Search: " + time / 1000.0 + " seconds");
			context.log("In total " + visitedStates + " unique states were visisted.");
			context.log("In total " + traversedArcs + " arcs were traversed.");
			context.log("In total " + queuedStates + " states were queued.");
			context.log("In total " + aStar.getStatespace().size()
					+ " marking-parikhvector pairs were stored in the statespace.");
			context.log("In total " + aStar.getStatespace().getMemory() / (1024.0 * 1024.0)
					+ " MB were needed for the statespace.");
			context.log("At most " + maxStateCount / (1024.0 * 1024.0) + " MB was needed for a trace (overestimate).");
			context.log("States / second:  " + visitedStates / (time / 1000.0));
			context.log("Traversed arcs / second:  " + traversedArcs / (time / 1000.0));
			context.log("Queued states / second:  " + queuedStates / (time / 1000.0));
			context.log("Storage / second: " + aStar.getStatespace().size() / ((ui - start) / 1000.0));
			//			context.log("EQUAL calls:" + PHeadCompressor.EQUALCALLS);
			//			context.log("EQUAL hash: " + PHeadCompressor.EQUALHASH);
			//			context.log("UNEQUAL:    " + PHeadCompressor.NONEQUAL);
		}
		synchronized (col) {
			PNMatchInstancesRepResult res = new PNMatchInstancesRepResult(col);
			return res;
		}
	}

	protected AllOptAlignmentsTreeDelegate getDelegate(PetrinetGraph net, XLog log, XEventClasses classes,
			TransEvClassMapping map, Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost,
			int delta, boolean allMarkingsAreFinal, Marking[] finalMarkings) {
		if (net instanceof ResetInhibitorNet) {
			return new AllOptAlignmentsTreeDelegate((ResetInhibitorNet) net, log, classes, map, mapTrans2Cost,
					mapEvClass2Cost, delta, allMarkingsAreFinal, finalMarkings);
		} else if (net instanceof ResetNet) {
			return new AllOptAlignmentsTreeDelegate((ResetNet) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost,
					delta, allMarkingsAreFinal, finalMarkings);
		} else if (net instanceof InhibitorNet) {
			return new AllOptAlignmentsTreeDelegate((InhibitorNet) net, log, classes, map, mapTrans2Cost,
					mapEvClass2Cost, delta, allMarkingsAreFinal, finalMarkings);
		} else if (net instanceof Petrinet) {
			return new AllOptAlignmentsTreeDelegate((Petrinet) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost,
					delta, allMarkingsAreFinal, finalMarkings);
		}
		return null;
	}

	protected int addReplayResults(AllOptAlignmentsTreeDelegate delegate, XTrace trace, MatchInstancesRes r,
			TIntIntMap doneMap, XLog log, List<AllSyncReplayResult> col, int traceIndex,
			Map<Integer, AllSyncReplayResult> mapRes, double minCostMoveModel) {
		AllSyncReplayResult repResult = recordToResult(delegate, trace, r.filteredTrace, r.records, traceIndex,
				r.states, r.queuedStates, r.reliable, r.milliseconds, minCostMoveModel);
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
					addReplayResults(delegate, keyTrace, r, doneMap, log, col, key, mapRes, 0.0);
					done = true;
				}
			}
		}
		col.add(repResult);

		return r.states;
	}

	/**
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
	 * @return
	 */
	protected AllSyncReplayResult recordToResult(AllOptAlignmentsTreeDelegate d, XTrace trace, Trace filteredTrace,
			Set<PRecord> records, int traceIndex, int states, int queuedStates, boolean isReliable, long milliseconds,
			double minCostMoveModel) {
		List<List<Object>> lstNodeInstanceLst = new ArrayList<List<Object>>(records.size());
		List<List<StepTypes>> lstStepTypesLst = new ArrayList<List<StepTypes>>(records.size());

		double cost = 0;
		boolean isFirst = true;

		long extraTime = System.currentTimeMillis();

		for (PRecord r : records) {
			List<PRecord> history = PRecord.getHistory(r);
			//			double mmUpper = 0;
			//			double mlUpper = 0;
			//		double smCost = 0;
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
						cost += d.getCostForMoveModel((short) rec.getModelMove()) / d.getDelta();
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
							cost += d.getCostForMoveLog(a) / d.getDelta();
						}
					} else {
						// sync move
						stepTypes.add(StepTypes.LMGOOD);
						nodeInstance.add(d.getTransition((short) rec.getModelMove()));
						if (isFirst) {
							cost += (d.getCostForMoveSync((short) rec.getModelMove()) / d.getDelta());
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
		res.addInfo(PNMatchInstancesRepResult.QUEUEDSTATE, (double) states);
		res.addInfo(PNMatchInstancesRepResult.ORIGTRACELENGTH, (double) trace.size());
		res.addInfo(PNMatchInstancesRepResult.TIME, (double) (extraTime + milliseconds));
		res.addInfo(PNMatchInstancesRepResult.NUMALIGNMENTS, (double) records.size());

		return res;
	}
}
