package org.processmining.models.graphbased.directed.petrinet.elements;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.AttributeMap.ArrowType;
import org.processmining.models.graphbased.LocalNodeID;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;

public class Arc extends PetrinetEdge<PetrinetNode, PetrinetNode> {

	private int weight;

	public Arc(PetrinetNode source, PetrinetNode target, int weight) {
		this(source, target, weight, null);
	}

	public Arc(PetrinetNode source, PetrinetNode target, int weight, ExpandableSubNet parent) {
		this(source, target, weight, parent, new LocalNodeID());
	}
	
	public Arc(PetrinetNode source, PetrinetNode target, int weight, ExpandableSubNet parent, LocalNodeID id) {
		super(parent, source, target, "" + weight, id);
		this.weight = weight;
		getAttributeMap().put(AttributeMap.LABEL, "" + weight);
		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_TECHNICAL);
		getAttributeMap().put(AttributeMap.EDGEENDFILLED, true);
		getAttributeMap().put(AttributeMap.SHOWLABEL, weight > 1);
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
		getAttributeMap().put(AttributeMap.LABEL, "" + weight);
		getAttributeMap().put(AttributeMap.SHOWLABEL, weight > 1);
		getGraph().graphElementChanged(this);
	}

	public String toString() {
		return getLabel() + " (" + weight + ")";
	}

}
