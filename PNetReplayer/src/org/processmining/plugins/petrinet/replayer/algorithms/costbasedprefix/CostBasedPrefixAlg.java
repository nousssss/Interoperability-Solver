/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.costbasedprefix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections15.Bag;
import org.apache.commons.collections15.bag.HashBag;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParamProvider;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.util.LogCounterSyncReplay;
import org.processmining.plugins.petrinet.replayer.util.codec.PNCodec;
import org.processmining.plugins.petrinet.replayer.util.statespaces.CPNCostBasedTreeNode;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.PNRepResultImpl;
import org.processmining.plugins.petrinet.replayresult.StepTypes;

/**
 * Use the A* that gives matching instance with the least cost of move on log/
 * move on model
 * 
 * NOTE: This replay algorithm ignores final marking!
 * 
 * @author arya
 * 
 */
//@PNReplayAlgorithm
public class CostBasedPrefixAlg implements IPNReplayAlgorithm {
	/**
	 * Imported parameters
	 */
	// replay parameters
	private int maxNumOfStates;

	// cost
	private int inappropriateTransFireCost; // event that executed without
	// proper predecessors
	private int replayedEventCost; // number of replayed events
	private int skippedEventCost; // number of events that are ignored in replay
	private int heuristicDistanceCost; // number of events still left to be
	// replayed
	private int selfExecInviTaskCost; // number of invisible tasks that are
	// executed without any occurrence of
	// its corresponding events.
	private int selfExecRealTaskCost; // number of tasks that are executed
	// without any occurrence of its
	// corresponding events.

	// allowed moves
	private boolean allowInviTaskMove = true;
	private boolean allowRealTaskMove = true;
	private boolean allowEventSkip = true;
	private boolean allowExecWOTokens = false;
	private boolean allowExecViolating = true;

	private Marking initialMarking = null;

	// classifier for log
	private XEventClassifier classifier;

	public String toString() {
		return "Cost-based Fitness Petri net replay (not considering completion)";
	}

	private Bag<Short> encodeSetPlaces(Marking initMarking, PNCodec codec) {
		Bag<Short> res = new HashBag<Short>();
		for (Place place : initMarking) {
			res.add(codec.getEncodeOfPlace(place), initMarking.occurrences(place));
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	private PNRepResult replayLogInEncodedForm(final PluginContext context, final PetrinetGraph net, final XLog log,
			final Map<XEventClass, List<Short>> transitionMapping, final Set<Short> setInviTrans, final PNCodec codec,
			final Bag<Short> encInitMarking, final boolean isGUI) {
		final XLogInfo summary = XLogInfoFactory.createLogInfo(log, classifier);
		final XEventClasses classes = summary.getEventClasses();

		// required to produce correct output object to be visualized 
		final LogCounterSyncReplay counter = new LogCounterSyncReplay();
		counter.setCosts(inappropriateTransFireCost, skippedEventCost, selfExecInviTaskCost, selfExecRealTaskCost);

		// set context
		final Progress progress = context.getProgress();
		progress.setValue(0);

		int threads = Runtime.getRuntime().availableProcessors() / 2 + 1;
		ExecutorService executor = Executors.newFixedThreadPool(threads);

		int index = 0;
		final Map<List<XEventClass>, List<Integer>> listTraces = new HashMap<List<XEventClass>, List<Integer>>();
		for (final XTrace trace : log) {

			final List<XEventClass> listTrace = getListMappedEventClass(trace, classes, transitionMapping);
			if (listTraces.containsKey(listTrace)) {
				listTraces.get(listTrace).add(log.indexOf(trace));
			} else {
				ArrayList<Integer> arrList = new ArrayList<Integer>();
				arrList.add(log.indexOf(trace));
				listTraces.put(listTrace, arrList);
				if (isGUI) {
					context.log("Replaying trace: " + index + " of length " + trace.size());
				}
				index++;

				executor.execute(new Runnable() {

					public void run() {
						PriorityQueue<CPNCostBasedTreeNode> pq = new PriorityQueue<CPNCostBasedTreeNode>();

						Object[] replayRes = OptimizedCostBasedPNReplayAlgorithm.replayTraceInEncodedForm(progress,
								listTrace, transitionMapping, setInviTrans, codec, encInitMarking, maxNumOfStates,
								inappropriateTransFireCost, replayedEventCost, skippedEventCost, heuristicDistanceCost,
								selfExecInviTaskCost, selfExecRealTaskCost, allowInviTaskMove, allowRealTaskMove,
								allowEventSkip, allowExecWOTokens, allowExecViolating, pq);

						if (progress.isCancelled()) {
							return;
						}

						List<Pair<StepTypes, Object>> result = (List<Pair<StepTypes, Object>>) replayRes[OptimizedCostBasedPNReplayAlgorithm.LISTOFPAIR];
						boolean isReliable = Boolean.valueOf(replayRes[OptimizedCostBasedPNReplayAlgorithm.ISRELIABLE]
								.toString());

						if (result == null) { // trace can NOT be replayed
							if (isGUI) {
								context.log("Trace " + XConceptExtension.instance().extractName(trace)
										+ " can't be replayed");
							}
						} else { // trace can be replayed, its result may not be guaranteed to be correct (if heuristics applied)
							Object[] splitResult = splitReplayResult(result);
							counter.add(listTrace, (List<Object>) splitResult[0], (List<StepTypes>) splitResult[1],
									log.indexOf(trace), isReliable, (Double) replayRes[OptimizedCostBasedPNReplayAlgorithm.TIME] );
							if (!isReliable) {
								if (isGUI) {
									context.log("Trace " + XConceptExtension.instance().extractName(trace)
											+ " result is unreliable");
								}
							}
						}
						progress.inc();
					}

				});
			}
		}
		progress.setIndeterminate(false);
		progress.setMinimum(0);
		progress.setMaximum(listTraces.keySet().size());

		executor.shutdown();
		try {
			while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
				// try again if not terminated.
				if (progress.isCancelled()) {
					executor.shutdownNow();
				}
			}
		} catch (InterruptedException e) {
			if (isGUI) {
				context.log(e);
			}
			return null;
		}

		for (List<XEventClass> listTrace : listTraces.keySet()) {
			for (Integer traceIndex : listTraces.get(listTrace)) {
				counter.inc(listTrace, traceIndex);
			}
		}
		return new PNRepResultImpl(counter.getResult());
	}

	private Object[] splitReplayResult(List<Pair<StepTypes, Object>> listPair) {
		List<StepTypes> listStep = new LinkedList<StepTypes>();
		List<Object> nodeInstances = new LinkedList<Object>();

		for (Pair<StepTypes, Object> pair : listPair) {
			listStep.add(pair.getFirst());
			switch (pair.getFirst()) {
				case L :
					nodeInstances.add(((XEventClass) pair.getSecond()).toString());
					break;
				default :
					nodeInstances.add(pair.getSecond());
					break;
			}
		}
		return new Object[] { nodeInstances, listStep };
	}

	private Map<XEventClass, List<Short>> getEncodedEventMapping(TransEvClassMapping mapping, PNCodec codec) {
		Map<XEventClass, List<Short>> res = new HashMap<XEventClass, List<Short>>();

		for (Transition trans : mapping.keySet()) {
			short mappedTrans = codec.getEncodeOfTransition(trans);
			XEventClass event = mapping.get(trans);
			if (res.containsKey(event)) {
				res.get(event).add(mappedTrans);
			} else {
				// create new list
				List<Short> listShort = new LinkedList<Short>();
				listShort.add(mappedTrans);
				res.put(event, listShort);
			}
		}

		return res;
	}

	private Set<Short> getInviTransCodec(PetrinetGraph net, TransEvClassMapping mapping, PNCodec codec) {
		Set<Short> setInviTrans = new HashSet<Short>();

		for (Transition transition : mapping.keySet()) {
			if (transition.isInvisible()) {
				setInviTrans.add(codec.getEncodeOfTransition(transition));
			}
		}

		return setInviTrans;
	}

	private void importParameters(CostBasedPrefixParam parameters) {
		maxNumOfStates = parameters.getMaxNumOfStates();
		inappropriateTransFireCost = parameters.getInappropriateTransFireCost();
		replayedEventCost = parameters.getReplayedEventCost();
		skippedEventCost = parameters.getSkippedEventCost();
		heuristicDistanceCost = parameters.getHeuristicDistanceCost();
		selfExecInviTaskCost = parameters.getSelfExecInviTaskCost();
		selfExecRealTaskCost = parameters.getSelfExecRealTaskCost();
		allowInviTaskMove = parameters.getAllowInviTaskMove();
		allowRealTaskMove = parameters.getAllowRealTaskMove();
		allowEventSkip = parameters.getAllowEventSkip();
		allowExecWOTokens = parameters.getAllowExecWOTokens();
		allowExecViolating = parameters.getAllowExecViolating();
		initialMarking = parameters.getInitialMarking();
	}

	private List<XEventClass> getListMappedEventClass(XTrace selectedTrace, XEventClasses classes,
			Map<XEventClass, List<Short>> transitionMapping) {
		List<XEventClass> res = new LinkedList<XEventClass>();
		for (XEvent evt : selectedTrace) {
			XEventClass evClass = classes.getClassOf(evt);
			if (transitionMapping.get(evClass) != null) {
				res.add(evClass);
			}
		}
		return res;
	}

	public String getHTMLInfo() {
		return "<html>This is an algorithm to calculate cost-based fitness between a log and a Petri net. <br/><br/>"
				+ "Given a trace and a Petri net (can also be reset/inhibitor net), this algorithm "
				+ "return a matching between the trace and an allowed firing sequence of the net with the"
				+ "least deviation cost using Best-First Search algorithm technique. The firing sequence does not "
				+ "necessarily reach proper termination (possible final markings/dead markings) of the net." + "<br/>"
				+ "<br/> There is only a single cost for skipping "
				+ "any activity (single cost for any move on model), as well as the cost for "
				+ "inserting (move on log) activities. <br/><br/>"
				+ "All event classes in the trace that are not mapped to any transition are ignored completely "
				+ "and does not appear in the resulted matching." + "<br/>" + "<br/>" + "Reference: <br/>"
				+ "[1] Adriansyah, A., Dongen, B.F. van & Aalst, W.M.P. van der (2011). Conformance Checking "
				+ "using Cost-Based Fitness Analysis. In 15th IEEE International Enterprise Distributed Object "
				+ "Computing Conference (EDOC 2011).</html>";
	}

	/**
	 * provider of parameters for this algorithm
	 */
	public IPNReplayParamProvider constructParamProvider(PluginContext context, PetrinetGraph net, XLog log,
			TransEvClassMapping mapping) {
		return new CostBasedPrefixParamProvider(context, net);
	}

	public boolean isReqWOParameterSatisfied(PluginContext context, PetrinetGraph net, XLog log,
			TransEvClassMapping mapping) {
		return ((net instanceof ResetInhibitorNet) || (net instanceof InhibitorNet) || (net instanceof ResetNet)
				|| (net instanceof Petrinet));
	}

	public boolean isAllReqSatisfied(PluginContext context, PetrinetGraph net, XLog log, TransEvClassMapping mapping,
			IPNReplayParameter parameter) {
		if ((net instanceof ResetInhibitorNet) || (net instanceof InhibitorNet) || (net instanceof ResetNet)
				|| (net instanceof Petrinet)) {
			if (!(parameter instanceof CostBasedPrefixParam)) {
				return false;
			} else {
				CostBasedPrefixParam paramImported = (CostBasedPrefixParam) parameter;
				return ((paramImported.getMaxNumOfStates() != null)
						&& (paramImported.getInappropriateTransFireCost() != null)
						&& (paramImported.getReplayedEventCost() != null)
						&& (paramImported.getSkippedEventCost() != null)
						&& (paramImported.getHeuristicDistanceCost() != null)
						&& (paramImported.getSelfExecInviTaskCost() != null)
						&& (paramImported.getSelfExecRealTaskCost() != null)
						&& (paramImported.getAllowInviTaskMove() != null)
						&& (paramImported.getAllowRealTaskMove() != null)
						&& (paramImported.getAllowEventSkip() != null)
						&& (paramImported.getAllowExecWOTokens() != null)
						&& (paramImported.getAllowExecViolating() != null) && (paramImported.getInitialMarking() != null));
			}
		}
		return false;
	}

	public PNRepResult replayLog(PluginContext context, PetrinetGraph net, XLog log, TransEvClassMapping mapping,
			IPNReplayParameter parameter) {
		importParameters((CostBasedPrefixParam) parameter);
		classifier = mapping.getEventClassifier();

		PNCodec codec = new PNCodec(net);

		Set<Short> setUnmappedTrans = getInviTransCodec(net, mapping, codec);
		Map<XEventClass, List<Short>> transitionMapping = getEncodedEventMapping(mapping, codec);

		Bag<Short> encInitMarking = encodeSetPlaces(initialMarking, codec);

		// replay on log
		PNRepResult pnLogRepResult = replayLogInEncodedForm(context, net, log, transitionMapping, setUnmappedTrans,
				codec, encInitMarking, parameter.isGUIMode());

		return pnLogRepResult;
	}
}
