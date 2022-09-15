package org.processmining.petrinets.utils;

import java.util.HashSet;
import java.util.Set;

import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.graphbased.directed.DirectedGraphNode;

public class DirectedGraphUtils {

	public static <D extends DirectedGraph<?, ?>> Set<DirectedGraphNode> getPostSet(D graph, DirectedGraphNode node) {
		Set<DirectedGraphNode> postSet = new HashSet<DirectedGraphNode>();
		for (DirectedGraphEdge<?, ?> outEdge : graph.getOutEdges(node)) {
			for (DirectedGraphNode n : graph.getNodes()) {
				if (graph.getInEdges(n).contains(outEdge)) {
					postSet.add(n);
				}
			}
		}
		return postSet;
	}

	public static <D extends DirectedGraph<?, ?>> Set<DirectedGraphNode> getPreSet(D graph, DirectedGraphNode node) {
		Set<DirectedGraphNode> preSet = new HashSet<DirectedGraphNode>();
		for (DirectedGraphEdge<?, ?> inEdge : graph.getInEdges(node)) {
			for (DirectedGraphNode n : graph.getNodes()) {
				if (graph.getOutEdges(n).contains(inEdge)) {
					preSet.add(n);
				}
			}
		}
		return preSet;
	}

	/**
	 * 
	 * @param graph
	 * @param source
	 * @param target
	 * @return the edge connecting source and target, null if such edge does not
	 *         exist in the graph
	 */
	public static <E extends DirectedGraphEdge<?, ?>> E getConnectingEdge(DirectedGraph<?, E> graph,
			DirectedGraphNode source, DirectedGraphNode target) {
		for (E e : graph.getEdges()) {
			if (e.getSource().equals(source) && e.getTarget().equals(target)) {
				return e;
			}
		}
		return null;
	}

	public static <E extends DirectedGraphEdge<?, ?>> Set<Pair<DirectedGraphNode, DirectedGraphNode>> translateToNodePairs(
			Set<E> edges) {
		Set<Pair<DirectedGraphNode, DirectedGraphNode>> res = new HashSet<Pair<DirectedGraphNode, DirectedGraphNode>>();
		for (E e : edges) {
			res.add(new Pair<DirectedGraphNode, DirectedGraphNode>(e.getSource(), e.getTarget()));
		}
		return res;
	}

	public static Set<DirectedGraphNode> getSources(DirectedGraph<?, ?> graph) {
		Set<DirectedGraphNode> sources = new HashSet<DirectedGraphNode>();
		for (DirectedGraphNode n : graph.getNodes()) {
			if (graph.getInEdges(n).isEmpty()) {
				sources.add(n);
			}
		}
		return sources;
	}

}
