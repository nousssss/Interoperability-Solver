/**
 * 
 */
package org.processmining.plugins.astar.petrinet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.KeepInProMCache;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.impl.AStarRestrictedMoveModelThread;
import org.processmining.plugins.astar.petrinet.impl.AbstractPILPDelegate;
import org.processmining.plugins.astar.petrinet.impl.PHead;
import org.processmining.plugins.astar.petrinet.impl.PILPDelegate;
import org.processmining.plugins.astar.petrinet.impl.PILPTail;
import org.processmining.plugins.astar.petrinet.impl.PRecord;
import org.processmining.plugins.astar.petrinet.manifestreplay.CostBasedCompleteManifestParam;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayer.annotations.PNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.PNRepResultImpl;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.TShortSet;
import gnu.trove.set.hash.TShortHashSet;
import nl.tue.astar.AStarThread.Canceller;
import nl.tue.astar.ObservableAStarThread;
import nl.tue.astar.Trace;
import nl.tue.astar.impl.memefficient.MemoryEfficientAStarAlgorithm;

/**
 * This replay use ILP, and some transitions are not allowed to do move on
 * models
 * 
 * @author aadrians Feb 21, 2012
 * 
 */
@KeepInProMCache
@PNReplayAlgorithm
public class PetrinetReplayerILPRestrictedMoveModel extends PetrinetReplayerWithILP {
	/**
	 * Transition for which move on model is restricted
	 */
	private Set<Transition> restrictedTrans;

	@Override
	public boolean isAllReqSatisfied(PluginContext context, PetrinetGraph net, XLog log, TransEvClassMapping mapping,
			IPNReplayParameter parameter) {
		if (parameter instanceof CostBasedCompleteManifestParam) {
			if (super.isAllReqSatisfied(context, net, log, mapping, parameter)) {
				Marking[] finalMarking = ((CostBasedCompleteParam) parameter).getFinalMarkings();
				if ((finalMarking != null) && (finalMarking.length > 0)) {
					return true;
				}
				;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "A* Cost-based Replay with ILP with move model restriction, assuming at most " + Short.MAX_VALUE
				+ " tokens in each place.";
	}

	@Override
	public String getHTMLInfo() {
		return "<html>This is an algorithm to calculate alignment between "
				+ "a log and a net under possible restrictions that move model cannot be performed for some transitions. <br/><br/>"
				+ "Given a trace and a Petri net, this algorithm "
				+ "return a matching between the trace and an allowed firing sequence of the net with the"
				+ "least deviation cost using the A* algorithm-based technique. The firing sequence has to reach proper "
				+ "termination (possible final markings/dead markings) of the net. <br/><br/>"
				+ "To minimize the number of explored state spaces, the algorithm prunes visited states/equally visited states. <br/><br/>"
				+ "Cost for skipping (move on model) and inserting (move on log) "
				+ "activities can be assigned uniquely for each move on model/log. </html>";
	}

	@Override
	public PNRepResult replayLog(final PluginContext context, PetrinetGraph net, final XLog log,
			TransEvClassMapping mapping, final IPNReplayParameter param) {
		final CostBasedCompleteManifestParam parameters = (CostBasedCompleteManifestParam) param;
		this.restrictedTrans = parameters.getRestrictedTrans();

		importParameters(parameters);
		classifier = mapping.getEventClassifier();

		if (parameters.isGUIMode()) {
			if (maxNumOfStates != Integer.MAX_VALUE) {
				context.log("Starting replay with max state " + maxNumOfStates + "...");
			} else {
				context.log("Starting replay with no limit for max explored state...");
			}
		}

		final XLogInfo summary = XLogInfoFactory.createLogInfo(log, classifier);
		final XEventClasses classes = summary.getEventClasses();

		final int delta = 1000;
		final int threads = 1;
		final PILPDelegate delegate = getDelegate(net, log, classes, mapping, delta, threads);

		final MemoryEfficientAStarAlgorithm<PHead, PILPTail> aStar = new MemoryEfficientAStarAlgorithm<PHead, PILPTail>(
				delegate);

		ExecutorService pool = Executors.newFixedThreadPool(threads);

		final List<Future<Result>> result = new ArrayList<Future<Result>>();

		final TIntObjectMap<Representative> doneMap = new TIntObjectHashMap<Representative>();

		long start = System.currentTimeMillis();

		if (context != null) {
			context.getProgress().setMaximum(log.size() + 1);
		}
		TObjectIntMap<Trace> traces = new TObjectIntHashMap<Trace>(log.size() / 2, 0.5f, -1);

		final List<SyncReplayResult> col = new ArrayList<SyncReplayResult>();

		// short representation of nonskipped transitions
		TShortSet nonSkippedTrans = new TShortHashSet();
		for (Transition t : restrictedTrans) {
			nonSkippedTrans.add(delegate.getIndexOf(t));
		}

		try {
			// calculate first cost of empty trace

			// CPU EFFICIENT:
			//TObjectIntMap<PHead> head2int = new TObjectIntHashMap<PHead>(256 * 1024);
			//List<State<PHead, T>> stateList = new ArrayList<State<PHead, T>>(256 * 1024);

			int minCostMoveModel = getMinBoundMoveModel(parameters, delta, aStar, delegate);

			final Canceller canceller = parameters.getCanceller() == null ? new Canceller() {
				public boolean isCancelled() {
					if (context != null) {
						return context.getProgress().isCancelled();
					}
					return false;
				}
			} : parameters.getCanceller();

			for (int i = 0; i < log.size(); i++) {
				if (parameters.getCanceller() != null) {
					if (parameters.getCanceller().isCancelled()) {
						break;
					}
				}

				PHead initial = constructHead(delegate, initMarking, log.get(i));
				final TIntList unUsedIndices = new TIntArrayList();
				final TIntIntMap trace2orgTrace = new TIntIntHashMap(log.get(i).size(), 0.5f, -1, -1);
				//TODO: ALlow for partially ordered traces
				final Trace trace = getLinearTrace(log, i, delegate, unUsedIndices, trace2orgTrace);
				int first = traces.get(trace);
				if (first >= 0) {
					doneMap.put(i, new Representative(first, unUsedIndices, trace2orgTrace));
					//System.out.println(i + "/" + log.size() + "-is the same as " + first);
					continue;
				} else {
					traces.put(trace, i);
				}

				final ObservableAStarThread<PHead, PILPTail> thread;

				// MEMORY EFFICIENT
				thread = new AStarRestrictedMoveModelThread.MemoryEfficient<PHead, PILPTail>(aStar, initial, trace,
						maxNumOfStates, nonSkippedTrans);

				// CPU EFFICIENT:
				//thread = new FastAStarThread<PHead, T>(delegate, head2int, stateList, initial, trace, maxNumOfStates);

				// To output dot files for each graph, use:
				//------------------
				//								String traceID = XConceptExtension.instance().extractName(log.get(i));
				//								if (traceID == null || traceID.isEmpty()) {
				//									traceID = "" + i;
				//								}
				//								final DotGraphAStarObserver graphObserver = new DotGraphAStarObserver(new java.io.File("D:/temp/trace_"
				//										+ traceID + "_graph.txt"));
				//								thread.addObserver(graphObserver);
				//								final AStarObserver treeObserver = new DotSpanningTreeObserver(new java.io.File("D:/temp/trace_"
				//										+ traceID + "_sptree.txt"));
				//								thread.addObserver(treeObserver);
				//------------------
				// To use a fast implementation rather than a memory-efficient,use:
				//				TObjectIntMap<PHead> head2int = new TObjectIntHashMap<PHead>(10000);
				//				List<State<PHead, T>> stateList = new ArrayList<State<PHead, T>>(10000);
				//				thread = new FastAStarThread<PHead, T>(delegate,
				//						head2int, stateList, initial, trace, "trace" + i, maxNumOfStates);

				final int j = i;
				result.add(pool.submit(new Callable<Result>() {

					public Result call() throws Exception {
						Result result = new Result();
						result.trace = j;
						result.filteredTrace = trace;
						result.unUsedIndices = unUsedIndices;
						result.trace2orgTrace = trace2orgTrace;

						// long start = System.nanoTime();
						long start = System.currentTimeMillis();
						result.record = (PRecord) thread.getOptimalRecord(canceller);
						//long end = System.nanoTime();
						long end = System.currentTimeMillis();
						result.reliable = thread.wasReliable();

						//uncomment to have all classes of optimal alignments
						//while (thread.wasReliable()) {
						//thread.getOptimalRecord(c, result.record.getTotalCost());
						//}

						if (context != null) {
							synchronized (context) {
								if (parameters.isGUIMode() && (j % 100 == 0)) {
									//									context.log(j + "/" + log.size() + " queueing " + thread.getQueuedStateCount()
									//											+ " states, visiting " + thread.getVisitedStateCount() + " states took "
									//											+ (end - start) / 1000000000.0 + " seconds.");
									context.log(j + "/" + log.size() + " queueing " + thread.getQueuedStateCount()
											+ " states, visiting " + thread.getVisitedStateCount() + " states took "
											+ (end - start) + " seconds.");
								}
								context.getProgress().inc();

							}
						}
						visitedStates += thread.getVisitedStateCount();
						queuedStates += thread.getQueuedStateCount();
						traversedArcs += thread.getTraversedArcCount();

						result.queuedStates = thread.getQueuedStateCount();
						result.states = thread.getVisitedStateCount();
						result.milliseconds = end - start;
						result.traversedArcs = thread.getTraversedArcCount();

						// uncomment the following two lines if state space graph is printed
						//						graphObserver.close();
						//						treeObserver.close();

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
				}
			}

			((AbstractPILPDelegate<?>) delegate).deleteLPs();

			long maxStateCount = 0;
			long time = 0;
			//			long ui = System.currentTimeMillis();
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
					int states = addReplayResults(context.getProgress(), delegate, trace, r, r.unUsedIndices,
							r.trace2orgTrace, doneMap, log, col, r.trace, minCostMoveModel);//, null);
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
				context.log("Time for A*: " + time / 1000.0 + " seconds");
				context.log("In total " + visitedStates + " unique states were visited.");
				context.log("In total " + traversedArcs + " arcs were traversed.");
				context.log("In total " + queuedStates + " states were queued.");
				context.log("At most " + maxStateCount / (1024.0 * 1024.0)
						+ " MB was needed for a trace (overestimate).");
				context.log("States / second:  " + visitedStates / (time / 1000.0));
				context.log("Traversed arcs / second:  " + traversedArcs / (time / 1000.0));
				context.log("Queued states / second:  " + queuedStates / (time / 1000.0));
			}
			synchronized (col) {
				return new PNRepResultImpl(col);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;// debug code
	}

	//	@Override
	//	protected PILPManifestDelegate getDelegate(PetrinetGraph net, XLog log, XEventClasses classes,
	//			TransEvClassMapping mapping, int delta, int threads) {
	//		if (net instanceof ResetInhibitorNet) {
	//			return new PILPManifestDelegate((ResetInhibitorNet) net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost, mapSync2Cost, delta, threads,
	//					finalMarkings, moveModelTrans, fragmentTrans);
	//		} else if (net instanceof ResetNet) {
	//			return new PILPManifestDelegate((ResetNet) net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost, mapSync2Cost, delta, threads,
	//					finalMarkings, moveModelTrans, fragmentTrans);
	//		} else if (net instanceof InhibitorNet) {
	//			return new PILPManifestDelegate((InhibitorNet) net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost, mapSync2Cost, delta, threads,
	//					finalMarkings, moveModelTrans, fragmentTrans);
	//		} else if (net instanceof Petrinet) {
	//			return new PILPManifestDelegate((Petrinet) net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost, mapSync2Cost, delta, threads,
	//					finalMarkings, moveModelTrans, fragmentTrans);
	//		}
	//		return null;
	//	}
	//
	//	@Override
	//	protected PHead constructHead(PILPDelegate delegate, Marking m, XTrace xtrace) {
	//		return new PILPManifestHead(delegate, m, xtrace);
	//	}
}
