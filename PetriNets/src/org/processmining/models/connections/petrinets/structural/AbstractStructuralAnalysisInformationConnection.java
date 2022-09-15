/**
 * 
 */
package org.processmining.models.connections.petrinets.structural;

import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.analysis.NetAnalysisInformation;

/**
 * @author s072211
 * @email arya.adriansyah@gmail.com
 * @version Oct 6, 2008
 */
public abstract class AbstractStructuralAnalysisInformationConnection extends AbstractConnection {

	public final static String NET = "Net";
	public final static String NETANALYSISINFORMATION = "NetAnalysisInformation";

	/**
	 * Connection with marking
	 * 
	 * @param net
	 * @param marking
	 * @param netAnalysisInformation
	 */
	public AbstractStructuralAnalysisInformationConnection(PetrinetGraph net,
			NetAnalysisInformation<?> netAnalysisInformation) {
		super("Connection between " + net.getLabel() + " and net analysis " + netAnalysisInformation.getLabel());
		put(NET, net);
		put(NETANALYSISINFORMATION, netAnalysisInformation);
	}
}
