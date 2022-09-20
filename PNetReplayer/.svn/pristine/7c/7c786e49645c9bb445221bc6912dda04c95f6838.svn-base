/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.swapping;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;

/**
 * @author aadrians Oct 9, 2012
 * 
 */
public class CostBasedSwapParamConnection extends AbstractConnection {
	public final static String NET = "Net";
	public final static String LOG = "Log";
	public final static String PARAM = "Parameter";

	public CostBasedSwapParamConnection(PetrinetGraph net, XLog log, CostBasedSwapParam param) {
		super("Connection " + net.getLabel() + "," + XConceptExtension.instance().extractName(log)
				+ ", and default param for cost based swap replay");
		put(NET, net);
		put(LOG, log);
		put(PARAM, param);
	}

	public void setParam(CostBasedSwapParam param) {
		put(PARAM, param);
	}

}
