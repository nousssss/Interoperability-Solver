/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.syncproduct;

import javax.swing.JComponent;

import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParamProvider;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;

/**
 * @author aadrians
 * Oct 22, 2011
 *
 */
public class SyncProductParamProvider implements IPNReplayParamProvider {

	// precalculated initial marking
	private Marking m;
	
	@SuppressWarnings("unused")
	private SyncProductParamProvider(){};
	
	public SyncProductParamProvider(PluginContext context, Petrinet net){
		// precalculate initial marking
		this.m = getInitialMarking(context, net);
	}
	
	public IPNReplayParameter constructReplayParameter(JComponent ui) {
		if (ui instanceof SyncProductUI){
			SyncProductUI spui = (SyncProductUI) ui;
			
			// create result object and set the values
			SyncProductParam res = new SyncProductParam();

			res.setMoveOnLogOnly(spui.getCostMoveOnLogOnly());
			res.setMoveOnModelOnlyInvi(spui.getCostMoveOnModelOnlyInvi());
			res.setMoveOnModelOnlyReal(spui.getCostMoveOnLogModelOnlyReal());
			res.setMoveSynchronizedViolating(spui.getCostMoveSyncViolating());
			res.setMoveSynchronizedViolatingPartially(spui.isSyncViolatingPartiallyAllowed());
			res.setInitialMarking(m);
			
			return res;
		} else {
			return null;
		}
	}

	public JComponent constructUI() {
		return new SyncProductUI();
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
