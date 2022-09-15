/**
 * 
 */
package org.processmining.plugins.multietc.automaton;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.processmining.framework.util.collection.MultiSet;
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
public class AutomatonNodeNoOrder extends AutomatonNode {
	
	MultiSet<Transition> feat;
	
	public AutomatonNodeNoOrder(MultiSet<Transition> f){
		super();
		feat = f;
	}
	
	public void computeMarking(Petrinet net, Marking iniM, Map<PetrinetNode, PetrinetNode> orig2new,
			Map<PetrinetNode, PetrinetNode> new2orig) {
		
		PetrinetSemantics sem = PetrinetSemanticsFactory.regularPetrinetSemantics(Petrinet.class);
		sem.initialize(net.getTransitions(), iniM);
		
		Marking reachedM = computeMarkingRec(sem,iniM,feat.toList(), orig2new, new2orig);
		this.setMarking(reachedM);		
	}
	
	private Marking computeMarkingRec(PetrinetSemantics sem, Marking currM, List<Transition> tasks, Map<PetrinetNode, PetrinetNode> orig2new,
			Map<PetrinetNode, PetrinetNode> new2orig){
		
		//Base case: all tasks has been fired
		if(tasks.isEmpty()){
			Marking m = new Marking();
			for(Place p: currM){
				m.add((Place) new2orig.get(p));
			}
			return m;
		}
		
		//Recursive Case
		else{
			sem.setCurrentState(currM);
			Collection<Transition> enabled = sem.getExecutableTransitions();
			Marking finalM = null;
			
			int i = 0;
			//While a solution hasn't been found and there are more tasks to explore
			while(i<tasks.size() && finalM == null){
				if(enabled.contains(orig2new.get(tasks.get(i)))){
					
					sem.setCurrentState(currM);
					Transition t = (Transition) orig2new.get(tasks.get(i));
					try {
						sem.executeExecutableTransition(t);
					} catch (IllegalTransitionException e) {
						// This can't happen because only enabled tasks are fired
						e.printStackTrace();
					}
					Marking markingAfterI = sem.getCurrentState();
					
					List<Transition> tasksWithoutI = new LinkedList<Transition>(tasks);
					tasksWithoutI.remove(i);
					
					finalM = computeMarkingRec(sem, markingAfterI, tasksWithoutI, orig2new, new2orig);
				}
				i++;
			}
			return finalM;
		}
	}
	
	
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof AutomatonNodeNoOrder)) return false;
	    AutomatonNodeNoOrder otherNode = (AutomatonNodeNoOrder) other;
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
