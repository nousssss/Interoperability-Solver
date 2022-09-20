/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.rpstwrapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.jbpt.algo.tree.rpst.IRPSTNode;
import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.DirectedGraph;
import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.hypergraph.abs.Vertex;
import org.processmining.plugins.petrinet.replayer.util.codec.EncPNWSetFinalMarkings;


/**
 * This class wraps the RPST into a representation that helps determining for a
 * given marking, 1. Which transitions need to be fired to reach proper
 * termination. 2. Transitions that can never be fired anymore
 * 
 * @author aadrians
 * 
 */
public class RPSTTreeWrapper {
	/**
	 * This attribute map arcs (encoded form) and their representation as a
	 * trivial fragment
	 */
	private Map<Integer, Map<Integer, RPSTNodeWSiblings>> mapArc2TreeNode = new HashMap<Integer, Map<Integer, RPSTNodeWSiblings>>();
	private Map<RPSTNodeWSiblings, Set<Integer>> mapBondNodeToSuccEncTrans = new HashMap<RPSTNodeWSiblings, Set<Integer>>();
	private Map<RPSTNodeWSiblings, Set<RPSTNodeWSiblings>> mapBondNodeToChildren = new HashMap<RPSTNodeWSiblings, Set<RPSTNodeWSiblings>>();
	private Map<RPSTNodeWSiblings, RPSTNodeWSiblings> mapPolygonNode2LeftMostChild = new HashMap<RPSTNodeWSiblings, RPSTNodeWSiblings>();
	
	/**
	 * get node (with its sibling information) return null if no RPST node is
	 * found
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	public RPSTNodeWSiblings getRPSTNodeWSiblings(int source, int target) {
		Map<Integer, RPSTNodeWSiblings> mapToTarget = mapArc2TreeNode.get(source);
		if (mapToTarget == null) {
			return null;
		} else {
			return mapToTarget.get(target);
		}
	}
	
	/**
	 * get transitions that need to be executed if the bondNode is visited
	 * 
	 * @param bondNode
	 * @return
	 */
	public Set<Integer> getSuccessorBond(RPSTNodeWSiblings bondNode){
		return mapBondNodeToSuccEncTrans.get(bondNode);
	}

	/**
	 * Default constructor, based on encoded petri net
	 * 
	 * @param codec
	 */
	public RPSTTreeWrapper(EncPNWSetFinalMarkings codec) throws Exception {
		// create JBPT data structure
		DirectedGraph g = new DirectedGraph();
		Map<Integer, Vertex> mapInt2Vertex = new HashMap<Integer, Vertex>();
		Map<Vertex, Integer> mapVertex2Int = new HashMap<Vertex, Integer>();

		Map<Integer, Map<Integer, Integer>> mapWeight = codec.getMapArc2Weight();
		for (Integer source : mapWeight.keySet()) {
			Vertex sourceVertex = mapInt2Vertex.get(source);
			if (sourceVertex == null) {
				sourceVertex = new Vertex(String.valueOf(source));
				g.addVertex(sourceVertex);
				mapInt2Vertex.put(source, sourceVertex);
				mapVertex2Int.put(sourceVertex, source);
			}

			// add successors
			Map<Integer, Integer> map = mapWeight.get(source);
			if (map != null) {
				for (Integer target : map.keySet()) {
					Vertex targetVertex = mapInt2Vertex.get(target);
					if (targetVertex == null) {
						// create new vertex
						targetVertex = new Vertex(String.valueOf(target));
						g.addVertex(targetVertex);
						mapInt2Vertex.put(target, targetVertex);
						mapVertex2Int.put(targetVertex, target);
					}
					g.addEdge(sourceVertex, targetVertex);
				}
			}
		}

		RPST<DirectedEdge, Vertex> rpst = new RPST<DirectedEdge, Vertex>(g);

		// wrap rpst node and provide mapping for trivial fragments
		// iterate breadth-first approach
		Queue<IRPSTNode<DirectedEdge, Vertex>> nodesToExpand = new LinkedList<IRPSTNode<DirectedEdge, Vertex>>();
		Queue<RPSTNodeWSiblings> nodesToExpandInternal = new LinkedList<RPSTNodeWSiblings>();

		IRPSTNode<DirectedEdge, Vertex> root = rpst.getRoot();
		
		if (root == null){
			// there has to be a problem in not satisfying requirement for RPST implementation
			throw new Exception("No root of RPST node is found. Only net with a single entry node and a single exit node is supported");
		}
		
		RPSTNodeWSiblings rootInternal = new RPSTNodeWSiblings(mapVertex2Int.get(root.getEntry()),
				mapVertex2Int.get(root.getExit()), conv2RPSTWithDefPolValue(root.getType()));

		nodesToExpand.add(root);
		nodesToExpandInternal.add(rootInternal);

		// collect all bond nodes
		Set<RPSTNodeWSiblings> bondLeafNodes = new HashSet<RPSTNodeWSiblings>();

		while (!nodesToExpand.isEmpty()) {
			IRPSTNode<DirectedEdge, Vertex> currNode = nodesToExpand.poll();
			RPSTNodeWSiblings currNodeInternal = nodesToExpandInternal.poll();

			switch (currNodeInternal.getFragmentType()) {
				case POLYGONFORWARD :
					updatePolygonChildren(rpst, currNode, currNodeInternal, nodesToExpand, nodesToExpandInternal,
							mapVertex2Int, codec, bondLeafNodes);
					break;
				case POLYGONBACKWARD :
					updatePolygonChildren(rpst, currNode, currNodeInternal, nodesToExpand, nodesToExpandInternal,
							mapVertex2Int, codec, bondLeafNodes);
					break;
				case TRIVIAL :
					// trivial nodes, cannot be expanded further
					Integer encodedEntry = mapVertex2Int.get(currNode.getEntry());
					Integer encodedExit = mapVertex2Int.get(currNode.getExit());

					Map<Integer, RPSTNodeWSiblings> mappingToSibling = mapArc2TreeNode.get(encodedEntry);
					if (mappingToSibling == null) {
						mappingToSibling = new HashMap<Integer, RPSTNodeWSiblings>();
						mapArc2TreeNode.put(encodedEntry, mappingToSibling);
					}

					mappingToSibling.put(encodedExit, currNodeInternal);
					break;
				case BOND :
					Collection<IDirectedEdge<IRPSTNode<DirectedEdge, Vertex>>> edges2 = rpst.getOutgoingEdges(currNode);

					Set<RPSTNodeWSiblings> allChildren = new HashSet<RPSTNodeWSiblings>();

					// nothing special to do for other fragments
					for (IDirectedEdge<IRPSTNode<DirectedEdge, Vertex>> edge : edges2) {
						IRPSTNode<DirectedEdge, Vertex> childNode = edge.getTarget();

						RPSTNodeWSiblings newNodeInternal = null;
						// If a bond's child is a polygon, need to be distinguished between the forward
						// (go towards final marking) and backward (go against final marking) polygon

						if (childNode.getType().equals(TCType.POLYGON)) {
							// bond node whose child is polygon
							if (currNode.getEntry().equals(childNode.getEntry())) {
								// entry of this polygon is the same as the entry of the bond
								// hence, its going towards final marking
								newNodeInternal = new RPSTNodeWSiblings(mapVertex2Int.get(childNode.getEntry()),
										mapVertex2Int.get(childNode.getExit()), RPSTFragmentType.POLYGONFORWARD);
							} else {
								// going against final marking
								newNodeInternal = new RPSTNodeWSiblings(mapVertex2Int.get(childNode.getEntry()),
										mapVertex2Int.get(childNode.getExit()), RPSTFragmentType.POLYGONBACKWARD);
							}
						} else {
							RPSTFragmentType childNodeType = conv2RPSTWithDefPolValue(childNode.getType());

							// bond node whose child is not polygon (either trivial or rigid)
							newNodeInternal = new RPSTNodeWSiblings(mapVertex2Int.get(childNode.getEntry()),
									mapVertex2Int.get(childNode.getExit()), childNodeType);

							if (childNodeType.equals(RPSTFragmentType.BOND)) {
								bondLeafNodes.add(newNodeInternal);
							}
						}

						allChildren.add(newNodeInternal);
						newNodeInternal.setParent(currNodeInternal);

						nodesToExpand.add(childNode);
						nodesToExpandInternal.add(newNodeInternal);
					}

					mapBondNodeToChildren.put(currNodeInternal, allChildren);

					break;
				default : // RIGID
					Collection<IDirectedEdge<IRPSTNode<DirectedEdge, Vertex>>> edges3 = rpst.getOutgoingEdges(currNode);

					// nothing special to do for other fragments
					for (IDirectedEdge<IRPSTNode<DirectedEdge, Vertex>> edge : edges3) {
						IRPSTNode<DirectedEdge, Vertex> childNode = edge.getTarget();

						RPSTFragmentType childNodeType = conv2RPSTWithDefPolValue(childNode.getType());

						RPSTNodeWSiblings newNodeInternal = new RPSTNodeWSiblings(mapVertex2Int.get(childNode
								.getEntry()), mapVertex2Int.get(childNode.getExit()), childNodeType);

						if (childNodeType.equals(RPSTFragmentType.BOND)) {
							bondLeafNodes.add(newNodeInternal);
						}

						newNodeInternal.setParent(currNodeInternal);

						nodesToExpand.add(childNode);
						nodesToExpandInternal.add(newNodeInternal);
					}
					break;
			}
		}

		Set<RPSTNodeWSiblings> newLeafNodes = new HashSet<RPSTNodeWSiblings>();
		// find leafs of bond nodes
		do {
			// initiate new leaf nodes
			newLeafNodes.clear();
			newLeafNodes.addAll(bondLeafNodes);

			// throw away nodes in leafNodes set that are parents of other bond nodes
			for (RPSTNodeWSiblings node : newLeafNodes) {
				RPSTNodeWSiblings parentsOfOtherBondNode = null;
				if ((parentsOfOtherBondNode = findBondParent(node)) != null) {
					bondLeafNodes.remove(parentsOfOtherBondNode);
				}
			}
		} while (!newLeafNodes.equals(bondLeafNodes));

		// leafNodes contain all bond leaf nodes
		Queue<RPSTNodeWSiblings> bondNodeToBeAnalyzedNext = new LinkedList<RPSTNodeWSiblings>();
		bondNodeToBeAnalyzedNext.addAll(bondLeafNodes);

		// temp variables
		// set of transitions that need to be fired when this node is visited
		Set<Integer> setEncTransitions = new HashSet<Integer>();

		analyzeNext: while (bondNodeToBeAnalyzedNext.peek() != null) {
			RPSTNodeWSiblings node = bondNodeToBeAnalyzedNext.poll();
			bondLeafNodes.add(node);

			// get its children
			Set<RPSTNodeWSiblings> children = mapBondNodeToChildren.get(node);
			for (RPSTNodeWSiblings child : children) {
				setEncTransitions.clear();
				if (child.getFragmentType().equals(RPSTFragmentType.POLYGONFORWARD)) {
					// get all transitions required to fire
					RPSTNodeWSiblings childOfPolygon = mapPolygonNode2LeftMostChild.get(child);
					while (childOfPolygon != null) {
						if (childOfPolygon.getFragmentType().equals(RPSTFragmentType.BOND)) {
							// grandchild of the bond is another bond (that should've been calculated before)
							Set<Integer> setTrans = mapBondNodeToSuccEncTrans.get(childOfPolygon);
							if (setTrans != null) {
								setEncTransitions.addAll(setTrans);
							} else {
								// means that this node need to be further investigated
								bondNodeToBeAnalyzedNext.add(node);
								continue analyzeNext;
							}
						} else if (codec.isTransition(childOfPolygon.getExit())) {
							// conclude the next executed transition from the next node to be executed
							setEncTransitions.add(childOfPolygon.getExit());
						}
						childOfPolygon = childOfPolygon.getNextSibling();
					}

					// get previously identified "must be executed next transitions"
					Set<Integer> oldSucc = mapBondNodeToSuccEncTrans.get(node);
					if (oldSucc == null) {
						oldSucc = new HashSet<Integer>();
						oldSucc.addAll(setEncTransitions);
						mapBondNodeToSuccEncTrans.put(node, oldSucc);
					} else {
						// another polygon branch has been identified
						if (codec.isTransition(node.getEntry())) {
							// just add
							oldSucc.addAll(setEncTransitions);
						} else {
							// since this bond has more than one branch, take intersection
							oldSucc.retainAll(setEncTransitions);
						}
					}
				}
			}
			
			if (codec.isTransition(node.getEntry())) {
				Set<Integer> oldSucc = mapBondNodeToSuccEncTrans.get(node);
				if (oldSucc == null) {
					oldSucc = new HashSet<Integer>();
					mapBondNodeToSuccEncTrans.put(node, oldSucc);					
				}
				oldSucc.add(node.getEntry());
			}

			RPSTNodeWSiblings bondParentNode = findBondParent(node);
			if (bondParentNode != null) {
				bondNodeToBeAnalyzedNext.add(bondParentNode);
				// note that one bond parent may be examined more than once
			}
		}

	}

	/**
	 * Find a node which is parent of this node and also a bond
	 * 
	 * @param node
	 * @return
	 */
	private RPSTNodeWSiblings findBondParent(RPSTNodeWSiblings node) {
		RPSTNodeWSiblings pointer = node.getParent();
		while (pointer != null) {
			if (pointer.getFragmentType().equals(RPSTFragmentType.BOND)) {
				return pointer;
			}
			pointer = pointer.getParent();
		}
		return null;
	}

	private void updatePolygonChildren(RPST<DirectedEdge, Vertex> rpst, IRPSTNode<DirectedEdge, Vertex> currNode,
			RPSTNodeWSiblings currNodeInternal, Queue<IRPSTNode<DirectedEdge, Vertex>> nodesToExpand,
			Queue<RPSTNodeWSiblings> nodesToExpandInternal, Map<Vertex, Integer> mapVertex2Int,
			EncPNWSetFinalMarkings codec, Set<RPSTNodeWSiblings> setOfBondNodes) {
		assert(currNode.getType().equals(TCType.POLYGON));
		
		// children of a polygon fragment cannot be another polygon. It can only be
		// rigid, bond, or trivial
		// get children
		Collection<IDirectedEdge<IRPSTNode<DirectedEdge, Vertex>>> edges = rpst.getOutgoingEdges(currNode);
		int childrenSize = edges.size();

		// the children must be inserted in an ordered way
		List<IRPSTNode<DirectedEdge, Vertex>> orderedVertex = getOrderedVertex(edges, currNode.getEntry());
		List<RPSTNodeWSiblings> orderedInternalChildNodes = new LinkedList<RPSTNodeWSiblings>();

		for (IRPSTNode<DirectedEdge, Vertex> node : orderedVertex) {
			nodesToExpand.add(node);
			RPSTFragmentType fragmentType = conv2RPSTWithDefPolValue(node.getType());
			RPSTNodeWSiblings newInternalChildNode = new RPSTNodeWSiblings(mapVertex2Int.get(node.getEntry()),
					mapVertex2Int.get(node.getExit()), fragmentType);
			newInternalChildNode.setParent(currNodeInternal);
			orderedInternalChildNodes.add(newInternalChildNode);

			// add to set of bond nodes
			if (fragmentType.equals(RPSTFragmentType.BOND)) {
				setOfBondNodes.add(newInternalChildNode);
			}
		}
		;

		int indexCounter = 0;
		while (indexCounter < childrenSize) {
			RPSTNodeWSiblings currInternalNode = orderedInternalChildNodes.get(indexCounter);
			if ((indexCounter + 1) < childrenSize) {
				currInternalNode.setNextSibling(orderedInternalChildNodes.get(indexCounter + 1));
			}
			if ((indexCounter - 1) >= 0) {
				currInternalNode.setPrevSibling(orderedInternalChildNodes.get(indexCounter - 1));
			}
			nodesToExpandInternal.add(currInternalNode);
			indexCounter++;
		}

		// set the left most child
		mapPolygonNode2LeftMostChild.put(currNodeInternal, orderedInternalChildNodes.get(0));
	}

	/**
	 * Convert original RPST type with its internal representation in
	 * RPSTFragmentType
	 * 
	 * @param type
	 * @return
	 */
	private RPSTFragmentType conv2RPSTWithDefPolValue(TCType type) {
		switch (type) {
			case RIGID :
				return RPSTFragmentType.RIGID;
			case BOND :
				return RPSTFragmentType.BOND;
			case POLYGON :
				return RPSTFragmentType.POLYGONFORWARD;
			case TRIVIAL :
				return RPSTFragmentType.TRIVIAL;
			default :
				return RPSTFragmentType.UNDEFINED;
		}
	}

	/**
	 * return ordered vertex of a polygon fragment. Starting from the entry
	 * vertex
	 * 
	 * @param edges
	 * @param entry
	 * @return
	 */
	private List<IRPSTNode<DirectedEdge, Vertex>> getOrderedVertex(Collection<IDirectedEdge<IRPSTNode<DirectedEdge, Vertex>>> edges,
			Vertex entry) {
		List<IRPSTNode<DirectedEdge, Vertex>> res = new LinkedList<IRPSTNode<DirectedEdge, Vertex>>();

		Iterator<IDirectedEdge<IRPSTNode<DirectedEdge, Vertex>>> it = edges.iterator();
		Set<IRPSTNode<DirectedEdge, Vertex>> unorderedNodes = new HashSet<IRPSTNode<DirectedEdge, Vertex>>();

		IRPSTNode<DirectedEdge, Vertex> lastOrderedNode = null;
		while (it.hasNext()) {
			IRPSTNode<DirectedEdge, Vertex> node = it.next().getTarget();
			if (node.getEntry().equals(entry)) {
				lastOrderedNode = node;
				res.add(node);
			} else {
				unorderedNodes.add(node);
			}
		}

		// there could only be one fragment with the same entry as its polygon parent 
		assert(res.size() == 1); 
		
		// there has to be a fragment that shares entry point
		assert(lastOrderedNode != null);
		
		// order by looking at the last added node
		while (!unorderedNodes.isEmpty()) {
			Iterator<IRPSTNode<DirectedEdge, Vertex>> nit = unorderedNodes.iterator();
			while (nit.hasNext()) {
				IRPSTNode<DirectedEdge, Vertex> node = nit.next();
				if (lastOrderedNode.getExit().equals(node.getEntry())) {
					res.add(node);
					lastOrderedNode = node;
					nit.remove();
				}
			}
		}

		return res;
	}
}
