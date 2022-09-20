/**
 * 
 */
package org.processmining.models.connections.petrinets;

import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.plugins.petrinet.replayer.util.codec.PNCodec;

/**
 * @author Arya Adriansyah
 * @email a.adriansyah@tue.nl
 * @version Mar 4, 2011
 */
public class PNCodecConnection extends AbstractConnection {
	public final static String PETRINETGRAPH = "PetrinetGraph";
	public final static String PNCODEC = "PNCodec";

	public PNCodecConnection(String label, PetrinetGraph net, PNCodec codec) {
		super(label);
		put(PETRINETGRAPH, net);
		put(PNCODEC, codec);
	}
}