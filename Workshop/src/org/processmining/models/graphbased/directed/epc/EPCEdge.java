package org.processmining.models.graphbased.directed.epc;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.EdgeID;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;

public abstract class EPCEdge<S extends EPCNode, T extends EPCNode> extends AbstractDirectedGraphEdge<S, T> {

	private final EdgeID id = new EdgeID();

	public EPCEdge(S source, T target, String label) {
		super(source, target);
		getAttributeMap().put(AttributeMap.LABEL, label);
	}

	public int compareTo(EPCEdge<S, T> edge) {
		return edge.id.compareTo(id);
	}

	public int hashCode() {
		// Hashcode not based on source and target, which
		// respects contract that this.equals(o) implies
		// this.hashCode()==o.hashCode()
		return id.hashCode();
	}

	public boolean equals(Object o) {
		if (!(this.getClass().equals(o.getClass()))) {
			return false;
		}
		EPCEdge<?, ?> edge = (EPCEdge<?, ?>) o;

		return edge.id.equals(id);
	}

}
