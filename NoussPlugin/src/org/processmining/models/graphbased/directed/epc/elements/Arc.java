package org.processmining.models.graphbased.directed.epc.elements;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.AttributeMap.ArrowType;
import org.processmining.models.graphbased.directed.epc.EPCEdge;
import org.processmining.models.graphbased.directed.epc.EPCNode;

public class Arc extends EPCEdge<EPCNode, EPCNode> {

	public Arc(EPCNode source, EPCNode target, String label) {
		super(source, target, label);
		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_CLASSIC);
		getAttributeMap().put(AttributeMap.EDGEENDFILLED, true);
	}

	public boolean equals(Object o) {
		return (o == this);
	}

}
