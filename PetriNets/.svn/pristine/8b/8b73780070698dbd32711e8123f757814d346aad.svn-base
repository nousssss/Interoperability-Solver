package org.processmining.models.connections.petrinets.structural;

import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.analysis.NonFreeChoiceClustersSet;

public class NonFreeChoiceClustersConnection extends AbstractConnection {

	public final static String NET = "Net";
	public final static String CLUSTERS = "Non-free-choice clusters";

	public NonFreeChoiceClustersConnection(PetrinetGraph net, NonFreeChoiceClustersSet clusters) {
		super("Connection to non-free-choice clusters of " + net.getLabel());
		put(NET, net);
		put(CLUSTERS, clusters);
	}
}
