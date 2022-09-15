package org.processmining.plugins.etconformance.util;

import java.util.Collection;
import java.util.Iterator;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.ExecutionInformation;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.models.semantics.petrinet.PetrinetSemantics;
import org.processmining.plugins.connectionfactories.logpetrinet.EvClassLogPetrinetConnectionFactoryUI;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;

/**
 * PetriNet Semantics that only allows firing invisible (not associated with an
 * event) transitions.
 * 
 * @author Jorge Munoz-Gama (jmunoz)
 */
public class InvisiblePetrinetSemantics implements PetrinetSemantics {

	/** Serial Version UID */
	private static final long serialVersionUID = 1L;

	TransEvClassMapping mapping;
	PetrinetSemantics sem;

	public InvisiblePetrinetSemantics(PetrinetSemantics s, TransEvClassMapping m) {
		sem = s;
		this.mapping = m;
	}

	public Collection<Transition> getExecutableTransitions() {
		//The only different method: only invisible transitions are executable
		Collection<Transition> trans = sem.getExecutableTransitions();
		Iterator<Transition> transIt = trans.iterator();

		while (transIt.hasNext()) {
			Transition t = transIt.next();
			XEventClass task = mapping.get(t);
			XEventClass dummy = EvClassLogPetrinetConnectionFactoryUI.DUMMY;
			
			if (task != dummy) {
				transIt.remove();
			}
		}

		return trans;
	}

	public ExecutionInformation executeExecutableTransition(Transition toExecute) throws IllegalTransitionException {
		return sem.executeExecutableTransition(toExecute);
	}

	public Marking getCurrentState() {
		return sem.getCurrentState();
	}

	public void initialize(Collection<Transition> transitions, Marking initialState) {
		sem.initialize(transitions, initialState);

	}

	public void setCurrentState(Marking currentState) {
		sem.setCurrentState(currentState);

	}

}
