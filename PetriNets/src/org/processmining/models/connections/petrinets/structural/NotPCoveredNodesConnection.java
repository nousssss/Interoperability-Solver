package org.processmining.models.connections.petrinets.structural;

import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.analysis.NotPCoveredNodesSet;

public class NotPCoveredNodesConnection extends AbstractConnection {

	public final static String NET = "Net";
	public final static String NODES = "Nodes";

	public NotPCoveredNodesConnection(PetrinetGraph net, NotPCoveredNodesSet nodes) {
		super("Connection to not P-covered nodes of " + net.getLabel());
		put(NET, net);
		put(NODES, nodes);
	}
}
