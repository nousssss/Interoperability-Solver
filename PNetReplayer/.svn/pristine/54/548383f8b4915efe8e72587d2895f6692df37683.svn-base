/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express;

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
import org.processmining.plugins.astar.petrinet.impl.PDelegate;
import org.processmining.plugins.astar.petrinet.impl.PHead;
import org.processmining.plugins.astar.petrinet.impl.PRecord;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.annotations.PNReplayMultipleAlignmentAlgorithm;
import org.processmining.plugins.petrinet.replayer.matchinstances.InfoObjectConst;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.AllSyncReplayResult;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import nl.tue.astar.AStarException;
import nl.tue.astar.AStarThread;
import nl.tue.astar.AStarThread.ASynchronousMoveSorting;
import nl.tue.astar.AStarThread.Canceller;
import nl.tue.astar.Record;
import nl.tue.astar.Trace;
import nl.tue.astar.impl.DijkstraTail;
import nl.tue.astar.impl.memefficient.MemoryEfficientAStarAlgorithm;

/**
 * @author aadrians Mar 3, 2013
 * 
 */
@PNReplayMultipleAlignmentAlgorithm
@KeepInProMCache
public class AllOptAlignmentsGraphSamplingAlg extends AbstractAllOptAlignmentsGraphAlg<PDelegate, DijkstraTail> {
	public String toString() {
		return "Graph-based state space replay to obtain extended representatives of all optimal alignments (with state sorting)";
	}

	@Override
	public String getHTMLInfo() {
		return "<html>Returns extended representatives (with sorting of states) of all optimal alignments. Some samples may share prefix and infix <br/>"
				+ "along state space graph. Assuming that the model does not allow loop/infinite firing sequences of cost 0. <br/>"
				+ "The technique use graph-based state space. The _lower_bound_ of the number of optimal alignments is computed and also provided as part of results.<br/>"
				+ "NOTE:it is a lower bound because sync move is prioritized over move log/move model.</html>";
	};

	/**
	 * Since we only need samples, sorting may take place
	 * 
	 * @throws AStarException
	 */
	protected AllSamplingOptAlignmentsGraphThread<PHead, DijkstraTail> getThread(
			MemoryEfficientAStarAlgorithm<PHead, DijkstraTail> aStar, PHead initial, Trace trace, int maxNumOfStates)
			throws AStarException {
		AllSamplingOptAlignmentsGraphThread<PHead, DijkstraTail> thread = new AllSamplingOptAlignmentsGraphThread.MemoryEfficient<PHead, DijkstraTail>(
				aStar, initial, trace, maxNumOfStates);
		thread.setASynchronousMoveSorting(ASynchronousMoveSorting.MODELMOVEFIRST);
		return thread;
	}

	@SuppressWarnings("unchecked")
	public PNMatchInstancesRepResult replayLog(final PluginContext context, PetrinetGraph net, Marking initMarking,
			Marking finalMarking, final XLog log, final TransEvClassMapping mapping, Object[] parameters) throws AStarException {
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

		final int delta = 1000;

		// for sake of compatibility, insert cost of dummy xevent class
		mapEvClass2Cost.put(mapping.getDummyEventClass(), 0);

		final PDelegate delegate = getDelegate(net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost, delta,
				false, finalMarkings);

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
			//TODO: ALlow for partially ordered traces
			final Trace trace = getLinearTrace(log, i, delegate);
			int first = traces.get(trace);
			if (first >= 0) {
				doneMap.put(i, first);
				continue;
			} else {
				traces.put(trace, i);
			}
			final AllSamplingOptAlignmentsGraphThread<PHead, DijkstraTail> thread = getThread(aStar, initial, trace,
					maxNumOfStates);
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

					int threshold = record.getCostSoFar();
					if (result.reliable) {
						record = (PRecord) thread.getOptimalRecord(c, threshold,-1.0);
						while (thread.wasReliable()) {
							result.addRecord(record);
							record = (PRecord) thread.getOptimalRecord(c, threshold,-1.0);
						}
						result.mapRecordToSameSuffix = thread.getMapToStatesWSameSuffix();
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

		int numOfOptAlignments = 0;
		double cost = 0.00;

		List<Integer> numRepresented = new LinkedList<Integer>(); // record of number alignments represented

		long extraTime = System.currentTimeMillis();

		// we'll have multiple records if there are more than one paths to reach termination
		// with cost 0 and each path does not share the same prefix
		for (PRecord r : records) {
			// uncomment this to get more optimal alignments
			// start block -----------------------------------
			// compute the number of represented alignments
			PRecord pred = r.getPredecessor(); // the main state
			int numRepresentedBySample = 1 + countOptimalAlignments(pred, mapToStatesWSameSuffix);
			numRepresented.add(numRepresentedBySample); // add number of alignment the sample represents
			numOfOptAlignments += numRepresentedBySample;
			cost = constructResult(r, d, trace, filteredTrace, true, lstNodeInstanceLst, lstStepTypesLst);

			// do this for the other represented
			List<Record> others = mapToStatesWSameSuffix.get(r);
			if (others != null) {
				for (Record rOther : others) {
					numRepresentedBySample = 1 + countOptimalAlignments(rOther, mapToStatesWSameSuffix);
					numRepresented.add(numRepresentedBySample); // add number of alignment the sample represents
					numOfOptAlignments += numRepresentedBySample;
					cost = constructResult((PRecord) rOther, d, trace, filteredTrace, true, lstNodeInstanceLst,
							lstStepTypesLst);
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
		res.addInfoObject(InfoObjectConst.NUMREPRESENTEDALIGNMENT, numRepresented);

		// set infos
		res.addInfo(PNMatchInstancesRepResult.RAWFITNESSCOST, cost);
		res.addInfo(PNMatchInstancesRepResult.NUMSTATES, (double) states);
		res.addInfo(PNMatchInstancesRepResult.QUEUEDSTATE, (double) queuedStates);
		res.addInfo(PNMatchInstancesRepResult.ORIGTRACELENGTH, (double) trace.size());
		res.addInfo(PNMatchInstancesRepResult.TIME, (double) (extraTime + milliseconds));
		res.addInfo(PNMatchInstancesRepResult.NUMALIGNMENTS, (double) numOfOptAlignments);

		return res;
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
			return result;
		}
	}

	protected PDelegate getDelegate(PetrinetGraph net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost, int delta,
			boolean allMarkingsAreFinal, Marking[] finalMarkings) {
		PDelegate d = null;
		if (net instanceof ResetInhibitorNet) {
			d = new PDelegate((ResetInhibitorNet) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, delta,
					allMarkingsAreFinal, finalMarkings);

		} else if (net instanceof ResetNet) {
			d = new PDelegate((ResetNet) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, delta,
					allMarkingsAreFinal, finalMarkings);
		} else if (net instanceof InhibitorNet) {
			d = new PDelegate((InhibitorNet) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, delta,
					allMarkingsAreFinal, finalMarkings);
		} else if (net instanceof Petrinet) {
			d = new PDelegate((Petrinet) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, delta,
					allMarkingsAreFinal, finalMarkings);
		}
		if (d != null) {
			d.setEpsilon(0);
		}
		return d;
	}
}
