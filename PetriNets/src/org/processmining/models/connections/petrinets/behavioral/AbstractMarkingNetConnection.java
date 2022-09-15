package org.processmining.models.connections.petrinets.behavioral;

import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;

abstract class AbstractMarkingNetConnection extends AbstractConnection {

	public final static String NET = "Net";
	public final static String MARKING = "Marking";

	/**
	 * 
	 */
	private static final long serialVersionUID = 3134438310240127451L;

	/**
	 * The marking contains only places from the net, i.e.
	 * assert(net.getPlaces().containsAll(m.toSet()));
	 * 
	 * @param net
	 * @param marking
	 */
	public AbstractMarkingNetConnection(PetrinetGraph net, Marking m) {
		super("Connection " + net.getLabel() + " marked with " + m.toString());
		put(NET, net);
		put(MARKING, m);
	}

}
