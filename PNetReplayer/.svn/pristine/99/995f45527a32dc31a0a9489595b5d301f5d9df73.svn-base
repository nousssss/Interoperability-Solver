/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.util.codec;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.InhibitorArc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.ResetArc;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * @author aadrians
 * 
 */
public class EncPN {
	// all about arcs
	private Map<Integer, Map<Integer, Integer>> mapArc2Weight; // from -> to -> weight. Store arc from place-transition as well as transition-place
	private Map<Integer, Set<Integer>> mapInhibitorArcs; // transitions --> its inhibitors
	private Map<Integer, Set<Integer>> mapResetArcs; // transition --> places it reset
	private Map<Integer, Set<Integer>> predecessors; // only store ordinary arcs, predecessor of transitions (not places)

	// mapping back to node of original net
	private Map<Integer, PetrinetNode> mapEncNode2NodeGraph;
	private Map<PetrinetNode, Integer> mapNodeGraph2EncNode;

	// cost of deviation (move on log, move on model), only contains transition that represent deviations
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
	 * @param mapArc2Weight
	 *            the mapArc2Weight to set
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
	 * @param mapInhibitorArcs
	 *            the mapInhibitorArcs to set
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
	 * @param mapResetArcs
	 *            the mapResetArcs to set
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
	 * @param predecessors
	 *            the predecessors to set
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
	 * @param mapEncNode2NodeGraph
	 *            the mapEncNode2NodeGraph to set
	 */
	public void setMapEncNode2NodeGraph(Map<Integer, PetrinetNode> mapEncNode2NodeGraph) {
		this.mapEncNode2NodeGraph = mapEncNode2NodeGraph;
	}

	/**
	 * @return the encFinalMarking
	 */
	public Map<Integer, Integer> getEncFinalMarking() {
		return encFinalMarking;
	}

	/**
	 * @param encFinalMarking
	 *            the encFinalMarking to set
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
	 * @param encInitialMarking
	 *            the encInitialMarking to set
	 */
	public void setEncInitialMarking(Map<Integer, Integer> encInitialMarking) {
		this.encInitialMarking = encInitialMarking;
	}

	public boolean isMoveOnModelOnly(Integer trans) {
		return (mapEncNode2NodeGraph.get(trans) != null);
	}

	/**
	 * Note : mapEvClass2Trans is NOT USED. go back to this later
	 * 
	 * @param net
	 * @param initMarking
	 * @param finalMarking
	 * @param mapEvClass2Trans
	 * @param mapTrans2Cost
	 */
	public EncPN(PetrinetGraph net, Marking initMarking, Marking finalMarking, Map<Transition, Integer> mapTrans2Cost) {
		// arcs
		mapInhibitorArcs = new HashMap<Integer, Set<Integer>>();
		mapResetArcs = new HashMap<Integer, Set<Integer>>();
		predecessors = new HashMap<Integer, Set<Integer>>();

		mapArc2Weight = new HashMap<Integer, Map<Integer, Integer>>();
		mapEncNode2NodeGraph = new HashMap<Integer, PetrinetNode>();
		mapNodeGraph2EncNode = new HashMap<PetrinetNode, Integer>();

		// for all model transition
		int nodeId = Integer.MIN_VALUE; // id for either transitions or places
		for (Transition t : net.getTransitions()) {
			// encode transition with encTransId
			int encTransId = nodeId;
			mapEncNode2NodeGraph.put(nodeId, t);
			mapNodeGraph2EncNode.put(t, nodeId);
			nodeId++;

			// model places
			// predecessor, can be inhibitors, resets, or ordinary arcs
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> inEdges = net.getInEdges(t);
			if (inEdges.size() > 0) {
				Set<Integer> encPred = null; // contains only ordinary weighted arc
				Set<Integer> encInhibitor = null; // contains only inhibitor arcs
				Set<Integer> encReset = null; // contains only reset arc

				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : inEdges) {
					// no matter what, predecessor node need to be documented
					Integer encSource = mapNodeGraph2EncNode.get(edge.getSource());
					if (encSource == null) {
						encSource = nodeId;
						mapEncNode2NodeGraph.put(nodeId, edge.getSource());
						mapNodeGraph2EncNode.put(edge.getSource(), nodeId);
						nodeId++;
					}

					if (edge instanceof InhibitorArc) {
						if (encInhibitor == null) {
							encInhibitor = new HashSet<Integer>(1);
						}
						encInhibitor.add(encSource);
					} else if (edge instanceof ResetArc) {
						if (encReset == null) {
							encReset = new HashSet<Integer>(1);
						}
						encReset.add(encSource);
					} else {
						if (encPred == null) {
							encPred = new HashSet<Integer>(1);
						}
						encPred.add(encSource);

						Map<Integer, Integer> encTargetMap = mapArc2Weight.get(encSource);
						if (encTargetMap == null) {
							encTargetMap = new HashMap<Integer, Integer>(1);
							encTargetMap.put(encTransId, net.getArc(edge.getSource(), edge.getTarget()).getWeight());
							mapArc2Weight.put(encSource, encTargetMap);
						} else {
							encTargetMap.put(encTransId, net.getArc(edge.getSource(), edge.getTarget()).getWeight());
						}
					}
				}
				if (encPred != null) {
					predecessors.put(encTransId, encPred);
				}
				if (encInhibitor != null) {
					mapInhibitorArcs.put(encTransId, encInhibitor);
				}
				if (encReset != null) {
					mapResetArcs.put(encTransId, encReset);
				}
			}

			// successors can only be ordinary arcs
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> outEdges = net.getOutEdges(t);
			if (outEdges.size() > 0) {

				Map<Integer, Integer> encTargets = null;

				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : outEdges) {
					Integer encTarget = mapNodeGraph2EncNode.get(edge.getTarget());
					if (encTarget == null) {
						encTarget = nodeId;
						mapEncNode2NodeGraph.put(nodeId, edge.getTarget());
						mapNodeGraph2EncNode.put(edge.getTarget(), nodeId);
						nodeId++;
					}

					if (encTargets == null) {
						encTargets = new HashMap<Integer, Integer>();
					}
					encTargets.put(encTarget, net.getArc(edge.getSource(), edge.getTarget()).getWeight());

				}

				if (encTargets != null) {
					mapArc2Weight.put(encTransId, encTargets);
				}

			}
		} // finish iterating all transitions

		// import the costs
		mapEncNode2Cost = new HashMap<Integer, Integer>();
		if (mapTrans2Cost != null) {
			for (Transition trans : mapTrans2Cost.keySet()) {
				mapEncNode2Cost.put(mapNodeGraph2EncNode.get(trans), mapTrans2Cost.get(trans));
			}
		}

		// if no final marking, give an empty marking
		encFinalMarking = new HashMap<Integer, Integer>();
		if (finalMarking != null) {
			for (Place p : finalMarking) {
				Integer encNode = mapNodeGraph2EncNode.get(p);
				if (encNode != null) {
					encFinalMarking.put(encNode, finalMarking.occurrences(p));
				}
			}
		}

		// initial marking
		if (initMarking != null) {
			encInitialMarking = new HashMap<Integer, Integer>();
			for (Place p : initMarking) {
				Integer encNode = mapNodeGraph2EncNode.get(p);
				if (encNode != null) {
					encInitialMarking.put(encNode, initMarking.occurrences(p));
				}
			}
		}
	}

	public String toString() {
		String newLine = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer();

		for (Integer key : mapArc2Weight.keySet()) {
			PetrinetNode node = mapEncNode2NodeGraph.get(key);
			sb.append(newLine);
			sb.append("------------------------------------");
			sb.append(newLine);
			if (node != null) {
				sb.append(node.getLabel() + "[" + key + "]");
			} else {
				sb.append(key);
			}

			if (mapEncNode2Cost.get(key) != null) {
				sb.append(" - Cost: " + mapEncNode2Cost.get(key));
			} else {
				sb.append(" - Cost: 0 (null)");
			}
			sb.append(newLine);

			sb.append("--- Predecessors ---");
			sb.append(newLine);
			Set<Integer> predKeySet = predecessors.get(key);
			if (predKeySet != null) {
				for (Integer predKey : predKeySet) {
					sb.append(predKey);
					if (mapEncNode2NodeGraph.get(predKey) != null) {
						sb.append("[" + mapEncNode2NodeGraph.get(predKey).getLabel() + "]");
					}
					Integer res = mapArc2Weight.get(predKey).get(key);
					if (res != null) {
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
			if (predKeyInhibitorSet != null) {
				for (Integer predKey : predKeyInhibitorSet) {
					sb.append(predKey);
					if (mapEncNode2NodeGraph.get(predKey) != null) {
						sb.append("[" + mapEncNode2NodeGraph.get(predKey).getLabel() + "]");
					}
					sb.append(newLine);
				}
			}
			sb.append("--- Reset arcs ---");
			sb.append(newLine);
			Set<Integer> predKeyResetSet = mapResetArcs.keySet();
			if (predKeyResetSet != null) {
				for (Integer predKey : predKeyResetSet) {
					sb.append(predKey);
					if (mapEncNode2NodeGraph.get(predKey) != null) {
						sb.append("[" + mapEncNode2NodeGraph.get(predKey).getLabel() + "]");
					}
					sb.append(newLine);
				}
			}
			sb.append("--- Successors ---");
			sb.append(newLine);
			Map<Integer, Integer> mapToSucc = mapArc2Weight.get(key);
			if (mapToSucc != null) {
				for (Integer keySucc : mapToSucc.keySet()) {
					sb.append(keySucc);
					if (mapEncNode2NodeGraph.get(keySucc) != null) {
						sb.append("[" + mapEncNode2NodeGraph.get(keySucc).getLabel() + "]");
					}
					sb.append(newLine);
				}
			}
		}

		sb.append(newLine);
		sb.append("Final Markings : ");
		sb.append(newLine);
		for (Integer key : encFinalMarking.keySet()) {
			sb.append(key);
			sb.append(newLine);
		}

		sb.append("Initial Marking : ");
		sb.append(newLine);
		for (Integer key : encInitialMarking.keySet()) {
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

	public int getEncOf(Transition transition) {
		return mapNodeGraph2EncNode.get(transition);
	}

	public boolean isTransition(Integer integer) {
		return (this.mapEncNode2NodeGraph.get(integer) instanceof Transition);
	}

	public PetrinetNode getPetrinetNodeOf(Integer encodedPNNode) {
		return this.mapEncNode2NodeGraph.get(encodedPNNode);
	}
}
