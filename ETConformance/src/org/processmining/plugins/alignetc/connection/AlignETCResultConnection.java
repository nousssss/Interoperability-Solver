package org.processmining.plugins.alignetc.connection;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.alignetc.result.AlignETCResult;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;

/**
 * Connection between the Align ETConformance Results and all the elements
 * used to computed it.
 * 
 * @author Jorge Munoz-Gama (jmunoz)
 */
public class AlignETCResultConnection extends AbstractConnection {
	public final static String PN = "Petrinet";
	public final static String MARKING = "InitialMarking";
	public final static String LOG = "Log";
	public final static String ALIGNMENTS = "Alignments";
	public final static String ALIGNETCRESULT = "AlignETCResult";
	
	public AlignETCResultConnection(String label, PetrinetGraph net, Marking initMarking, XLog log, PNMatchInstancesRepResult alignments, AlignETCResult result) {
		super(label);
		put(PN, net);
		put(MARKING, initMarking);
		put(LOG, log);
		put(ALIGNMENTS, alignments);
		put(ALIGNETCRESULT, result);
	}

}
