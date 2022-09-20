package org.processmining.models.ghzbue.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingConstants;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.DirectedGraphNode;

/**
 * The Ghz graph. Used to visualize abstracted Ghz models.
 * 
 * @author Ghzzz
 * 
 */

public class GhzGraph extends AbstractDirectedGraph<GhzNode, GhzEdge> {
	
	/**
	 * The label of the graph.
	 */
	private String label;
	/**
	 * The nodes in the graph.
	 */
	private Set<GhzNode> nodes;
	/**
	 * The edges in the graph.
	 */
	private Set<GhzEdge> edges;
	
	/**
	 * Creates an empty graph with given label.
	 * 
	 * @param label
	 *            The given label.
	 */
	public GhzGraph(String label) {
		super();
		getAttributeMap().put(AttributeMap.LABEL, label);
		getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.NORTH);
		nodes = new HashSet<GhzNode>();
		edges = new HashSet<GhzEdge>();
	}
	
	/**
	 * Gets the label of the graph.
	 * 
	 * @return The label of the graph.
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Gets an empty clone of this graph.
	 * 
	 * @return An empty clone of this graph.
	 */
	protected AbstractDirectedGraph<GhzNode, GhzEdge> getEmptyClone() {
		return new GhzGraph(label);
	}

	/**
	 * Clones all nodes and edges from the given graph to this graph.
	 * 
	 * @param graph
	 *            The given graph.
	 * @return A map that links nodes and edges in the given graph to clones
	 *         nodes and edges in this graph.
	 */
	protected Map<? extends DirectedGraphElement, ? extends DirectedGraphElement> cloneFrom(
			DirectedGraph<GhzNode, GhzEdge> graph) {
		assert (graph instanceof GhzGraph);
		Map<DirectedGraphElement, DirectedGraphElement> map = new HashMap<DirectedGraphElement, DirectedGraphElement>();
		Map<GhzNode, GhzNode> nodeMap = new HashMap<GhzNode, GhzNode>();
		GhzGraph graphToClone = (GhzGraph) graph;
		for (GhzNode nodeToClone : graphToClone.getNodes()) {
			GhzNode node = new GhzNode(nodeToClone.getEventClass(), this);
			addNode(node);
			map.put(nodeToClone, node);
			nodeMap.put(nodeToClone, node);
		}
		for (GhzEdge edgeToClone : graphToClone.getEdges()) {
			GhzEdge edge = new GhzEdge(nodeMap.get(edgeToClone.getSource()), nodeMap.get(edgeToClone
					.getTarget()), edgeToClone.getCardinality());
			addEdge(edge);
			map.put(edgeToClone, edge);
		}
		return map;
	}
	
	/**
	 * Adds a node to the graph.
	 * 
	 * @param node
	 *            The node to add.
	 */
	public void addNode(GhzNode node) {
		nodes.add(node);
		graphElementAdded(node);
		
	}
	
	/**
	 * Adds an edge to this graph.
	 * 
	 * @param edge
	 *            The edge to add.
	 */
	public void addEdge(GhzEdge edge) {
		edges.add(edge);
		graphElementAdded(edge);		
	}

	/**
	 * Gets all nodes in the graph.
	 * 
	 * @return The nodes in the graph.
	 */
	public Set<GhzNode> getNodes() {
		return nodes;
	}
	
	/**
	 * Gets all edges in the graph.
	 * 
	 * @return The edges in the graph.
	 */
	public Set<GhzEdge> getEdges() {
		return edges;
	}

	/**
	 * Removes a node from the graph.
	 * 
	 * @param node
	 *            The node to remove from the graph.
	 */
	public void removeNode(DirectedGraphNode node) {
		if (node instanceof GhzNode) {
			removeNodeFromCollection(nodes, (GhzNode) node);
			nodes.remove(node);

		}
		
	}
	
	/**
	 * Removes an edge from the graph.
	 * 
	 * @param edge
	 *            The edge to remove from the graph.
	 */
	public void removeEdge(@SuppressWarnings("rawtypes") DirectedGraphEdge edge) {
		if (edge instanceof GhzEdge) {
			removeNodeFromCollection(edges, (GhzEdge) edge);
			edges.remove(edge);
		}
		
	}
}
