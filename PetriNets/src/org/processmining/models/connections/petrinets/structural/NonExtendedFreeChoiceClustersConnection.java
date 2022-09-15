package org.processmining.models.connections.petrinets.structural;

import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.analysis.NonExtendedFreeChoiceClustersSet;

public class NonExtendedFreeChoiceClustersConnection extends AbstractConnection {

	public final static String NET = "Net";
	public final static String CLUSTERS = "Non-extended-free-choice clusters";

	public NonExtendedFreeChoiceClustersConnection(PetrinetGraph net, NonExtendedFreeChoiceClustersSet clusters) {
		super("Connection to non-extended-free-choice clusters of " + net.getLabel());
		put(NET, net);
		put(CLUSTERS, clusters);
	}
}
