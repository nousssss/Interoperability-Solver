/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.rpstwrapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.plugins.petrinet.replayer.util.codec.EncPNWSetFinalMarkings;

/**
 * Assumption: A-All arcs are weighed 1 B-The net is sound workflow net (one
 * entry node and one exit node is required by the implementation of RPST)
 * 
 * @author aadrians
 * 
 */
public class RPSTConsultant {
	/**
	 * Main information that is calculated by this class
	 */
	private Map<Integer, Map<Integer, Integer>> mapEncMarking2RequiredSuccessors;
	private Map<Integer, Set<XEventClass>> mapEncMarking2ImpossibleSuccessors;

	private RPSTTreeWrapper rpst;

	/**
	 * Default constructor
	 * 
	 * @param rpst
	 */
	public RPSTConsultant(RPSTTreeWrapper rpst) {
		this.rpst = rpst;
		this.mapEncMarking2RequiredSuccessors = new HashMap<Integer, Map<Integer, Integer>>();
		this.mapEncMarking2ImpossibleSuccessors = new HashMap<Integer, Set<XEventClass>>(3);
	}

	/**
	 * public method to get required successors (XEventClass), given a marking
	 * 
	 * @param encMarking
	 * @param mapInt2Marking
	 * @param encodedPN
	 * @param mapEncTrans2EvClass
	 * @return
	 */
	public synchronized Map<Integer, Integer> getRequiredSuccessors(Integer encMarking,
			Map<Integer, Map<Integer, Integer>> mapInt2Marking, EncPNWSetFinalMarkings encodedPN) {
		Map<Integer, Integer> res = mapEncMarking2RequiredSuccessors.get(encMarking);
		if (res == null) {
			// search for it
			res = searchRequiredSuccessorsFromThisMarking(encMarking, mapInt2Marking, encodedPN);
			mapEncMarking2RequiredSuccessors.put(encMarking, res);
		}
		return res;
	}

	/**
	 * Get xevent class that could have never occur, given a marking
	 * Status: not implemented yet
	 * 
	 * @param encMarking
	 * @param mapInt2Marking
	 * @param encodedPN
	 * @param mapEncTrans2EvClass
	 * @return
	 */
	public synchronized Set<XEventClass> getImpossibleSuccessors(Integer encMarking,
			Map<Integer, Map<Integer, Integer>> mapInt2Marking, EncPNWSetFinalMarkings encodedPN,
			Map<Integer, XEventClass> mapEncTrans2EvClass) {
		Set<XEventClass> res = mapEncMarking2ImpossibleSuccessors.get(encMarking);
		if (res == null) {
			// search for it
			res = searchImpossibleSuccessorsFromThisMarking(encMarking, mapInt2Marking, encodedPN, mapEncTrans2EvClass);
			mapEncMarking2ImpossibleSuccessors.put(encMarking, res);
		}
		return res;
	}

	/**
	 * return impossible successors from this marking TODO: complete this
	 * 
	 * @param encMarking
	 * @param mapInt2Marking
	 * @param encodedPN
	 * @param mapEncTrans2EvClass
	 * @return
	 */
	private Set<XEventClass> searchImpossibleSuccessorsFromThisMarking(Integer encMarking,
			Map<Integer, Map<Integer, Integer>> mapInt2Marking, EncPNWSetFinalMarkings encodedPN,
			Map<Integer, XEventClass> mapEncTrans2EvClass) {
		return new HashSet<XEventClass>(1);
	}

	/**
	 * Method to search successors (XEventClass), given a marking
	 * 
	 * @param encMarking
	 * @param mapInt2Marking
	 * @param encodedPN
	 * @param mapEncTrans2EvClass
	 * @return
	 */
	private Map<Integer, Integer> searchRequiredSuccessorsFromThisMarking(Integer encMarking,
			Map<Integer, Map<Integer, Integer>> mapInt2Marking, EncPNWSetFinalMarkings encodedPN) {
		// utility variables
		Map<Integer, Integer> realMarking = mapInt2Marking.get(encMarking);
		Map<Integer, Map<Integer, Integer>> mapArc2Weight = encodedPN.getMapArc2Weight();

		Map<Integer, Integer> requiredSucessors = new HashMap<Integer, Integer>();
		for (Integer outputPlace : realMarking.keySet()) {
			Map<Integer, Integer> successors = mapArc2Weight.get(outputPlace);
			if (successors != null) {
				if (successors.size() > 1) { // xor
					Set<Map<Integer, Integer>> setSuccessorTransitions = new HashSet<Map<Integer, Integer>>(
							successors.size());
					// there are more than one successor. Later, only use the result if predicted set of XEventclass
					// from all transitions are the same
					for (Integer transition : successors.keySet()) {
						setSuccessorTransitions.add(getRequiredSuccessorTrans(
								rpst.getRPSTNodeWSiblings(outputPlace, transition), encodedPN));
					}

					// get the intersection of all sets
					Iterator<Map<Integer, Integer>> it = setSuccessorTransitions.iterator();
					Map<Integer, Integer> result = it.next(); // base result
					Set<Integer> setResultKey = result.keySet();

					while (it.hasNext()) {
						Map<Integer, Integer> map = it.next();

						setResultKey.retainAll(map.keySet());

						for (Integer key : setResultKey) {
							if (result.get(key) < map.get(key)) {
								result.put(key, map.get(key));
							} // else, do nothing
						}
					}

					if (!setResultKey.isEmpty()) {
						for (Integer key : setResultKey) {
							Integer finalRes = requiredSucessors.get(key);
							if (finalRes == null) {
								requiredSucessors.put(key, result.get(key));
							} else {
								finalRes += result.get(key);
							}
						}
					}
				} else {
					// successor of this place is only one.
					Map<Integer, Integer> result = getRequiredSuccessorTrans(
							rpst.getRPSTNodeWSiblings(outputPlace, successors.keySet().iterator().next()), encodedPN);
					if (result != null) {
						for (Integer key : result.keySet()) {
							Integer finalRes = requiredSucessors.get(key);
							if (finalRes == null) {
								requiredSucessors.put(key, result.get(key));
							} else {
								finalRes += result.get(key);
							}
						}
					}
				}
			} // else, no successor at all 
		}
		return requiredSucessors;
	}

	/**
	 * Return the set of required event class based on corresponding rpst node
	 * 
	 * @param startPointSiblingsExploration
	 * @return
	 */
	private Map<Integer, Integer> getRequiredSuccessorTrans(RPSTNodeWSiblings startPointSiblingsExploration,
			EncPNWSetFinalMarkings encodedPN) {
		Map<Integer, Integer> res = new HashMap<Integer, Integer>();

		while (startPointSiblingsExploration != null) { // until the root level is reached
			RPSTNodeWSiblings nPointer = startPointSiblingsExploration;

			if ((startPointSiblingsExploration != null)
					&& (startPointSiblingsExploration.getFragmentType().equals(RPSTFragmentType.POLYGONBACKWARD))) {
				// the parent must be BOUND
				Set<Integer> setTrans = rpst.getSuccessorBond(startPointSiblingsExploration.getParent());
				if (setTrans != null) {
					for (Integer trans : setTrans) {
						Integer val = res.get(trans);
						if (val == null) {
							res.put(trans, 1);
						} else {
							res.put(trans, val + 1);
						}
						;
					}
				}
			} else {

				// iterate in the same level
				while (nPointer != null) {
					if ((!nPointer.equals(startPointSiblingsExploration))
							&& (nPointer.getFragmentType().equals(RPSTFragmentType.BOND))) {
						Set<Integer> setTrans = rpst.getSuccessorBond(nPointer);
						if (setTrans != null) {
							for (Integer trans : setTrans) {
								if (trans != null) {
									Integer val = res.get(trans);
									if (val == null) {
										res.put(trans, 1);
									} else {
										res.put(trans, val + 1);
									}
								}
							}
						}
					} else if ((nPointer.getNextSibling() != null)
							&& (nPointer.getNextSibling().getFragmentType().equals(RPSTFragmentType.BOND))) {
						// do nothing, because cost of bond is already pre-calculated
					} else if (nPointer.getFragmentType().equals(RPSTFragmentType.TRIVIAL)) {
						if (encodedPN.isTransition(nPointer.getExit())) {
							// derive from next executed transitions
							Integer val = res.get(nPointer.getExit());
							if (val == null) {
								val = 1;
								res.put(nPointer.getExit(), val);
							} else {
								res.put(nPointer.getExit(), val + 1);
							}
						}
					}
					nPointer = nPointer.getNextSibling();
				}
			}

			// go to the next hierarchy
			startPointSiblingsExploration = startPointSiblingsExploration.getParent();
		}

		return res;
	}

}
