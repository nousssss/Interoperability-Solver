package org.processmining.models.workshop.graph;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.AttributeMap.ArrowType;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;

/**
 * Edges in the workshop graph.
 * 
 * @author hverbeek
 * 
 */
public class WorkshopEdge extends AbstractDirectedGraphEdge<WorkshopNode, WorkshopNode> {

	/**
	 * The source node.
	 */
	private WorkshopNode source;
	/**
	 * The target node.
	 */
	private WorkshopNode target;
	/**
	 * The cardinality.
	 */
	private Integer cardinality;

	/**
	 * Creates an edge from the given source node to the given target node with
	 * given cardinality.
	 * 
	 * @param source
	 *            The source node.
	 * @param target
	 *            The target node.
	 * @param cardinality
	 *            The cardinality.
	 */
	public WorkshopEdge(WorkshopNode source, WorkshopNode target, int cardinality) {
		super(source, target);
		this.source = source;
		this.target = target;
		this.cardinality = cardinality;
		getAttributeMap().put(AttributeMap.LABEL, this.cardinality.toString());
		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_SIMPLE);
		getAttributeMap().put(AttributeMap.EDGEENDFILLED, true);
		getAttributeMap().put(AttributeMap.SHOWLABEL, true);
	}

	/**
	 * Gets the source node.
	 * 
	 * @return The source node.
	 */
	public WorkshopNode getSource() {
		return source;
	}

	/**
	 * Gets the target node.
	 * 
	 * @return The target node.
	 */
	public WorkshopNode getTarget() {
		return target;
	}

	/**
	 * Gets the cardinality.
	 * 
	 * @return The cardinality.
	 */
	public Integer getCardinality() {
		return cardinality;
	}
}
