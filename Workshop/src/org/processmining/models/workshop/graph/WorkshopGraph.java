package org.processmining.models.workshop.graph;

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
 * The workshop graph. Used to visualize abstracted workshop models.
 * 
 * @author hverbeek
 * 
 */
public class WorkshopGraph extends AbstractDirectedGraph<WorkshopNode, WorkshopEdge> {

	/**
	 * The label of the graph.
	 */
	private String label;
	/**
	 * The nodes in the graph.
	 */
	private Set<WorkshopNode> nodes;
	/**
	 * The edges in the graph.
	 */
	private Set<WorkshopEdge> edges;

	/**
	 * Creates an empty graph with given label.
	 * 
	 * @param label
	 *            The given label.
	 */
	public WorkshopGraph(String label) {
		super();
		getAttributeMap().put(AttributeMap.LABEL, label);
		getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.NORTH);
		nodes = new HashSet<WorkshopNode>();
		edges = new HashSet<WorkshopEdge>();
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
	protected AbstractDirectedGraph<WorkshopNode, WorkshopEdge> getEmptyClone() {
		return new WorkshopGraph(label);
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
			DirectedGraph<WorkshopNode, WorkshopEdge> graph) {
		assert (graph instanceof WorkshopGraph);
		Map<DirectedGraphElement, DirectedGraphElement> map = new HashMap<DirectedGraphElement, DirectedGraphElement>();
		Map<WorkshopNode, WorkshopNode> nodeMap = new HashMap<WorkshopNode, WorkshopNode>();
		WorkshopGraph graphToClone = (WorkshopGraph) graph;
		for (WorkshopNode nodeToClone : graphToClone.getNodes()) {
			WorkshopNode node = new WorkshopNode(nodeToClone.getEventClass(), this);
			addNode(node);
			map.put(nodeToClone, node);
			nodeMap.put(nodeToClone, node);
		}
		for (WorkshopEdge edgeToClone : graphToClone.getEdges()) {
			WorkshopEdge edge = new WorkshopEdge(nodeMap.get(edgeToClone.getSource()), nodeMap.get(edgeToClone
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
	public void addNode(WorkshopNode node) {
		nodes.add(node);
		graphElementAdded(node);
	}

	/**
	 * Adds an edge to this graph.
	 * 
	 * @param edge
	 *            The edge to add.
	 */
	public void addEdge(WorkshopEdge edge) {
		edges.add(edge);
		graphElementAdded(edge);
	}

	/**
	 * Gets all nodes in the graph.
	 * 
	 * @return The nodes in the graph.
	 */
	public Set<WorkshopNode> getNodes() {
		return nodes;
	}

	/**
	 * Gets all edges in the graph.
	 * 
	 * @return The edges in the graph.
	 */
	public Set<WorkshopEdge> getEdges() {
		return edges;
	}

	/**
	 * Removes a node from the graph.
	 * 
	 * @param node
	 *            The node to remove from the graph.
	 */
	public void removeNode(DirectedGraphNode node) {
		if (node instanceof WorkshopNode) {
			removeNodeFromCollection(nodes, (WorkshopNode) node);
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
		if (edge instanceof WorkshopEdge) {
			removeNodeFromCollection(edges, (WorkshopEdge) edge);
			edges.remove(edge);
		}
	}

}
