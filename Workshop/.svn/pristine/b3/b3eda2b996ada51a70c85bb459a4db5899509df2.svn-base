package org.processmining.models.workshop.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;
import org.processmining.models.shapes.Decorated;
import org.processmining.models.shapes.Ellipse;

/**
 * Nodes in the workshop graph.
 * 
 * @author hverbeek
 * 
 */
public class WorkshopNode extends AbstractDirectedGraphNode implements Decorated {

	/**
	 * The payload of this node: the event class.
	 */
	private XEventClass eventClass;
	/**
	 * A back link to the graph the node is in.
	 */
	private WorkshopGraph graph;

	/**
	 * Creates a node for the given graph with given event class. Note that one
	 * still needs to add this node to the graph, as this method only creates
	 * the node for the graph, but does not add it to the grpah.
	 * 
	 * @param eventClass
	 *            The given event class.
	 * @param graph
	 *            The given graph.
	 */
	public WorkshopNode(XEventClass eventClass, WorkshopGraph graph) {
		this.eventClass = eventClass;
		this.graph = graph;
		getAttributeMap().put(AttributeMap.LABEL, eventClass.getId());
		getAttributeMap().put(AttributeMap.SHAPE, new Ellipse());
		getAttributeMap().put(AttributeMap.SQUAREBB, false);
		getAttributeMap().put(AttributeMap.RESIZABLE, true);
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(100, 60));
		getAttributeMap().put(AttributeMap.FILLCOLOR, Color.LIGHT_GRAY);
		getAttributeMap().put(AttributeMap.AUTOSIZE, false);
	}

	/**
	 * Decorates the node.
	 */
	public void decorate(Graphics2D g2d, double x, double y, double width, double height) {
	}

	/**
	 * Checks whether the given object is equal to this node.
	 * 
	 * @param o
	 *            The given object.
	 */
	public boolean equals(Object o) {
		return (o instanceof WorkshopNode ? eventClass.equals(((WorkshopNode) o).eventClass) : false);
	}

	/**
	 * Gets the hash code for this node.
	 * 
	 * @return The hash code for this node.
	 */
	public int hashCode() {
		return eventClass.hashCode();
	}

	/**
	 * Gets the graph this node was created for.
	 * 
	 * @return The graph this node was created for.
	 */
	public AbstractDirectedGraph<?, ?> getGraph() {
		// TODO Auto-generated method stub
		return graph;
	}

	/**
	 * Gets the payload for this node, that is, its event class.
	 * 
	 * @return The event class for this node.
	 */
	public XEventClass getEventClass() {
		return eventClass;
	}
}
