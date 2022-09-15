package org.processmining.models.connections.petrinets.structural;

import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.analysis.UnconnectedNodesSet;

public class UnconnectedNodesConnection extends AbstractConnection {

	public final static String NET = "Net";
	public final static String NODES = "Unconnected nodes";

	public UnconnectedNodesConnection(PetrinetGraph net, UnconnectedNodesSet nodes) {
		super("Connection to unconnected nodes of " + net.getLabel());
		put(NET, net);
		put(NODES, nodes);
	}

}
