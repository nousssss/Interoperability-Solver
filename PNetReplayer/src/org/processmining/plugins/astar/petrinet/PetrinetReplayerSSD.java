/**
 * 
 */
package org.processmining.plugins.astar.petrinet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.processmining.plugins.astar.petrinet.impl.AbstractPDelegate;
import org.processmining.plugins.astar.petrinet.impl.DijkstraPDelegate;
import org.processmining.plugins.astar.petrinet.impl.PHead;
import org.processmining.plugins.astar.petrinet.impl.PRecord;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayer.annotations.PNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express.AllOptAlignmentsTreeThread;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.PNRepResultImpl;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
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
 * @author aadrians Dec 22, 2011
 * 
 */
//@PNReplayAlgorithm
@KeepInProMCache
@PNReplayAlgorithm
public class PetrinetReplayerSSD extends AbstractPetrinetReplayer<DijkstraTail, DijkstraPDelegate> {

	public String toString() {
		return "Best First Search SSD Calculation, assuming at most " + Short.MAX_VALUE + " tokens in each place.";
	}

	protected DijkstraPDelegate getDelegate(PetrinetGraph net, XLog log, XEventClasses classes,
			TransEvClassMapping mapping, int delta, int threads) {
		if (net instanceof ResetInhibitorNet) {
			return new DijkstraPDelegate((ResetInhibitorNet) net, log, classes, mapping, mapTrans2Cost,
					mapEvClass2Cost, delta, false, finalMarkings);
		} else if (net instanceof ResetNet) {
			return new DijkstraPDelegate((ResetNet) net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost, delta,
					false, finalMarkings);
		} else if (net instanceof InhibitorNet) {
			return new DijkstraPDelegate((InhibitorNet) net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost,
					delta, false, finalMarkings);
		} else if (net instanceof Petrinet) {
			return new DijkstraPDelegate((Petrinet) net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost, delta,
					false, finalMarkings);
		}
		return null;
	}

	@Override
	public PNRepResult replayLog(final PluginContext context, PetrinetGraph net, final XLog log,
			TransEvClassMapping mapping, final IPNReplayParameter parameters) throws AStarException {
		importParameters((CostBasedCompleteParam) parameters);
		classifier = mapping.getEventClassifier();

		// obtain maximum cost
		int maxPenalty = Integer.MIN_VALUE;
		for (int val : mapTrans2Cost.values()) {
			if (maxPenalty < val) {
				maxPenalty = val;
			}
		}
		;
		for (int val : mapEvClass2Cost.values()) {
			if (maxPenalty < val) {
				maxPenalty = val;
			}
		}

		if (parameters.isGUIMode()) {
			if (maxNumOfStates != Integer.MAX_VALUE) {
				context.log("Starting replay with max state " + maxNumOfStates + "...");
			} else {
				context.log("Starting replay with no limit for max explored state...");
			}
		}

		final XLogInfo summary = XLogInfoFactory.createLogInfo(log, classifier);
		final XEventClasses classes = summary.getEventClasses();

		final int threads = 1;
		final DijkstraPDelegate delegate = getDelegate(net, log, classes, mapping, 1, threads);

		final MemoryEfficientAStarAlgorithm<PHead, DijkstraTail> aStar = new MemoryEfficientAStarAlgorithm<PHead, DijkstraTail>(
				delegate);

		ExecutorService pool = Executors.newFixedThreadPool(threads);

		final List<Future<Result>> result = new ArrayList<Future<Result>>();

		final TIntIntMap doneMap = new TIntIntHashMap(log.size(), 0.5f, -1, -1);

		long start = System.currentTimeMillis();

		if (context != null) {
			context.getProgress().setMaximum(log.size() + 1);
		}

		TObjectIntMap<Trace> traces = new TObjectIntHashMap<Trace>(log.size() / 2, 0.5f, -1);

		final List<SyncReplayResult> col = new ArrayList<SyncReplayResult>();

		for (int i = 0; i < log.size(); i++) {
			if (context != null) {
				if (context.getProgress().isCancelled()) {
					break;
				}
			}
			PHead initial = new PHead(delegate, initMarking, log.get(i));
			final TIntList unUsedIndices = new TIntArrayList();
			final TIntIntMap trace2orgTrace = new TIntIntHashMap(log.get(i).size(), 0.5f, -1, -1);
			//TODO: ALlow for partially ordered traces
			final Trace trace = getLinearTrace(log, i, delegate, unUsedIndices, trace2orgTrace);
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
			// To output dot files for each graph, use:
			//------------------
			//			String traceID = XConceptExtension.instance().extractName(log.get(i));
			//			if (traceID == null || traceID.isEmpty()) {
			//				traceID = "" + i;
			//			}
			//			thread.addObserver(new DotGraphAStarObserver(new java.io.File("D:/temp/trace_" + traceID + "_graph.txt")));
			//			thread.addObserver(new DotSpanningTreeObserver(new java.io.File("D:/temp/trace_" + traceID + "_sptree.txt")));
			//------------------
			// To use a fast implementation rather than a memory-efficient,use:
			//				TObjectIntMap<PHead> head2int = new TObjectIntHashMap<PHead>(10000);
			//				List<State<PHead, T>> stateList = new ArrayList<State<PHead, T>>(10000);
			//				thread = new FastAStarThread<PHead, T>(delegate,
			//			

			final int j = i;
			result.add(pool.submit(new Callable<Result>() {

				public Result call() throws Exception {
					Result result = new Result();
					result.trace = j;
					result.filteredTrace = trace;
					result.unUsedIndices = unUsedIndices;
					result.trace2orgTrace = trace2orgTrace;

					Canceller c = new Canceller() {
						public boolean isCancelled() {
							if (context != null) {
								return context.getProgress().isCancelled();
							}
							return false;
						}
					};

					//long start = System.nanoTime();
					long start = System.currentTimeMillis();
					result.record = (PRecord) thread.getOptimalRecord(c);
					//long end = System.nanoTime();
					long end = System.currentTimeMillis();
					result.reliable = thread.wasReliable();

					// uncomment this if observers are used
					// thread.closeObservers();

					if (context != null) {
						synchronized (context) {
							if (parameters.isGUIMode() && (j % 100 == 0)) {
								//								context.log(j + "/" + log.size() + " visiting " + thread.getVisitedStateCount()
								//										+ " states took " + (end - start) / 1000000000.0 + " seconds.");
								context.log(j + "/" + log.size() + " visiting " + thread.getVisitedStateCount()
										+ " states took " + (end - start) + " seconds.");
							}
							context.getProgress().inc();
						}
					}
					visitedStates += thread.getVisitedStateCount();
					traversedArcs += thread.getTraversedArcCount();
					queuedStates += thread.getQueuedStateCount();

					result.states = thread.getVisitedStateCount();
					result.queuedStates = thread.getQueuedStateCount();
					// result.milliseconds = (long) ((end - start) / 1000000.0);
					result.milliseconds = end - start;
					result.traversedArcs = thread.getTraversedArcCount();

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
		for (Future<Result> f : result) {
			Result r = null;
			try {
				while (r == null) {
					try {
						r = f.get();
					} catch (InterruptedException e) {
					}
				}
				XTrace trace = log.get(r.trace);
				int states = addReplayResults(delegate, trace, r, doneMap, log, col, r.trace, maxPenalty);//, null);
				maxStateCount = Math.max(maxStateCount, states);
				time += r.milliseconds;
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		long end = System.currentTimeMillis();
		// each PRecord uses 56 bytes in memory

		maxStateCount *= 56;
		if (parameters.isGUIMode()) {
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
			return new PNRepResultImpl(col);
		}
	}

	protected int addReplayResults(DijkstraPDelegate delegate, XTrace trace, Result r, TIntIntMap doneMap, XLog log,
			List<SyncReplayResult> col, int traceIndex, double maxPenalty) { //, Map<Integer, SyncReplayResult> mapRes) {

		SyncReplayResult srr = recordToResultSSD(delegate, trace, r.filteredTrace, r.record, traceIndex, r.states,
				r.states < maxNumOfStates, r.milliseconds, r.queuedStates, r.traversedArcs, maxPenalty,
				r.unUsedIndices, r.trace2orgTrace);
		col.add(srr);
		//BVD		if (mapRes == null) {
		HashMap<Integer, SyncReplayResult> mapRes = new HashMap<Integer, SyncReplayResult>(4);
		//BVD		}
		mapRes.put(traceIndex, srr);

		//BVD		boolean done = false;
		forLoop: for (int key : doneMap.keys()) {
			if (doneMap.get(key) == r.trace) {
				// This should only be done for similar traces.
				XTrace keyTrace = log.get(key);
				// check if trace == keyTrace
				for (Integer keyMapRes : mapRes.keySet()) {
					if (compareEventClassList(delegate, log.get(keyMapRes), keyTrace)) {
						mapRes.get(keyMapRes).addNewCase(key);
						//BVD						doneMap.put(key, -2);
						continue forLoop;
					}
				}

				srr = recordToResultSSD(delegate, keyTrace, r.filteredTrace, r.record, key, r.states,
						r.states < maxNumOfStates, r.milliseconds, r.queuedStates, r.traversedArcs, maxPenalty,
						r.unUsedIndices, r.trace2orgTrace);
				col.add(srr);
				mapRes.put(key, srr);

				//BVD			if (!done) {
				//BVD				// Now they are not the same.
				//BVD				addReplayResults(delegate, keyTrace, r, doneMap, log, col, key, maxPenalty, mapRes);
				//BVD				done = true;
				//BVD			}
			}
		}
		//BVD		col.add(srr);

		return r.states;
	}

	@Override
	protected SyncReplayResult recordToResult(AbstractPDelegate<?> d, XTrace trace, Trace filteredTrace, PRecord r,
			int traceIndex, int stateCount, boolean isReliable, long milliseconds, int queuedStates, int traversedArcs,
			int minCostMoveModel, TIntList unUsedIndices, TIntIntMap trace2orgTrace) {
		return recordToResultSSD((DijkstraPDelegate) d, trace, filteredTrace, r, traceIndex, stateCount, isReliable,
				milliseconds, queuedStates, traversedArcs, minCostMoveModel, unUsedIndices, trace2orgTrace);
	}

	protected SyncReplayResult recordToResultSSD(DijkstraPDelegate d, XTrace trace, Trace filteredTrace, PRecord r,
			int traceIndex, int stateCount, boolean isReliable, long milliseconds, int queuedStates, int traversedArcs,
			double maxPenalty, TIntList unUsedIndices, TIntIntMap trace2orgTrace) {
		List<PRecord> history = PRecord.getHistory(r);
		double mmCost = 0;
		double mlCost = 0;
		double mmUpper = 0;
		double mlUpper = 0;
		int eventInTrace = -1;
		List<StepTypes> stepTypes = new ArrayList<StepTypes>(history.size());
		List<Object> nodeInstance = new ArrayList<Object>();
		TIntIterator it = unUsedIndices.iterator();
		int firstUnUsed = it.hasNext() ? it.next() : Integer.MAX_VALUE;
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
				mmCost += (d.getCostForMoveModel((short) rec.getModelMove()));
				mmUpper += (d.getCostForMoveModel((short) rec.getModelMove()));
			} else {
				// a move occurred in the log. Check if class aligns with class in trace
				// check rec.getMovedEvent. If this is larger than unUsedIndices, then include all unUsedIndices
				// upto rec.getMovedEvent as LogMoves right now.
				while (trace2orgTrace.get(rec.getMovedEvent()) > firstUnUsed) {
					XEventClass clsInTrace = d.getClassOf(trace.get(firstUnUsed)); // this an unused event

					stepTypes.add(StepTypes.L);
					nodeInstance.add(clsInTrace);
					mlCost += mapEvClass2Cost.get(clsInTrace);
					eventInTrace++;
					clsInTrace = d.getClassOf(trace.get(eventInTrace));

					firstUnUsed = it.hasNext() ? it.next() : Integer.MAX_VALUE;
				}

				short a = (short) filteredTrace.get(rec.getMovedEvent());
				eventInTrace++;
				//				XEventClass clsInTrace = d.getClassOf(trace.get(eventInTrace));
				//				while (d.getIndexOf(clsInTrace) != a) {
				//					// The next event in the trace is not of the same class as the next event in the A-star result.
				//					// This is caused by the class in the trace not being mapped to any transition.
				//					// move log only
				//					stepTypes.add(StepTypes.L);
				//					nodeInstance.add(clsInTrace);
				//					mlCost += mapEvClass2Cost.get(clsInTrace);
				//					mlUpper += mapEvClass2Cost.get(clsInTrace);
				//					eventInTrace++;
				//					clsInTrace = d.getClassOf(trace.get(eventInTrace));
				//				}
				if (rec.getModelMove() == AStarThread.NOMOVE) {
					// move log only
					stepTypes.add(StepTypes.L);
					nodeInstance.add(d.getEventClass(a));
					mlCost += (d.getCostForMoveLog(a));
					mlUpper += (d.getCostForMoveLog(a));
				} else {
					// sync move
					stepTypes.add(StepTypes.LMGOOD);
					nodeInstance.add(d.getTransition((short) rec.getModelMove()));
					mlUpper += (d.getCostForMoveLog(a));
					mmUpper += (d.getCostForMoveModel((short) rec.getModelMove()));
				}
			}

		}

		// add the rest of the trace
		eventInTrace++;
		//		while (eventInTrace < trace.size()) {
		while (firstUnUsed < trace.size()) {
			// move log only
			//			XEventClass a = d.getClassOf(trace.get(eventInTrace++));
			XEventClass a = d.getClassOf(trace.get(firstUnUsed));
			eventInTrace++;
			stepTypes.add(StepTypes.L);
			nodeInstance.add(a);
			mlCost += mapEvClass2Cost.get(a);
			mlUpper += mapEvClass2Cost.get(a);
			firstUnUsed = it.hasNext() ? it.next() : Integer.MAX_VALUE;
		}
		SyncReplayResult res = new SyncReplayResult(nodeInstance, stepTypes, traceIndex);

		res.setReliable(isReliable);
		Map<String, Double> info = new HashMap<String, Double>();
		info.put(PNRepResult.RAWFITNESSCOST, (mmCost + mlCost));

		if (mlCost > 0) {
			info.put(PNRepResult.MOVELOGFITNESS, 1 - (mlCost / mlUpper));
		} else {
			info.put(PNRepResult.MOVELOGFITNESS, 1.0);
		}

		if (mmCost > 0) {
			info.put(PNRepResult.MOVEMODELFITNESS, 1 - (mmCost / mmUpper));
		} else {
			info.put(PNRepResult.MOVEMODELFITNESS, 1.0);
		}
		info.put(PNRepResult.NUMSTATEGENERATED, (double) stateCount);
		info.put(PNRepResult.QUEUEDSTATE, (double) queuedStates);
		info.put(PNRepResult.TRAVERSEDARCS, (double) traversedArcs);

		// set info fitness
		info.put("SSD", ((mmCost + mlCost) / (maxPenalty * eventInTrace)));
		info.put(PNRepResult.TIME, (double) milliseconds);
		info.put(PNRepResult.ORIGTRACELENGTH, (double) eventInTrace);
		res.setInfo(info);
		return res;
	}

	public String getHTMLInfo() {
		return "<html>This is an algorithm to calculate Simple String Distance Metric of Cook and Wolf [1]. <br/><br/>"
				+ "Given a log and a Petri net, this algorithm "
				+ "return an alignment between each trace in the log and an allowed complete firing sequence of the net with the"
				+ "least cost using the best first search technique. The firing sequence has to reach proper "
				+ "termination (possible final markings/dead markings) of the net. <br/><br/>"
				+ "Cost for skipping (move on model) and inserting (move on log) "
				+ "activities can be assigned uniquely for each move on model/log. <br/><br/>"
				+ "Reference: <br/>"
				+ "[1] J.E. Cook and A.L. Wolf. Software Process Validation: Quantitatively Measuring the Correspondence "
				+ "of a Process to a Model. ACM Transactions on Software Engineering and Methodology (TOSEM), 8:147-176."
				+ " April 1999</html>";
	}
}