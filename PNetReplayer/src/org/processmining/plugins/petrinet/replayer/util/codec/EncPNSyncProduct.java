/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.util.codec;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.InhibitorArc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.ResetArc;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.replayresult.StepTypes;

/**
 * Assumption: - the original net may have weights - the original net may have
 * inhibitors and reset arcs - the original net have interleaving semantics 
 * 
 * @author aadrians
 * 
 */
public class EncPNSyncProduct {
	// all about arcs
	private Map<Integer, Map<Integer, Integer>> mapArc2Weight; // from -> to -> weight. Store arc from place-transition as well as transition-place
	private Map<Integer, Set<Integer>> mapInhibitorArcs; // transitions --> its inhibitors
	private Map<Integer, Set<Integer>> mapResetArcs; // transition --> places it reset
	private Map<Integer, Set<Integer>> predecessors; // only store ordinary arcs, predecessor of transitions (not places)

	// mapping back to node of original net
	private Map<Integer, PetrinetNode> mapEncNode2NodeGraph;
	private Map<Integer, Integer> mapMoveOnLogTrans;
	private Map<Integer, Integer> mapMoveOnBothTrans;

	// cost of deviation, only contains transition that represent deviations
	private Map<Integer, Integer> mapEncNode2Cost;

	// marking = mapping from place to number in encoded fashion
	private Map<Integer, Integer> encFinalMarking;
	private Map<Integer, Integer> encInitialMarking;

	/**
	 * @return the mapArc2Weight
	 */
	public Map<Integer, Map<Integer, Integer>> getMapArc2Weight() {
		return mapArc2Weight;
	}

	/**
	 * @param mapArc2Weight the mapArc2Weight to set
	 */
	public void setMapArc2Weight(Map<Integer, Map<Integer, Integer>> mapArc2Weight) {
		this.mapArc2Weight = mapArc2Weight;
	}

	/**
	 * @return the mapInhibitorArcs
	 */
	public Map<Integer, Set<Integer>> getMapInhibitorArcs() {
		return mapInhibitorArcs;
	}

	/**
	 * @param mapInhibitorArcs the mapInhibitorArcs to set
	 */
	public void setMapInhibitorArcs(Map<Integer, Set<Integer>> mapInhibitorArcs) {
		this.mapInhibitorArcs = mapInhibitorArcs;
	}

	/**
	 * @return the mapResetArcs
	 */
	public Map<Integer, Set<Integer>> getMapResetArcs() {
		return mapResetArcs;
	}

	/**
	 * @param mapResetArcs the mapResetArcs to set
	 */
	public void setMapResetArcs(Map<Integer, Set<Integer>> mapResetArcs) {
		this.mapResetArcs = mapResetArcs;
	}

	/**
	 * @return the predecessors
	 */
	public Map<Integer, Set<Integer>> getPredecessors() {
		return predecessors;
	}

	/**
	 * @param predecessors the predecessors to set
	 */
	public void setPredecessors(Map<Integer, Set<Integer>> predecessors) {
		this.predecessors = predecessors;
	}

	/**
	 * @return the mapEncNode2NodeGraph
	 */
	public Map<Integer, PetrinetNode> getMapEncNode2NodeGraph() {
		return mapEncNode2NodeGraph;
	}

	/**
	 * @param mapEncNode2NodeGraph the mapEncNode2NodeGraph to set
	 */
	public void setMapEncNode2NodeGraph(Map<Integer, PetrinetNode> mapEncNode2NodeGraph) {
		this.mapEncNode2NodeGraph = mapEncNode2NodeGraph;
	}

	/**
	 * @return the mapEncNode2Cost
	 */
	public Map<Integer, Integer> getMapEncNode2Cost() {
		return mapEncNode2Cost;
	}

	/**
	 * @param mapEncNode2Cost the mapEncNode2Cost to set
	 */
	public void setMapEncNode2Cost(Map<Integer, Integer> mapEncNode2Cost) {
		this.mapEncNode2Cost = mapEncNode2Cost;
	}

	/**
	 * @return the encFinalMarking
	 */
	public Map<Integer, Integer> getEncFinalMarking() {
		return encFinalMarking;
	}

	/**
	 * @param encFinalMarking the encFinalMarking to set
	 */
	public void setEncFinalMarking(Map<Integer, Integer> encFinalMarking) {
		this.encFinalMarking = encFinalMarking;
	}

	/**
	 * @return the encInitialMarking
	 */
	public Map<Integer, Integer> getEncInitialMarking() {
		return encInitialMarking;
	}

	/**
	 * @param encInitialMarking the encInitialMarking to set
	 */
	public void setEncInitialMarking(Map<Integer, Integer> encInitialMarking) {
		this.encInitialMarking = encInitialMarking;
	}

	
	public boolean isMoveOnModelOnly(Integer trans){
		return (mapEncNode2NodeGraph.get(trans) != null);
	}
	
	/**
	 * create EncPNSyncProduct from scratch
	 * 
	 * @param net
	 * @param initMarking
	 * @param finalMarking
	 * @param mapEvClass2Trans
	 * @param mapTrans2Cost cost of [0]: skipping activity (move on model), [1]: inserting
	 * activity (move on log)
	 * @param trace
	 * @param classes
	 */
	public EncPNSyncProduct(PetrinetGraph net, Marking initMarking, Marking finalMarking,
			Map<XEventClass, Set<Transition>> mapEvClass2Trans, Map<Transition, int[]> mapTrans2Cost, List<XEventClass> mappedEvClass) {
		// arcs
		mapInhibitorArcs = new HashMap<Integer, Set<Integer>>();
		mapResetArcs = new HashMap<Integer, Set<Integer>>();
		predecessors = new HashMap<Integer, Set<Integer>>();
		
		mapArc2Weight = new HashMap<Integer, Map<Integer,Integer>>();
		mapEncNode2NodeGraph = new HashMap<Integer, PetrinetNode>();
		mapMoveOnLogTrans = new HashMap<Integer, Integer>();
		mapMoveOnBothTrans = new HashMap<Integer, Integer>();
		mapEncNode2Cost = new HashMap<Integer, Integer>();
		
		// temporary variable
		Map<PetrinetNode, Integer> mapNodeGraph2EncNode = new HashMap<PetrinetNode, Integer>();
		
		// for all model transition
		int nodeId = Integer.MIN_VALUE; // id for either transitions or places
		for (Transition t: net.getTransitions()){
			// encode transition with encTransId
			int encTransId = nodeId;
			mapEncNode2NodeGraph.put(nodeId, t);
			mapEncNode2Cost.put(nodeId, mapTrans2Cost.get(t)[0]);
			mapNodeGraph2EncNode.put(t, nodeId);
			nodeId++;
			
			// model places
			// predecessor, can only be inhibitors or ordinary arcs
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> inEdges = net.getInEdges(t);
			if (inEdges.size() > 0){
				Set<Integer> encPred = null; // contains only ordinary weighted arc
				Set<Integer> encInhibitor = null; // contains only inhibitor arcs
				
				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : inEdges){
					// no matter what, predecessor node need to be documented
					Integer encSource = mapNodeGraph2EncNode.get(edge.getSource());
					if (encSource == null){
						encSource = nodeId;
						mapEncNode2NodeGraph.put(nodeId, edge.getSource());
						mapNodeGraph2EncNode.put(edge.getSource(), nodeId);
						nodeId++;
					}
					
					if (edge instanceof InhibitorArc){
						if (encInhibitor == null){
							encInhibitor = new HashSet<Integer>(1);
						}
						encInhibitor.add(encSource);
					} else { 
						if (encPred == null){
							encPred = new HashSet<Integer>(1);
						}
						encPred.add(encSource);
						
						Map<Integer, Integer> encTargetMap = mapArc2Weight.get(encSource);
						if (encTargetMap == null){
							encTargetMap = new HashMap<Integer, Integer>(1);
							encTargetMap.put(encTransId,net.getArc(edge.getSource(), edge.getTarget()).getWeight());
							mapArc2Weight.put(encSource, encTargetMap);
						} else {
							encTargetMap.put(encTransId,net.getArc(edge.getSource(), edge.getTarget()).getWeight());
						}
					}
				}
				if (encPred != null){
					predecessors.put(encTransId, encPred);
				}
				if (encInhibitor != null){
					mapInhibitorArcs.put(encTransId, encInhibitor);
				}
			}
			
			// successors can only be resets or ordinary arcs
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> outEdges = net.getOutEdges(t);
			if (outEdges.size() > 0){
				Set<Integer> encReset = null;
				Map<Integer, Integer> encTargets = null;
				
				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : outEdges){
					Integer encTarget = mapNodeGraph2EncNode.get(edge.getTarget());
					if (encTarget == null){
						encTarget = nodeId;
						mapEncNode2NodeGraph.put(nodeId, edge.getTarget());
						mapNodeGraph2EncNode.put(edge.getTarget(), nodeId);
						nodeId++;
					}
					
					if (edge instanceof ResetArc){
						if (encReset == null){
							encReset = new HashSet<Integer>(1);
						}
						encReset.add(encTarget);
					} else {
						if (encTargets == null){
							encTargets = new HashMap<Integer, Integer>();
						}
						encTargets.put(encTarget, net.getArc(edge.getSource(), edge.getTarget()).getWeight());
					}
				}
				
				if (encTargets != null){
					mapArc2Weight.put(encTransId, encTargets);
				}
				if (encReset != null){
					mapResetArcs.put(encTransId, encReset);
				}
			}
		} // finish iterating all transitions
		
		
		// add event net
		int initPlaceEventNet = nodeId; // initial marking

		int encPredPlace = nodeId; // predecessor of current event
		int encSuccPlace = nodeId; // initialize final marking of the event net
		nodeId++;

		for (XEventClass evClass: mappedEvClass){
			Set<Transition> setCandidates = mapEvClass2Trans.get(evClass);
			if (setCandidates != null){ // the event is mapped to at least one transition
				
				// node representing inserted activity
				int encCurrNode = nodeId;
				nodeId++;
				
				// get the lowest cost of inserting
				int minCost = Integer.MAX_VALUE;
				Transition minTransInstance =  null;
				for (Transition trans : setCandidates){
					minCost = minCost < mapTrans2Cost.get(trans)[1] ? minCost : mapTrans2Cost.get(trans)[1];
					minTransInstance = trans;
				}
				mapEncNode2Cost.put(encCurrNode, minCost);
				mapMoveOnLogTrans.put(encCurrNode, mapNodeGraph2EncNode.get(minTransInstance));
				
				// create the input node of the inserted activity
				Map<Integer, Integer> mapInputArc = new HashMap<Integer, Integer>(2);								
				mapInputArc.put(encCurrNode, 1);
				mapArc2Weight.put(encPredPlace, mapInputArc);
				
				// record predecessor for transition
				Set<Integer> predSet = new HashSet<Integer>(2);
				predSet.add(encPredPlace);
				predecessors.put(encCurrNode, predSet);
				
				// create the output node of the inserted activity
				encSuccPlace = nodeId;
				nodeId++;
				
				Map<Integer, Integer> mapOutputArc = new HashMap<Integer, Integer>(2);
				mapOutputArc.put(encSuccPlace, 1);
				mapArc2Weight.put(encCurrNode, mapOutputArc);
				
				// create also the synchronous product
				for (Transition trans : setCandidates){
					int syncTransition = nodeId; // the transition and candidate
					nodeId++;
					
					mapMoveOnBothTrans.put(syncTransition, mapNodeGraph2EncNode.get(trans));
					
					Integer encCandidate = mapNodeGraph2EncNode.get(trans);
					
					Set<Integer> inhibitors = mapInhibitorArcs.get(encCandidate);
					if (inhibitors != null){
						mapInhibitorArcs.put(syncTransition, inhibitors);
					}
					
					Set<Integer> resetArcs = mapResetArcs.get(encCandidate);
					if (resetArcs != null){
						mapResetArcs.put(syncTransition, resetArcs);
					}
					
					Set<Integer> predSyncTrans = null;
					Set<Integer> predTrans = predecessors.get(encCandidate);
					if (predTrans != null){
						predSyncTrans = new HashSet<Integer>(predTrans.size() + 1);
						for (Integer pred : predTrans){
							Map<Integer, Integer> predArc = mapArc2Weight.get(pred);
							predArc.put(syncTransition, mapArc2Weight.get(pred).get(encCandidate));
						}
						predSyncTrans.addAll(predTrans);
					} else {
						predSyncTrans = new HashSet<Integer>(1);
					} 
					predSyncTrans.add(encPredPlace);
					predecessors.put(syncTransition, predSyncTrans);
					
					mapArc2Weight.get(encPredPlace).put(syncTransition, 1);
					
					Map<Integer, Integer> succSyncTrans = null;				
					Map<Integer, Integer> succTrans = mapArc2Weight.get(encCandidate);
					if (succTrans != null){
						succSyncTrans = new HashMap<Integer, Integer>(succTrans.size() + 2);
						for (Integer key : succTrans.keySet()){
							succSyncTrans.put(key, succTrans.get(key));
						}
					} else {
						succSyncTrans = new HashMap<Integer, Integer>(2);
					}
					succSyncTrans.put(encSuccPlace, 1);
					mapArc2Weight.put(syncTransition, succSyncTrans);
				}
				
				// continue process
				encPredPlace = encSuccPlace;
			} // else, no transition candidates for the event, ignore it
		} // all events are explored
		
		// final marking would be the encSuccPlace and the end place of original net
		encFinalMarking = new HashMap<Integer, Integer>();
		encFinalMarking.put(encSuccPlace, 1);
		for (Place p : finalMarking){
			Integer encNode = mapNodeGraph2EncNode.get(p);
			if (encNode != null){
				encFinalMarking.put(encNode, finalMarking.occurrences(p));
			}
		}
		
		// initial marking
		encInitialMarking = new HashMap<Integer, Integer>();
		encInitialMarking.put(initPlaceEventNet, 1);
		for (Place p : initMarking){
			Integer encNode = mapNodeGraph2EncNode.get(p);
			if (encNode != null){
				encInitialMarking.put(encNode, initMarking.occurrences(p));
			}
		}
	}
	
	public int getCostOfFiringTransition(int trans){
		Integer cost = mapEncNode2Cost.get(trans);
		return (cost != null) ? cost : 0;
	}
	
	public String toString(){
		String newLine = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer();

		for (Integer key : mapArc2Weight.keySet()){
			PetrinetNode node = mapEncNode2NodeGraph.get(key);
			sb.append(newLine);
			sb.append("------------------------------------");
			sb.append(newLine);
			if (node != null){
				sb.append(node.getLabel() + "[" + key +"]");
			} else {
				sb.append(key);
			}
			
			if (mapEncNode2Cost.get(key) != null){
				sb.append(" - Cost: "+ mapEncNode2Cost.get(key));
			} else {
				sb.append(" - Cost: 0 (null)");
			}
			sb.append(newLine);
			
			sb.append("--- Predecessors ---");
			sb.append(newLine);
			Set<Integer> predKeySet = predecessors.get(key);
			if (predKeySet != null){
				for (Integer predKey : predKeySet){
					sb.append(predKey);
					if (mapEncNode2NodeGraph.get(predKey) != null){
						sb.append("["+ mapEncNode2NodeGraph.get(predKey).getLabel() +"]");
					}
					Integer res = mapArc2Weight.get(predKey).get(key);
					if (res != null){
						sb.append(" -- weight : " + mapArc2Weight.get(predKey).get(key) + " --");
					} else {
						sb.append(" -- weight : 1 (not recorded) --");
					}
					sb.append(newLine);
					sb.append(newLine);
				}
			}
			sb.append("--- Inhibitors ---");
			sb.append(newLine);
			Set<Integer> predKeyInhibitorSet = mapInhibitorArcs.keySet();
			if (predKeyInhibitorSet != null){
				for (Integer predKey : predKeyInhibitorSet){
					sb.append(predKey);
					if (mapEncNode2NodeGraph.get(predKey) != null){
						sb.append("["+ mapEncNode2NodeGraph.get(predKey).getLabel() +"]");
					}
					sb.append(newLine);
				}
			}
			sb.append("--- Reset arcs ---");
			sb.append(newLine);
			Set<Integer> predKeyResetSet = mapResetArcs.keySet();
			if (predKeyResetSet != null){
				for (Integer predKey : predKeyResetSet){
					sb.append(predKey);
					if (mapEncNode2NodeGraph.get(predKey) != null){
						sb.append("["+ mapEncNode2NodeGraph.get(predKey).getLabel() +"]");
					}
					sb.append(newLine);
				}
			}
			sb.append("--- Successors ---");
			sb.append(newLine);
			Map<Integer, Integer> mapToSucc = mapArc2Weight.get(key);
			if (mapToSucc != null){
				for (Integer keySucc : mapToSucc.keySet()){
					sb.append(keySucc);
					if (mapEncNode2NodeGraph.get(keySucc) != null){
						sb.append("["+ mapEncNode2NodeGraph.get(keySucc).getLabel() +"]");
					}
					sb.append(newLine);
				}
			}
		}
		
		sb.append(newLine);
		sb.append("Final Marking : ");
		sb.append(newLine);
		for (Integer key : encFinalMarking.keySet()){
			sb.append(key);
			sb.append(newLine);
		}
		
		sb.append("Initial Marking : ");
		sb.append(newLine);
		for (Integer key : encInitialMarking.keySet()){
			sb.append(key);
			sb.append(newLine);
		}
			
		return sb.toString();
		
	}

	public Set<Integer> getPredecessorsOf(Integer trans) {
		return predecessors.get(trans);
	}

	public Set<Integer> getInhibitorsOf(Integer trans) {
		return mapInhibitorArcs.get(trans);
	}

	public Set<Integer> getResetsOf(Integer trans) {
		return mapResetArcs.get(trans);
		
	}

	public Pair<StepTypes, Transition> getTransitionType(int selectedTransition) {
		PetrinetNode guess = mapEncNode2NodeGraph.get(selectedTransition);
		if (guess != null){
			if (((Transition) guess).isInvisible()){
				return new Pair<StepTypes, Transition> (StepTypes.MINVI, (Transition) guess);
			} else {
				return new Pair<StepTypes, Transition> (StepTypes.MREAL, (Transition) guess);
			}
		} else {
			Integer guessInt = mapMoveOnLogTrans.get(selectedTransition);
			if (guessInt != null){
				return new Pair<StepTypes, Transition> (StepTypes.L, (Transition) mapEncNode2NodeGraph.get(guessInt));
			} else {
				return new Pair<StepTypes, Transition> (StepTypes.LMGOOD, (Transition) mapEncNode2NodeGraph.get(mapMoveOnBothTrans.get(selectedTransition)));
			}
		}
	}
}
