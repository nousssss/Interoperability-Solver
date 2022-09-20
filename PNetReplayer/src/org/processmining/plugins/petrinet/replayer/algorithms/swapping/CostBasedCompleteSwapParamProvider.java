/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.swapping;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParamProvider;

/**
 * @author aadrians Sep 13, 2012
 * 
 */
public class CostBasedCompleteSwapParamProvider extends CostBasedCompleteParamProvider {
	
	protected XEventClass dummyEvClass;
	protected CostBasedSwapParam defaultParam = null;
	
	public CostBasedCompleteSwapParamProvider(PluginContext context, PetrinetGraph net, XLog log,
			TransEvClassMapping mapping) {
		super(context, net, log, mapping);
		this.dummyEvClass = mapping.getDummyEventClass();
		
		// obtain default param
		try {
			CostBasedSwapParamConnection conn = context.getConnectionManager().getFirstConnection(CostBasedSwapParamConnection.class, context, net, log);
			defaultParam = conn.getObjectWithRole(CostBasedSwapParamConnection.PARAM);
		} catch (ConnectionCannotBeObtained e) {
			// connection cannot be obtained
			System.out.println("no connection can be obtained");
		}
	}

	@Override
	public JComponent constructUI() {
		List<XEventClass> evClassColWODummy = new ArrayList<XEventClass>(evClassCol);
		evClassColWODummy.remove(dummyEvClass);
		return new CostBasedCompleteSwapUI(transCol, evClassCol, evClassColWODummy, defaultParam);
	}

	@Override
	public IPNReplayParameter constructReplayParameter(JComponent ui) {
		if (ui instanceof CostBasedCompleteSwapUI) {
			CostBasedCompleteSwapUI cbui = (CostBasedCompleteSwapUI) ui;

			CostBasedSwapParam paramObj = new CostBasedSwapParam(cbui.getMapEvClassToCost(), cbui.getMapTransToCost(), cbui.getMapSyncToCost(),
					initMarking, finalMarkings, cbui.getMaxNumOfStates(), cbui.getSwapCost(), cbui.getReplacementCost());

			return paramObj;
		} else {
			return null;
		}
	}
}
