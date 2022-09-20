/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.util.codec.EncPNWSetFinalMarkings;

/**
 * @author aadrians
 * Oct 24, 2011
 *
 */
public class AbstractReplayerBasicFunctionProvider {
	/**
	 * Get mapping from event class to encoded transition
	 * 
	 * @param mapping
	 * @param encPN
	 * @return
	 */
	protected Map<XEventClass, Set<Integer>> getMappingEventClass2EncTrans(TransEvClassMapping mapping,
			EncPNWSetFinalMarkings encPN) {
		Map<XEventClass, Set<Integer>> mapEvClass2Trans = new HashMap<XEventClass, Set<Integer>>();
		for (Transition transition : mapping.keySet()) {
			Set<Integer> setTrans = mapEvClass2Trans.get(mapping.get(transition));
			if (setTrans == null) {
				setTrans = new HashSet<Integer>();
				mapEvClass2Trans.put(mapping.get(transition), setTrans);
			}
			setTrans.add(encPN.getEncOf(transition));
		}
		return mapEvClass2Trans;
	}
	
	/**
	 * Get mapping from encoded transition to cost of move on model only
	 * 
	 * @param encodedPN
	 * @param mapTrans2Cost
	 * @return
	 */
	protected Map<Integer, Integer> getTransViolationCosts(EncPNWSetFinalMarkings encodedPN,
			Map<Transition, Integer> mapTrans2Cost) {
		Set<Transition> setTrans = mapTrans2Cost.keySet();
		Map<Integer, Integer> res = new HashMap<Integer, Integer>(setTrans.size());
		for (Transition t : setTrans) {
			res.put(encodedPN.getEncOf(t), mapTrans2Cost.get(t));
		}
		return res;
	}

	/**
	 * Return transitions that are enabled in particular marking
	 * 
	 * @param encodedPN
	 * @param marking
	 * @param mapArc2Weight
	 * @return
	 */
	protected SortedSet<Integer> getEnabledTransitions(EncPNWSetFinalMarkings encodedPN, Map<Integer, Integer> marking,
			Map<Integer, Map<Integer, Integer>> mapArc2Weight) {
		// get continuation from marking
		SortedSet<Integer> enabledTransitions = new TreeSet<Integer>();
		for (Integer place : marking.keySet()) {
			Map<Integer, Integer> succTrans = mapArc2Weight.get(place);
			if (succTrans != null) {
				enabledTransitions.addAll(succTrans.keySet());
			}
		}
		// filter out tobechecked
		Iterator<Integer> it = enabledTransitions.iterator();
		Integer tempNumMarking;
		iterateTransition: while (it.hasNext()) {
			Integer transition = it.next();

			// consider inhibitor
			Set<Integer> inhibitors = encodedPN.getInhibitorsOf(transition);
			if (inhibitors != null) {
				for (Integer inh : inhibitors) {
					tempNumMarking = marking.get(inh);
					if ((tempNumMarking != null) && (!tempNumMarking.equals(0))) {
						// marked
						it.remove();
						continue iterateTransition;
					}
				}
			}

			Set<Integer> predecessors = encodedPN.getPredecessorsOf(transition);
			if (predecessors != null) {
				for (Integer place : predecessors) {
					Integer numTokens = marking.get(place);
					if (numTokens != null) {
						if (numTokens < mapArc2Weight.get(place).get(transition)) {
							it.remove();
							continue iterateTransition;
						}
					} else {
						// not sufficient token
						it.remove();
						continue iterateTransition;
					}
				}
			}
		}
		return enabledTransitions;
	}
	

	/**
	 * Identify the enabled transitions and update marking map
	 * 
	 * @param encMarking
	 * @param mapMarking2EnabledTrans
	 * @param encodedPN
	 * @param mapArc2Weight
	 * @return
	 */
	protected synchronized SortedSet<Integer> identifyEnabledTransitions(Integer encMarking,
			Map<Integer, Map<Integer, Integer>> mapInt2Marking,
			Map<Integer, SortedSet<Integer>> mapMarking2EnabledTrans, EncPNWSetFinalMarkings encodedPN,
			Map<Integer, Map<Integer, Integer>> mapArc2Weight) {
		SortedSet<Integer> transSet = mapMarking2EnabledTrans.get(encMarking);
		if (transSet == null) {
			transSet = getEnabledTransitions(encodedPN, mapInt2Marking.get(encMarking), mapArc2Weight);
			mapMarking2EnabledTrans.put(encMarking, transSet);
		}
		return transSet;
	}
	
	/**
	 * return true if currEncMarking is one of the end markings or if there is
	 * no other transitions enabled in this marking
	 * 
	 * @param currEncMarking
	 * @param encFinalMarkings
	 * @param encodedPN
	 * @param mapInt2Marking
	 * @param mapArc2Weight
	 * @return
	 */
	protected synchronized boolean isEndOfModel(int currEncMarking, Set<Integer> encFinalMarkings,
			EncPNWSetFinalMarkings encodedPN, Map<Integer, Map<Integer, Integer>> mapInt2Marking,
			Map<Integer, SortedSet<Integer>> mapMarking2EnabledTrans, Map<Integer, Map<Integer, Integer>> mapArc2Weight) {
		return (encFinalMarkings.contains(currEncMarking) || (identifyEnabledTransitions(currEncMarking,
				mapInt2Marking, mapMarking2EnabledTrans, encodedPN, mapArc2Weight).isEmpty()));
	}
	
	/**
	 * get encoded marking of provided newMarking. Create new encoding if there
	 * is no newMarking.
	 * 
	 * @param newMarking
	 * @param mapMarking2Int
	 * @param mapInt2Marking
	 * @param numGenerator
	 * @return
	 */
	private synchronized Integer getEncodedMarking(Map<Integer, Integer> newMarking,
			Map<Map<Integer, Integer>, Integer> mapMarking2Int, Map<Integer, Map<Integer, Integer>> mapInt2Marking,
			Random numGenerator) {
		Integer newMarkingIndex = mapMarking2Int.get(newMarking);
		if (newMarkingIndex == null) {
			int index = numGenerator.nextInt();
			while (mapInt2Marking.get(index) != null) {
				index = numGenerator.nextInt();
			}
			mapMarking2Int.put(newMarking, index);
			mapInt2Marking.put(index, newMarking);
			newMarkingIndex = index;
		}
		return newMarkingIndex;
	}
	
	/**
	 * Fire a transition and get an index that represent the marking obtained
	 * This method also update the encoding of marking
	 * 
	 * @param currEncMarking
	 * @param trans
	 * @param mapInt2Marking
	 * @param mapMarking2Int
	 * @param numGenerator
	 * @param mapFiringTransitions
	 * @param encodedPN
	 * @param mapArc2Weight
	 * @return
	 */
	protected synchronized Integer fireTransition(int currEncMarking, Integer trans,
			Map<Integer, Map<Integer, Integer>> mapInt2Marking, Map<Map<Integer, Integer>, Integer> mapMarking2Int,
			Random numGenerator, Map<Integer, Map<Integer, Integer>> mapFiringTransitions,
			EncPNWSetFinalMarkings encodedPN, Map<Integer, Map<Integer, Integer>> mapArc2Weight) {
		Map<Integer, Integer> mapFiring = mapFiringTransitions.get(currEncMarking);
		if (mapFiring == null) {
			mapFiring = new HashMap<Integer, Integer>(3);
			mapFiringTransitions.put(currEncMarking, mapFiring);
		}

		Integer newMarkingIndex = mapFiring.get(trans);
		if (newMarkingIndex == null) { // transition never been fired from this marking
			Map<Integer, Integer> newMarking = new HashMap<Integer, Integer>(mapInt2Marking.get(currEncMarking));
			for (Integer predecessor : encodedPN.getPredecessorsOf(trans)) {
				int newNumToken = newMarking.get(predecessor) - mapArc2Weight.get(predecessor).get(trans);
				if (newNumToken > 0) {
					newMarking.put(predecessor, newNumToken);
				} else {
					newMarking.remove(predecessor);
				}
			}
			;

			// process reset arcs
			Set<Integer> resets = encodedPN.getResetsOf(trans);
			if (resets != null) {
				for (Integer reset : resets) {
					newMarking.remove(reset);
				}
			}

			// add the rest
			Map<Integer, Integer> successorMap = mapArc2Weight.get(trans);
			if (successorMap != null) {
				for (Integer place : successorMap.keySet()) {
					Integer numTokens = newMarking.get(place);
					if (numTokens == null) {
						newMarking.put(place, successorMap.get(place));
					} else {
						newMarking.put(place, newMarking.get(place) + successorMap.get(place));
					}
				}
			}

			newMarkingIndex = getEncodedMarking(newMarking, mapMarking2Int, mapInt2Marking, numGenerator);

			mapFiring.put(trans, newMarkingIndex);
		}
		return newMarkingIndex;
	}
	
	/**
	 * Get mapping from encoded transition to event class
	 * 
	 * @param mapping
	 * @param encodedPN
	 * @return
	 */
	protected Map<Integer, XEventClass> getMappingEncTrans2EncEventClass(TransEvClassMapping mapping,
			EncPNWSetFinalMarkings encodedPN) {
		Map<Integer, XEventClass> res = new HashMap<Integer, XEventClass>();
		for (Transition trans : mapping.keySet()) {
			res.put(encodedPN.getEncOf(trans), mapping.get(trans));
		}
		return res;
	}
	
	/**
	 * Get mapping from encoded transitions to event classes
	 * 
	 * @param mapEvClass2EncTrans
	 * @return
	 */
	protected Map<Integer, XEventClass> getMappingEncTrans2EvClass(Map<XEventClass, Set<Integer>> mapEvClass2EncTrans) {
		Map<Integer, XEventClass> res = new HashMap<Integer, XEventClass>();
		for (XEventClass evClass : mapEvClass2EncTrans.keySet()) {
			for (Integer encTrans : mapEvClass2EncTrans.get(evClass)) {
				res.put(encTrans, evClass);
			}
		}
		return res;
	}

	/**
	 * Get enabled activities from the particular marking try to see the
	 * directly enabled activities iteratively, redo invisible transition firing
	 * 
	 * @param m
	 * @param encodedPN
	 * @param encInvisTransitions
	 * @param mapEncTrans2EvClass
	 * @return
	 */
	protected Set<XEventClass> getEnabledActivities(Map<Integer, Integer> m, EncPNWSetFinalMarkings encodedPN,
			Set<Integer> encInvisTransitions, Map<Integer, XEventClass> mapEncTrans2EvClass) {
		// result
		Set<XEventClass> enabledEvClass = new HashSet<XEventClass>();

		// utilities
		Map<Integer, Map<Integer, Integer>> mapArc2Weight = encodedPN.getMapArc2Weight();

		// check possible inverse execution of invisible transitions
		Set<Integer> uncheckedInvisTrans = new HashSet<Integer>(encInvisTransitions);

		// list for forward execution
		List<Map<Integer, Integer>> listForwardMarking = new LinkedList<Map<Integer, Integer>>();
		List<Integer> listFireableTransition = new LinkedList<Integer>();

		Map<Integer, Integer> currMarking = null;

		// inverse execution
		List<Map<Integer, Integer>> listInverseMarking = new LinkedList<Map<Integer, Integer>>();
		listInverseMarking.add(new HashMap<Integer, Integer>(m));
		do {
			currMarking = listInverseMarking.remove(0);

			// get directly enabled 
			Set<Integer> setEnabledForwardTransitions = getEnabledTransitions(encodedPN, currMarking, mapArc2Weight);
			for (Integer enabledTrans : setEnabledForwardTransitions) {
				if (((Transition) encodedPN.getPetrinetNodeOf(enabledTrans)).isInvisible()) {
					listForwardMarking.add(new HashMap<Integer, Integer>(currMarking));
					listFireableTransition.add(enabledTrans);
				} else {
					enabledEvClass.add(mapEncTrans2EvClass.get(enabledTrans));
				}
			}

			// get reversible invisible transitions
			for (Integer reversibleTrans : getEnabledInverseTransitions(encodedPN, currMarking, mapArc2Weight,
					uncheckedInvisTrans)) {
				Map<Integer, Integer> newMarking = new HashMap<Integer, Integer>(currMarking);

				Map<Integer, Integer> outputPlaces = mapArc2Weight.get(reversibleTrans);
				for (Integer outputPlace : outputPlaces.keySet()) {
					Integer numTokens = newMarking.get(outputPlace);
					if (numTokens > outputPlaces.get(outputPlace)) {
						outputPlaces.put(outputPlace, numTokens - outputPlaces.get(outputPlace));
					} else {
						newMarking.remove(outputPlace);
					}
				}

				for (Integer inputPlace : encodedPN.getPredecessorsOf(reversibleTrans)) {
					Integer numTokens = newMarking.get(inputPlace);
					if (numTokens != null) {
						newMarking.put(inputPlace, numTokens + mapArc2Weight.get(inputPlace).get(reversibleTrans));
					} else {
						newMarking.put(inputPlace, mapArc2Weight.get(inputPlace).get(reversibleTrans));
					}
				}

				// insert reversible marking, update checked transitions
				uncheckedInvisTrans.remove(reversibleTrans);
				listInverseMarking.add(newMarking);
			}
			;
		} while (listInverseMarking.size() > 0);

		// move forward
		while (listFireableTransition.size() > 0) {
			// fire the transition
			Integer trans2Fire = listFireableTransition.remove(0);
			Map<Integer, Integer> marking = listForwardMarking.remove(0);

			for (Integer inputPlace : encodedPN.getPredecessorsOf(trans2Fire)) {
				Integer numTokens = marking.get(inputPlace) - mapArc2Weight.get(inputPlace).get(trans2Fire);
				if (numTokens > 0) {
					marking.remove(inputPlace);
				} else {
					marking.put(inputPlace, numTokens);
				}
			}

			// add tokens
			Map<Integer, Integer> outputArcs = mapArc2Weight.get(trans2Fire);
			for (Integer outputPlace : outputArcs.keySet()) {
				Integer numTokens = marking.get(outputPlace);
				if (numTokens != null) {
					marking.put(outputPlace, numTokens + outputArcs.get(outputPlace));
				} else {
					marking.put(outputPlace, outputArcs.get(outputPlace));
				}
			}

			// check if there is any enabled transition
			Set<Integer> enabledTrans = getEnabledTransitions(encodedPN, marking, mapArc2Weight);
			for (Integer t : enabledTrans) {
				if (((Transition) encodedPN.getPetrinetNodeOf(t)).isInvisible()) {
					if (uncheckedInvisTrans.contains(t)) {
						// try to execute it again
						listFireableTransition.add(t);
						listForwardMarking.add(new HashMap<Integer, Integer>(marking));
					}
				} else {
					enabledEvClass.add(mapEncTrans2EvClass.get(t));
				}
			}
		}

		return enabledEvClass;
	}
	
	/**
	 * Return member of uncheckedInvisTrans that can be "inversed"
	 * 
	 * @param encodedPN
	 * @param m
	 * @param mapArc2Weight
	 * @param uncheckedInvisTrans
	 * @return
	 */
	private Set<Integer> getEnabledInverseTransitions(EncPNWSetFinalMarkings encodedPN, Map<Integer, Integer> m,
			Map<Integer, Map<Integer, Integer>> mapArc2Weight, Set<Integer> uncheckedInvisTrans) {
		Set<Integer> res = new HashSet<Integer>();
		iterateTrans: for (Integer encTrans : uncheckedInvisTrans) {
			// get all successor of encTrans
			Map<Integer, Integer> outputArcs = mapArc2Weight.get(encTrans);
			for (Integer encOutputPlace : outputArcs.keySet()) {
				Integer numCurrTokens = m.get(encOutputPlace);
				if (numCurrTokens != null) {
					if (numCurrTokens < mapArc2Weight.get(encTrans).get(encOutputPlace)) {
						continue iterateTrans;
					}
				} else {
					continue iterateTrans;
				}
			}
			res.add(encTrans);
		}

		return res;
	}
	
	/**
	 * get list of event class. Record the indexes of non-mapped event classes.
	 * 
	 * @param trace
	 * @param classes
	 * @param mapEvClass2Trans
	 * @param listMoveOnLog
	 * @return
	 */
	protected List<XEventClass> getListEventClass(XTrace trace, XEventClasses classes,
			Map<XEventClass, Set<Integer>> mapEvClass2Trans, List<Pair<Integer, XEventClass>> listMoveOnLog) {
		int inverseIndex = trace.size() - 1;
		List<XEventClass> res = new LinkedList<XEventClass>();
		for (XEvent evt : trace) {
			XEventClass evClass = classes.getClassOf(evt);
			res.add(evClass);
			if (mapEvClass2Trans.get(evClass) == null) {
				listMoveOnLog.add(0, new Pair<Integer, XEventClass>(inverseIndex, evClass));
			}
			inverseIndex--;
		}
		return res;
	}
}
