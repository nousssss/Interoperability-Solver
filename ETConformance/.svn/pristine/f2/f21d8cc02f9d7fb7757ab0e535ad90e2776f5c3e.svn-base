package org.processmining.plugins.multietc.automaton;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

public class AutomatonEdge {
	
	private AutomatonNode source;
	private AutomatonNode target;
	private Transition transition;
	
	public AutomatonEdge (AutomatonNode s, AutomatonNode t, Transition trans){
		source = s;
		target = t;
		transition = trans;
	}
	
	public Transition getTransition(){
		return transition;
	}

	public AutomatonNode getSource() {
		return source;
	}

	public AutomatonNode getTarget() {
		return target;
	}
	

	@Override
    public String toString() {
		return transition.getLabel();
	}


}
