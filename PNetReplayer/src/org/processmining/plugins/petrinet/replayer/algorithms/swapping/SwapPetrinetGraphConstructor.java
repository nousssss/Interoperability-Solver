/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.swapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.InhibitorArc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.ResetArc;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.manifestreplay.CostBasedCompleteManifestParam;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;

/**
 * @author aadrians Sep 12, 2012
 * 
 */
public class SwapPetrinetGraphConstructor {
	public static ResetInhibitorNet createSwapPetrinet(
			// input: 
			PetrinetGraph origNet, TransEvClassMapping origMapping, CostBasedSwapParam param,

			// input/output: 
			final TransEvClassMapping finalMapping, final CostBasedCompleteManifestParam finalParam,

			// input/output to translate the swap petrinet back to original ones
			Map<Transition, Transition> trans2Orig, Map<Transition, Transition> replacementTrans,
			Map<Transition, Pair<Transition, Transition>> swapTrans) {

		// create suitable petri net
		ResetInhibitorNet finalNet = PetrinetFactory.newResetInhibitorNet(origNet.getLabel());
		copyAll(origNet, origMapping, param, finalMapping, finalParam, trans2Orig, replacementTrans, swapTrans,
				finalNet);
		return finalNet;
	}

	/**
	 * Copy everything except arcs, because reset/inhibitor arcs can only be
	 * added for reset/inhibitor net
	 * 
	 * @param origNet
	 * @param origMapping
	 * @param param
	 * @param finalMapping
	 * @param finalParam
	 * @param trans2Orig
	 * @param replacementTrans
	 * @param swapTrans
	 * @param place2Orig
	 * @param finalNet
	 */
	protected static void copyAll(PetrinetGraph origNet, TransEvClassMapping origMapping, CostBasedSwapParam param,
			TransEvClassMapping finalMapping, CostBasedCompleteManifestParam finalParam,
			Map<Transition, Transition> trans2Orig, Map<Transition, Transition> replacementTrans,
			Map<Transition, Pair<Transition, Transition>> swapTrans, final ResetInhibitorNet finalNet) {

		// local variables
		Map<Place, Place> placeOrig2newPlace = new HashMap<Place, Place>();
		Map<Transition, Transition> transOrig2newTrans = new HashMap<Transition, Transition>();
		Set<Transition> restrictedTrans = new HashSet<Transition>(); // move on model is restricted

		// copy original transitions
		Map<Transition, Integer> mapOrigTrans2Cost = param.getMapTrans2Cost();
		Map<Transition, Integer> mapOrigSyncCost = param.getMapSync2Cost();
		for (Transition origT : origNet.getTransitions()) {
			Transition finalT = finalNet.addTransition(origT.getLabel());
			finalT.setInvisible(origT.isInvisible());
			trans2Orig.put(finalT, origT);
			transOrig2newTrans.put(origT, finalT);

			// copy mapping
			finalMapping.put(finalT, origMapping.get(origT));

			// copy the cost of move on model
			finalParam.getMapTrans2Cost().put(finalT, mapOrigTrans2Cost.get(origT));

			Integer syncCost = mapOrigSyncCost.get(origT);
			if (syncCost != null) {
				finalParam.getMapSync2Cost().put(finalT, syncCost);
			}

		}

		// copy places
		for (Place origP : origNet.getPlaces()) {
			Place finalP = finalNet.addPlace(origP.getLabel());
			placeOrig2newPlace.put(origP, finalP);
		}

		// copy arcs
		for (Transition origT : origNet.getTransitions()) {
			copySurroundingArcs(origNet, origT, finalNet, transOrig2newTrans.get(origT), placeOrig2newPlace);
		}

		// create inverse mapping
		Map<XEventClass, List<Transition>> evClass2Trans = new HashMap<XEventClass, List<Transition>>(); // map ev class to trans
		for (Entry<Transition, XEventClass> origEntry : origMapping.entrySet()) {
			List<Transition> lst = evClass2Trans.get(origEntry.getValue());
			if (lst == null) {
				lst = new ArrayList<Transition>(2);
				evClass2Trans.put(origEntry.getValue(), lst);
			}
			lst.add(origEntry.getKey());
		}

		// add replacement transitions
		Map<XEventClass, List<Pair<XEventClass, Integer>>> replacementCostMap = param.getReplacementCostMap();
		if (replacementCostMap != null) {
			for (Entry<XEventClass, List<Pair<XEventClass, Integer>>> entry : replacementCostMap.entrySet()) {
				List<Transition> lst = evClass2Trans.get(entry.getKey());
				if (lst != null) { // there is a transition mapped to the event
					for (Transition t : lst) {
						for (Pair<XEventClass, Integer> p : entry.getValue()) {
							// duplicate the transition and map it to the replacement event class
							Transition finalT = finalNet.addTransition("rep-" + t.getLabel() + "-"
									+ p.getFirst().getId());
							replacementTrans.put(finalT, t); // finalT is replacing t, with different eventclass mapping

							// map the new transition to the event class
							finalMapping.put(finalT, p.getFirst());

							// add cost of replacing one activity with another (cost of sync move)
							finalParam.getMapSync2Cost().put(finalT, p.getSecond());

							// add cost of move model for the new transition
							finalParam.getMapTrans2Cost().put(finalT, param.getMapTrans2Cost().get(t));

							// add the new transition to fragment trans (where move on model is restricted)
							restrictedTrans.add(finalT);

							// copy all arcs
							copySurroundingArcs(origNet, t, finalNet, finalT, placeOrig2newPlace);
						}
					}
				}
			}
		}

		// identify maximum cost for dummy cost of swapping transitions
		// cost need to be high enough such that the ILP never suggest to do move on model
		int costUnskippable = -Integer.MAX_VALUE;
		for (Entry<Transition, Integer> entry : param.getMapTrans2Cost().entrySet()) {
			if (entry.getValue() > costUnskippable) {
				costUnskippable = entry.getValue();
			}
		}
		costUnskippable *= origNet.getTransitions().size();

		// add swap transitions
		Map<XEventClass, List<Pair<XEventClass, Integer>>> swapCost = param.getSwapCostMap();
		if (swapCost != null) {
			for (Entry<XEventClass, List<Pair<XEventClass, Integer>>> entry : swapCost.entrySet()) {
				// make a pair of transitions
				// find the transitions mapped to entry.getKey
				List<Transition> transLst1 = evClass2Trans.get(entry.getKey());
				if (transLst1 != null) {
					for (Pair<XEventClass, Integer> pair : entry.getValue()) {
						List<Transition> transLst2 = evClass2Trans.get(pair.getFirst()); // these are the transitions it can be swapped with
						if (transLst2 != null) {
							for (Transition t1 : transLst1) {
								for (Transition t2 : transLst2) {
									// swap t2 and t1
									Transition t1prime = finalNet.addTransition("swap-" + t1.getLabel() + "-with-"
											+ t2.getLabel());
									Transition t2prime = finalNet.addTransition("swap-" + t2.getLabel() + "-with-"
											+ t1.getLabel());

									// update swap mapping
									swapTrans.put(t1prime, new Pair<Transition, Transition>(t1, t2));
									swapTrans.put(t2prime, new Pair<Transition, Transition>(t2, t1));

									Place pSwap = finalNet.addPlace("swap");
									finalNet.addArc(t2prime, pSwap);
									finalNet.addArc(pSwap, t1prime);

									// swap mapping
									finalMapping.put(t2prime, pair.getFirst());
									finalMapping.put(t1prime, entry.getKey());
									
									// add inhibitor arc
//									finalNet.addInhibitorArc(pSwap, transOrig2newTrans.get(t1));
//									finalNet.addInhibitorArc(pSwap, transOrig2newTrans.get(t2));

									// add cost of swapping
									int halfCost = pair.getSecond() / 2;
									finalParam.getMapSync2Cost().put(t2prime, halfCost);
									finalParam.getMapSync2Cost().put(t1prime, pair.getSecond() - halfCost);

									// add cost of move model
									finalParam.getMapTrans2Cost().put(t2prime, costUnskippable);
									finalParam.getMapTrans2Cost().put(t1prime, costUnskippable);

									// add the new transition to fragment trans (where move on model is restricted)
									restrictedTrans.add(t1prime);
									restrictedTrans.add(t2prime);

									// add surrounding arcs
									copySurroundingArcs(origNet, t1, finalNet, t2prime, placeOrig2newPlace);
									copySurroundingArcs(origNet, t2, finalNet, t1prime, placeOrig2newPlace);
								}
							}
						}
					}
				}
			}
		}

		// copy initial marking
		Marking finalInitMarking = new Marking();
		Marking origInitMarking = param.getInitialMarking();
		for (Place origP : origInitMarking.baseSet()) {
			finalInitMarking.add(placeOrig2newPlace.get(origP), origInitMarking.occurrences(origP));
		}
		finalParam.setInitialMarking(finalInitMarking);

		// copy final marking(s)
		Marking[] origFinalMarkings = param.getFinalMarkings();
		if (origFinalMarkings != null) {
			Marking[] finalFinalMarkings = new Marking[origFinalMarkings.length];
			int counter = 0;
			for (Marking origFinalMarking : origFinalMarkings) {
				Marking finalFinalMarking = new Marking();
				for (Place origP : origFinalMarking.baseSet()) {
					finalFinalMarking.add(placeOrig2newPlace.get(origP), origFinalMarking.occurrences(origP));
				}
				finalFinalMarkings[counter] = finalFinalMarking;
				counter++;
			}
			finalParam.setFinalMarkings(finalFinalMarkings);
		}

		// set parameter for restricted transitions
		finalParam.setRestrictedTrans(restrictedTrans);
	}

	/**
	 * Add surrounding arcs of newTrans as if newTrans is origT in origNet
	 * 
	 * @param origNet
	 * @param origT
	 * @param finalNet
	 * @param newTrans
	 * @param placeOrig2newPlace
	 */
	private static void copySurroundingArcs(PetrinetGraph origNet, Transition origT, PetrinetGraph finalNet,
			Transition newTrans, Map<Place, Place> placeOrig2newPlace) {
		Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> inEdges = origNet.getInEdges(origT);
		Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> outEdges = origNet.getOutEdges(origT);

		if (inEdges != null) {
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> inEdge : inEdges) {
				if (inEdge instanceof InhibitorArc) {
					if (finalNet instanceof InhibitorNet) {
						((InhibitorNet) finalNet).addInhibitorArc(placeOrig2newPlace.get(inEdge.getSource()), newTrans);
					} else if (finalNet instanceof ResetInhibitorNet) {
						((ResetInhibitorNet) finalNet).addInhibitorArc(placeOrig2newPlace.get(inEdge.getSource()),
								newTrans);
					}
				} else if (inEdge instanceof ResetArc) {
					if (finalNet instanceof ResetNet) {
						((ResetNet) finalNet).addResetArc(placeOrig2newPlace.get(inEdge.getSource()), newTrans);
					} else if (finalNet instanceof ResetInhibitorNet) {
						((ResetInhibitorNet) finalNet)
								.addResetArc(placeOrig2newPlace.get(inEdge.getSource()), newTrans);
					}
				} else if (inEdge instanceof Arc) {
					finalNet.addArc(placeOrig2newPlace.get(inEdge.getSource()), newTrans, ((Arc) inEdge).getWeight());
				}
			}
		}

		if (outEdges != null) {
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> outEdge : outEdges) {
				//inhibitorArcs and ResetArcs do not exit transitions.
				if (outEdge instanceof Arc) {
					finalNet.addArc(newTrans, placeOrig2newPlace.get(outEdge.getTarget()), ((Arc) outEdge).getWeight());
				}
			}
		}
	}

}
