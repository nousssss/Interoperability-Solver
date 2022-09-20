/**
 * 
 */
package org.processmining.models.connections.petrinets;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;

/**
 * @author aadrians
 *
 */
public class PNMatchInstancesRepResultConnection extends AbstractConnection {
	public final static String PN = "Petrinet";
	public final static String MARKING = "InitialMarking";
	public final static String LOG = "Log";
	public final static String PNREPRESULT = "AllPNReplayResult";
	
	public PNMatchInstancesRepResultConnection(String label, PetrinetGraph net, Marking initMarking, XLog log, PNMatchInstancesRepResult allRepResult) {
		super(label);
		put(PN, net);
		put(MARKING, initMarking);
		put(LOG, log);
		put(PNREPRESULT, allRepResult);
	}

}
