/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.costbasedprefix;

import javax.swing.JComponent;

import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParamProvider;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;

/**
 * @author aadrians
 * Oct 22, 2011
 *
 */
public class CostBasedPrefixParamProvider implements IPNReplayParamProvider {

	// precalculated initial marking
	private Marking m;
	
	@SuppressWarnings("unused")
	private CostBasedPrefixParamProvider(){};
	
	public CostBasedPrefixParamProvider(PluginContext context, PetrinetGraph net){
		// precalculate initial marking
		this.m = getInitialMarking(context, net);

	}
	
	public IPNReplayParameter constructReplayParameter(JComponent ui) {
		if (ui instanceof CostBasedPrefixUI){
			CostBasedPrefixUI cbui = (CostBasedPrefixUI) ui;
			
			// create result object and set the values
			CostBasedPrefixParam res = new CostBasedPrefixParam();
			res.setMaxNumOfStates(cbui.getMaxNumOfStates());
			res.setInappropriateTransFireCost(cbui.getInappropriateTransFireCost());
			res.setReplayedEventCost(cbui.getReplayedEventCost());
			res.setSkippedEventCost(cbui.getSkippedEventCost());
			res.setHeuristicDistanceCost(cbui.getHeuristicDistanceCost());
			res.setSelfExecInviTaskCost(cbui.getSelfExecInviTaskCost());
			res.setSelfExecRealTaskCost(cbui.getSelfExecRealTaskCost());
			res.setAllowInviTaskMove(cbui.isAllowInviTaskMove());
			res.setAllowRealTaskMove(cbui.isAllowRealTaskMove());
			res.setAllowEventSkip(cbui.isAllowEventSkip());
			res.setAllowExecWOTokens(cbui.isAllowExecWOTokens());
			res.setAllowExecViolating(cbui.isAllowExecViolating());
			res.setInitialMarking(m);
			return res;
		} else {
			return null;
		}
	}

	public JComponent constructUI() {
		return new CostBasedPrefixUI();
	}
	
	/**
	 * get initial marking
	 * 
	 * @param context
	 * @param net
	 * @return
	 */
	private Marking getInitialMarking(PluginContext context, PetrinetGraph net) {
		// check connection between petri net and marking
		Marking initMarking = null;
		try {
			initMarking = context.getConnectionManager()
					.getFirstConnection(InitialMarkingConnection.class, context, net)
					.getObjectWithRole(InitialMarkingConnection.MARKING);
		} catch (ConnectionCannotBeObtained exc) {
			initMarking = new Marking();
		}
		return initMarking;
	}

	
}
