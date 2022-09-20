/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete;

import java.util.Collection;
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
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParamProvider;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;

/**
 * @author aadrians Oct 22, 2011
 * 
 */
public class CostBasedCompleteParamProvider implements IPNReplayParamProvider {
	// reference to objects
	protected Collection<Transition> transCol;
	protected Collection<XEventClass> evClassCol;

	// precalculated initial and final markings
	protected Marking initMarking;
	protected Marking[] finalMarkings;

	@SuppressWarnings("unused")
	private CostBasedCompleteParamProvider() {
	}

	public CostBasedCompleteParamProvider(PluginContext context, PetrinetGraph net, XLog log,
			TransEvClassMapping mapping) {
		// get initial marking
		initMarking = getInitialMarking(context, net);

		// get final markings
		finalMarkings = getFinalMarkings(context, net, initMarking);

		// populate transitions 
		transCol = net.getTransitions();

		// populate event classes
		XEventClassifier classifier = mapping.getEventClassifier();
		XLogInfo summary = XLogInfoFactory.createLogInfo(log, classifier);
		XEventClasses eventClassesName = summary.getEventClasses();
		evClassCol = new HashSet<XEventClass>(eventClassesName.getClasses());
		evClassCol.add(mapping.getDummyEventClass());
	}

	public IPNReplayParameter constructReplayParameter(JComponent ui) {
		if (ui instanceof CostBasedCompleteUI) {
			CostBasedCompleteUI cbui = (CostBasedCompleteUI) ui;
			
			CostBasedCompleteParam paramObj = new CostBasedCompleteParam(cbui.getMapEvClassToCost(), cbui.getTransitionWeight());
			paramObj.setMapSync2Cost(cbui.getSyncCost());
			paramObj.setMaxNumOfStates(cbui.getMaxNumOfStates());
			paramObj.setInitialMarking(initMarking);
			paramObj.setFinalMarkings(finalMarkings);
			paramObj.setUsePartialOrderedEvents(cbui.isUsePartialOrderedEvents());
			
			return paramObj;
		} else {
			return null;
		}
	}

	public JComponent constructUI() {
		return new CostBasedCompleteUI(transCol, evClassCol);
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
			Collection<FinalMarkingConnection> finalMarkingConnections = context.getConnectionManager()
					.getConnections(FinalMarkingConnection.class, context, net);
			if (finalMarkingConnections.size() != 0) {
				Set<Marking> setFinalMarkings = new HashSet<Marking>();
				for (FinalMarkingConnection conn : finalMarkingConnections) {
					setFinalMarkings.add((Marking) conn.getObjectWithRole(FinalMarkingConnection.MARKING));
				}
				finalMarkings = setFinalMarkings.toArray(new Marking[setFinalMarkings.size()]);
			} else {
				finalMarkings = new Marking[0];
			}
		} catch (ConnectionCannotBeObtained exc) {
			// no final marking provided, give an empty marking
			finalMarkings = new Marking[0];
		}
		return finalMarkings;
	}
}
