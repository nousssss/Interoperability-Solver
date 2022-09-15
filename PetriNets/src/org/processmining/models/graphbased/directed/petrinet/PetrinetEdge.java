package org.processmining.models.graphbased.directed.petrinet;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.LocalNodeID;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.elements.ExpandableSubNet;

public abstract class PetrinetEdge<S extends PetrinetNode, T extends PetrinetNode> extends
		AbstractDirectedGraphEdge<S, T> implements ContainableDirectedGraphElement {

	private final ExpandableSubNet parent;
	private LocalNodeID localID;
	
	public PetrinetEdge(ExpandableSubNet parent, S source, T target, String label) {
		this(parent, source, target, label, new LocalNodeID());
	}
	
	public PetrinetEdge(ExpandableSubNet parent, S source, T target, String label, LocalNodeID localID) {
		super(source, target);
		this.parent = parent;
		this.localID = localID;
		getAttributeMap().put(AttributeMap.LABEL, label);
	}

	public ExpandableSubNet getParent() {
		return parent;
	}
	
	public LocalNodeID getLocalID(){
		return localID;
	}
	
	public void setLocalID(LocalNodeID newLocalID){
		this.localID = newLocalID;
	}
}
