/**
 * 
 */
package org.processmining.modelrepair.plugins.align;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.modelrepair.plugins.uma.UmaPromUtil;
import org.processmining.models.connections.petrinets.EvClassLogPetrinetConnection;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithoutILP;
import org.processmining.plugins.connectionfactories.logpetrinet.EvClassLogPetrinetConnectionFactoryUI;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayer.ui.PNReplayerUI;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

import nl.tue.astar.AStarException;

/**
 * @author aadrians, dfahland
 * 
 */
public class PNLogReplayer extends org.processmining.plugins.petrinet.replayer.PNLogReplayer {
	
	private IPNReplayAlgorithm	 usedAlgorithm;
	private IPNReplayParameter   usedAlgParameters;
	private TransEvClassMapping  usedMapping;
	

	public PNRepResult replayLog(final UIPluginContext context, PetrinetGraph net, XLog log)
			throws ConnectionCannotBeObtained {
		PNReplayerUI pnReplayerUI = new PNReplayerUI();
		Object[] resultConfiguration = pnReplayerUI.getConfiguration(context, net, log);
		if (resultConfiguration == null) {
			context.getFutureResult(0).cancel(true);
			return null;
		}

		// if all parameters are set, replay log
		if (resultConfiguration[PNReplayerUI.MAPPING] != null) {
			context.log("replay is performed. All parameters are set.");

			// This connection MUST exists, as it is constructed by the configuration if necessary
			context.getConnectionManager().getFirstConnection(EvClassLogPetrinetConnection.class, context, net, log);

			// get all parameters
			IPNReplayAlgorithm selectedAlg = (IPNReplayAlgorithm) resultConfiguration[PNReplayerUI.ALGORITHM];
			IPNReplayParameter algParameters = (IPNReplayParameter) resultConfiguration[PNReplayerUI.PARAMETERS];
			
			usedAlgorithm = selectedAlg;
			usedAlgParameters = algParameters;
			usedMapping = (TransEvClassMapping) resultConfiguration[PNReplayerUI.MAPPING];
			
			// since based on GUI, create connection
			algParameters.setCreateConn(true);
			algParameters.setGUIMode(true);

			try {
				PNRepResult res = replayLog(context, net, log,
						(TransEvClassMapping) resultConfiguration[PNReplayerUI.MAPPING], selectedAlg, algParameters);
				
				context.getFutureResult(0).setLabel(
						"Replay result - log " + XConceptExtension.instance().extractName(log) + " on "
								+ net.getLabel() + " using " + selectedAlg.toString());
				return res;
				
			} catch (AStarException e) {
				return cancel(context, "Could not replay the log on the model: "+e.toString());
			}
		} else {
			context.log("replay is not performed because not enough parameter is submitted");
			context.getFutureResult(0).cancel(true);
			return null;
		}
	}

	public IPNReplayAlgorithm getUsedAlgorithm() {
		return usedAlgorithm;
	}

	public IPNReplayParameter getUsedAlgParameters() {
		return usedAlgParameters;
	}
	
	public TransEvClassMapping getUsedMapping() {
		return usedMapping;
	}

	// dummy event class (for unmapped transitions)
	public final static XEventClass DUMMY = EvClassLogPetrinetConnectionFactoryUI.DUMMY;
	
	public static TransEvClassMapping getEventClassMapping(PluginContext context, PetrinetGraph net, XLog log, XEventClassifier classifier) {
		
		TransEvClassMapping map = new TransEvClassMapping(classifier, DUMMY);
		
		List<XEventClass> eventClasses = getEventClasses(log, classifier);
		for (Transition t : net.getTransitions()) {
			if (t.isInvisible()) {
				map.put(t, DUMMY);
			} else {
				// try to match transition to available event classes
				XEventClass match = getEventClassMapping_preSelectOption(t.getLabel(), eventClasses);
				// failed, try alternative match if life-cycle information is not used for matching 
				if (match == null) match = getEventClassMapping_preSelectOption_ignoreLifeCycle(t.getLabel(), eventClasses);
				if (match != null) {
					map.put(t, match);
				} else {
					map.put(t, DUMMY);
				}
			}
		}
		
		// mapping is finished, create connection
		EvClassLogPetrinetConnection con = new EvClassLogPetrinetConnection("Connection between " + net.getLabel() + " and "
				+ XConceptExtension.instance().extractName(log), net, log, classifier, map);
		context.addConnection(con);
		
		return map;
	}
	
	/**
	 * Adopt an existing transition to event class mapping between an old net
	 * and the given log, to a new mapping between the given net and log. Reuses
	 * the original event classifier and the original dummy event class.
	 * 
	 * @param context
	 * @param net
	 * @param log
	 * @param old_map
	 * @param createConnection
	 * @return mapping between net and log
	 */
	public static TransEvClassMapping adoptEventClassMapping(PluginContext context, PetrinetGraph net, XLog log, TransEvClassMapping old_map, boolean createConnection) {
		// retrieve event classifier and dummy event from existing mapping
		XEventClassifier classifier = old_map.getEventClassifier();
		XEventClass dummy = old_map.getDummyEventClass();
		
		// retrieve all event classes from the log info, in case new transitions were added, they
		// can be mapped to the event classes
		XEventClasses _eventClasses = log.getInfo(classifier).getEventClasses(classifier);
		Collection<XEventClass> eventClasses = _eventClasses.getClasses();
		
		TransEvClassMapping map = new TransEvClassMapping(classifier, dummy);
		// for each transition
		for (Transition t : net.getTransitions()) {
			if (t.isInvisible()) {
				map.put(t, dummy);
			} else {
				// check whether it already is mapped to an event in the old mapping 
				String name = t.getLabel();
				XEventClass e_old = null;
				// find any transition from the old mapping (old net) carrying the same label
				// and use its event as the mapped event for the current transition
				for (Transition t_old : old_map.keySet()) {
					if (t_old.getLabel().equals(name)) {
						if (e_old != null && e_old != old_map.get(t_old)) {
							System.out.println("Warning: "+name+" can be mapped to different event classes "+e_old+" and "+old_map.get(t_old));
						}
						e_old = old_map.get(t_old);
					}
				}
				if (e_old != null) {
					// found a matching event class for t in the old mapping
					map.put(t, e_old);
				} else {
					// did not find a matching event class: guess a new event class
					// as transitions were added due to log events and names are generated based on
					// existing events, there should be a matching event
					XEventClass preSelect =  getEventClassMapping_preSelectOption(name, eventClasses);
					if (preSelect != null) {
						map.put(t, preSelect);
					} else {
						// if not, match to none
						map.put(t, dummy);
					}
				}
			}
		}
		
		// mapping is finished, create connection
		EvClassLogPetrinetConnection con = new EvClassLogPetrinetConnection("Connection between [" + net.getLabel() + "] and "
				+ XConceptExtension.instance().extractName(log), net, log, classifier, map);
		context.addConnection(con);
		
		return map;
	}
	
	/**
	 * Returns the Event Option Box index of the most similar event for the
	 * transition.
	 * 
	 * @param transition
	 *            Name of the transitions
	 * @param events
	 *            Array with the options for this transition
	 * @return Index of option more similar to the transition
	 */
	public static XEventClass getEventClassMapping_preSelectOption(String transition, Collection<XEventClass> eventClasses) {

		for (XEventClass cl : eventClasses) {
			if (cl == DUMMY) continue;
			if (transition.startsWith(cl.toString())) return cl;
		}
		return null;
	}
	
	/**
	 * Returns the Event Option Box index of the most similar event for the
	 * transition, ignores any lifecycle information (after a "+") in the name
	 * for the matching. Do not apply if the log uses different life-cycle transitions.
	 * 
	 * @param transition
	 *            Name of the transitions
	 * @param events
	 *            Array with the options for this transition
	 * @return Index of option more similar to the transition
	 */
	public static XEventClass getEventClassMapping_preSelectOption_ignoreLifeCycle(String transition, Collection<XEventClass> eventClasses) {

		String transition_noLC = (transition.indexOf("+") > 0) ? transition.substring(0,transition.indexOf("+")) : transition;
		
		for (XEventClass cl : eventClasses) {
			if (cl == DUMMY) continue;
			
			String evClassName = cl.toString();
			evClassName = (evClassName.indexOf("+") > 0) ? evClassName.substring(0,evClassName.indexOf("+")) : evClassName;

			if (transition_noLC.equals(evClassName)) return cl;
		}
		return null;
	}
	
	public static List<XEventClass> getEventClasses(XLog log, XEventClassifier classifier) {
		
		// default classifier = XLogInfoImpl.NAME_CLASSIFIER
		XLogInfo summary = XLogInfoFactory.createLogInfo(log, classifier);
		XEventClasses eventClasses = summary.getEventClasses();
		
		List<XEventClass> classes = new ArrayList<XEventClass>(eventClasses.getClasses());
		classes.add(0, DUMMY);
		
		return classes;
	}
	
	/**
	 * @param log
	 * @return default event classifier of the log
	 */
	public static XEventClassifier getDefaultClassifier(XLog log) {
		XLogInfo summary = XLogInfoFactory.createLogInfo(log);
		return summary.getEventClasses().getClassifier();
	}
	
	public static class ReplayParams {
		public IPNReplayAlgorithm selectedAlg;
		public Map<XEventClass, Integer> mapEvClass2Cost;
		public Map<Transition, Integer> mapTrans2Cost;
		public Map<Transition, Integer> mapSyncCost;
		public CostBasedCompleteParam parameters;
	}
	
	public static ReplayParams getReplayerParameters(PluginContext context, PetrinetGraph net, Marking m_initial, Marking m_final, XLog log, XEventClassifier classifier) {
		
		ReplayParams rParams = new ReplayParams();
		
		rParams.selectedAlg = new PetrinetReplayerWithoutILP();
		List<XEventClass> eventClasses = getEventClasses(log, classifier);
		rParams.mapEvClass2Cost = new HashMap<XEventClass, Integer>();
		for (XEventClass cl : eventClasses) {
			rParams.mapEvClass2Cost.put(cl, 1000);
		}

		rParams.mapTrans2Cost = new HashMap<Transition, Integer>();
		for (Transition t : net.getTransitions()) {
			if (t.isInvisible()) rParams.mapTrans2Cost.put(t, 0);
			else rParams.mapTrans2Cost.put(t, 1);
		}
		
		rParams.parameters = new CostBasedCompleteParam(rParams.mapEvClass2Cost, rParams.mapTrans2Cost);
		rParams.parameters.setMaxNumOfStates(200000);
		rParams.parameters.setInitialMarking(m_initial);
		rParams.parameters.setFinalMarkings(m_final);
		
		return rParams;
	}
	
	public static PNRepResult callReplayer(PluginContext context, PetrinetGraph net, XLog log, TransEvClassMapping mapping, ReplayParams par) {
		PNLogReplayer replayer = new PNLogReplayer();
		System.out.println("replaying");
		try {
			return replayer.replayLog(context, net, log, mapping, par.selectedAlg, par.parameters);
		} catch (AStarException e) {
			UmaPromUtil.printMessage(context, "Uma/call replayer", "could not replay log on model "+e);
			return null;
		}
	}
	
	public static Object[] constructFinalMarking(PluginContext context, PetrinetGraph net) {
		
		Marking finalMarking = new Marking();
		for (Place p : net.getPlaces()) {
			if (net.getOutEdges(p).size() == 0) finalMarking.add(p);
		}
		
		Collection<Place> colPlaces = net.getPlaces();
		for (Place p : finalMarking) {
			if (!colPlaces.contains(p)) {
				throw new IllegalArgumentException("Final marking contains places outside of the net");
			}
		}
		FinalMarkingConnection conn = new FinalMarkingConnection(net, finalMarking);
		context.addConnection(conn);
		return new Object[] { conn , finalMarking };
	}

	/**
	 * Create a final marking for the given net based on a final marking of another net.
	 * @param context
	 * @param net
	 * @param old_m_final
	 * @param createConnection	whether to create a {@link FinalMarkingConnection} between {@code net} and new final marking
	 * @return new final making 
	 */
	public static Marking adoptFinalMarking(PluginContext context, PetrinetGraph net, Marking old_m_final, boolean createConnection) {
		
		Marking finalMarking = new Marking();
		for (Place p : net.getPlaces()) {
			// a place of the given net gets marked if there is a place with the same name in the old marking
			for (Place p_old : old_m_final.baseSet()) {
				if (p_old.getLabel().equals(p.getLabel())) finalMarking.add(p);
			}
		}
		
		// sanity check for correctness of final marking
		Collection<Place> colPlaces = net.getPlaces();
		for (Place p : finalMarking) {
			if (!colPlaces.contains(p)) {
				throw new IllegalArgumentException("Final marking contains places outside of the net");
			}
		}
		
		// create connection if desired
		if (createConnection) {
			FinalMarkingConnection conn = new FinalMarkingConnection(net, finalMarking);
			context.addConnection(conn);
		}
		return finalMarking;
	}
	
	protected static PNRepResult cancel(PluginContext context, String message) {
		System.out.println("[ModelRepair/PNLogReplayer]: "+message);
		context.log(message);
		context.getFutureResult(0).cancel(true);
		return null;
	}
	
}
