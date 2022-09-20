/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.behavapp;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParamProvider;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;

/**
 * @author aadrians Oct 24, 2011
 * 
 */
public class BehavAppParamProvider implements IPNReplayParamProvider {

	// precalculated initial and final markings
	private Marking initMarking;
	private Marking[] finalMarkings;
	private Collection<XEventClass> evClassCol;

	@SuppressWarnings("unused")
	private BehavAppParamProvider() {
	}

	public BehavAppParamProvider(PluginContext context, PetrinetGraph net, XLog log, TransEvClassMapping mapping) {
		// get initial marking
		initMarking = getInitialMarking(context, net);

		// get final markings
		finalMarkings = getFinalMarkings(context, net, initMarking);

		// populate event classes
		XEventClassifier classifier = mapping.getEventClassifier();
		XLogInfo summary = XLogInfoFactory.createLogInfo(log, classifier);
		XEventClasses eventClassesName = summary.getEventClasses();
		evClassCol = new HashSet<XEventClass>(eventClassesName.getClasses());
		evClassCol.add(mapping.getDummyEventClass());
	}

	public IPNReplayParameter constructReplayParameter(JComponent ui) {
		if (ui instanceof BehavAppUI) {
			BehavAppUI bui = (BehavAppUI) ui;

			// create result object and set the values
			BehavAppParam paramObj = new BehavAppParam();

			paramObj.setMaxNumStates(bui.getMaxNumStates());
			paramObj.setUseLogWeight(bui.isUseLogWeight());
			if (bui.isUseModelWeight()) {
				paramObj.setxEventClassWeightMap(bui.getModelWeight());
			} else {
				paramObj.setxEventClassWeightMap(new HashMap<XEventClass, Integer>(1));
			}
			paramObj.setInitialMarking(initMarking);
			paramObj.setFinalMarkings(finalMarkings);
			return paramObj;
		} else {
			return null;
		}
	}

	public JComponent constructUI() {
		return new BehavAppUI(evClassCol);
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

	/**
	 * Derive final markings from accepting states
	 * 
	 * @param context
	 * @param net
	 * @param initMarking
	 * @return
	 */
	private Marking[] getFinalMarkings(PluginContext context, PetrinetGraph net, Marking initMarking) {
		// check if final marking exists
		Marking[] finalMarkings = null;
		try {
			Collection<FinalMarkingConnection> finalMarkingConnections = context.getConnectionManager().getConnections(
					FinalMarkingConnection.class, context, net);

			Set<Marking> setFinalMarkings = new HashSet<Marking>();
			for (FinalMarkingConnection conn : finalMarkingConnections) {
				setFinalMarkings.add((Marking) conn.getObjectWithRole(FinalMarkingConnection.MARKING));
			}
			finalMarkings = setFinalMarkings.toArray(new Marking[setFinalMarkings.size()]);
		} catch (ConnectionCannotBeObtained exc) {
			// no final marking provided, return empty marking
			finalMarkings = new Marking[0];
		}
		return finalMarkings;
	}

}
