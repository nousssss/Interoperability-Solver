package org.processmining.models.connections.petrinets.behavioral;

import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.analysis.NonLiveTransitionsSet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.Semantics;
import org.processmining.models.semantics.petrinet.Marking;

public class NonLiveTransitionsConnection extends AbstractSemanticConnection {
	public final static String TRANSITIONS = "ComponentSet";

	public NonLiveTransitionsConnection(PetrinetGraph net, NonLiveTransitionsSet transitions, Marking marking,
			Semantics<Marking, Transition> semantics) {
		super("Connection to non-live transitions of " + net.getLabel() + " (represents " + semantics.toString() + ")", net, marking,
				semantics);
		put(TRANSITIONS, transitions);
	}

}