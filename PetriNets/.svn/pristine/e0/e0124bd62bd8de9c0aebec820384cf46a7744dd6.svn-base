package org.processmining.models.connections.petrinets.behavioral;

import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.analysis.AbstractMarkingSet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.Semantics;
import org.processmining.models.semantics.petrinet.Marking;

public class HomeMarkingConnection extends MarkingsetNetConnection {

	public final static String INITIALMARKING = "Initial Marking";

	public HomeMarkingConnection(PetrinetGraph net, Marking initial, AbstractMarkingSet<?> marking,
			Semantics<Marking, Transition> semantics, String setName) {
		super(net, marking, semantics, setName);
		put(INITIALMARKING, initial);
		// TODO Auto-generated constructor stub
	}

}
