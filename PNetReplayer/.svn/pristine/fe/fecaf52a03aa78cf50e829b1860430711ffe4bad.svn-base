/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import nl.tue.astar.AStarException;
import nl.tue.astar.AStarThread;
import nl.tue.astar.AStarThread.Canceller;
import nl.tue.astar.Record;
import nl.tue.astar.Trace;
import nl.tue.astar.impl.DijkstraTail;
import nl.tue.astar.impl.memefficient.MemoryEfficientAStarAlgorithm;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.impl.PDelegate;
import org.processmining.plugins.astar.petrinet.impl.PHead;
import org.processmining.plugins.astar.petrinet.impl.PRecord;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.AllSyncReplayResult;

/**
 * @author aadrians Mar 13, 2013
 * 
 */
public class NBestPrefixAlignmentsGraphGuessMarkingAlg extends AllPrefixOptAlignmentsGraphGuessMarkingAlg implements NBestAlignmentsAlg {
	// additional attribute: the number of expected best prefix alignments
	protected int expectedAlignments = 2;

	public String toString() {
		return "Graph-based state space replay to obtain N-best prefix optimal alignments (not necessarily optimal) " +
				"that leads to different markings";
	}

	public String getHTMLInfo() {
		return "<html>Returns N-best prefix optimal alignments that yields to different markings using graph-based state space.</html>";
	};

	/**
	 * This method needs to be overridden, because n best alignments do not
	 * necessarily be optimal alignments. Moreover, the most important is
	 * different markings. parameter array [0]: map transitions to costs, [1]:
	 * max explored states, [2] map event classes to cost, [3]: expected number
	 * of alignments.
	 * 
	 * @throws AStarException
	 */
	@Override
	@SuppressWarnings("unchecked")
	public PNMatchInstancesRepResult replayLog(final PluginContext context, PetrinetGraph net, Marking initMarking,
			Marking finalMarking, final XLog log, final TransEvClassMapping mapping, Object[] parameters) throws AStarException {
		this.initMarking = initMarking;
		this.finalMarkings = new Marking[] { finalMarking };

		mapTrans2Cost = (Map<Transition, Integer>) parameters[MAPTRANSTOCOST];
		mapEvClass2Cost = (Map<XEventClass, Integer>) parameters[MAPXEVENTCLASSTOCOST];
		maxNumOfStates = (Integer) parameters[MAXEXPLOREDINSTANCES];
		expectedAlignments = (Integer) parameters[3];

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

		final int delta = 1000;

		// for sake of compatibility, insert cost of dummy xevent class
		mapEvClass2Cost.put(mapping.getDummyEventClass(), 0);

		final PDelegate delegate = getDelegate(net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost, delta, true,
				finalMarkings);

		final MemoryEfficientAStarAlgorithm<PHead, DijkstraTail> aStar = new MemoryEfficientAStarAlgorithm<PHead, DijkstraTail>(
				delegate);

		ExecutorService pool = Executors.newFixedThreadPool(threads);

		final List<Future<MatchInstancesGraphRes>> result = new ArrayList<Future<MatchInstancesGraphRes>>();

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
			PHead initial = new PHead(delegate, initMarking, log.get(i));
			//TODO: Allow for partially ordered traces
			final Trace trace = getLinearTrace(log, i, delegate);
			int first = traces.get(trace);
			if (first >= 0) {
				doneMap.put(i, first);
				continue;
			} else {
				traces.put(trace, i);
			}
			final AllOptAlignmentsGraphThread<PHead, DijkstraTail> thread = getThread(aStar, initial, trace,
					maxNumOfStates);

			//			final MemoryEfficientAStarTreeStateSpaceThread<PHead, PILPTail> thread = new MemoryEfficientAStarTreeStateSpaceThread<PHead, PILPTail>(
			//					aStar, initial, trace, maxNumOfStates);
			// To output dot files for each graph, use:
			//------------------
			//									String traceID = XConceptExtension.instance().extractName(log.get(i));
			//									if (traceID == null || traceID.isEmpty()) {
			//										traceID = "" + i;
			//									}
			//									thread.addObserver(new DotGraphAStarObserver(new java.io.File("D:/temp/trace_" + traceID + "_graph.txt")));
			//									thread.addObserver(new DotSpanningTreeObserver(new java.io.File("D:/temp/trace_" + traceID + "_sptree.txt")));
			//------------------
			// To use a fast implementation rather than a memory-efficient,use:
			//				TObjectIntMap<PHead> head2int = new TObjectIntHashMap<PHead>(10000);
			//				List<State<PHead, T>> stateList = new ArrayList<State<PHead, T>>(10000);
			//				thread = new FastAStarThread<PHead, T>(delegate,
			//			

			final int j = i;
			result.add(pool.submit(new Callable<MatchInstancesGraphRes>() {

				public MatchInstancesGraphRes call() throws Exception {
					MatchInstancesGraphRes result = new MatchInstancesGraphRes();
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
					if (result.reliable) {
						int counter = 1;
						while (counter < expectedAlignments) {
							record = (PRecord) thread.getOptimalRecord(c);
							if (thread.wasReliable()) {
								result.addRecord(record);
								counter++;
							} else {
								break;
							}
						}
						result.mapRecordToSameSuffix = thread.getMapToStatesWSameSuffix();
						result.reliable = thread.getVisitedStateCount() < maxNumOfStates;
					}

					long end = System.nanoTime();

					// uncomment if the observers are used
					//					 thread.closeObservers();

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
		for (Future<MatchInstancesGraphRes> f : result) {
			MatchInstancesGraphRes r = null;
			try {
				while (r == null) {
					try {
						r = f.get();
					} catch (InterruptedException e) {
					}
				}
				XTrace trace = log.get(r.trace);
				int states = addReplayResults(delegate, trace, r, doneMap, log, col, r.trace, null,
						new LinkedList<Object>(), new LinkedList<StepTypes>());
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
		}
		synchronized (col) {
			PNMatchInstancesRepResult res = new PNMatchInstancesRepResult(col);
			return res;
		}
	}

	/**
	 * This method needs to be overridden because there is no need to compute
	 * the number of all optimal alignments as it is computed in the parent
	 * class
	 */
	@Override
	protected AllSyncReplayResult recordToResult(PDelegate d, XTrace trace, Trace filteredTrace, Collection<PRecord> records,
			int traceIndex, int states, int queuedStates, boolean isReliable, long milliseconds,
			final List<Object> suffixNodeInstance, final List<StepTypes> suffixStepTypes,
			AllSyncReplayResult prevResult, Map<Record, List<Record>> mapToStatesWSameSuffix) {

		List<List<Object>> lstNodeInstanceLst = new ArrayList<List<Object>>(records.size());
		List<List<StepTypes>> lstStepTypesLst = new ArrayList<List<StepTypes>>(records.size());

		double minCost = Double.NaN;
		double maxCost = Double.NaN;
		double cost = 0;

		long extraTime = System.currentTimeMillis();
		
		for (PRecord r : records) {
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
					cost += (d.getCostForMoveModel((short) rec.getModelMove()) - 1) / d.getDelta();
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
						cost += mapEvClass2Cost.get(clsInTrace);
						eventInTrace++;
						clsInTrace = d.getClassOf(trace.get(eventInTrace));
					}
					if (rec.getModelMove() == AStarThread.NOMOVE) {
						// move log only
						stepTypes.add(StepTypes.L);
						nodeInstance.add(d.getEventClass(a));
						cost += (d.getCostForMoveLog(a) - 1) / d.getDelta();
					} else {
						// sync move
						stepTypes.add(StepTypes.LMGOOD);
						nodeInstance.add(d.getTransition((short) rec.getModelMove()));
						cost += (d.getCostForMoveSync((short) rec.getModelMove()) - 1) / d.getDelta();
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
				cost += mapEvClass2Cost.get(a);
			}

			lstNodeInstanceLst.add(nodeInstance);
			lstStepTypesLst.add(stepTypes);

			if (Double.compare(Double.NaN, minCost) == 0) {
				minCost = cost;
			} else if (Double.compare(minCost, cost) > 0) {
				minCost = cost;
			}

			if (Double.compare(Double.NaN, maxCost) == 0) {
				maxCost = cost;
			} else if (Double.compare(maxCost, cost) < 0) {
				maxCost = cost;
			}
			cost = 0.00;
		}
		
		extraTime = System.currentTimeMillis() - extraTime;
		
		AllSyncReplayResult res = new AllSyncReplayResult(lstNodeInstanceLst, lstStepTypesLst, traceIndex, isReliable);

		// set infos
		res.addInfo(PNMatchInstancesRepResult.MINFITNESSCOST, minCost);
		res.addInfo(PNMatchInstancesRepResult.MAXFITNESSCOST, maxCost);
		res.addInfo(PNMatchInstancesRepResult.NUMSTATES, (double) states);
		res.addInfo(PNMatchInstancesRepResult.QUEUEDSTATE, (double) queuedStates);
		res.addInfo(PNMatchInstancesRepResult.ORIGTRACELENGTH, (double) trace.size());
		res.addInfo(PNMatchInstancesRepResult.TIME, (double) (extraTime + milliseconds));
		res.addInfo(PNMatchInstancesRepResult.NUMALIGNMENTS, (double) records.size());

		return res;
	}
}
