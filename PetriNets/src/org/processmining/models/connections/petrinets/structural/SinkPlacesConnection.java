package org.processmining.models.connections.petrinets.structural;

import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.analysis.SinkPlacesSet;

public class SinkPlacesConnection extends AbstractConnection {

	public final static String NET = "Net";
	public final static String PLACES = "Sink places";

	public SinkPlacesConnection(PetrinetGraph net, SinkPlacesSet places) {
		super("Connection to sink places of " + net.getLabel());
		put(NET, net);
		put(PLACES, places);
	}
}
