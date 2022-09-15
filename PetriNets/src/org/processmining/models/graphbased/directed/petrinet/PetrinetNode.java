package org.processmining.models.graphbased.directed.petrinet;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.LocalNodeID;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;
import org.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.elements.ExpandableSubNet;

public abstract class PetrinetNode extends AbstractDirectedGraphNode implements ContainableDirectedGraphElement {

	private final ExpandableSubNet parent;
	protected int stdWidth = 50;
	protected int stdHeight = 50;
	private LocalNodeID localID;

	private final AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> graph;

	public PetrinetNode(
			AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> net,
			ExpandableSubNet parent, String label) {
		this(net, parent, label, new LocalNodeID());
	}
	
	public PetrinetNode(
			AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> net,
			ExpandableSubNet parent, String label, LocalNodeID localID) {
		super();
		graph = net;
		this.parent = parent;
		if (parent != null) {
			parent.addChild(this);
		}
		this.localID = localID;
		getAttributeMap().put(AttributeMap.LABEL, label);
	}

	public AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> getGraph() {
		return graph;
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
