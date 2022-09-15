package org.processmining.models.connections.petrinets.behavioral;

import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.analysis.DeadTransitionsSet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.Semantics;
import org.processmining.models.semantics.petrinet.Marking;

public class DeadTransitionsConnection extends AbstractSemanticConnection {

	public DeadTransitionsConnection(PetrinetGraph net, Marking marking, Semantics<Marking, Transition> semantics,
			DeadTransitionsSet dead) {
		super("Connection to dead transitions of " + net.getLabel() + " (represents " + semantics.toString() + ")", net, marking,
				semantics);
		put(TRANSITIONS, dead);
	}

	public final static String TRANSITIONS = "ComponentSet";

}