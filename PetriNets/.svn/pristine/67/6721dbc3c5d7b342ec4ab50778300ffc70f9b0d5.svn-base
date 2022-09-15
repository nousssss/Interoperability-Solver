package org.processmining.models.connections.petrinets.structural;

import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;

public class ShortCircuitedNetConnection extends AbstractConnection {
	public final static String NET = "Net";
	public final static String SCNET = "Short-circuited Net";
	public final static String FINALMARKING = "Final marking";

	public ShortCircuitedNetConnection(PetrinetGraph net, PetrinetGraph sCNet, Marking finalMarking) {
		super("Connection to short-circuited net of " + net.getLabel());
		put(NET, net);
		put(SCNET, sCNet);
		put(FINALMARKING, finalMarking);
	}
}
