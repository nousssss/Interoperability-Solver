package org.processmining.models.connections.opennet;

import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.graphbased.directed.opennet.OpenNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

public class OpenNetConnection extends AbstractConnection {

	public final static String NET = "net";
	public final static String OPENNET = "opennet";

	public OpenNetConnection(Petrinet net, OpenNet openNet) {
		super("Open net for " + net.getLabel());
		// TODO Auto-generated constructor stub
		put(NET, net);
		put(OPENNET, openNet);
	}

}
