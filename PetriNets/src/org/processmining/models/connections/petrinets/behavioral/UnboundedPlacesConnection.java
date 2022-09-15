package org.processmining.models.connections.petrinets.behavioral;

import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.analysis.UnboundedPlacesSet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.Semantics;
import org.processmining.models.semantics.petrinet.Marking;

public class UnboundedPlacesConnection extends AbstractSemanticConnection {
	public final static String PLACES = "Places";

	public UnboundedPlacesConnection(PetrinetGraph net, UnboundedPlacesSet places, Marking marking,
			Semantics<Marking, Transition> semantics) {
		super("Connection to unbounded places of " + net.getLabel() + " (represents " + semantics.toString() + ")", net, marking,
				semantics);
		put(PLACES, places);
	}
}
