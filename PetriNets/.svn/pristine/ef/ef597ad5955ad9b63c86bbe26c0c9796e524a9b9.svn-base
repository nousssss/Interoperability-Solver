/**
 * 
 */
package org.processmining.models.connections.petrinets.structural;

import org.processmining.framework.connections.annotations.ConnectionDoesntExistMessage;
import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.analysis.AbstractInvariantSet;

/**
 * @author s072211
 * @email arya.adriansyah@gmail.com
 * @version Oct 3, 2008
 */
@ConnectionDoesntExistMessage(message = "The given invariant is not an invariant of the given net.")
public abstract class AbstractInvariantMarkingConnection extends AbstractConnection {
	public final static String NET = "Net";
	public final static String INVARIANTMARKING = "InvariantMarking";

	public AbstractInvariantMarkingConnection(PetrinetGraph net, AbstractInvariantSet<?> invariantMarking) {
		super("Connection to invariant of " + net.getLabel());
		put(NET, net);
		put(INVARIANTMARKING, invariantMarking);
	}
}
