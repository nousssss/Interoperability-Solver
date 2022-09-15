/**
 * 
 */
package org.processmining.models.connections.petrinets.structural;

import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.analysis.AbstractComponentSet;

/**
 * @author s072211
 * @email arya.adriansyah@gmail.com
 * @version Oct 6, 2008
 */
public abstract class AbstractComponentSetConnection extends AbstractConnection {
	public final static String NET = "Net";
	public final static String COMPONENTSET = "ComponentSet";

	public AbstractComponentSetConnection(PetrinetGraph net, AbstractComponentSet<?> nodeMarking) {
		super("Connection to component set of " + net.getLabel() + " (represents " + nodeMarking.getLabel() + ")");
		put(NET, net);
		put(COMPONENTSET, nodeMarking);
	}

}
