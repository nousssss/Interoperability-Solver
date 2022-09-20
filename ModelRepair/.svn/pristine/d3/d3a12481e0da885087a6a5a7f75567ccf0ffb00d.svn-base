package org.processmining.modelrepair.plugins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.modelrepair.plugins.align.PNLogReplayer;
import org.processmining.modelrepair.plugins.data.SimpleAlignment;
import org.processmining.modelrepair.plugins.uma.UmaPromUtil;
import org.processmining.modelrepair.plugins.uma.UmaPromUtil.ProMToUmaBridge;
import org.processmining.models.connections.petrinets.EvClassLogPetrinetConnection;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

import hub.top.petrinet.PetriNet;
import hub.top.scenario.OcletIO_Out;
import hub.top.uma.DNodeRefold;
import hub.top.uma.InvalidModelException;
import nl.tue.astar.AStarException;
import viptool.algorithm.postprocessing.pnetiplp.ExtendedExhuminator;

@Plugin(name = "Repair Model (remove unused parts)",
		level = PluginLevel.PeerReviewed,
		parameterLabels = { "a Log", "a Petri net", "an Alignment", "initial marking", "final marking"}, //
		returnLabels = { "Repaired Petri net", "Initial Marking", "Final Marking" },
		returnTypes = { Petrinet.class, Marking.class, Marking.class }, 
		help = "Repair a process model to such that the model becomes more precise for the given event log by removing unused parts from the model. Other repair plugins allow different repairs (adding loops/subprocesses to increase fitness).",
		userAccessible = true,
		mostSignificantResult = 1)
public class Uma_RepairModel_RemoveDeadParts_Plugin {
	
	// take log and net as input and guess initial marking
	@UITopiaVariant(
			affiliation="TU/e",
			author="D. Fahland",
			email="d.fahland@tue.nl",
			website = "http://service-technology.org/uma",
			pack="ModelRepair")
	@PluginVariant(variantLabel = "Repair Model (remove unused parts)", requiredParameterLabels = { 0, 1 })
	public Object[] repairModel(UIPluginContext context, XLog log, PetrinetGraph net) {

		Marking initMarking;
		try {
			initMarking = context.tryToFindOrConstructFirstObject(Marking.class, InitialMarkingConnection.class, InitialMarkingConnection.MARKING, net);
		} catch (ConnectionCannotBeObtained e) {
			return cancel(context, "No initial marking found.");
		}
		
		Marking finalMarking;
		try {
			finalMarking = context.tryToFindOrConstructFirstObject(Marking.class, FinalMarkingConnection.class, FinalMarkingConnection.MARKING, net);
		} catch (ConnectionCannotBeObtained e) {
			return cancel(context, "No final marking found.");
		}

		// replay log on model (or obtain existing replay result)
		PNRepResult result = null;
		try {
			PNLogReplayer replayer = new PNLogReplayer();
			result = replayer.replayLog(context, net, log);
			
			IPNReplayAlgorithm selectedAlg = replayer.getUsedAlgorithm();
			IPNReplayParameter parameters = replayer.getUsedAlgParameters();
			TransEvClassMapping mapping = replayer.getUsedMapping();
			
			replayer.replayLog(context, net, log, mapping, selectedAlg, parameters);
			
		} catch (ConnectionCannotBeObtained e) {
			return cancel(context, "Could not replay the log on the model. No replayer found.");
		}  catch (AStarException e) {
			return cancel(context, "Could not replay the log on the model: "+e.toString());
		}
		return repairModel(context, log, net, initMarking, finalMarking, result);
	}
	
	// take log and net as input and guess initial marking
	@UITopiaVariant(
			affiliation="TU/e",
			author="D. Fahland",
			email="d.fahland@tue.nl",
			website = "http://service-technology.org/uma",
			pack="ModelRepair")
	@PluginVariant(variantLabel = "Repair Model", requiredParameterLabels = { 0, 1, 2 })
	public Object[] repairModel(UIPluginContext context, XLog log, PetrinetGraph net, PNRepResult result) {

		Marking initMarking;
		try {
			initMarking = context.tryToFindOrConstructFirstObject(Marking.class, InitialMarkingConnection.class, InitialMarkingConnection.MARKING, net);
		} catch (ConnectionCannotBeObtained e) {
			return cancel(context, "No initial marking found.");
		}
		
		Marking finalMarking;
		try {
			finalMarking = context.tryToFindOrConstructFirstObject(Marking.class, FinalMarkingConnection.class, FinalMarkingConnection.MARKING, net);
		} catch (ConnectionCannotBeObtained e) {
			return cancel(context, "No final marking found.");
		}
		
		return repairModel(context, log, net, initMarking, finalMarking, result);
	}
	
	
	protected Object[] repairModel(PluginContext context, XLog log, PetrinetGraph net, Marking initMarking, Marking finalMarking, PNRepResult result) {

		EvClassLogPetrinetConnection conn;
		try {
			conn = context.getConnectionManager().getFirstConnection(EvClassLogPetrinetConnection.class, context, net,	log);
		} catch (ConnectionCannotBeObtained e1) {
			e1.printStackTrace();
			return null;
		}
		// generate alignment in simplified form for model repair steps 
		TransEvClassMapping map = (TransEvClassMapping) conn.getObjectWithRole(EvClassLogPetrinetConnection.TRANS2EVCLASSMAPPING);

		try {
			Object[] repaired_net_and_marking = run_repairModel(context, log, net, initMarking, finalMarking, result, map, true, 1, true);
	
			String netName = "repaired net from "+net.getLabel();
			
	  		context.addConnection(new InitialMarkingConnection((Petrinet)repaired_net_and_marking[0], (Marking)repaired_net_and_marking[1]));
	  		context.addConnection(new FinalMarkingConnection((Petrinet)repaired_net_and_marking[0], (Marking)repaired_net_and_marking[2]));
			
	  		// set label before result output
	  		context.getFutureResult(0).setLabel(netName);
	  		context.getFutureResult(1).setLabel("Initial Marking of "+netName);
	  		context.getFutureResult(2).setLabel("Final Marking of "+netName);
			
			return repaired_net_and_marking;
			
		} catch (IOException e) {
			return cancel(context, "Failed to write temp output: "+e);
		} catch (InvalidModelException e) {
			e.printStackTrace();
			return cancel(context, "Invalid model: "+e);
		}
	}
	
	protected Object[] run_repairModel(PluginContext context, XLog log, PetrinetGraph net, Marking initMarking, Marking finalMarking, PNRepResult result, TransEvClassMapping map, boolean alignAlignments, int deleteIfLessThan, boolean updateFinalMarking) throws IOException, InvalidModelException {

		ProMToUmaBridge b = UmaPromUtil.toPNAPIFormat(net, initMarking);
		PetriNet _net = b.pnet;
		_net.turnIntoLabeledNet();
		_net.makeNormalNet();
		
		// convert final marking to hub.top.petrinet.* model, needed to update final marking in case of structural changes
		List<hub.top.petrinet.Place> _finalMarking = new ArrayList<hub.top.petrinet.Place>();
		for (Place p : finalMarking) _finalMarking.add((hub.top.petrinet.Place)b.nodeMap.get(p)); // remember with multiplicities
		
		List<SimpleAlignment> alignment = SimpleAlignment.getAlignment(result, b.nodeMap, map.getEventClassifier());
		
//		for (Move[] moves : alignment) {
//			for (Move m : moves) System.out.print(m+", ");
//			System.out.println();
//		}

		// reverse transition to event mapping
		Map<String, String> eventToTransition =  new HashMap<String, String>();
		for (Transition t : map.keySet()) {
			eventToTransition.put(map.get(t).toString(), t.getLabel());
		}
		
		String tempOutputFile = System.getProperty("java.io.tmpdir")+"/prom_align";
		
		_stats = new RepairStats();
		
		HashMap<Object, String> colorMap = new HashMap<Object, String>();
		for (hub.top.petrinet.Transition t : _net.getTransitions()) {
			if (t.getName().startsWith("SILENT") || t.getName().isEmpty())
			colorMap.put(t, "grey");
		}
		OcletIO_Out.writeToFile(_net, tempOutputFile+"_original.dot", OcletIO_Out.FORMAT_DOT, 0);
		OcletIO_Out.writeFile(_net.toDot(colorMap), tempOutputFile+"_original.dot");

		
        DNodeRefold build = ModelRepair_Simple.getInitialUnfolding(_net);
        ModelRepair_SubProcess repair = new ModelRepair_SubProcess(_net, build);
        
        repair.replayAlignment(tempOutputFile, alignment, eventToTransition);
        
		_stats.removed_trans = _net.getTransitions().size();
		_stats.removed_places = _net.getPlaces().size();
		_stats.removed_arcs = _net.getArcs().size();
		
		System.out.println("removing unused nodes...");
		repair.removeInfrequentNodes(deleteIfLessThan);
		try {
			ExtendedExhuminator.reduce(_net);
		} catch (Throwable e) {
			// in case dependent libraries could not be found, just ignore
		}
		
		// fix final marking if required 
		List<hub.top.petrinet.Place> _repairedFinalMarking = repair.repairFinalMarking(_finalMarking, updateFinalMarking);

		_stats.removed_trans -= _net.getTransitions().size();
		_stats.removed_places -= _net.getPlaces().size();
		_stats.removed_arcs -= _net.getArcs().size();
		
		System.out.println(_stats);
		
		colorMap = new HashMap<Object, String>();
		for (hub.top.petrinet.Transition t : _net.getTransitions()) {
			if (t.getName().startsWith("SILENT") || t.getName().isEmpty())
			colorMap.put(t, "grey");
		}
		OcletIO_Out.writeFile(_net.toDot_swimlanes(colorMap), tempOutputFile+"_repaired_box.dot");

		String netName = "repaired net from "+net.getLabel();
		Object promNet[] = UmaPromUtil.toPromFormat(_net, netName);
		@SuppressWarnings("unchecked")
		Map<hub.top.petrinet.Node, PetrinetNode> nodeMap = (Map<hub.top.petrinet.Node, PetrinetNode>)promNet[2];
		
		Petrinet repairedNet = (Petrinet)promNet[0];
		for (Transition transition : repairedNet.getTransitions()) {
			// by default, suggest invisible if the name of transitions:
			// 1. started with "tr"
			// 2. started with "SILENT"
			// 3. started with "tau"
			// 4. started with "invi"
			String lowCase = transition.getLabel().toLowerCase();
			if ((lowCase.startsWith("tr")) || (lowCase.startsWith("silent")) || (lowCase.startsWith("tau"))
					|| (lowCase.startsWith("invi"))) {
				transition.setInvisible(true);
			}
		}

		Marking repaired_initialMarking = (Marking)promNet[1];
		Marking repaired_finalMarking = UmaPromUtil.toPromFormat(_repairedFinalMarking, nodeMap);

		return new Object[] { repairedNet, repaired_initialMarking, repaired_finalMarking };
	}
	
	protected RepairStats _stats;
	
	public static class RepairStats {
		public int num_subprocesses = 0;
		public int added_trans = 0;
		public int added_places = 0;
		public int added_arcs = 0;
		
		public int added_trans_max = 0;
		public int added_places_max = 0;
		public int added_arcs_max = 0;
		
		public int removed_trans = 0;
		public int removed_places = 0;
		public int removed_arcs = 0;
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("added "+num_subprocesses+"\n");
			if (num_subprocesses > 0) {
				sb.append("avg. P: "+added_places/num_subprocesses+" T: "+added_trans/num_subprocesses+" F: "+added_arcs/num_subprocesses+"\n");
			}
			sb.append("max. P: "+added_places_max+" T: "+added_trans_max+" F: "+added_arcs_max+"\n");
			sb.append("added P: "+added_places+" T: "+added_trans+" F: "+added_arcs+"\n");
			sb.append("removed P: "+removed_places+" T: "+removed_trans+" F: "+removed_arcs+"\n");
			
			return sb.toString();
		}
	}

	protected static Object[] cancel(PluginContext context, String message) {
		System.out.println("[ModelRepair]: "+message);
		context.log(message);
		context.getFutureResult(0).cancel(true);
		return null;
	}
}
