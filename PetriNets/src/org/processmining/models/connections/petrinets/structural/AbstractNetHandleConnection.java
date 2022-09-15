package org.processmining.models.connections.petrinets.structural;

import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.analysis.AbstractNodePairSet;

public abstract class AbstractNetHandleConnection extends AbstractConnection {

	public final static String NET = "Net";
	public final static String HANDLES = "Handles";

	public AbstractNetHandleConnection(PetrinetGraph net, AbstractNodePairSet<?, ?> handles) {
		super("Connection to handles of " + net.getLabel());
		put(NET, net);
		put(HANDLES, handles);
	}

}
