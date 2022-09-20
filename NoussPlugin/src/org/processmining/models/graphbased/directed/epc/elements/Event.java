package org.processmining.models.graphbased.directed.epc.elements;

import java.awt.Color;
import java.awt.Dimension;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.epc.EPCEdge;
import org.processmining.models.graphbased.directed.epc.EPCNode;
import org.processmining.models.shapes.Hexagon;

public class Event extends EPCNode {

	public Event(AbstractDirectedGraph<EPCNode, EPCEdge<? extends EPCNode, ? extends EPCNode>> epc, String label) {
		super(epc, label);
		getAttributeMap().put(AttributeMap.SHAPE, new Hexagon(0.1));
		getAttributeMap().put(AttributeMap.RESIZABLE, false);
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(60, 40));
		getAttributeMap().put(AttributeMap.FILLCOLOR, new Color(255, 128, 128));
	}

}
