package org.processmining.plugins.interoperability;

import org.deckfour.xes.model.XLog;
import org.processmining.connections.interoperability.AbstractSolvedConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.interoperability.LabelledPetrinet;

/**
 * Connects an event log, a petrinet and an annotated repaired model.
 * 
 * @author nousssss
 *
 */
public class InteroperabilitySolverConnection extends AbstractSolvedConnection {

	/**
	 * Creates the connection between the log, petrinet, and annotated repaired model.
	 * @param log: The given event log.
	 * @param net: The given petrinet.
	 * @param labelled: The given annotated repaired model.
	 */
	public InteroperabilitySolverConnection(XLog log, Petrinet net, LabelledPetrinet labelled) {
		super(log, net, labelled);
	}

}
