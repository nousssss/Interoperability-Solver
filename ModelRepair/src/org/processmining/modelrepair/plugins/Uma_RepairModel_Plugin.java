package org.processmining.modelrepair.plugins;

import java.io.IOException;
import java.util.Collection;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.Connection;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.connections.annotations.ConnectionObjectFactory;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.PluginExecutionResult;
import org.processmining.framework.plugin.PluginParameterBinding;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.Pair;
import org.processmining.modelrepair.parameters.RepairConfiguration;
import org.processmining.modelrepair.plugins.align.PNLogReplayer;
import org.processmining.modelrepair.plugins.align.PNLogReplayer.ReplayParams;
import org.processmining.modelrepair.plugins.align.Uma_AlignForGlobalRepair_Plugin;
import org.processmining.modelrepair.plugins.align.Uma_AlignForLoopDiscovery_Plugin;
import org.processmining.models.connections.petrinets.EvClassLogPetrinetConnection;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

import hub.top.uma.InvalidModelException;

@Plugin(name = "Repair Model",
		level = PluginLevel.PeerReviewed,
		parameterLabels = { "a Log", "a Petri net", "initial marking",  "final marking", "configuration", "transition to event class mapping", "event classifier"}, //
		returnLabels = { "Repaired Petrinet", "Initial Marking", "Final Marking" },
		returnTypes = { Petrinet.class, Marking.class, Marking.class }, 
		userAccessible = true,
		help = "Repair a process model to such that the model perfectly fits the given event log; applies minimal changes such as adding loops, sub-processes, skip-transitions, and removing dead parts of the model; several repair options are available.",
		mostSignificantResult = 1)
public class Uma_RepairModel_Plugin {
	
	// take log and net as input and guess initial marking
	@UITopiaVariant(
			affiliation="TU/e",
			author="D. Fahland",
			email="d.fahland@tue.nl",
			website = "http://service-technology.org/uma",
			pack="ModelRepair")
	@PluginVariant(variantLabel = "Repair Model", requiredParameterLabels = { 0, 1 })
	public Object[] repairModel(UIPluginContext context, XLog log, PetrinetGraph net) {

		Marking initMarking;
		try {
			initMarking = context.tryToFindOrConstructFirstObject(Marking.class, InitialMarkingConnection.class, InitialMarkingConnection.MARKING, net);
		} catch (ConnectionCannotBeObtained e) {
			return cancel(context, "No initial marking found.");
		}
		
		// check existence of final marking
		try {
			context.getConnectionManager().getFirstConnection(FinalMarkingConnection.class, context, net);
		} catch (ConnectionCannotBeObtained exc) {
			if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(new JPanel(),
					"No final marking is found for this model. Do you want to create one?", "No Final Marking",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)) {
				createMarking(context, net, FinalMarkingConnection.class);
			}
			;
		} catch (Exception e) {
			e.printStackTrace();
		}
		Marking finalMarking;
		try {
			finalMarking = context.tryToFindOrConstructFirstObject(Marking.class, FinalMarkingConnection.class, FinalMarkingConnection.MARKING, net);
		} catch (ConnectionCannotBeObtained e) {
			return cancel(context, "No final marking found.");
		}
		
		RepairConfiguration config = new RepairConfiguration();		
		Uma_RepairModel_UI ui = new Uma_RepairModel_UI(config);
		if (ui.setParameters(context, config) != InteractionResult.CANCEL)
			return repairModel_getT2Econnection(context, log, net, initMarking, finalMarking, config);
		else
			return cancel(context, "Cancelled by user.");
	}
	
	/**
	 * Invoke plugin to create a marking of the given type (and add the marking
	 * with a corresponding connection to the workspace)
	 * 
	 * @param context
	 * @param net
	 *            net for which the marking shall be created
	 * @param classType
	 *            marking to create
	 * @return whether creation of marking was successful
	 */
	private boolean createMarking(UIPluginContext context, PetrinetGraph net, Class<? extends Connection> classType) {
		boolean result = false;
		Collection<Pair<Integer, PluginParameterBinding>> plugins = context.getPluginManager().find(
				ConnectionObjectFactory.class, classType, context.getClass(), true, false, false, net.getClass());
		PluginContext c2 = context.createChildContext("Creating connection of Type " + classType);
		Pair<Integer, PluginParameterBinding> pair = plugins.iterator().next();
		PluginParameterBinding binding = pair.getSecond();
		try {
			PluginExecutionResult pluginResult = binding.invoke(c2, net);
			pluginResult.synchronize();
			context.getProvidedObjectManager().createProvidedObjects(c2); // push the objects to main context
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c2.getParentContext().deleteChild(c2);
		}
		return result;
	}
	
	/**
	 * Invokes Repair Model plugin with given log, net, and initia/final
	 * marking. Will attempt to retrieve the {@link TransEvClassMapping} based
	 * on existing connections. If no such connection exists, the method
	 * automatically constructs a {@link TransEvClassMapping} based on the
	 * default classifier of the log. If a different classifier shall be used during construction, use
	 * 
	 * @param context
	 * @param log
	 * @param net
	 * @param initMarking
	 * @param finalMarking
	 * @param config
	 * @return the repaired net with an initial marking
	 */
	@PluginVariant(variantLabel = "Repair Model", requiredParameterLabels = { 0, 1, 2, 3, 4})
	public Object[] repairModel_getT2Econnection(PluginContext context, XLog log, PetrinetGraph net, Marking initMarking, Marking finalMarking, RepairConfiguration config) {
		
		// replay log on model (or obtain existing replay result)
		TransEvClassMapping current_map;
		try {
			EvClassLogPetrinetConnection conn = context.getConnectionManager().getFirstConnection(EvClassLogPetrinetConnection.class, context, net,	log);
			current_map = (TransEvClassMapping) conn.getObjectWithRole(EvClassLogPetrinetConnection.TRANS2EVCLASSMAPPING);
		} catch (ConnectionCannotBeObtained e1) {
			XEventClassifier classifier = PNLogReplayer.getDefaultClassifier(log);
			current_map = PNLogReplayer.getEventClassMapping(context, net, log, classifier);
		}
		
		if (current_map != null) {
			return repairModel(context, log, net, initMarking, finalMarking, config, current_map);
		} else {
			return cancel(context, "Could not obtain 'transition to event class mapping'.");
		}

	}
	
	/**
	 * Invokes Repair Model plugin with given log, net, and initial/final
	 * marking and an {@link XEventClassifier} of the log which is used to
	 * automatically construct a new {@link TransEvClassMapping}. Use this, if
	 * no existing {@link TransEvClassMapping} can be retrieved.
	 * 
	 * @param context
	 * @param log
	 * @param net
	 * @param initMarking
	 * @param finalMarking
	 * @param config
	 * @return the repaired net with an initial marking
	 */
	@PluginVariant(variantLabel = "Repair Model", requiredParameterLabels = { 0, 1, 2, 3, 4, 6})
	public Object[] repairModel_buildT2Econnection(PluginContext context, XLog log, PetrinetGraph net, Marking initMarking, Marking finalMarking, RepairConfiguration config, XEventClassifier classifier) {
		
		// replay log on model (or obtain existing replay result)
		if (classifier == null)
			classifier = PNLogReplayer.getDefaultClassifier(log);
		TransEvClassMapping current_map = PNLogReplayer.getEventClassMapping(context, net, log, classifier);
		
		if (current_map != null) {
			return repairModel(context, log, net, initMarking, finalMarking, config, current_map);
		} else {
			return cancel(context, "Could not obtain 'transition to event class mapping'.");
		}

	}
	
	/**
	 * Invokes Repair model plugin with given log, net, initial/final marking
	 * and provided {@link TransEvClassMapping}.
	 * 
	 * @param context
	 * @param log
	 * @param net
	 * @param initMarking
	 * @param finalMarking
	 * @param config
	 * @param current_map
	 * @return the repaired net with an initial marking
	 */
	@PluginVariant(variantLabel = "Repair Model", requiredParameterLabels = { 0, 1, 2, 3, 4, 5 })
	public Object[] repairModel(PluginContext context, XLog log, PetrinetGraph net, Marking initMarking, Marking finalMarking, RepairConfiguration config, TransEvClassMapping current_map) {
		try {
			Object[] repaired_net_and_marking = run_repairModel(context, log, net, initMarking, finalMarking, config, current_map);
			
			if (repaired_net_and_marking != null) {
				String netName = net.getLabel()+" (repaired)";
				
		  		context.addConnection(new InitialMarkingConnection((Petrinet)repaired_net_and_marking[0], (Marking)repaired_net_and_marking[1]));
		  		context.addConnection(new FinalMarkingConnection((Petrinet)repaired_net_and_marking[0], (Marking)repaired_net_and_marking[2]));
				
		  		// set label before result output
		  		context.getFutureResult(0).setLabel(netName);
		  		context.getFutureResult(1).setLabel("Initial Marking of "+netName);
		  		context.getFutureResult(2).setLabel("Final Marking of "+netName);
				
				return repaired_net_and_marking;
			} else {
				return cancel(context, "return no result");
			}
		} catch (IOException e) {
			return cancel(context, "Failed to write temp output: "+e);
		} catch (InvalidModelException e) {
			e.printStackTrace();
			return cancel(context, "Invalid model: "+e);
		}
	}

	/**
	 * Repair the given net to fit the log according to the given configuration.
	 * The {@link TransEvClassMapping} is assumed to map the net transitions to
	 * the log event classes.
	 * 
	 * @param context
	 * @param log
	 * @param net
	 * @param initMarking
	 * @param finalMarking
	 * @param config
	 * @param current_map
	 * @return the repaired net with an initial marking 
	 * @throws InvalidModelException 
	 * @throws IOException 
	 */
	public Object[] run_repairModel(PluginContext context, XLog log, PetrinetGraph net, Marking initMarking, Marking finalMarking, RepairConfiguration config, TransEvClassMapping current_map) throws IOException, InvalidModelException {

		PetrinetGraph 	current_net = net; 
		Marking 		current_m_init = initMarking;
		Marking			current_m_final = finalMarking;
		XEventClassifier classifier = current_map.getEventClassifier();
		
		if (config.detectLoops) {
			System.out.println("detect loops...");
			ReplayParams params = Uma_AlignForLoopDiscovery_Plugin.getReplayerParameters(context, current_net, current_m_init, current_m_final, log, classifier, config.loopModelMoveCosts);
			PNRepResult repairAlignment = Uma_AlignForLoopDiscovery_Plugin.getLoopAlignment(context, log, current_net, params, current_map, true);
			
			if (repairAlignment == null) {
				return cancel(context, "Could not get alignment.");
			}
			
//			if (config.globalCostAlignment) {
//				Uma_AlignForGlobalRepair_Plugin globalAlign = new Uma_AlignForGlobalRepair_Plugin();
//				repairAlignment = globalAlign.getGlobalAlignment(context, log, current_net, repairAlignment, params.selectedAlg, params.parameters, current_map, config.globalCost_maxIterations);
//			}
			
			Uma_RepairModel_Loops_Plugin repair = new Uma_RepairModel_Loops_Plugin();
			Object[] loopRepaired = repair.run_repairModel(context, log, current_net, current_m_init, current_m_final, repairAlignment, current_map, config.alignAlignments, config.repairFinalMarking);
			
			current_net = (PetrinetGraph)loopRepaired[0];
			current_m_init = (Marking)loopRepaired[1];
			current_m_final = (Marking)loopRepaired[2];
			current_map = PNLogReplayer.adoptEventClassMapping(context, current_net, log, current_map, true);
		}
		
		if (config.detectSubProcesses) {
			System.out.println("detect subprocesses...");
			ReplayParams params = PNLogReplayer.getReplayerParameters(context, current_net, current_m_init, current_m_final, log, classifier);
			PNRepResult repairAlignment = PNLogReplayer.callReplayer(context, current_net, log, current_map, params);
			
			if (config.globalCostAlignment) {
				Uma_AlignForGlobalRepair_Plugin globalAlign = new Uma_AlignForGlobalRepair_Plugin();
				repairAlignment = globalAlign.getGlobalAlignment(context, log, current_net, repairAlignment, params.selectedAlg, params.parameters, current_map, config.globalCost_maxIterations);
			}
			
			Uma_RepairModel_Subprocess_Plugin repair = new Uma_RepairModel_Subprocess_Plugin();
			Object[] subProcessRepaired = repair.run_repairModel(context, log, current_net, current_m_init, current_m_final, repairAlignment, current_map, config.alignAlignments, -1, config.repairFinalMarking);
			current_net = (PetrinetGraph)subProcessRepaired[0];
			current_m_init = (Marking)subProcessRepaired[1];
			current_m_final = (Marking)subProcessRepaired[2];
			current_map = PNLogReplayer.adoptEventClassMapping(context, current_net, log, current_map, true);
		}
		
		if (config.removeInfrequentNodes) {
			System.out.println("remove infrequent nodes...");
			
			// FIXME: current final marking is empty, returns wrong alignment without sync moves, only log moves and model moves in wrong order 
			
			ReplayParams params = PNLogReplayer.getReplayerParameters(context, current_net, current_m_init, current_m_final, log, classifier);
			PNRepResult repairAlignment = PNLogReplayer.callReplayer(context, current_net, log, current_map, params);
			
			if (config.globalCostAlignment) {
				Uma_AlignForGlobalRepair_Plugin globalAlign = new Uma_AlignForGlobalRepair_Plugin();
				repairAlignment = globalAlign.getGlobalAlignment(context, log, current_net, repairAlignment, params.selectedAlg, params.parameters, current_map, config.globalCost_maxIterations);
			}
			
			Uma_RepairModel_RemoveDeadParts_Plugin repair = new Uma_RepairModel_RemoveDeadParts_Plugin();
			Object[] removeRepaired = repair.run_repairModel(context, log, current_net, current_m_init, current_m_final, repairAlignment, current_map, true, config.remove_keepIfAtLeast, config.repairFinalMarking);
			current_net = (PetrinetGraph)removeRepaired[0];
			current_m_init = (Marking)removeRepaired[1];
			current_m_final = (Marking)removeRepaired[2];
			current_map = PNLogReplayer.adoptEventClassMapping(context, current_net, log, current_map, true);
		}

		return new Object[] { current_net, current_m_init, current_m_final };
	}
	


	public static Object[] cancel(PluginContext context, String message) {
		System.out.println("[ModelRepair/repair model]: "+message);
		context.log(message);
		context.getFutureResult(0).cancel(true);
		return null;
	}
}
