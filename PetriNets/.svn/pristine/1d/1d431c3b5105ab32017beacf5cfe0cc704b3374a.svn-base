package org.processmining.models.graphbased.directed.petrinet.elements;

import java.awt.Dimension;

import javax.swing.SwingConstants;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.LocalNodeID;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.shapes.Ellipse;

public class Place extends PetrinetNode {

	private static final long serialVersionUID = -140549782392304694L;

	public Place(String label,
			AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> net) {
		this(label, net, null);
	}

	public Place(String label,
			AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> net,
			ExpandableSubNet parent) {

		this(label, net, parent, new LocalNodeID());
	}
	
	public Place(String label,
			AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> net,
			ExpandableSubNet parent, LocalNodeID id) {

		super(net, parent, label, id);
		getAttributeMap().put(AttributeMap.SHAPE, new Ellipse());
		getAttributeMap().put(AttributeMap.SQUAREBB, true);
		getAttributeMap().put(AttributeMap.RESIZABLE, true);
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(25, 25));
		getAttributeMap().put(AttributeMap.SHOWLABEL, false);
		getAttributeMap().put(AttributeMap.LABELVERTICALALIGNMENT, SwingConstants.CENTER);
	}
}
