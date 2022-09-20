/**
 * 
 */
package org.processmining.models.connections.petrinets;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.impl.AbstractStrongReferencingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;

/**
 * @author aadrians
 *
 */
public class EvClassLogPetrinetConnection extends AbstractStrongReferencingConnection {
	public final static String PETRINETGRAPH = "PetrinetGraph";
	public final static String LOG = "Log";
	public final static String EVENTCLASSIFIER = "EventClassifier";
	public final static String TRANS2EVCLASSMAPPING = "Trans2EvClassMapping";
	
	public EvClassLogPetrinetConnection(String label, PetrinetGraph net, XLog log, XEventClassifier eventClassifier, TransEvClassMapping transEvClassMapping) {
		super(label);
		put(PETRINETGRAPH, net);
		put(LOG, log);
		putStrong(EVENTCLASSIFIER, eventClassifier);
		putStrong(TRANS2EVCLASSMAPPING, transEvClassMapping);
	}
}