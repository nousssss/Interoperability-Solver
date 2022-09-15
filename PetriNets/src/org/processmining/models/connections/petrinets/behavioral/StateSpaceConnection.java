package org.processmining.models.connections.petrinets.behavioral;

import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.transitionsystem.TransitionSystem;
import org.processmining.models.semantics.Semantics;
import org.processmining.models.semantics.petrinet.Marking;

public class StateSpaceConnection extends AbstractSemanticConnection {

	public final static String STATEPACE = "Statespace";

	private static final long serialVersionUID = 3134438310240127451L;

	public StateSpaceConnection(PetrinetGraph net, Marking marking, TransitionSystem statespace,
			Semantics<Marking, Transition> semantics) {
		super("Connection to statespace of " + net.getLabel(), net, marking, semantics);
		put(STATEPACE, statespace);
	}

}
