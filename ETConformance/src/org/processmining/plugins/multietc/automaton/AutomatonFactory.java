package org.processmining.plugins.multietc.automaton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.processmining.framework.util.collection.HashMultiSet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.multietc.sett.MultiETCSettings;

/**
 * Factory to create all the precision automaton elements according with the settings.
 * 
 * @author Jorge Munoz-Gama (jmunoz)
 */
public class AutomatonFactory {
	
	/** Settings to determine the properties of the object returned by the factory */
	MultiETCSettings sett;
	
	/**
	 * Create a factory.
	 * @param s Settings that determine the properties of the objects returned by the factory.
	 */
	public AutomatonFactory(MultiETCSettings s){
		sett = s;
	}
	
	/**
	 * Create a precision automaton according with the settings of the factory.
	 * @return
	 */
	public Automaton createAutomaton(){
		return new Automaton(this);
	}
	
	/**
	 * Create a automaton node according with the settings of the factory.
	 * @param past Sequence of transitions considered before the state of the created node.
	 * @param future Sequence of transitions to considere after the stated represented by the created node.
	 * @return Node created according to the settings of the factory considering the giving the seen and to see
	 * sequence of transitions.
	 */
	public AutomatonNode createNode(List<Transition> past, List<Transition> future){
		
		MultiETCSettings.Representation  r = sett.getRepresentation();
		MultiETCSettings.Window w = sett.getWindow();
		
		//PAST
		if(w == MultiETCSettings.Window.BACKWARDS && 
				r == MultiETCSettings.Representation.ORDERED){
			return new AutomatonNodeOrder(new LinkedList<Transition>(past));
		}
		
		else if(w == MultiETCSettings.Window.BACKWARDS && 
				r == MultiETCSettings.Representation.UNORDERED){
			return new AutomatonNodeNoOrder(new HashMultiSet<Transition>(past));
		}
		
		//FUTURE
		else if(w == MultiETCSettings.Window.FORWARDS && 
				r == MultiETCSettings.Representation.ORDERED){
			List<Transition> revFuture = new ArrayList<Transition>(future);
			Collections.reverse(revFuture);
			return new AutomatonNodeOrder(new LinkedList<Transition>(revFuture));
		}
		
		else if(w == MultiETCSettings.Window.FORWARDS && 
				r == MultiETCSettings.Representation.UNORDERED){
			return new AutomatonNodeNoOrder(new HashMultiSet<Transition>(future));
		}
		
		else return null;
	}

	/**
	 * Return the settings that control the factory.
	 * @return Settings of the factory.
	 */
	public MultiETCSettings getSett() {
		return sett;
	}

	
	
}
