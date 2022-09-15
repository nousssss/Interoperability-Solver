package org.processmining.plugins.nouss;

import org.processmining.models.graphbased.LocalNodeID;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

public class LabelledTransition extends Transition {

	    private String msgName="";
	    private String msgType="";
	    
	    public LabelledTransition(String label,
				AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> net) {
			super(label,net);
		}

		public LabelledTransition(String label,
				AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> net,
				ExpandableSubNet parent) {
			super(label,net,parent);
		}
		
		public LabelledTransition(String label,
				AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> net,
				ExpandableSubNet parent, LocalNodeID id) {
			super(label,net,parent,id);
		}
	    
	    public String getMsgName() {
	    	return this.msgName;
	    }
	    
	    public void setMsgName(String msgName) {
	    	this.msgName = msgName;
	    }
	    public String getMsgType() {
	    	return this.msgType;
	    }
	    
	    public void setMsgType(String msgType) {
	    	this.msgType = msgType;
	    }
}
