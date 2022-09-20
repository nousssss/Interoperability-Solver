/**
 * 
 */
package org.processmining.plugins.modelrepair;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;

/**
 * @author aadrians Oct 22, 2011
 * 
 */
public class CostBasedCompleteParamProvider_nonUI extends org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParamProvider {
	
	// default value 
	private static final int DEFCOSTMOVEONLOG = 5;
	private static final int DEFCOSTMOVEONMODEL = 2;
	private static final int MAXLIMMAXNUMINSTANCES = 10001;
	private static final int DEFLIMMAXNUMINSTANCES = 2000;
	
	// reference to objects
	private Collection<Transition> transCol;
	private Collection<XEventClass> evClassCol;

	// precalculated initial and final markings
	private Marking initMarking;
	private Marking[] finalMarkings;
	
	private PluginContext context;

	public CostBasedCompleteParamProvider_nonUI(PluginContext context, PetrinetGraph net, XLog log, TransEvClassMapping mapping)
	{
		super(context, net, log, mapping);
		
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
	
	/**
	 * Get map from event class to cost of move on log
	 * 
	 * @return
	 */
	public Map<XEventClass, Integer> getDefaultEventCost() {
		Map<XEventClass, Integer> mapEvClass2Cost = new HashMap<XEventClass, Integer>();
		for (XEventClass evClass : evClassCol) {
			mapEvClass2Cost.put(evClass, DEFCOSTMOVEONLOG);
		}
		return mapEvClass2Cost;
	}
	
	/**
	 * get penalty when move on model is performed
	 * 
	 * @return
	 */
	public Map<Transition, Integer> getDefaultTransitionCost() {
		Map<Transition, Integer> costs = new HashMap<Transition, Integer>();
		for (Transition trans : transCol) {
			costs.put(trans, DEFCOSTMOVEONMODEL);
		}
		return costs;
	}

	public IPNReplayParameter constructReplayParameter(Map<XEventClass, Integer> mapEvClass2Cost, Map<Transition, Integer> mapTrans2Cost, int maxNumOfStates) {
		CostBasedCompleteParam paramObj = new CostBasedCompleteParam(mapEvClass2Cost, mapTrans2Cost);
		paramObj.setMaxNumOfStates(maxNumOfStates);
		paramObj.setInitialMarking(initMarking);
		paramObj.setFinalMarkings(finalMarkings);
		return paramObj;
	}
	
	public int getDefaultNumOfStates() {
		return DEFLIMMAXNUMINSTANCES;
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

	public IPNReplayParameter constructReplayParameter(JComponent ui) {
		if (ui == null || !(context instanceof UIPluginContext))
			return constructReplayParameter(getDefaultEventCost(), getDefaultTransitionCost(), getDefaultNumOfStates());
		else
			return super.constructReplayParameter(ui);
	}

	public JComponent constructUI() {
		if (context instanceof UIPluginContext) return super.constructUI();
		return null;
	}
}
