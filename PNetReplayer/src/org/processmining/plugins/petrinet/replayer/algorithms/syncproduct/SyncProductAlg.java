/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.syncproduct;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.packages.PackageDescriptor;
import org.processmining.framework.packages.PackageManager;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.Progress;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParamProvider;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.util.CombinationGenerator;
import org.processmining.plugins.petrinet.replayer.util.LogCounterSyncReplay;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.PNRepResultImpl;
import org.processmining.plugins.petrinet.replayresult.StepTypes;

/**
 * Use synchronous product of two Petri nets to give a matching instance with
 * the least cost.
 * 
 * NOTE: This replay algorithm ignores final marking!
 * 
 * @author Arya Adriansyah
 * @email a.adriansyah@tue.nl
 * @version Feb 16, 2011
 */
//@PNReplayAlgorithm
public class SyncProductAlg implements IPNReplayAlgorithm {

	// available move considered (match with GUI)
	private static int MOVEONLOGONLY = 0;
	private static int MOVEONMODELONLYINVI = 1;
	private static int MOVEONMODELONLYREAL = 2;
	private static int MOVESYNCHRONIZEDVIOLATING = 3; // firing without taking any token
	private static int MOVESYNCHRONIZEDVIOLATINGPARTIALLY = 4; // only take a part of token
	private static int MOVESYNCHRONIZED = 5;

	public static String SARAFILE = "sara.exe";

	private Map<XEventClass, List<Transition>> getEncodedEventMapping(TransEvClassMapping mapping) {
		Map<XEventClass, List<Transition>> res = new HashMap<XEventClass, List<Transition>>();

		for (Transition transition : mapping.keySet()) {
			XEventClass event = mapping.get(transition);
			if (res.containsKey(event)) {
				res.get(event).add(transition);
			} else {
				// create new list
				List<Transition> listShort = new LinkedList<Transition>();
				listShort.add(transition);
				res.put(event, listShort);
			}
		}
		return res;
	}

	private List<XEventClass> getListEventClass(List<XEvent> selectedTrace, XEventClasses classes) {
		List<XEventClass> res = new LinkedList<XEventClass>();
		for (XEvent evt : selectedTrace) {
			res.add(classes.getClassOf(evt));
		}
		return res;
	}

	public String toString() {
		return "Synchronous Product Fitness Petri net replay (using Sara tool, for Windows only)";
	}

	/**
	 * Get only the events that are mapped to a transition
	 * 
	 * @param selectedTrace
	 * @param classes
	 * @param transitionMapping
	 * @return
	 */
	private List<XEvent> getMappedEventsList(List<XEvent> selectedTrace, XEventClasses classes,
			Map<XEventClass, List<Transition>> transitionMapping) {
		// filter out events that are not mapped to any transitions
		List<XEvent> mappedEventsLst = new LinkedList<XEvent>();
		for (XEvent event : selectedTrace) {
			if (transitionMapping.get(classes.getClassOf(event)) != null) {
				mappedEventsLst.add(event);
			}
		}
		return mappedEventsLst;
	}

	/**
	 * Replay list of events, assuming that each event is mapped to at least a
	 * transition
	 * 
	 * @param context
	 * @param net
	 * @param m
	 * @param mappedEventsLst
	 * @param logInfo
	 * @param classes
	 * @param transitionMapping
	 * @param weights
	 * @param id
	 * @return list of visited transitions, total time spent, map clone to orig
	 */
	@SuppressWarnings("unchecked")
	private Object[] replayXTracePrivateAssumingFullyMappedTrace(PluginContext context, PetrinetGraph net, Marking m,
			List<XEvent> mappedEventsLst, XLogInfo logInfo, XEventClasses classes,
			Map<XEventClass, List<Transition>> transitionMapping, int[] weights, int id, boolean isGUIMode) {
		// create petri net and marking
		Object[] synchroResult = createPetriNetAndMarking((Petrinet) net, m, mappedEventsLst, classes,
				transitionMapping, weights);

		Petrinet resultNet = (Petrinet) synchroResult[0];
		Marking mResultNet = (Marking) synchroResult[1];

		// others that might be needed later
		Place costPool = (Place) synchroResult[2];
		Set<Place> finalLogPlaces = (Set<Place>) synchroResult[3];
		Map<Transition, Transition> mapCloneToOrig = (Map<Transition, Transition>) synchroResult[4];

		// check coverability in net and obtain witness (using Sara) 
		// by default, just return one possible path
		Object[] traceAndTime = identifyMinimumCostPath(context, resultNet, mResultNet, costPool, finalLogPlaces,
				false, context.getProgress(), id, isGUIMode);
		try {
			if (traceAndTime != null) {
				return new Object[] { traceAndTime[0], traceAndTime[1], mapCloneToOrig };
			} else {
				return new Object[] { new ArrayList<Transition>(0), 0L, mapCloneToOrig };
			}
		} catch (OutOfMemoryError exc) {
			return new Object[] { new ArrayList<Transition>(0), 0L, mapCloneToOrig };
		}
	}

	private Object[] createPetriNetAndMarking(Petrinet origNet, Marking m, List<XEvent> selectedTrace,
			XEventClasses classes, Map<XEventClass, List<Transition>> transitionMapping, int[] weight) {
		Petrinet netResult = PetrinetFactory.newPetrinet("replay net of " + origNet.getLabel());
		Marking markResult = new Marking();
		Place costPool = netResult.addPlace("cp");

		Collection<Transition> cOrigTrans = origNet.getTransitions();

		// store mapping, successors, predecessors from original to clone
		Map<Transition, Transition> mapOrigToClone = new HashMap<Transition, Transition>();
		Map<Place, Place> mapPlaceOrigToClone = new HashMap<Place, Place>();
		Map<Transition, Transition> mapCloneToOrig = new HashMap<Transition, Transition>();
		Map<Transition, Set<Place>> successors = new HashMap<Transition, Set<Place>>(); // store clone 
		Map<Transition, Set<Place>> predecessors = new HashMap<Transition, Set<Place>>(); // store clone
		Map<XEvent, Transition> mapEvtToTrans = new HashMap<XEvent, Transition>(selectedTrace.size());

		// create move only on model (with/without token)
		createMoveModelOnly(origNet, cOrigTrans, netResult, costPool, weight, mapOrigToClone, mapPlaceOrigToClone,
				mapCloneToOrig, predecessors, successors, classes);

		// add log only model
		Set<Place> finalLogPlaces = createMoveLogOnly(netResult, markResult, selectedTrace, costPool,
				weight[MOVEONLOGONLY], mapEvtToTrans, predecessors, successors, classes);

		// add synchronized move and synchronize without consuming tokens
		createSynchronizeMove(mapEvtToTrans, transitionMapping, classes, netResult, mapOrigToClone, mapCloneToOrig,
				predecessors, successors, costPool, weight[MOVESYNCHRONIZEDVIOLATING],
				weight[MOVESYNCHRONIZEDVIOLATINGPARTIALLY] == 1);

		// add marking
		for (Place p : m) {
			markResult.add(mapPlaceOrigToClone.get(p), m.occurrences(p));
		}
		markResult.add(costPool, selectedTrace.size() * weight[MOVEONLOGONLY]);

		return new Object[] { netResult, markResult, costPool, finalLogPlaces, mapCloneToOrig };
	}

	private void createSynchronizeMove(Map<XEvent, Transition> mapEvtToTrans,
			Map<XEventClass, List<Transition>> transitionMapping, XEventClasses classes, Petrinet netResult,
			Map<Transition, Transition> mapOrigToClone, Map<Transition, Transition> mapCloneToOrig,
			Map<Transition, Set<Place>> predecessors, Map<Transition, Set<Place>> successors, Place costPool,
			int violatingCost, boolean identifyPartialViolating) {
		for (XEvent evt : mapEvtToTrans.keySet()) {
			// extract its event class
			for (Transition origTrans : transitionMapping.get(classes.getClassOf(evt))) {
				Transition synchroTrans = netResult.addTransition(MOVESYNCHRONIZED + "|" + classes.getClassOf(evt));

				// add mapping to original transition
				mapCloneToOrig.put(synchroTrans, origTrans);

				// model side
				Transition modelTrans = mapOrigToClone.get(origTrans);
				for (Place clonePlace : predecessors.get(modelTrans)) { // update predecessor 
					netResult.addArc(clonePlace, synchroTrans);
				}
				for (Place clonePlace : successors.get(modelTrans)) { // update successor 
					netResult.addArc(synchroTrans, clonePlace);
				}

				Set<Transition> violatingTransitions = null;
				if (violatingCost > 0) {
					violatingTransitions = new HashSet<Transition>();

					if (identifyPartialViolating) {
						// get all place predecessor
						Set<Place> setPlace = predecessors.get(modelTrans);
						Place[] arrPlace = new Place[setPlace.size()];
						int idx = 0;
						for (Place place : setPlace) {
							arrPlace[idx] = place;
							idx++;
						}

						for (int i = 1; i < arrPlace.length; i++) {
							CombinationGenerator combGen = new CombinationGenerator(arrPlace.length, i);
							while (combGen.hasMore()) {
								Transition violatingTrans = netResult.addTransition(MOVESYNCHRONIZEDVIOLATING + "|"
										+ classes.getClassOf(evt));

								for (int j : combGen.getNext()) {
									netResult.addArc(arrPlace[j], violatingTrans);
								}
								violatingTransitions.add(violatingTrans);
							}
						}
					}

					// add transition that violates without taking any tokens
					Transition violatingTrans = netResult.addTransition(MOVESYNCHRONIZEDVIOLATING + "|"
							+ classes.getClassOf(evt));
					violatingTransitions.add(violatingTrans);

					mapCloneToOrig.put(violatingTrans, origTrans);
				}

				// log side
				Transition logTrans = mapEvtToTrans.get(evt);
				for (Place clonePlace : predecessors.get(logTrans)) { // update predecessor 
					netResult.addArc(clonePlace, synchroTrans);

					if (violatingTransitions != null) {
						for (Transition violatingTrans : violatingTransitions) {
							netResult.addArc(clonePlace, violatingTrans);
						}
					}
				}

				for (Place clonePlace : successors.get(logTrans)) { // update successor 
					netResult.addArc(synchroTrans, clonePlace);

					if (violatingTransitions != null) {
						for (Transition violatingTrans : violatingTransitions) {
							netResult.addArc(violatingTrans, clonePlace);
						}
					}
				}

				// add transitions that connect to cost pool
				if (violatingTransitions != null) {
					for (Transition violatingTrans : violatingTransitions) {
						netResult.addArc(costPool, violatingTrans, violatingCost);
					}
				}

			}
		}
	}

	private Set<Place> createMoveLogOnly(Petrinet netResult, Marking markResult, List<XEvent> selectedTrace,
			Place costPool, int cost, Map<XEvent, Transition> mapEvtToTrans, Map<Transition, Set<Place>> predecessors,
			Map<Transition, Set<Place>> successors, XEventClasses classes) {

		// partial order between events are determined by timestamp (can also be other things)
		// assume that event access already ordered by timestamp
		XTimeExtension xte = XTimeExtension.instance();

		Date latestTimestamp = new Date(Long.MIN_VALUE); // assumption, timestamp is always after the date

		// pointer to last expanded transition
		Set<Transition> lastExpandedTransitions = null;

		Set<XEvent> parEvts = new HashSet<XEvent>(); // events with the same order

		// finalLogPlace
		Set<Place> finalLogPlaces = new HashSet<Place>();

		for (XEvent event : selectedTrace) {
			if (xte.extractTimestamp(event).after(latestTimestamp)) {
				if (!parEvts.isEmpty()) {
					// cleanup parEvts
					lastExpandedTransitions = addLogTransitions(netResult, markResult, lastExpandedTransitions,
							parEvts, costPool, cost, mapEvtToTrans, predecessors, successors, false, finalLogPlaces,
							classes);
				}
				parEvts.add(event);
				latestTimestamp = xte.extractTimestamp(event);
			} else { // paralellism happen
				parEvts.add(event);
			}
		}

		if (!parEvts.isEmpty()) { // handle last transition
			addLogTransitions(netResult, markResult, lastExpandedTransitions, parEvts, costPool, cost, mapEvtToTrans,
					predecessors, successors, true, finalLogPlaces, classes);
		}
		for (Place pl : finalLogPlaces) {
			pl.getAttributeMap().put(AttributeMap.FILLCOLOR, Color.CYAN);
		}
		return finalLogPlaces;
	}

	private Set<Transition> addLogTransitions(Petrinet netResult, Marking markResult,
			Set<Transition> lastExpandedTransitions, Set<XEvent> parEvts, Place costPool, int cost,
			Map<XEvent, Transition> mapEvtToTrans, Map<Transition, Set<Place>> predecessors,
			Map<Transition, Set<Place>> successors, boolean finalize, Set<Place> finalLogPlaces, XEventClasses classes) {

		if (lastExpandedTransitions == null) {
			// this is the first events, update initial marking 
			Set<Transition> setEvTrans = new HashSet<Transition>(parEvts.size());
			for (XEvent ev : parEvts) {
				// connect the transition to lastExpandedTransition
				Place inPlace = netResult.addPlace(MOVEONLOGONLY + "|i|" + classes.getClassOf(ev));

				// create transition
				Transition evTrans = netResult.addTransition(MOVEONLOGONLY + "|" + classes.getClassOf(ev));

				netResult.addArc(inPlace, evTrans);

				setEvTrans.add(evTrans);

				markResult.add(inPlace); // update initial marking

				mapEvtToTrans.put(ev, evTrans); // update mapping

				// update cost arc
				if (cost > 0) {
					netResult.addArc(costPool, evTrans, cost);
				}

				// update predecessors
				Set<Place> inputPlaceSet = new HashSet<Place>(1);
				inputPlaceSet.add(inPlace);
				predecessors.put(evTrans, inputPlaceSet);

				if (finalize) {
					// create additional output place set
					Place outPlace = netResult.addPlace(MOVEONLOGONLY + "|i|" + classes.getClassOf(ev));

					netResult.addArc(evTrans, outPlace);

					if (finalLogPlaces != null) {
						finalLogPlaces.add(outPlace);
					}

					Set<Place> outputFinal = new HashSet<Place>(1);
					outputFinal.add(outPlace);
					successors.put(evTrans, outputFinal); // update successors
				}
			}

			parEvts.clear();

			return setEvTrans;

		} else { // not the first events 
			assert (lastExpandedTransitions.size() > 0);
			Set<Transition> setEvTrans = new HashSet<Transition>(parEvts.size());

			for (XEvent ev : parEvts) {
				// create a transition to represent event
				Transition evTrans = netResult.addTransition(MOVEONLOGONLY + "|" + classes.getClassOf(ev));

				setEvTrans.add(evTrans);

				mapEvtToTrans.put(ev, evTrans); // update mapping

				// connect the transition to lastExpandedTransitions
				for (Transition prevTrans : lastExpandedTransitions) {
					// connect the transition to lastExpandedTransition
					Place inPlace = netResult.addPlace(MOVEONLOGONLY + "|i|" + classes.getClassOf(ev));
					netResult.addArc(prevTrans, inPlace);

					// update successor of prevTrans
					if (successors.containsKey(prevTrans)) {
						successors.get(prevTrans).add(inPlace);
					} else {
						// create new set 
						Set<Place> setOut = new HashSet<Place>();
						setOut.add(inPlace);
						successors.put(prevTrans, setOut);
					}

					netResult.addArc(inPlace, evTrans);

					// update predecessors of current trans
					if (predecessors.containsKey(evTrans)) {
						predecessors.get(evTrans).add(inPlace);
					} else {
						// create new set 
						Set<Place> setInCurr = new HashSet<Place>();
						setInCurr.add(inPlace);
						predecessors.put(evTrans, setInCurr);
					}

				}

				if (cost > 0) {
					netResult.addArc(costPool, evTrans, cost);
				}

				if (finalize) {
					// create additional output place set
					Place outPlace = netResult.addPlace(MOVEONLOGONLY + "|i|" + classes.getClassOf(ev));

					netResult.addArc(evTrans, outPlace);

					if (finalLogPlaces != null) {
						finalLogPlaces.add(outPlace);
					}

					Set<Place> outputFinal = new HashSet<Place>(1);
					outputFinal.add(outPlace);
					successors.put(evTrans, outputFinal);
				}
			}

			parEvts.clear();

			return setEvTrans;
		}

	}

	/**
	 * 
	 * @param context
	 * @param resultNet
	 * @param mResultNet
	 * @param costPool
	 * @param finalLogPlaces
	 * @param allPaths
	 * @param progBar
	 * @param id
	 * @param isGUI
	 * @return array object [List<Transition>, Total Time (nanoseconds) in long
	 *         datatype]
	 */
	@SuppressWarnings("unchecked")
	private Object[] identifyMinimumCostPath(PluginContext context, Petrinet resultNet, Marking mResultNet,
			Place costPool, Set<Place> finalLogPlaces, boolean allPaths, Progress progBar, int id, boolean isGUI) {
		// generate temp file that contains 
		PackageManager manager = PackageManager.getInstance();

		try {
			PackageDescriptor[] packages = null;
			packages = manager.findOrInstallPackages("PNetReplayer");
			String saraPath = packages[0].getLocalPackageDirectory().getAbsolutePath() + File.separator + "lib"
					+ File.separator;

			// check os
			// only support win32 and win64
			String os = System.getProperty("os.name").toLowerCase();
			if (os.indexOf("win") >= 0) {
				if (System.getenv("ProgramFiles(x86)") != null) {
					saraPath += "sara-win64" + File.separator;
				} else if (System.getProperty("os.arch").indexOf("64") != -1) {
					saraPath += "sara-win64" + File.separator;
				} else {
					saraPath += "sara-win32" + File.separator;
				}
			} else {
				// not supported
				if (isGUI) {
					context.log("This platform not supported. Currently only support windows 32/64 bit.");
				}
				context.getFutureResult(0).cancel(true);
				return null;
			}

			Object[] resultObjects = new Object[4];

			File petriFile = generateLolaPetrinet(context, resultNet, mResultNet, costPool, finalLogPlaces, saraPath
					+ String.valueOf(id) + "-net.lola", resultObjects);

			Map<Place, String> mapPlace = (Map<Place, String>) resultObjects[2]; // mapping from places to places in sara format
			Map<String, Transition> mapStrTrans = (Map<String, Transition>) resultObjects[3]; // mapping from transitions in sara format to transitions
			String cpStr = (String) resultObjects[0]; // cost place, represented in string
			Set<String> finalLogPlacesStr = (Set<String>) resultObjects[1]; // final log places, represented in string

			// replay parameters
			int upperBound = mResultNet.occurrences(costPool);
			int lowerBound = 0;
			double realCost = (lowerBound + upperBound) / 2; // real cost

			// helper variables
			Set<Integer> triedCost = new HashSet<Integer>();
			Set<Integer> failCost = new HashSet<Integer>();

			// counter based on trial
			int maxTrial = (int) Math.ceil(Math.log(upperBound) / Math.log(2)) + 2; // heuristic maximum trial
			int currTrial = 0;

			CancellationThread ct = new CancellationThread(context.getProgress());
			File taskFile = null;
			int tryCost;

			long totalTime = 0L;
			long startTime = 0L;
			do {
				tryCost = (int) Math.floor(realCost);
				triedCost.add(tryCost);
				taskFile = generateSaraTaskFile(saraPath + String.valueOf(id) + "-net-" + currTrial + ".sara",
						mResultNet, cpStr, finalLogPlacesStr, tryCost, ct, mapPlace, String.valueOf(id) + "-net.lola");

				// ask sara and retrieve result
				startTime = System.nanoTime();
				if (askSaraToCheck(taskFile, saraPath, ct, context)) {
					totalTime = System.nanoTime() - startTime;
					lowerBound = (int) Math.floor(realCost);
				} else {
					totalTime = System.nanoTime() - startTime;
					failCost.add(tryCost);
					upperBound = (int) Math.ceil(realCost);
				}
				realCost = (lowerBound + upperBound) / 2;

				taskFile.delete();
				currTrial++;
			} while ((upperBound > lowerBound) && (currTrial < maxTrial)
					&& (!triedCost.contains((int) Math.floor(realCost))));

			// retrieve the result
			Set<String[]> res = new HashSet<String[]>();
			if (!failCost.contains(upperBound)) { // upperbound might work
				taskFile = generateSaraTaskFile(saraPath + String.valueOf(id) + "-net-" + currTrial + ".sara",
						mResultNet, cpStr, finalLogPlacesStr, upperBound, ct, mapPlace, String.valueOf(id)
								+ "-net.lola");

				if (askSaraToCheck(taskFile, saraPath, ct, context)) {
					startTime = System.nanoTime();
					res = askSaraToReturnPaths(taskFile, saraPath, ct, context, allPaths);
					totalTime = System.nanoTime() - startTime;
				} else {
					taskFile.delete();
					taskFile = generateSaraTaskFile(saraPath + String.valueOf(id) + "-net-" + currTrial + ".sara",
							mResultNet, cpStr, finalLogPlacesStr, lowerBound, ct, mapPlace, String.valueOf(id)
									+ "-net.lola");
					startTime = System.nanoTime();
					res = askSaraToReturnPaths(taskFile, saraPath, ct, context, allPaths);
					totalTime = System.nanoTime() - startTime;
				}
			} else { // only lowerbound works
				taskFile = generateSaraTaskFile(saraPath + String.valueOf(id) + "-net-" + currTrial + ".sara",
						mResultNet, cpStr, finalLogPlacesStr, lowerBound, ct, mapPlace, String.valueOf(id)
								+ "-net.lola");
				startTime = System.nanoTime();
				res = askSaraToReturnPaths(taskFile, saraPath, ct, context, allPaths);
				totalTime = System.nanoTime() - startTime;
			}

			if (progBar != null) {
				progBar.inc();
				// context.log("Finishes in " + (System.nanoTime() - start) + " ns");
			}

			// translate result to transitions
			List<Transition> listTrans = new LinkedList<Transition>();
			if (res.size() > 0) { // for now, only take one case
				String[] arrRes = res.iterator().next();

				for (int j = 2; j < arrRes.length; j++) {
					listTrans.add(mapStrTrans.get(arrRes[j]));
				}

			}

			// remove unnecessary files
			taskFile.delete();
			petriFile.delete();

			return new Object[] { listTrans, (double) totalTime / ((double) 1000000) };

		} catch (Exception e) {
			context.log(e);
		}
		return null;
	}

	private File generateSaraTaskFile(String fileName, Marking mResultNet, String costPool, Set<String> finalLogPlaces,
			int maxCost, CancellationThread ct, Map<Place, String> mapPlace, String lolaFileName) {
		File res = new File(fileName);
		BufferedWriter bwcf = null;
		try {
			bwcf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(res)));
			bwcf.write("PROBLEM test:");
			bwcf.newLine();
			bwcf.write("GOAL REACHABILITY;");
			bwcf.newLine();
			bwcf.write("FILE " + lolaFileName + " TYPE LOLA;");
			bwcf.newLine();
			bwcf.write("INITIAL ");
			// write initial 
			char delim = ' ';
			for (Place p : mResultNet.baseSet()) {
				bwcf.write(delim);
				bwcf.write(mapPlace.get(p));
				bwcf.write(':');
				bwcf.write(String.valueOf(mResultNet.occurrences(p)));
				delim = ',';
			}
			bwcf.write(';');

			bwcf.newLine();
			bwcf.write("FINAL COVER ");
			for (String str : finalLogPlaces) {
				bwcf.write(str);
				bwcf.write(">1,");
			}
			bwcf.write(costPool);
			bwcf.write(">");
			bwcf.write(String.valueOf(maxCost));
			bwcf.write(";");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (bwcf != null) {
				try {
					bwcf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return res;
	}

	private boolean askSaraToCheck(File taskFile, String saraPath, CancellationThread ct, PluginContext context) {
		// call Sara and wait until it is finished
		ProcessBuilder b = new ProcessBuilder(saraPath + SARAFILE, "-i", taskFile.getAbsolutePath(), "-L", "1");
		b.directory(new File(saraPath));

		Process processSARA = null;
		boolean result = false;
		try {
			processSARA = b.start();
			processSARA.getOutputStream().close();
			processSARA.getErrorStream().close();
			ct.setProcess(processSARA);

			if (context.getProgress().isCancelled()) {
				throw new InterruptedException("Execution cancelled by user");
			}

			// read Sara's output
			BufferedReader br = new BufferedReader(new InputStreamReader(processSARA.getInputStream()));

			// parse Sara's output
			String line = "";
			while ((line = br.readLine()) != null) {
				if (line.startsWith("sara: SOLUTION")) {
					result = true;
					break;
				}
			}

			// closing
			br.close();
			processSARA.getInputStream().close();
			processSARA.destroy();
			processSARA = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (processSARA != null) {
				try {
					processSARA.getInputStream().close();
					processSARA.destroy();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		return result;
	}

	private Set<String[]> askSaraToReturnPaths(File taskFile, String saraPath, CancellationThread ct,
			PluginContext context, boolean allPaths) {
		// call LoLA and wait until it is finished
		ProcessBuilder b = null;
		if (!allPaths) {
			b = new ProcessBuilder(saraPath + SARAFILE, "-i", taskFile.getAbsolutePath());
		} else {
			b = new ProcessBuilder(saraPath + SARAFILE, "-i", taskFile.getAbsolutePath(), "-C", "-P");
		}
		b.directory(new File(saraPath));

		Set<String[]> setString = new HashSet<String[]>();
		Process processSARA = null;
		try {
			processSARA = b.start();
			processSARA.getOutputStream().close();
			processSARA.getErrorStream().close();
			ct.setProcess(processSARA);

			if (context.getProgress().isCancelled()) {
				throw new InterruptedException("Execution cancelled by user");
			}

			// read Sara's output
			BufferedReader br = new BufferedReader(new InputStreamReader(processSARA.getInputStream()));

			// parse Sara's output
			String line = "";
			while ((line = br.readLine()) != null) {
				if (line.startsWith("sara: SOLUTION")) {
					setString.add(line.split(" "));
				}
			}

			br.close();

			// closing
			processSARA.getInputStream().close();
			processSARA.waitFor();
			processSARA.destroy();
			processSARA = null;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (processSARA != null) {
				try {
					processSARA.getInputStream().close();
					processSARA.destroy();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		return setString;
	}

	private File generateLolaPetrinet(PluginContext context, Petrinet resultNet, Marking mResultNet, Place costPool,
			Set<Place> finalLogPlaces, String fileName, Object[] resultObjects) {
		File res = new File(fileName);
		BufferedWriter bwcf = null;
		try {
			// library
			bwcf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(res)));
			bwcf.write("PLACE");
			bwcf.newLine();

			// PLACE
			int placeID = 0;
			Collection<Place> colPlace = resultNet.getPlaces();
			Map<Place, String> mapPlace = new HashMap<Place, String>(colPlace.size());

			char delim = ' ';
			for (Place p : colPlace) {
				mapPlace.put(p, ("p" + placeID));
				bwcf.write(delim);
				bwcf.write("p");
				bwcf.write(String.valueOf(placeID));
				placeID++;
				delim = ',';
			}
			bwcf.write(';');
			bwcf.newLine();

			// MARKING
			bwcf.write("MARKING");
			bwcf.newLine();
			delim = ' ';
			for (Place p : mResultNet.baseSet()) {
				bwcf.write(delim);
				bwcf.write(mapPlace.get(p));
				bwcf.write(':');
				bwcf.write(String.valueOf(mResultNet.occurrences(p)));
				delim = ',';
			}
			bwcf.write(';');
			bwcf.newLine();

			// TRANSITION
			int transID = 0;
			Collection<Transition> colTrans = resultNet.getTransitions();
			Map<String, Transition> mapStrTrans = new HashMap<String, Transition>(colTrans.size());

			for (Transition trans : colTrans) {
				bwcf.write("TRANSITION ");
				bwcf.write("t");
				bwcf.write(String.valueOf(transID));
				bwcf.newLine();

				bwcf.write("CONSUME ");
				delim = ' ';
				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> inEdge : resultNet.getInEdges(trans)) {
					bwcf.write(delim);
					bwcf.write(mapPlace.get(inEdge.getSource()));
					bwcf.write(":");
					bwcf.write("" + ((Arc) inEdge).getWeight());
					delim = ',';
				}
				bwcf.write(';');
				bwcf.newLine();

				bwcf.write("PRODUCE ");
				delim = ' ';
				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> outEdge : resultNet
						.getOutEdges(trans)) {
					bwcf.write(delim);
					bwcf.write(mapPlace.get(outEdge.getTarget()));
					bwcf.write(":");
					bwcf.write("" + ((Arc) outEdge).getWeight());
					delim = ',';
				}
				bwcf.write(';');
				bwcf.newLine();
				bwcf.newLine();

				mapStrTrans.put("t" + transID, trans);
				transID++;
			}

			resultObjects[0] = mapPlace.get(costPool);
			Set<String> finalLogPlacesString = new HashSet<String>(finalLogPlaces.size());
			for (Place pl : finalLogPlaces) {
				finalLogPlacesString.add(mapPlace.get(pl));
			}

			resultObjects[1] = finalLogPlacesString;
			resultObjects[2] = mapPlace;
			resultObjects[3] = mapStrTrans;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (bwcf != null) {
				try {
					bwcf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return res;
	}

	private void createMoveModelOnly(Petrinet origNet, Collection<Transition> cOrigTrans, Petrinet netResult,
			Place costPool, int[] weight, Map<Transition, Transition> mapOrigToClone,
			Map<Place, Place> mapPlaceOrigToClone, Map<Transition, Transition> mapCloneToOrig,
			Map<Transition, Set<Place>> predecessors, Map<Transition, Set<Place>> successors, XEventClasses classes) {

		// move on model only, still with tokens
		for (Transition origTrans : cOrigTrans) {
			// create transition
			Transition cloneTrans;
			if (origTrans.isInvisible()) {
				cloneTrans = netResult.addTransition(MOVEONMODELONLYINVI + "|" + origTrans.getLabel());
			} else {
				cloneTrans = netResult.addTransition(MOVEONMODELONLYREAL + "|" + origTrans.getLabel());
			}
			mapOrigToClone.put(origTrans, cloneTrans);
			mapCloneToOrig.put(cloneTrans, origTrans);

			// add input places
			Set<Place> sp = new HashSet<Place>();
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : origNet.getInEdges(origTrans)) {
				Place clonePlace = null;
				if (!mapPlaceOrigToClone.containsKey(edge.getSource())) {
					clonePlace = netResult.addPlace(edge.getSource().getLabel());
					mapPlaceOrigToClone.put((Place) edge.getSource(), clonePlace);
				} else {
					clonePlace = mapPlaceOrigToClone.get(edge.getSource());
				}
				netResult.addArc(clonePlace, cloneTrans);
				sp.add(clonePlace);
			}
			predecessors.put(cloneTrans, sp);

			// add output places
			Set<Place> ss = new HashSet<Place>();
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : origNet.getOutEdges(origTrans)) {
				Place clonePlace = null;
				if (!mapPlaceOrigToClone.containsKey(edge.getTarget())) {
					clonePlace = netResult.addPlace(edge.getTarget().getLabel());
					mapPlaceOrigToClone.put((Place) edge.getTarget(), clonePlace);
				} else {
					clonePlace = mapPlaceOrigToClone.get(edge.getTarget());
				}
				netResult.addArc(cloneTrans, clonePlace);
				ss.add(clonePlace);

			}
			successors.put(cloneTrans, ss);

			// add arc to costpool
			if (origTrans.isInvisible()) {
				if (weight[MOVEONMODELONLYINVI] > 0) {
					netResult.addArc(costPool, cloneTrans, weight[MOVEONMODELONLYINVI]);
				}
			} else {
				if (weight[MOVEONMODELONLYREAL] > 0) {
					netResult.addArc(costPool, cloneTrans, weight[MOVEONMODELONLYREAL]);
				}
			}
		}
	}

	public String getHTMLInfo() {
		return "<html>This is an algorithm to calculate cost-based fitness between a log and a Petri net. <br/><br/>"
				+ "Given a trace and a classical Petri net (NOT reset/inhibitor net), this algorithm "
				+ "return a matching between the trace and an allowed firing sequence of the net with the "
				+ "least deviation cost using the synchronous-product-based technique. The firing sequence does not "
				+ "necessarily reach proper termination (possible final markings/dead markings) of the net." + "<br/>"
				+ "<br/> There is only a single cost for skipping "
				+ "any activity (single cost for any move on model), as well as the cost for "
				+ "inserting (move on log) activities. <br/><br/>"
				+ "All event classes in the trace that are not mapped to any transition are ignored completely "
				+ "and does not appear in the resulted matching. <br/><br/>"
				+ "The Sara tool (windows version), developed in University of Rostock, is used internally. "
				+ "Therefore, this plugin can only work in Windows environment. " + "<br/>" + "<br/>"
				+ "Reference: <br/>"
				+ "[1] Adriansyah, A., Sidorova, N., and Dongen, B.F. van (2011). Cost-based Fitness in Conformance "
				+ "Checking. In Proceedings of the 11th IEEE International Conference on Application of Concurrency "
				+ "to System Design (ACSD 2011). <br/>"
				+ "[2] The Sara tool, <a href='http://www.informatik.uni-rostock.de/~nl/wiki/tools/download'>"
				+ "http://www.informatik.uni-rostock.de/~nl/wiki/tools/download</a> </html>";
	}

	public PNRepResult replayLog(final PluginContext context, final PetrinetGraph net, final XLog log,
			TransEvClassMapping mapping, final IPNReplayParameter parameter) {
		SyncProductParam param = (SyncProductParam) parameter;

		final int[] weights = new int[5];
		weights[MOVEONLOGONLY] = param.getMoveOnLogOnly();
		weights[MOVEONMODELONLYINVI] = param.getMoveOnModelOnlyInvi();
		weights[MOVEONMODELONLYREAL] = param.getMoveOnModelOnlyReal();
		weights[MOVESYNCHRONIZEDVIOLATING] = param.getMoveSynchronizedViolating();
		weights[MOVESYNCHRONIZEDVIOLATINGPARTIALLY] = param.getMoveSynchronizedViolatingPartially() ? 1 : 0;
		final Marking m = param.getInitialMarking();

		// get mapping (or create) between Petri Net transition and class events in event logs
		final Map<XEventClass, List<Transition>> transitionMapping = getEncodedEventMapping(mapping);

		XEventClassifier classifier = mapping.getEventClassifier();
		final XLogInfo logInfo = XLogInfoFactory.createLogInfo(log, classifier);
		final XEventClasses classes = logInfo.getEventClasses();

		// required to produce correct output object to be visualized 
		final LogCounterSyncReplay counter = new LogCounterSyncReplay();

		// decide which cost to use for violating steps
		counter.setCosts(weights[MOVESYNCHRONIZEDVIOLATING], weights[MOVEONLOGONLY], weights[MOVEONMODELONLYINVI],
				weights[MOVEONMODELONLYREAL]);

		// set context
		final Progress progress = context.getProgress();
		progress.setValue(0);

		int threads = Runtime.getRuntime().availableProcessors() / 2 + 1;
		ExecutorService executor = Executors.newFixedThreadPool(threads);

		int index = 0;
		final Map<List<XEventClass>, List<Integer>> listTraces = new HashMap<List<XEventClass>, List<Integer>>();

		for (final XTrace trace : log) {
			// filter out unmapped events
			final List<XEvent> filteredTrace = getMappedEventsList(trace, classes, transitionMapping);
			final List<XEventClass> listTrace = getListEventClass(filteredTrace, classes);
			if (listTraces.containsKey(listTrace)) {
				listTraces.get(listTrace).add(log.indexOf(trace));
			} else {
				listTraces.put(listTrace, new ArrayList<Integer>());
				if (parameter.isGUIMode()) {
					context.log("Replaying trace: " + index + " of length " + filteredTrace.size());
				}
				final int id = index;
				index++;
				executor.execute(new Runnable() {

					public void run() {

						Object[] pairOfListAndMapping = replayXTracePrivateAssumingFullyMappedTrace(context, net, m,
								filteredTrace, logInfo, classes, transitionMapping, weights, id, parameter.isGUIMode());

						if (pairOfListAndMapping != null) {
							@SuppressWarnings("unchecked")
							List<Transition> listTrans = (List<Transition>) pairOfListAndMapping[0];
							Double totalTime = (Double) pairOfListAndMapping[1];
							@SuppressWarnings("unchecked")
							Map<Transition, Transition> mapCloneToOrig = (Map<Transition, Transition>) pairOfListAndMapping[2];

							if (progress.isCancelled()) {
								return;
							}

							// create list of string and list of step types
							// create selected traces representation
							List<StepTypes> listStep = new LinkedList<StepTypes>();
							List<Object> nodeInstances = new LinkedList<Object>();
							String label = "";
							for (Transition t : listTrans) {
								label = t.getLabel();
								if (label.startsWith(Integer.toString(MOVESYNCHRONIZED))) {
									listStep.add(StepTypes.LMGOOD);
									nodeInstances.add(mapCloneToOrig.get(t));
								} else if (label.startsWith(Integer.toString(MOVEONMODELONLYINVI))) {
									listStep.add(StepTypes.MINVI);
									nodeInstances.add(mapCloneToOrig.get(t));
								} else if (label.startsWith(Integer.toString(MOVEONMODELONLYREAL))) {
									listStep.add(StepTypes.MREAL);
									nodeInstances.add(mapCloneToOrig.get(t));
								} else if (label.startsWith(Integer.toString(MOVEONLOGONLY))) {
									listStep.add(StepTypes.L);
									nodeInstances.add(label.substring(2));
								} else if (label.startsWith(Integer.toString(MOVESYNCHRONIZEDVIOLATING))) {
									listStep.add(StepTypes.LMNOGOOD);
									nodeInstances.add(mapCloneToOrig.get(t));
								}

							}

							counter.add(listTrace, nodeInstances, listStep, log.indexOf(trace), totalTime);

							progress.inc();

						} else {
							// no pair of listing and mapping
							progress.inc();
						}
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
			if (parameter.isGUIMode()) {
				context.log(e);
			}
			return null;
		}

		for (List<XEventClass> listTrace : listTraces.keySet()) {
			for (Integer traceIndex : listTraces.get(listTrace)) {
				counter.inc(listTrace, traceIndex);
			}
		}

		// check if encoding already exists
		return new PNRepResultImpl(counter.getResult());
	}

	public IPNReplayParamProvider constructParamProvider(PluginContext context, PetrinetGraph net, XLog log,
			TransEvClassMapping mapping) {
		if (net instanceof Petrinet) {
			return new SyncProductParamProvider(context, (Petrinet) net);
		} else {
			return null;
		}
	}

	public boolean isReqWOParameterSatisfied(PluginContext context, PetrinetGraph net, XLog log,
			TransEvClassMapping mapping) {
		if (net instanceof Petrinet) {
			// only support win32 and win64
			String os = System.getProperty("os.name").toLowerCase();
			return (os.indexOf("win") >= 0);
		}
		return false;
	}

	public boolean isAllReqSatisfied(PluginContext context, PetrinetGraph net, XLog log, TransEvClassMapping mapping,
			IPNReplayParameter parameter) {
		if (isReqWOParameterSatisfied(context, net, log, mapping)) {
			if (parameter instanceof SyncProductParam) {
				SyncProductParam param = (SyncProductParam) parameter;
				if ((param.getInitialMarking() != null) && (param.getMoveOnLogOnly() != null)
						&& (param.getMoveOnModelOnlyInvi() != null) && (param.getMoveOnModelOnlyReal() != null)
						&& (param.getMoveSynchronizedViolating() != null)
						&& (param.getMoveSynchronizedViolatingPartially() != null)) {
					// all cost should be >= 0, except getMoveSynchronizedViolating that
					// can be negative if no violating move is allowed
					return (param.getMoveOnLogOnly() >= 0) && (param.getMoveOnModelOnlyInvi() >= 0)
							&& (param.getMoveOnModelOnlyReal() >= 0);
				}
			}
		}
		;
		return false;
	}

}

class CancellationThread {

	private Thread thread;

	private final Progress progress;

	// Start cancellation thread
	public CancellationThread(Progress progress) {
		this.progress = progress;
	}

	public void setProcess(final Process process) {
		thread = new Thread(new Runnable() {

			public void run() {
				while (true) {
					// If canceled, destroy process
					if (progress.isCancelled()) {
						process.destroy();
					}
					// Check if process finished. Has to be done with exception 
					// handling
					try {
						process.exitValue();
						return;
					} catch (IllegalThreadStateException e) {
						// Not finished yet,
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
			}

		});
		thread.start();

	}
}
