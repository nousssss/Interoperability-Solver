package org.processmining.models.connections.petrinets.behavioral;

import org.processmining.framework.connections.annotations.ConnectionDoesntExistMessage;
import org.processmining.framework.connections.impl.AbstractStrongReferencingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.analysis.AbstractMarkingSet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.Semantics;
import org.processmining.models.semantics.petrinet.Marking;

@ConnectionDoesntExistMessage(message = "There is no known relation between the give set of markings and the given Petri net.")
public abstract class MarkingsetNetConnection extends AbstractStrongReferencingConnection {

	public final static String NET = "Net";
	public final static String MARKINGS = "Markings";
	public final static String SEMANTICS = "Semantics";

	/**
	 * 
	 */
	private static final long serialVersionUID = 3134438310240127451L;

	/**
	 * All markings contain only places from the net, i.e. for all elements m of
	 * marking holds that: assert(net.getPlaces().containsAll(m.toSet()));
	 * 
	 * @param net
	 * @param marking
	 */
	public MarkingsetNetConnection(PetrinetGraph net, AbstractMarkingSet<?> marking,
			Semantics<Marking, Transition> semantics, String setName) {
		super("Connection to " + setName + " of " + net.getLabel());
		putStrong(SEMANTICS, semantics);
		put(NET, net);
		put(MARKINGS, marking);
	}

}
