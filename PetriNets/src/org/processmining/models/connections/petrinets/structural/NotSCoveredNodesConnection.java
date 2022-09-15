package org.processmining.models.connections.petrinets.structural;

import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.analysis.NotSCoveredNodesSet;

public class NotSCoveredNodesConnection extends AbstractConnection {

	public final static String NET = "Net";
	public final static String NODES = "Non-S-covered nodes";

	public NotSCoveredNodesConnection(PetrinetGraph net, NotSCoveredNodesSet nodes) {
		super("Connection to not S-covered nodes of " + net.getLabel());
		put(NET, net);
		put(NODES, nodes);
	}
}
