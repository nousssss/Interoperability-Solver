package org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import nl.tue.astar.AStarException;
import nl.tue.astar.AStarThread;
import nl.tue.astar.AStarThread.Canceller;
import nl.tue.astar.Trace;
import nl.tue.astar.impl.DijkstraTail;
import nl.tue.astar.impl.memefficient.MemoryEfficientAStarAlgorithm;
import nl.tue.astar.util.LinearTrace;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.impl.PHead;
import org.processmining.plugins.astar.petrinet.impl.PHeadUnique;
import org.processmining.plugins.astar.petrinet.impl.PNaiveDelegate;
import org.processmining.plugins.astar.petrinet.impl.PNaiveTail;
import org.processmining.plugins.astar.petrinet.impl.PRecord;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.AllSyncReplayResult;

/**
 * Tree-based state space replay for N-best alignments (not necessarily optimal)
 * 
 * @author fmannhardt
 * 
 */
//@PNReplayMultipleAlignmentAlgorithm
public class NBestAlignmentsTreeAlg extends AllOptAlignmentsTreeAlg implements NBestAlignmentsAlg {

	private Integer expectedAlignments = 1;

	@Override
	public String toString() {
		return "Tree-based state space replay for N-best alignments (not necessarily optimal)";
	}

	@Override
	public String getHTMLInfo() {
		return "<html>Returns N-best samples of optimal alignments using tree-based state space.</html>";
	};

	@SuppressWarnings("unchecked")
	@Override
	public PNMatchInstancesRepResult replayLog(final PluginContext context, PetrinetGraph net, Marking initMarking,
			Marking finalMarking, final XLog log, final TransEvClassMapping mapping, Object[] parameters)
			throws AStarException {
		// Collection<AllSyncReplayResult> result = null;
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

		final int delta = 1;
		final int threads = 1;

		// for sake of compatibility, insert cost of dummy xevent class
		mapEvClass2Cost.put(mapping.getDummyEventClass(), 0);

		final PNaiveDelegate delegateMin = getMinBoundDelegate(net, log, classes, mapping, delta, threads);
		final MemoryEfficientAStarAlgorithm<PHead, PNaiveTail> aStarMinBound = new MemoryEfficientAStarAlgorithm<PHead, PNaiveTail>(
				delegateMin);

		ExecutorService pool = Executors.newFixedThreadPool(threads);

		final List<Future<MatchInstancesRes>> result = new ArrayList<Future<MatchInstancesRes>>();

		final TIntIntMap doneMap = new TIntIntHashMap();

		long start = System.currentTimeMillis();

		if (context != null) {
			context.getProgress().setMaximum(log.size() + 1);
		}

		final int minCostMoveModel = getMinBoundMoveModel(new Canceller() {
			public boolean isCancelled() {
				if (context != null) {
					return context.getProgress().isCancelled();
				}
				return false;
			}
		}, log, net, mapping, classes, delta, threads, aStarMinBound);

		TObjectIntMap<Trace> traces = new TObjectIntHashMap<Trace>(log.size() / 2, 0.5f, -1);

		final List<AllSyncReplayResult> col = new ArrayList<AllSyncReplayResult>();

		final AllOptAlignmentsTreeDelegate delegate = getDelegate(net, log, classes, mapping, mapTrans2Cost,
				mapEvClass2Cost, delta, false, finalMarkings);
		final MemoryEfficientAStarAlgorithm<PHead, DijkstraTail> aStar = new MemoryEfficientAStarAlgorithm<PHead, DijkstraTail>(
				delegate);

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
			//String traceID = XConceptExtension.instance().extractName(log.get(i));
			//if (traceID == null || traceID.isEmpty()) {
			//	traceID = "" + i;
			//}
			//thread.addObserver(new DotGraphAStarObserver(new java.io.File("C:/temp/trace_" + traceID + "_graph.txt")));
			//thread.addObserver(new DotSpanningTreeObserver(new java.io.File("C:/temp/trace_" + traceID + "_sptree.txt")));
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

					if (result.reliable) {
						int counter = 1;
						record = (PRecord) thread.getOptimalRecord(c, Integer.MAX_VALUE);
						while (shouldConsiderResult(result, thread, record, counter, log, minCostMoveModel)) {
							result.addRecord(record);
							record = (PRecord) thread.getOptimalRecord(c, Integer.MAX_VALUE);
							counter++;
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
				int states = addReplayResults(delegate, trace, r, doneMap, log, col, r.trace, null, minCostMoveModel);
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

	@Override
	protected AllSyncReplayResult recordToResult(AllOptAlignmentsTreeDelegate d, XTrace trace, Trace filteredTrace,
			Set<PRecord> records, int traceIndex, int states, int queuedStates, boolean isReliable, long milliseconds,
			double minCostMoveModel) {

		List<List<Object>> lstNodeInstanceLst = new ArrayList<List<Object>>(records.size());
		List<List<StepTypes>> lstStepTypesLst = new ArrayList<List<StepTypes>>(records.size());

		AllSyncReplayResult allRes = new AllSyncReplayResult(lstNodeInstanceLst, lstStepTypesLst, traceIndex,
				isReliable);

		double bestCost = Double.MAX_VALUE;

		// Sort records based on their total cost
		List<PRecord> sortedRecords = new ArrayList<PRecord>(records);
		Collections.sort(sortedRecords, new Comparator<PRecord>() {

			public int compare(PRecord o1, PRecord o2) {
				return (int) Math.signum(o1.getTotalCost() - o2.getTotalCost());
			}
		});

		long extraTime = System.currentTimeMillis();

		for (PRecord r : sortedRecords) {

			List<PRecord> history = PRecord.getHistory(r);
			double mmCost = 0; // total cost of move on model
			double mlCost = 0; // total cost of move on log
			double mSyncCost = 0; // total cost of synchronous move

			double mmUpper = 0; // total cost if all movements are move on model (including the synchronous one)
			double mlUpper = 0; // total cost if all events are move on log

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
					mmCost += (d.getCostForMoveModel((short) rec.getModelMove())) / d.getDelta();
					mmUpper += (d.getCostForMoveModel((short) rec.getModelMove())) / d.getDelta();
				} else {
					// a move occurred in the log. Check if class aligns with class in trace
					short a = (short) filteredTrace.get(rec.getMovedEvent()); // a is the event obtained from the replay
					eventInTrace++;
					XEventClass clsInTrace = d.getClassOf(trace.get(eventInTrace)); // this is the current event
					while (d.getIndexOf(clsInTrace) != a) {
						// The next event in the trace is not of the same class as the next event in the A-star result.
						// This is caused by the class in the trace not being mapped to any transition.
						// move log only
						stepTypes.add(StepTypes.L);
						nodeInstance.add(clsInTrace);
						mlCost += mapEvClass2Cost.get(clsInTrace);
						eventInTrace++;
						clsInTrace = d.getClassOf(trace.get(eventInTrace));
					}
					if (rec.getModelMove() == AStarThread.NOMOVE) {
						// move log only
						stepTypes.add(StepTypes.L);
						nodeInstance.add(d.getEventClass(a));
						mlCost += (d.getCostForMoveLog(a)) / d.getDelta();
					} else {
						// sync move
						stepTypes.add(StepTypes.LMGOOD);
						nodeInstance.add(d.getTransition((short) rec.getModelMove()));
						mSyncCost += (d.getCostForMoveSync((short) rec.getModelMove())) / d.getDelta();
						mmUpper += (d.getCostForMoveModel((short) rec.getModelMove())) / d.getDelta();
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
				mlCost += mapEvClass2Cost.get(a);
				//			mlUpper += mapEvClass2Cost.get(a);
			}

			// calculate mlUpper (because in cases where we have synchronous move in manifest, more than one events are aggregated
			// in one movement
			for (XEvent evt : trace) {
				mlUpper += mapEvClass2Cost.get(d.getClassOf(evt));
			}

			lstNodeInstanceLst.add(nodeInstance);
			lstStepTypesLst.add(stepTypes);

			// Reliable just if every alignment is reliable
			allRes.setReliable(isReliable && allRes.isReliable());

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
			info.put(PNRepResult.NUMSTATEGENERATED, (double) states);
			info.put(PNRepResult.QUEUEDSTATE, (double) queuedStates);

			// set info fitness
			if (mmCost > 0 || mlCost > 0 || mSyncCost > 0) {
				info.put(PNRepResult.TRACEFITNESS, 1 - ((mmCost + mlCost + mSyncCost) / (mlUpper + minCostMoveModel)));
			} else {
				info.put(PNRepResult.TRACEFITNESS, 1.0);
			}
			info.put(PNRepResult.TIME, (double) milliseconds);
			info.put(PNRepResult.ORIGTRACELENGTH, (double) eventInTrace);
			allRes.addSingleInfo(info);

			if (bestCost > (mmCost + mlCost)) {
				bestCost = mmCost + mlCost;
			}
		}

		extraTime = System.currentTimeMillis() - extraTime;

		// Set general infos
		allRes.addInfo(PNMatchInstancesRepResult.RAWFITNESSCOST, bestCost);
		allRes.addInfo(PNMatchInstancesRepResult.NUMSTATES, (double) states);
		allRes.addInfo(PNMatchInstancesRepResult.QUEUEDSTATE, (double) states);
		allRes.addInfo(PNMatchInstancesRepResult.ORIGTRACELENGTH, (double) trace.size());
		allRes.addInfo(PNMatchInstancesRepResult.TIME, (double) (extraTime + milliseconds));
		allRes.addInfo(PNMatchInstancesRepResult.NUMALIGNMENTS, (double) records.size());

		return allRes;
	}

	/**
	 * get cost if an empty trace is replayed on a model
	 * 
	 * @param context
	 * @param net
	 * @param mapping
	 * @param classes
	 * @param delta
	 * @param threads
	 * @param aStar
	 * @return
	 * @throws Exception
	 */
	protected int getMinBoundMoveModel(Canceller canceller, XLog log, PetrinetGraph net, TransEvClassMapping mapping,
			final XEventClasses classes, final int delta, final int threads,
			final MemoryEfficientAStarAlgorithm<PHead, PNaiveTail> aStar) throws AStarException {
		// create a log 
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		XTrace emptyTrace = factory.createTrace();

		final PNaiveDelegate delegateD = getMinBoundDelegate(net, log, classes, mapping, delta, threads);
		PHead initialD = new PHead(delegateD, initMarking, emptyTrace);

		final AStarThread<PHead, PNaiveTail> threadD = new AStarThread.MemoryEfficient<PHead, PNaiveTail>(aStar,
				initialD, new LinearTrace("Empty", 0), maxNumOfStates);
		try {
			if (canceller == null) {
				canceller = new Canceller() {

					@Override
					public boolean isCancelled() {
						return false;
					}
				};
			}
			PRecord recordD = (PRecord) threadD.getOptimalRecord(canceller);
			if (recordD == null) {
				return 0;
			}
			// resolution due to numerical inconsistency problem of double data type
			assert (recordD.getCostSoFar() - recordD.getBacktraceSize() - 1) % delta == 0;
			int tempRes = (recordD.getCostSoFar() - recordD.getBacktraceSize() - 1) / delta;

			//			AbstractPILPDelegate.calls = 0;
			return tempRes;

		} catch (AStarException e1) {
			e1.printStackTrace();
			return 0;
		}
	}

	private PNaiveDelegate getMinBoundDelegate(PetrinetGraph net, XLog log, XEventClasses classes,
			TransEvClassMapping mapping, int delta, int threads) {
		if (net instanceof ResetInhibitorNet) {
			return new PNaiveDelegate((ResetInhibitorNet) net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost,
					delta, false, finalMarkings);
		} else if (net instanceof ResetNet) {
			return new PNaiveDelegate((ResetNet) net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost, delta,
					false, finalMarkings);
		} else if (net instanceof InhibitorNet) {
			return new PNaiveDelegate((InhibitorNet) net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost, delta,
					false, finalMarkings);
		} else if (net instanceof Petrinet) {
			return new PNaiveDelegate((Petrinet) net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost, delta,
					false, finalMarkings);
		}
		return null;
	}

	protected boolean shouldConsiderResult(MatchInstancesRes result,
			final AllOptAlignmentsTreeThread<PHead, DijkstraTail> thread, PRecord record, int counter, XLog log,
			int minCostMoveModel) {
		return counter < expectedAlignments && thread.wasReliable();
	}

}
