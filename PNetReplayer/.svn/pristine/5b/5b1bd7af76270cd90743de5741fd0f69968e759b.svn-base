/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.behavapp;

import java.util.Collection;
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
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.Progress;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.AbstractReplayerBasicFunctionProvider;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParamProvider;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.util.LogAutomatonNode;
import org.processmining.plugins.petrinet.replayer.util.codec.EncPNWSetFinalMarkings;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.PNRepResultImpl;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

/**
 * Abstraction for replay algorithms to calculate behavioral appropriateness
 * that require array of final markings (can be empty).
 * 
 * @author aadrians Oct 24, 2011
 * 
 */
//@PNReplayAlgorithm
public abstract class AbstractBehavAppAlg extends AbstractReplayerBasicFunctionProvider implements IPNReplayAlgorithm,
		IBehavAppAlg {
	// required parameters for replay
	protected Integer maxNumStates;
	protected boolean useLogWeight;
	protected Map<XEventClass, Integer> mapEvClass2Cost;
	protected XEventClassifier classifier;
	protected Marking initMarking;
	protected Marking[] finalMarkings;

	// initialize
	protected Double sumAvgPrecisionValue = 0.0000;

	/**
	 * Return true if all replay inputs are correct: no null; numStates is non
	 * negative; all costs are non negative. xEventClassWeightMap can be an
	 * empty mapping, but if it is not empty all event classes should be covered
	 * including the dummy ones
	 */
	public boolean isAllReqSatisfied(PluginContext context, PetrinetGraph net, XLog log, TransEvClassMapping mapping,
			IPNReplayParameter parameter) {
		if ((net instanceof ResetInhibitorNet) || (net instanceof InhibitorNet) || (net instanceof ResetNet)
				|| (net instanceof Petrinet)) {
			if (parameter instanceof BehavAppParam) {
				BehavAppParam param = (BehavAppParam) parameter;
				if ((param.getUseLogWeight() != null) && (param.getMaxNumStates() != null)
						&& (param.getInitialMarking() != null) && (param.getFinalMarkings() != null)
						&& (param.getxEventClassWeightMap() != null)) {
					if (param.getMaxNumStates() >= 0) {
						if (param.getxEventClassWeightMap().size() > 0) {
							// check if all event classes mapped to weight
							Set<XEventClass> evClassWithCost = param.getxEventClassWeightMap().keySet();

							XEventClassifier classifier = mapping.getEventClassifier();
							XLogInfo summary = XLogInfoFactory.createLogInfo(log, classifier);
							XEventClasses eventClassesName = summary.getEventClasses();

							if (evClassWithCost.containsAll(eventClassesName.getClasses())) {
								// always non negative
								for (Integer costVal : param.getxEventClassWeightMap().values()) {
									if (costVal < 0) {
										return false;
									}
								}

								// mapping to weight should also contains dummy event class if exists
								if (mapping.getDummyEventClass() != null) {
									return evClassWithCost.contains(mapping.getDummyEventClass());
								} else {
									return true;
								}
							}
						} else {
							return true;
						}
					}
				}
				;
			}
		}
		return false;
	}

	/**
	 * Return true if input of replay without parameters are correct
	 */
	public boolean isReqWOParameterSatisfied(PluginContext context, PetrinetGraph net, XLog log,
			TransEvClassMapping mapping) {
		return ((net instanceof ResetInhibitorNet) || (net instanceof InhibitorNet) || (net instanceof ResetNet)
				|| (net instanceof Petrinet));
	}

	/**
	 * Import parameters from given array of objects
	 * 
	 * @param parameters
	 */
	protected void importParameters(BehavAppParam parameters) {
		maxNumStates = parameters.getMaxNumStates();
		useLogWeight = parameters.getUseLogWeight();
		mapEvClass2Cost = parameters.getxEventClassWeightMap();
		initMarking = parameters.getInitialMarking();
		finalMarkings = parameters.getFinalMarkings();
	}

	/**
	 * Main method to replay log
	 */
	public PNRepResult replayLog(PluginContext context, PetrinetGraph net, XLog log, TransEvClassMapping mapping,
			IPNReplayParameter parameters) {
		importParameters((BehavAppParam) parameters);
		classifier = mapping.getEventClassifier();

		if (parameters.isGUIMode()) {
			if (maxNumStates != Integer.MAX_VALUE) {
				context.log("Starting replay with max state " + maxNumStates + "...");
			} else {
				context.log("Starting replay with no limit for max explored state...");
			}
		}

		// create log automaton and trace indexing
		final XLogInfo summary = XLogInfoFactory.createLogInfo(log, classifier);
		final XEventClasses classes = summary.getEventClasses();

		// final marking can never be empty

		// replay variables
		final EncPNWSetFinalMarkings encodedPN = new EncPNWSetFinalMarkings(net, initMarking, finalMarkings, null);

		// encode current petri net
		final Map<XEventClass, Set<Integer>> mapEvClass2EncTrans = getMappingEventClass2EncTrans(mapping, encodedPN);
		final Map<Integer, XEventClass> mapEncTrans2EvClass = getMappingEncTrans2EvClass(mapEvClass2EncTrans);

		// utilities
		final Map<List<XEventClass>, Set<Integer>> traceClass = new HashMap<List<XEventClass>, Set<Integer>>();
		final Map<Integer, LogAutomatonNode> mapInt2Automaton = new HashMap<Integer, LogAutomatonNode>();

		// build automaton
		int automatonIndex = 0;
		LogAutomatonNode root = new LogAutomatonNode(automatonIndex, null, log.size());
		mapInt2Automaton.put(automatonIndex, root);
		automatonIndex++;

		LogAutomatonNode currNode = root;
		for (XTrace trace : log) {
			currNode = root;
			List<XEventClass> lstEvClass = new LinkedList<XEventClass>();
			for (XEvent evt : trace) {
				XEventClass evClass = classes.getClassOf(evt);
				// no need to filter out unmapped events
				LogAutomatonNode childNode = currNode.isParentOfClass(evClass);
				if (childNode == null) {
					// create a new node
					childNode = new LogAutomatonNode(automatonIndex, evClass, 1);
					mapInt2Automaton.put(automatonIndex, childNode);
					automatonIndex++;

					currNode.addChild(childNode);
				} else {
					childNode.incFrequency();
				}
				currNode = childNode;
				lstEvClass.add(evClass);
			}

			// add trace index
			Set<Integer> traceIndexes = traceClass.get(lstEvClass);
			if (traceIndexes == null) {
				traceIndexes = new HashSet<Integer>(1);
				traceClass.put(lstEvClass, traceIndexes);
			}
			traceIndexes.add(log.indexOf(trace));
		}

		// get helping variables
		final Map<Integer, Map<Integer, Integer>> mapArc2Weight = encodedPN.getMapArc2Weight();
		final Random numGenerator = new Random();

		// result variable
		final Collection<SyncReplayResult> colSyncRepResult = Collections
				.synchronizedSet(new HashSet<SyncReplayResult>());

		// encode marking
		final Map<Integer, Map<Integer, Integer>> mapInt2Marking = Collections
				.synchronizedMap(new HashMap<Integer, Map<Integer, Integer>>());
		final Map<Map<Integer, Integer>, Integer> mapMarking2Int = Collections
				.synchronizedMap(new HashMap<Map<Integer, Integer>, Integer>());

		Map<Integer, Integer> m = encodedPN.getEncInitialMarking();
		Integer markingIndex = numGenerator.nextInt();
		mapInt2Marking.put(markingIndex, m);
		mapMarking2Int.put(m, markingIndex);

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

		// get all invisible transitions
		final Set<Integer> encInvisTransitions = new HashSet<Integer>();
		for (Transition t : net.getTransitions()) {
			if (t.isInvisible()) {
				encInvisTransitions.add(encodedPN.getEncOf(t));
			}
		}

		// map from a marking to its equivalent marking (another marking that has the same set of 
		// enabled activities)
		final Map<Integer, Integer> mapMarking2EqMarkingClass = Collections
				.synchronizedMap(new HashMap<Integer, Integer>());

		// map from a marking to a set of enabled activities
		final Map<Integer, Set<XEventClass>> mapMarking2EnabledEvClass = Collections
				.synchronizedMap(new HashMap<Integer, Set<XEventClass>>());

		// map from a marking to a set of enabled transitions
		final Map<Integer, SortedSet<Integer>> mapMarking2Enabled = Collections
				.synchronizedMap(new HashMap<Integer, SortedSet<Integer>>());

		// map from marking to marking if a transition is fired
		final Map<Integer, Map<Integer, Integer>> mapFiringTransitions = Collections
				.synchronizedMap(new HashMap<Integer, Map<Integer, Integer>>());

		// get enabled event class for initial marking
		Set<XEventClass> enabledActivities = getEnabledActivities(m, encodedPN, encInvisTransitions,
				mapEncTrans2EvClass);

		// update marking class and enabled activities
		mapMarking2EnabledEvClass.put(markingIndex, enabledActivities);
		mapMarking2EqMarkingClass.put(markingIndex, markingIndex);

		// update progress bar
		final Progress progress = context.getProgress();
		progress.setValue(0);
		progress.setIndeterminate(false);
		progress.setMinimum(0);
		progress.setMaximum(log.size());

		int threads = Runtime.getRuntime().availableProcessors() / 2 + 1;
		ExecutorService executor = Executors.newFixedThreadPool(threads);

		//		long startTime = System.nanoTime();
		for (final List<XEventClass> lstEvtClass : traceClass.keySet()) {
			executor.execute(new Runnable() {
				public void run() {
					SyncReplayResult res = null;
					try {
						res = replayLoop(lstEvtClass, encodedPN, mapArc2Weight, mapInt2Marking, mapMarking2Int,
								mapInt2Automaton, mapEncTrans2EvClass, useLogWeight, mapEvClass2Cost, numGenerator,
								maxNumStates, traceClass, encInvisTransitions, encFinalMarkings,
								mapMarking2EqMarkingClass, mapMarking2EnabledEvClass, mapMarking2Enabled,
								mapFiringTransitions, progress);
					} catch (OutOfMemoryError exc) {
						// if out of memory, return empty unreliable result 
						Set<Integer> allEvtClass = traceClass.get(lstEvtClass);
						Iterator<Integer> it = allEvtClass.iterator();
						int tracePointer = it.next();

						res = new SyncReplayResult(new LinkedList<Object>(), new LinkedList<StepTypes>(), tracePointer);
						res.setReliable(false);
					}

					if (res != null) {
						// add result to collection
						colSyncRepResult.add(res);
					}
				}
			});
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
			if (parameters.isGUIMode()) {
				context.log(e);
			}
			return null;
		} catch (OutOfMemoryError memExc) {
			if (parameters.isGUIMode()) {
				context.log("Out of memory while synchronizing result. Continue processing what has been obtained.");
			}
		}

		return new PNRepResultImpl(colSyncRepResult);
	}

	public IPNReplayParamProvider constructParamProvider(PluginContext context, PetrinetGraph net, XLog log,
			TransEvClassMapping mapping) {
		return new BehavAppParamProvider(context, net, log, mapping);
	}

	/**
	 * Get one of the successors of the node automaton that refer to evClass
	 * 
	 * @param node
	 * @param evClass
	 * @return
	 */
	protected Integer getNextAutomatonIndex(LogAutomatonNode node, XEventClass evClass) {
		for (LogAutomatonNode child : node.getChildren()) {
			if (evClass.equals(child.getEventClass())) {
				return child.getId();
			}
		}
		return null;
	}

	/**
	 * Deviation ratio
	 * 
	 * @param logAutomatonNode
	 * @param continuationOfModel
	 * @param isLogScaled
	 * @param encActivityWeight
	 * @return
	 */
	protected double getDeviationRatio(LogAutomatonNode logAutomatonNode, Set<XEventClass> continuationOfModel,
			boolean isLogScaled, Map<XEventClass, Integer> encActivityWeight) {
		int nominator = 0; // increases when deviation occur

		// construct continuation from log side
		Set<XEventClass> continuationOfLog = new HashSet<XEventClass>(3);
		Set<LogAutomatonNode> children = null;
		if (logAutomatonNode != null) {
			children = logAutomatonNode.getChildren();
			if (children != null) {
				for (LogAutomatonNode node : children) {
					continuationOfLog.add(node.getEventClass());
				}
			}
		}

		int denominator = 0;

		// also calculating transitions not allowed by log 
		if (encActivityWeight.size() > 0) { // transitions are weighed
			for (XEventClass evClass4Trans : continuationOfModel) {
				// transition is mapped to an activity, but whether it is allowed according to log?
				if (!continuationOfLog.contains(evClass4Trans)) {
					// model allows more behavior than log
					nominator += encActivityWeight.get(evClass4Trans);
				}
				denominator += encActivityWeight.get(evClass4Trans);
			}
		} else { // transitions are not weighed
			for (XEventClass evClass4Trans : continuationOfModel) {
				// transition is mapped to an activity, but whether it is allowed according to log?
				if (!continuationOfLog.contains(evClass4Trans)) {
					// model allows more behavior than log
					nominator++;
				}
				denominator++;
			}
		}

		// identify deviation from log side
		// calculate events not allowed by the model
		if (children != null) {
			if (isLogScaled) {
				for (LogAutomatonNode node : children) {
					if (!continuationOfModel.contains(node.getEventClass())) {
						nominator += node.getFrequency();
					}
					denominator += node.getFrequency();
				}
			} else {
				for (LogAutomatonNode node : children) {
					if (!continuationOfModel.contains(node.getEventClass())) {
						nominator++;
					}
				}
				denominator += children.size();
			}
		}

		if (denominator > 0) {
			return ((double) nominator / (double) denominator);
		} else {
			return 0.00;
		}
	}

	protected synchronized Set<XEventClass> getEnabledActAndUpdate(Integer newMarkingIndex,
			Map<Integer, Map<Integer, Integer>> mapInt2Marking, EncPNWSetFinalMarkings encodedPN,
			Set<Integer> encInvisTransitions, Map<Integer, XEventClass> mapEncTrans2EvClass,
			Map<Integer, Set<XEventClass>> mapMarking2EnabledEvClass, Map<Integer, Integer> mapMarking2EqMarkingClass) {
		// update class
		Integer markingClass = mapMarking2EqMarkingClass.get(newMarkingIndex);
		if (markingClass == null) {
			// create new marking class
			mapMarking2EqMarkingClass.put(newMarkingIndex, newMarkingIndex);

			// get enabled activities
			Set<XEventClass> enabledActivities = getEnabledActivities(mapInt2Marking.get(newMarkingIndex), encodedPN,
					encInvisTransitions, mapEncTrans2EvClass);

			// update mapping class
			mapMarking2EnabledEvClass.put(newMarkingIndex, enabledActivities);

			return enabledActivities;
		} else {
			// only need to refer to equivalent marking class
			return mapMarking2EnabledEvClass.get(markingClass);
		}

	}
}
