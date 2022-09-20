/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.util.LogCounterCompleteCostBasedReplay;
import org.processmining.plugins.petrinet.replayer.util.codec.EncPNWSetFinalMarkings;
import org.processmining.plugins.petrinet.replayer.util.statespaces.CPNCostBasedTreeNodeEncFitness;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.PNRepResultImpl;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

/**
 * @author aadrians Oct 23, 2011
 * 
 */
public abstract class AbstractCostBasedCompleteNaiveAlg extends
		AbstractCostBasedCompleteAlg<CPNCostBasedTreeNodeEncFitness> implements IDefaultCostBasedCompleteAlg {

	public PNRepResult replayLog(PluginContext context, PetrinetGraph net, final XLog log, TransEvClassMapping mapping,
			IPNReplayParameter parameter) {
		importParameters((CostBasedCompleteParam) parameter);
		classifier = mapping.getEventClassifier();

		if (parameter.isGUIMode()) {
			if (maxNumOfStates != Integer.MAX_VALUE) {
				context.log("Starting replay with max state " + maxNumOfStates + "...");
			} else {
				context.log("Starting replay with no limit for max explored state...");
			}
		}

		final XLogInfo summary = XLogInfoFactory.createLogInfo(log, classifier);
		final XEventClasses classes = summary.getEventClasses();

		// required to produce correct output object to be visualized 
		final LogCounterCompleteCostBasedReplay counter = new LogCounterCompleteCostBasedReplay();

		// replay variables
		final EncPNWSetFinalMarkings encodedPN = new EncPNWSetFinalMarkings(net, initMarking, finalMarkings,
				mapTrans2Cost);

		// get helping variables
		final Map<XEventClass, Set<Integer>> mapEvClass2EncTrans = getMappingEventClass2EncTrans(mapping, encodedPN);
		final Map<Integer, Integer> mapEncTrans2Cost = getTransViolationCosts(encodedPN, mapTrans2Cost);
		final Map<Integer, Map<Integer, Integer>> mapArc2Weight = encodedPN.getMapArc2Weight();
		final Random numGenerator = new Random();
		final Map<Integer, SortedSet<Integer>> mapMarking2Enabled = Collections
				.synchronizedMap(new HashMap<Integer, SortedSet<Integer>>());

		final Map<Integer, Map<Integer, Integer>> mapFiringTransitions = Collections
				.synchronizedMap(new HashMap<Integer, Map<Integer, Integer>>());

		// prepare counter and invis transitions
		final Set<Integer> encInvisTransition = new HashSet<Integer>();
		Map<Transition, Integer> mapTrans2CostMvModelOnly = new HashMap<Transition, Integer>();
		for (Transition t : mapTrans2Cost.keySet()) {
			mapTrans2CostMvModelOnly.put(t, mapTrans2Cost.get(t));
			if (t.isInvisible()) {
				encInvisTransition.add(encodedPN.getEncOf(t));
			}
		}
		counter.setMapTrans2Cost(mapTrans2CostMvModelOnly);
		counter.setMapEvClass2Cost(mapEvClass2Cost);

		// encode marking
		final Map<Integer, Map<Integer, Integer>> mapInt2Marking = new HashMap<Integer, Map<Integer, Integer>>();
		final Map<Map<Integer, Integer>, Integer> mapMarking2Int = new HashMap<Map<Integer, Integer>, Integer>();

		final Integer encInitMarking = numGenerator.nextInt();
		final Map<Integer, Integer> m = encodedPN.getEncInitialMarking();
		mapInt2Marking.put(encInitMarking, m);
		mapMarking2Int.put(m, encInitMarking);

		// for all final markings, encode them
		final HashSet<Integer> encFinalMarkings = new HashSet<Integer>();
		for (Map<Integer, Integer> fm : encodedPN.getEncFinalMarkings()) {
			Integer encFinalMarking = numGenerator.nextInt();
			while (mapInt2Marking.get(encFinalMarking) != null) {
				encFinalMarking = numGenerator.nextInt();
			}

			mapInt2Marking.put(encFinalMarking, fm);
			mapMarking2Int.put(fm, encFinalMarking);

			encFinalMarkings.add(encFinalMarking);
		}

		// update progress bar
		final Progress progress = context.getProgress();
		progress.setValue(0);
		progress.setIndeterminate(false);
		progress.setMinimum(0);
		progress.setMaximum(log.size() + 2);

		if (parameter.isGUIMode()) {
			context.log("Calculate baseline cost...");
		}

		// calculate the baseline cost for move on model only
		Object[] baselineRes = replayLoop(new LinkedList<XEventClass>(), encodedPN, encInvisTransition, mapArc2Weight,
				mapInt2Marking, mapMarking2Int, encInitMarking, encFinalMarkings, numGenerator, maxNumOfStates,
				mapEvClass2EncTrans, mapEvClass2Cost, mapEncTrans2Cost, mapMarking2Enabled, mapFiringTransitions,
				progress);

		if (((Integer) baselineRes[1]) < this.maxNumOfStates) {
			counter.setBaselineIfAllMvOnModel(((CPNCostBasedTreeNodeEncFitness) baselineRes[0]).getCost());
		}
		progress.setValue(2);

		int threads = Runtime.getRuntime().availableProcessors() / 2 + 1;
		ExecutorService executor = Executors.newFixedThreadPool(threads);

		int index = 0;
		final Map<List<XEventClass>, List<Integer>> listTraces = new HashMap<List<XEventClass>, List<Integer>>();

		for (final XTrace trace : log) {
			// ignore event classes that does not have corresponding transition
			final List<Pair<Integer, XEventClass>> listMoveOnLog = new LinkedList<Pair<Integer, XEventClass>>();
			final List<XEventClass> listTrace = getListEventClass(trace, classes, mapEvClass2EncTrans, listMoveOnLog);

			if (listTraces.containsKey(listTrace)) {
				listTraces.get(listTrace).add(log.indexOf(trace));
			} else {
				ArrayList<Integer> arrList = new ArrayList<Integer>();
				arrList.add(log.indexOf(trace));
				listTraces.put(listTrace, arrList);
				if (parameter.isGUIMode()) {
					context.log("Replaying trace: " + index++ + " of length " + trace.size());
				}

				executor.execute(new Runnable() {

					public void run() {
						List<XEventClass> editedListTrace = new LinkedList<XEventClass>(listTrace);

						// remove listMoveOnLog temporarily
						int revIndexOriginalTrace = listTrace.size() - 1;
						for (int i = 0; i < listMoveOnLog.size(); i++) {
							Pair<Integer, XEventClass> pair = listMoveOnLog.get(i);
							editedListTrace.remove(revIndexOriginalTrace - pair.getFirst().intValue());
						}

						Object[] replayRes = null;
						try {
							replayRes = replayLoop(editedListTrace, encodedPN, encInvisTransition, mapArc2Weight,
									mapInt2Marking, mapMarking2Int, encInitMarking, encFinalMarkings, numGenerator,
									maxNumOfStates, mapEvClass2EncTrans, mapEvClass2Cost, mapEncTrans2Cost,
									mapMarking2Enabled, mapFiringTransitions, progress);
						} catch (OutOfMemoryError exc) {
							replayRes = new Object[] { null, maxNumOfStates };
						}

						if (progress.isCancelled()) {
							return;
						}

						List<Object> nodeInstanceLst = new LinkedList<Object>();
						List<StepTypes> stepTypesLst = new LinkedList<StepTypes>();

						if (replayRes[0] != null) {
							// replay finishes
							CPNCostBasedTreeNodeEncFitness solutionNode = (CPNCostBasedTreeNodeEncFitness) replayRes[0];
							createShortListFromTreeNode(encodedPN, solutionNode, nodeInstanceLst, stepTypesLst,
									editedListTrace, listMoveOnLog);
							counter.add(listTrace, nodeInstanceLst, stepTypesLst, log.indexOf(trace),
									((Integer) replayRes[1] < maxNumOfStates), (Integer) replayRes[1]);
						} else {
							// replay not finished because no final marking can be reached
							// although number of states is still less than threshold
							// OR out of memory exception occur
							for (XEventClass evClass : listTrace) {
								nodeInstanceLst.add(evClass);
								stepTypesLst.add(StepTypes.L);
							}
							counter.add(listTrace, nodeInstanceLst, stepTypesLst, log.indexOf(trace), false,
									(Integer) replayRes[1]);
						}

						progress.inc();
					}
				});
			}
		}

		executor.shutdown();
		try {
			while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
				// try again if not terminated.
				if (progress.isCancelled()) {
					executor.shutdownNow();
				}
			}
		} catch (InterruptedException e) {
			if (parameter.isGUIMode()) {
				context.log(e);
			}
			return null;
		} catch (OutOfMemoryError memExc) {
			if (parameter.isGUIMode()) {
				context.log("Out of memory while synchronizing result. Continue processing what has been obtained.");
			}
		}

		for (List<XEventClass> listTrace : listTraces.keySet()) {
			for (Integer traceIndex : listTraces.get(listTrace)) {
				counter.inc(listTrace, traceIndex);
			}
		}

		PNRepResult pnResult = new PNRepResultImpl(counter.getResult());

		// set log fitness log, only if there is minimum cost to reach end marking
		if (counter.getBaselineIfAllMvOnModel() > 0) {
			int totalCost = 0;
			int totalDivider = 0;
			int unReliableResults = 0;
			for (SyncReplayResult res : pnResult) {
				if (res.isReliable()) {
					int traceSize = res.getTraceIndex().size();
					totalCost += (res.getInfo().get(PNRepResult.RAWFITNESSCOST) * traceSize);
					Iterator<Object> itNodeInstance = res.getNodeInstance().iterator();

					totalDivider += (counter.getBaselineIfAllMvOnModel() * traceSize);
					for (Iterator<StepTypes> itStepTypes = res.getStepTypes().iterator(); itStepTypes.hasNext();) {
						switch (itStepTypes.next()) {
							case L :
								totalDivider += (mapEvClass2Cost.get(itNodeInstance.next()) * traceSize);
								break;
							case LMGOOD :
								totalDivider += (mapEvClass2Cost.get(mapping.get(itNodeInstance.next())) * traceSize);
								break;
							default :
								itNodeInstance.next();
								break;
						}
						;
					}
				} else {
					unReliableResults++;
				}
			}

			if (totalDivider > 0) {
				NumberFormat nf = NumberFormat.getInstance();
				nf.setMaximumFractionDigits(2);
				nf.setMinimumFractionDigits(2);
				pnResult.addInfo(PNRepResult.TRACEFITNESS,
						nf.format(1 - ((double) totalCost / (double) totalDivider)));
				pnResult.addInfo(PNRepResult.UNRELIABLEALIGNMENTS, Integer.toString(unReliableResults));
			} else {
				pnResult.addInfo(PNRepResult.TRACEFITNESS, "0.00");
				pnResult.addInfo(PNRepResult.UNRELIABLEALIGNMENTS, Integer.toString(unReliableResults));
			}
		} else {
			pnResult.addInfo(PNRepResult.TRACEFITNESS, "0.00");
		}

		return pnResult;
	}
}
