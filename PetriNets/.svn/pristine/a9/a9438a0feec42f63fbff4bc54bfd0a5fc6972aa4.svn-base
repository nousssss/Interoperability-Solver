package org.processmining.models.connections.petrinets.behavioral;

import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.transitionsystem.AcceptStateSet;
import org.processmining.models.semantics.Semantics;
import org.processmining.models.semantics.petrinet.Marking;

public class DeadMarkingConnection extends AbstractSemanticConnection {
	public final static String DEADMARKINGS = "Dead Markings";

	public DeadMarkingConnection(PetrinetGraph net, Marking initial, AcceptStateSet acceptingStates,
			Semantics<Marking, Transition> semantics) {
		super("Connection to Dead markings of " + net.getLabel(), net, initial, semantics);
		put(DEADMARKINGS, acceptingStates);
	}


}
