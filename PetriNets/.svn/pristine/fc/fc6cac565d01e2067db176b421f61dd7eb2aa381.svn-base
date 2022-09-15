package org.processmining.models.connections.petrinets.structural;

import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.analysis.SourcePlacesSet;

public class SourcePlacesConnection extends AbstractConnection {

	public final static String NET = "Net";
	public final static String PLACES = "Source places";

	public SourcePlacesConnection(PetrinetGraph net, SourcePlacesSet places) {
		super("Source places of " + net.getLabel());
		put(NET, net);
		put(PLACES, places);
	}
}
