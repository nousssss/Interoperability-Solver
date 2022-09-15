/**
 * 
 */
package org.processmining.plugins.multietc.automaton;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.models.semantics.petrinet.PetrinetSemantics;
import org.processmining.models.semantics.petrinet.impl.PetrinetSemanticsFactory;

/**
 * @author jmunoz
 *
 */
public class AutomatonNodeOrder extends AutomatonNode {
	
	List<Transition> feat;
	
	public AutomatonNodeOrder( List<Transition> f){
		super();
		feat = f;
	}
	
	public void computeMarking(Petrinet net, Marking iniM, Map<PetrinetNode, PetrinetNode> orig2new,
			Map<PetrinetNode, PetrinetNode> new2orig) {
		//Reproduce the trace in order. Note1: log refer to the original net transitions
		//Note 2: marking for this state refers to place in the original net
		
		PetrinetSemantics sem = PetrinetSemanticsFactory.regularPetrinetSemantics(Petrinet.class);
		sem.initialize(net.getTransitions(), iniM);
		
		//Replay all the trace in order
		boolean fit = true;
		for (Iterator<Transition> i = feat.iterator(); i.hasNext() && fit;) {
			Transition t = (Transition) orig2new.get(i.next());
			try {
				sem.executeExecutableTransition(t);
			} catch (IllegalTransitionException e) {
				fit = false;
			}
		}
		
		if(!fit){
			this.setMarking(null);
		}
		else{
			Marking m = new Marking();
			for(Place p: sem.getCurrentState()){
				m.add((Place) new2orig.get(p));
			}
			this.setMarking(m);
		}
		
	}
	
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof AutomatonNodeOrder)) return false;
	    AutomatonNodeOrder otherNode = (AutomatonNodeOrder) other;
	    return (this.feat.equals(otherNode.feat));
	}
	
	@Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + feat.hashCode();
        return hash;
    }
	
	@Override
    public String toString() {
		return feat.toString();
	}



}
