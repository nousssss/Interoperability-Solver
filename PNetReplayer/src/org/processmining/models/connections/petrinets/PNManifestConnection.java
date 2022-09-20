/**
 * 
 */
package org.processmining.models.connections.petrinets;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.impl.AbstractStrongReferencingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.plugins.petrinet.manifestreplayer.PNManifestReplayerParameter;
import org.processmining.plugins.petrinet.manifestreplayer.algorithms.IPNManifestReplayAlgorithm;
import org.processmining.plugins.petrinet.manifestreplayresult.Manifest;

/**
 * @author aadrians May 15, 2013
 * 
 */
public class PNManifestConnection extends AbstractStrongReferencingConnection {
	public final static String PN = "Petrinet";
	public final static String LOG = "Log";
	public final static String REPLAYALGORITHM = "ReplayAlgorithm";
	public final static String REPLAYPARAMETERS = "ReplayParameters";
	public final static String MANIFEST = "Manifest";

	//net, log, alg, parameter, manifest)

	public PNManifestConnection(String label, PetrinetGraph net, XLog log, IPNManifestReplayAlgorithm selectedAlg,
			PNManifestReplayerParameter parameters, Manifest manifest) {
		super(label);
		putStrong(PN, net);
		putStrong(LOG, log);
		putStrong(REPLAYALGORITHM, selectedAlg);
		putStrong(REPLAYPARAMETERS, parameters);
		putStrong(MANIFEST, manifest);
	}
}
