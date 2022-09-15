package org.processmining.models.graphbased.directed.epc.elements;

import java.awt.Color;
import java.awt.Dimension;

import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.epc.EPCEdge;
import org.processmining.models.graphbased.directed.epc.EPCNode;
import org.processmining.models.shapes.Rectangle;

public class Function extends EPCNode {

	public Function(AbstractDirectedGraph<EPCNode, EPCEdge<? extends EPCNode, ? extends EPCNode>> epc, String label) {
		super(epc, label);
		getAttributeMap().put(AttributeMap.SHAPE, new Rectangle(true));
		getAttributeMap().put(AttributeMap.SQUAREBB, false);
		getAttributeMap().put(AttributeMap.RESIZABLE, false);
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(60, 40));
		getAttributeMap().put(AttributeMap.FILLCOLOR, new Color(128, 255, 128));
	}

}
