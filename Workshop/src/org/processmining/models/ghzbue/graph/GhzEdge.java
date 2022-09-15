package org.processmining.models.ghzbue.graph;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.AttributeMap.ArrowType;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
/**
 * Edges in the Ghz graph.
 * 
 * @author Ghzzz
 * 
 */
public class GhzEdge extends AbstractDirectedGraphEdge<GhzNode, GhzNode> {
	
	/**
	 * The source node.
	 */
	private GhzNode source;
	/**
	 * The target node.
	 */
	private GhzNode target;
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
	public GhzEdge(GhzNode source, GhzNode target, int cardinality) {
		super(source, target);
		this.source = source;
		this.target = target;
		this.cardinality = cardinality;
		getAttributeMap().put(AttributeMap.LABEL, this.cardinality.toString());
		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_SIMPLE);
		getAttributeMap().put(AttributeMap.EDGEENDFILLED, true);
		getAttributeMap().put(AttributeMap.SHOWLABEL, true);
	}

	public GhzNode getSource() {
		return source;
	}

	public GhzNode getTarget() {
		return target;
	}

	public Integer getCardinality() {
		return cardinality;
	}
}
