package org.processmining.modelrepair.plugins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.modelrepair.plugins.ModelRepair_SubProcess.SubLog;
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
import org.processmining.plugins.ilpminer.ILPMiner;
import org.processmining.plugins.ilpminer.ILPMinerLogPetrinetConnection;
import org.processmining.plugins.ilpminer.ILPMinerSettings;
import org.processmining.plugins.ilpminer.ILPMinerSettings.SolverSetting;
import org.processmining.plugins.ilpminer.ILPMinerSettings.SolverType;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

import hub.top.petrinet.PetriNet;
import hub.top.scenario.OcletIO_Out;
import hub.top.uma.DNodeRefold;
import hub.top.uma.InvalidModelException;
import hub.top.uma.view.MineSimplify;
import nl.tue.astar.AStarException;
import viptool.algorithm.postprocessing.pnetiplp.ExtendedExhuminator;

@Plugin(name = "Repair Model (find subprocesses)",
		level = PluginLevel.PeerReviewed,
		parameterLabels = { "a Log", "a Petri net", "an Alignment", "initial marking", "final marking"}, //
		returnLabels = { "Repaired Petri net", "initial marking", "final marking"},
		returnTypes = { Petrinet.class, Marking.class, Marking.class },
		help = "Repair a process model to such that the model perfectly fits the given event log by adding minimal sub-processes and skip-transitions. Other repair plugins allow different repairs (loops, removal of dead parts).",
		userAccessible = true,
		mostSignificantResult = 1)
public class Uma_RepairModel_Subprocess_Plugin {
	
	private static boolean _debug = true;
	
	// take log and net as input and guess initial marking
	@UITopiaVariant(
			affiliation="TU/e",
			author="D. Fahland",
			email="d.fahland@tue.nl",
			website = "http://service-technology.org/uma",
			pack="ModelRepair")
	@PluginVariant(variantLabel = "Repair Model (find subprocesses)", requiredParameterLabels = { 0, 1 })
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

	/**
	 * Run repair with default parameters.
	 *  
	 * @param context
	 * @param log
	 * @param net
	 * @param initMarking
	 * @param finalMarking
	 * @param result
	 * @return
	 */
	protected Object[] repairModel(PluginContext context, XLog log, PetrinetGraph net, Marking initMarking, Marking finalMarking, PNRepResult result) {
		return repairModel(context, log, net, initMarking, finalMarking, result, true, -1, true);
	}
	
	/**
	 * Invoke repair with storing results and connections to plugin context. 
	 * 
	 * @param context
	 * @param log
	 * @param net
	 * @param initMarking
	 * @param finalMarking
	 * @param result
	 * @param alignAlignments
	 * @param deleteIfLessThan
	 * @return
	 */
	protected Object[] repairModel(PluginContext context, XLog log, PetrinetGraph net, Marking initMarking, Marking finalMarking, PNRepResult result, boolean alignAlignments, int deleteIfLessThan, boolean updateFinalMarking) {

		// get mapping between net and log
		EvClassLogPetrinetConnection conn;
		try {
			conn = context.getConnectionManager().getFirstConnection(EvClassLogPetrinetConnection.class, context, net,	log);
		} catch (ConnectionCannotBeObtained e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		TransEvClassMapping map = (TransEvClassMapping) conn.getObjectWithRole(EvClassLogPetrinetConnection.TRANS2EVCLASSMAPPING);

		try {
			
			// call actual plugin and 
			Object[] repaired_net_and_marking = run_repairModel(context, log, net, initMarking, finalMarking, result, map, alignAlignments, deleteIfLessThan, updateFinalMarking);

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
	
	public String result_infix = "";
	
	/**
	 * Actual repair method to repair net for given log based on provided replay result.
	 * 
	 * @param context
	 * @param log
	 * @param net
	 * @param initMarking
	 * @param result
	 * @map mapping between log and net
	 * @param alignAlignments group sequences of log moves to improve discovery of local subprocesses
	 * @param deleteIfLessThan delete all nodes from the model occurring less than the specified threshold
	 * @param updateFinalMarking whether to update the final marking of the net to consist of all final nodes 
	 * @return an array of [repaired net, initial marking, final marking]
	 * @throws InvalidModelException, IOException
	 */
	public Object[] run_repairModel(PluginContext context, XLog log, PetrinetGraph net, Marking initMarking, Marking finalMarking, PNRepResult result, TransEvClassMapping map, boolean alignAlignments, int deleteIfLessThan, boolean updateFinalMarking) throws IOException, InvalidModelException {

		ProMToUmaBridge b = UmaPromUtil.toPNAPIFormat(net, initMarking);
		PetriNet _net = b.pnet;
		_net.turnIntoLabeledNet();
		_net.makeNormalNet();
		
		// convert final marking to hub.top.petrinet.* model, needed to update final marking in case of structural changes
		List<hub.top.petrinet.Place> _finalMarking = new ArrayList<hub.top.petrinet.Place>();
		for (Place p : finalMarking) _finalMarking.add((hub.top.petrinet.Place)b.nodeMap.get(p)); // remember with multiplicities
		
		// generate alignment in simplified form for model repair steps 
		List<SimpleAlignment> alignment = SimpleAlignment.getAlignment(result, b.nodeMap, map.getEventClassifier());

		// reverse transition to event mapping
		Map<String, String> eventToTransition =  new HashMap<String, String>();
		for (Transition t : map.keySet()) {
			eventToTransition.put(map.get(t).toString(), t.getLabel());
		}
		
		String tempOutputFile = System.getProperty("java.io.tmpdir")+"/prom_align"+result_infix;
		
		_stats = new RepairStats();
		
		HashMap<Object, String> colorMap = new HashMap<Object, String>();
		for (hub.top.petrinet.Transition t : b.pnet.getTransitions()) {
			if (t.getName().startsWith("SILENT") || t.getName().isEmpty())
			colorMap.put(t, "grey");
		}
		OcletIO_Out.writeToFile(_net, tempOutputFile+"_original.dot", OcletIO_Out.FORMAT_DOT, 0);
		OcletIO_Out.writeFile(_net.toDot(colorMap), tempOutputFile+"_original.dot");

		
        DNodeRefold build = ModelRepair_Simple.getInitialUnfolding(_net);
        ModelRepair_SubProcess repair = new ModelRepair_SubProcess(_net, build);
        
        repair.replayAlignment(tempOutputFile, alignment, eventToTransition);
        repair.extendModelWithMoveOnModel();
        
		List<SubLog> _subLogs = repair.getExtensions();
		
		// new: align sublogs to improve quality of subprocesses
		List<SubLog> alignedSublogs;
		if (alignAlignments)
			alignedSublogs = repair.alignExtensions(_subLogs);
		else
			alignedSublogs = _subLogs;
		
		XFactory f = XFactoryRegistry.instance().currentDefault();
		
		int subCounter = 0;
		for (SubLog subLog : alignedSublogs) {
			Set<hub.top.petrinet.Place> pl = subLog.location;
			List<List<String>> sublog = subLog.getEventTraces();
			if (sublog == null || sublog.size() == 0) continue;
			
			subCounter++;
			if (_debug) System.out.println("---");
			for (List<String> tr : sublog) {
				tr.add(0, "SILENT sub_start"+subCounter);
				tr.add("SILENT sub_end"+subCounter);
				if (_debug) System.out.println("  trace: "+tr);
			}
			
			XLog l = f.createLog();
			
			XEventClassifier xnc = XLogInfoImpl.NAME_CLASSIFIER;
			l.getClassifiers().add(xnc);
			
			for (List<String> tr : sublog) {
				XTrace t = f.createTrace();
				
				for (String eventName : tr) {
					XEvent e = f.createEvent();
					XConceptExtension.instance().assignName(e, eventName);
					t.add(e);
				}
				
				l.add(t);
			}
			
			XLogInfoFactory.createLogInfo(l, xnc);
				
			try {
				Object[] subProcesses = doILPMining(context, l, xnc);
				
				Petrinet subNet = (Petrinet) subProcesses[0];
				Marking subMarking = (Marking) subProcesses[1];
				
				PetriNet _subNet = UmaPromUtil.toPNAPIFormat(subNet, subMarking);
				LinkedList<String[]> eventLog = new LinkedList<String[]>();
				for (List<String> trace : sublog) {
					eventLog.add(trace.toArray(new String[trace.size()]));
				}
				MineSimplify.Configuration config = new MineSimplify.Configuration();
				config.unfold_refold = false;
				config.abstract_chains = false;
				config.remove_flower_places = true;
				config.filter_threshold = 0;
				config.remove_implied = MineSimplify.Configuration.REMOVE_IMPLIED_PRESERVE_CONNECTED;
				
				MineSimplify simplify = new MineSimplify(_subNet, eventLog, config);
				simplify.prepareModel();
				simplify.run();
				
				_subNet = simplify.getSimplifiedNet();
				_subNet.makeNormalNet();
				
				System.out.println("sub"+subCounter+": "+_subNet.getInfo(false));
				
				_stats.num_subprocesses++;
				_stats.added_trans += _subNet.getTransitions().size();
				_stats.added_places += _subNet.getPlaces().size();
				_stats.added_arcs += _subNet.getArcs().size();
				if (_stats.added_trans_max < _subNet.getTransitions().size()) _stats.added_trans_max = _subNet.getTransitions().size();
				if (_stats.added_places_max < _subNet.getPlaces().size()) _stats.added_places_max = _subNet.getPlaces().size();
				if (_stats.added_arcs_max < _subNet.getArcs().size()) _stats.added_arcs_max = _subNet.getArcs().size();

				boolean isIsolatedExtension = !hasOverlappingLocation(subLog, alignedSublogs);
				
				hub.top.petrinet.Transition start = _subNet.findTransition("SILENT sub_start"+subCounter);
				hub.top.petrinet.Transition end = _subNet.findTransition("SILENT sub_end"+subCounter);
				repair.extendModelWithMoveOnLog(_subNet, start, end, sublog, pl, "sub"+subCounter, sublog.size(), isIsolatedExtension);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		_stats.removed_trans = _net.getTransitions().size();
		_stats.removed_places = _net.getPlaces().size();
		_stats.removed_arcs = _net.getArcs().size();
		
		if (deleteIfLessThan >= 0) {
			System.out.println("removing unused nodes...");
			repair.removeInfrequentNodes(deleteIfLessThan);
			try {
				ExtendedExhuminator.reduce(_net);
			} catch (Throwable e) {
				// in case dependent libraries could not be found, just ignore
			}
		}
		
		// repair final marking 
		List<hub.top.petrinet.Place> _repairedFinalMarking = repair.repairFinalMarking(_finalMarking, updateFinalMarking);

		_stats.removed_trans -= _net.getTransitions().size();
		_stats.removed_places -= _net.getPlaces().size();
		_stats.removed_arcs -= _net.getArcs().size();
		
		System.out.println(_stats);
		
		
//			Map<String, Integer> _node_duplication = new HashMap<String, Integer>();
//			for (hub.top.petrinet.Place p : pnet.getPlaces()) {
//				String pName = p.getName();
//				int hash = pName.indexOf('#');
//				if (hash >= 0) pName = pName.substring(0, hash);
//				if (!_node_duplication.containsKey(pName)) _node_duplication.put(pName, 1);
//				else {
//					int newNum = _node_duplication.get(pName)+1;
//					_node_duplication.put(pName, newNum);
//					p.setName(pName+"#"+newNum);
//				}
//			}
//			for (hub.top.petrinet.Transition t : pnet.getTransitions()) {
//				String tName = t.getName();
//				int hash = tName.indexOf('#');
//				if (hash >= 0) tName = tName.substring(0, hash);
//				if (!_node_duplication.containsKey(tName)) _node_duplication.put(tName, 1);
//				else {
//					int newNum = _node_duplication.get(tName)+1;
//					_node_duplication.put(tName, newNum);
//					t.setName(tName+"#"+newNum);
//				}
//			}
		
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
	
	private boolean hasOverlappingLocation(SubLog sublog, List<SubLog> allLogs) {
		for (SubLog otherSublog : allLogs) {
			if (sublog == otherSublog) continue;
			for (hub.top.petrinet.Place p : sublog.location) {
				if (otherSublog.location.contains(p)) return true;
			}
		}
		return false;
	}
	
	public RepairStats _stats;
	
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
		
		public String toString_csv() {
			StringBuilder sb = new StringBuilder();
			sb.append("added;"+num_subprocesses+";");
			if (num_subprocesses > 0) {
				sb.append("avg. P;"+added_places/num_subprocesses+";avg. T;"+added_trans/num_subprocesses+";avg. F;"+added_arcs/num_subprocesses+";");
			}
			sb.append("max. P;"+added_places_max+";max. T;"+added_trans_max+"; max. F;"+added_arcs_max+";");
			sb.append("added P;"+added_places+";added T;"+added_trans+"; added F;"+added_arcs+";");
			sb.append("removed P;"+removed_places+";removed T;"+removed_trans+";removed F;"+removed_arcs+";");
			
			return sb.toString();
		}
	}
	
	protected static Object[] cancel(PluginContext context, String message) {
		System.out.println("[ModelRepair/repair model]: "+message);
		context.log(message);
		context.getFutureResult(0).cancel(true);
		return null;
	}
	
	/**
	 * Directly invoke ILP miner using original ILP miner implementation.
	 * 
	 * @param context
	 * @param log
	 * @param classifier
	 * @return
	 * @throws Exception
	 */
	public Object[] doILPMining(PluginContext context, XLog log, XEventClassifier classifier) throws Exception {
//		try {
//			ILPMinerLogPetrinetConnection conn = context.getConnectionManager()
//					.getFirstConnection(ILPMinerLogPetrinetConnection.class,
//							context, log);
//		} catch (Exception e) {
//		}
		ILPMinerSettings settings = new ILPMinerSettings();
		Preferences prefs = Preferences.userNodeForPackage(ILPMiner.class);
		prefs.putInt("SolverEnum", ((SolverType) settings.getSolverSetting(SolverSetting.TYPE)).ordinal());
		prefs.put("LicenseDir", (String) settings.getSolverSetting(SolverSetting.LICENSE_DIR));
//		
//		ILPMiner ilpMiner = new ILPMiner();
//		
//		Class[] resultTypes = new Class[] {Petrinet.class, Marking.class};
//		Collection<Pair<Integer, PluginParameterBinding>> plugins = context.getPluginManager().find(Plugin.class, resultTypes, context.getPluginContextType(), true, false, false, XLog.class, XLogInfo.class, ILPMinerSettings.class);
//		Pair<Integer, PluginParameterBinding> ilp_plugin = plugins.iterator().next();
//		
//		PluginContext ilp_context = context.createChildContext("ILP mining");
//		context.getPluginLifeCycleEventListeners().firePluginCreated(ilp_context);
//		PluginExecutionResult pluginResult = ilp_plugin.getSecond().invoke(ilp_context, log, log.getInfo(classifier), settings);
//		pluginResult.synchronize();
//		Petrinet result = pluginResult.<Petrinet> getResult(ilp_plugin.getFirst());
//		Marking marking = pluginResult.<Marking> getResult(ilp_plugin.getFirst()+1);
//		context.deleteChild(ilp_context);
//
//		return new Object [] { result, marking };
		Petrinet result = context.tryToFindOrConstructFirstNamedObject(Petrinet.class, "ILP Miner", ILPMinerLogPetrinetConnection.class, null, log,
				log.getInfo(classifier), settings);
		
		Marking initMarking;
		try {
			initMarking = context.tryToFindOrConstructFirstObject(Marking.class, InitialMarkingConnection.class, InitialMarkingConnection.MARKING, result);
		} catch (ConnectionCannotBeObtained e) {
			String message = "No initial marking found for ILP miner result from sublog.";
			System.out.println("[ModelRepair/repair model]: "+message);
			context.log(message);
			throw e;
		}
		return new Object [] { result, initMarking };
	}
}
