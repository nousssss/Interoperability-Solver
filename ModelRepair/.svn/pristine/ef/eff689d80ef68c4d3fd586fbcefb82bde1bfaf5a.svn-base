package org.processmining.modelrepair.plugins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeMap;
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
import org.processmining.modelrepair.plugins.ModelRepair_SubProcess.MoveOnLog;
import org.processmining.modelrepair.plugins.ModelRepair_SubProcess.PetriNetWithMarkings;
import org.processmining.modelrepair.plugins.ModelRepair_SubProcess.SubLog;
import org.processmining.modelrepair.plugins.align.PNLogReplayer;
import org.processmining.modelrepair.plugins.align.Uma_AlignForLoopDiscovery_Plugin;
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
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

import hub.top.petrinet.PetriNet;
import hub.top.scenario.OcletIO_Out;
import hub.top.uma.DNodeRefold;
import hub.top.uma.InvalidModelException;

@Plugin(name = "Repair Model (find loops)",
		level = PluginLevel.PeerReviewed,
		parameterLabels = { "a Log", "a Petri net", "an Alignment", "initial marking", "final marking"}, //
		returnLabels = { "Repaired Petri net", "Initial Marking", "Final Marking" },
		returnTypes = { Petrinet.class, Marking.class, Marking.class }, 
		userAccessible = true,
		help = "Repair a process model to such that fitness to the given event log improves by identifying and adding loops in the log that are not described in the process.  Other repair plugins allow different repairs (adding subprocesses, removal of dead parts).", 
		mostSignificantResult = 1)
public class Uma_RepairModel_Loops_Plugin {
	
	// take log and net as input and guess initial marking
	@UITopiaVariant(
			affiliation="TU/e",
			author="D. Fahland",
			email="d.fahland@tue.nl",
			website = "http://service-technology.org/uma",
			pack="ModelRepair")
	@PluginVariant(variantLabel = "Repair Model (find loops)", requiredParameterLabels = { 0, 1 })
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
		TransEvClassMapping map;
		try {
			EvClassLogPetrinetConnection conn = context.getConnectionManager().getFirstConnection(EvClassLogPetrinetConnection.class, context, net,	log);
			map = (TransEvClassMapping) conn.getObjectWithRole(EvClassLogPetrinetConnection.TRANS2EVCLASSMAPPING);
		} catch (ConnectionCannotBeObtained e1) {
			XEventClassifier classifier = PNLogReplayer.getDefaultClassifier(log);
			map = PNLogReplayer.getEventClassMapping(context, net, log, classifier);
		}
		
		PNRepResult loop_alignment = Uma_AlignForLoopDiscovery_Plugin.getLoopAlignment(context, log, net, initMarking, finalMarking, map, true, 1);

		return runPlugin_repairModel(context, log, net, initMarking, finalMarking, loop_alignment);
	}
	
	// take log and net as input and guess initial marking
	@UITopiaVariant(
			affiliation="TU/e",
			author="D. Fahland",
			email="d.fahland@tue.nl",
			website = "http://service-technology.org/uma",
			pack="ModelRepair")
	@PluginVariant(variantLabel = "Repair Model", requiredParameterLabels = { 0, 1, 2 })
	public Object[] alignLogToModel(UIPluginContext context, XLog log, PetrinetGraph net, PNRepResult result) {

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

		return runPlugin_repairModel(context, log, net, initMarking, finalMarking, result);
	}
	
	/**
	 * Execute plugin and send result back to context with correct labels.
	 * 
	 * @param context
	 * @param log
	 * @param net
	 * @param initMarking
	 * @param result
	 * @return
	 */
	private Object[] runPlugin_repairModel(PluginContext context, XLog log, PetrinetGraph net, Marking initMarking, Marking finalMarking, PNRepResult result) {
		
		EvClassLogPetrinetConnection conn;
		try {
			conn = context.getConnectionManager().getFirstConnection(EvClassLogPetrinetConnection.class, context, net,	log);
		} catch (ConnectionCannotBeObtained e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		// generate alignment in simplified form for model repair steps 
		TransEvClassMapping map = (TransEvClassMapping) conn.getObjectWithRole(EvClassLogPetrinetConnection.TRANS2EVCLASSMAPPING);

		try {
			Object[] repaired_net_and_marking = run_repairModel(context, log, net, initMarking, finalMarking, result, map, true, true);
	
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
	
	/**
	 * Identify "loop traces" that cannot be replayed on the model, check if
	 * adding a loop-back transition allows to replay the "loop traces", and if
	 * so, add this transition to the model.
	 * 
	 * @param context
	 * @param log
	 * @param net
	 * @param initMarking
	 * @param finalMarking
	 * @param result
	 * @param map
	 * @param alignAlignments
	 * @param updateFinalMarking
	 * @return
	 */
	public Object[] run_repairModel(PluginContext context, XLog log, PetrinetGraph net, Marking initMarking, Marking finalMarking, PNRepResult result, TransEvClassMapping map, boolean alignAlignments, boolean updateFinalMarking) throws IOException, InvalidModelException {

		ProMToUmaBridge b = UmaPromUtil.toPNAPIFormat(net, initMarking);
		PetriNet _net = b.pnet;
		_net.turnIntoLabeledNet();
		_net.makeNormalNet();
		
		// convert final marking to hub.top.petrinet.* model, needed to update final marking in case of structural changes
		List<hub.top.petrinet.Place> _finalMarking = new ArrayList<hub.top.petrinet.Place>();
		for (Place p : finalMarking) _finalMarking.add((hub.top.petrinet.Place)b.nodeMap.get(p)); // remember with multiplicities

		
		List<SimpleAlignment> alignment = SimpleAlignment.getAlignment(result, b.nodeMap, map.getEventClassifier());

		// reverse transition to event mapping
		Map<String, String> eventToTransition =  new HashMap<String, String>();
		for (Transition t : map.keySet()) {
			eventToTransition.put(map.get(t).toString(), t.getLabel());
		}
		
		String tempOutputFile = System.getProperty("java.io.tmpdir")+"/prom_align";
		
		HashMap<Object, String> colorMap = new HashMap<Object, String>();
		for (hub.top.petrinet.Transition t : _net.getTransitions()) {
			if (t.getName().startsWith("SILENT") || t.getName().isEmpty())
			colorMap.put(t, "grey");
		}
		OcletIO_Out.writeToFile(_net, tempOutputFile+"_original.dot", OcletIO_Out.FORMAT_DOT, 0);
		OcletIO_Out.writeFile(_net.toDot(colorMap), tempOutputFile+"_original.dot");

		
        DNodeRefold build = ModelRepair_Simple.getInitialUnfolding(_net);
        ModelRepair_SubProcess repair = new ModelRepair_SubProcess(_net, build);
        
        System.out.println("replaying alignment on model for "+alignment.size()+" traces to identify deviations");
        repair.replayAlignment(tempOutputFile, alignment, eventToTransition);
        
        System.out.println("getting all non-fitting sublogs ");
		List<SubLog> _subLogs = repair.getExtensions();
		
		// new: align sublogs to improve quality of subprocesses
		List<SubLog> alignedSublogs;
		if (alignAlignments) {
			System.out.println("aligning non-fitting sublogs to each other");
			alignedSublogs = repair.alignExtensions(_subLogs);
		} else
			alignedSublogs = _subLogs;
		
		int _alignedsublogs_handled = 0;
		for (SubLog subLog : alignedSublogs) {
			_alignedsublogs_handled++;
			System.out.println("identifying loops for sublog "+_alignedsublogs_handled+"/"+alignedSublogs.size());
			List<List<MoveOnLog>> loopHypotheses = repair.getLoopHypotheses(subLog);
			if (loopHypotheses.size() > 0) {
				System.out.println("identified "+loopHypotheses.size()+" potential loops");
				for (List<MoveOnLog> trace : loopHypotheses) {
					System.out.println(trace);
				}
				
				Set<hub.top.petrinet.Node> loopBody = repair.getLoopBody(loopHypotheses);
				List<hub.top.petrinet.Place> entry = repair.getLoopEntry(loopBody);
				List<hub.top.petrinet.Place> exit = repair.getLoopExit(loopBody);
				
				System.out.println("at "+entry);
				System.out.println("and "+exit);
				
				if (!entry.isEmpty() && !exit.isEmpty()) {
					PetriNetWithMarkings loop = repair.getLoop(loopBody, entry, exit);
					XLog loopLog = toXLog(loopHypotheses, "loop hypothesis");

					if (isValidLoopBody(context, loop, loopLog, map.getEventClassifier(), 0)) {
						int trans_in_body = 0;
						for (hub.top.petrinet.Node n : loopBody) if (n instanceof hub.top.petrinet.Transition) trans_in_body++;
						_stats.bodySize.add(trans_in_body);
						
						hub.top.petrinet.Transition loopback = _net.addTransition("SILENT - LOOP");
						for (hub.top.petrinet.Place p : entry) {
							_net.addArc(loopback, p);
						}
						for (hub.top.petrinet.Place p : exit) {
							_net.addArc(p, loopback);
						}
					}
				}
			} else {
				System.out.println("found no loops");
			}
		}
		
		// repair final marking 
		List<hub.top.petrinet.Place> _repairedFinalMarking = repair.repairFinalMarking(_finalMarking, updateFinalMarking);
		
		colorMap = new HashMap<Object, String>();
		for (hub.top.petrinet.Transition t : _net.getTransitions()) {
			if (t.getName().startsWith("SILENT") || t.getName().isEmpty()) colorMap.put(t, "grey");
			if (t.getName().indexOf("SILENT - LOOP") >= 0) colorMap.put(t, "red");
		}
		OcletIO_Out.writeFile(_net.toDot_swimlanes(colorMap), tempOutputFile+"_repaired_loop.dot");

		String netName = "repaired net from "+net.getLabel()+" (loops)";
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

	public static class RepairStats {
		public List<Integer> bodySize = new LinkedList<Integer>();
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("found loops: "+bodySize.size()+"\n");
			
			if (bodySize.size() > 0) {
				int maxBody = 0;
				int avgBody = 0;
				for (int i : bodySize) {
					maxBody = Math.max(maxBody, i);
					avgBody += i;
				}
				avgBody /= bodySize.size();
				sb.append("avg. loop size: "+avgBody+"\n");
				sb.append("max. loop size: "+maxBody+"\n");
			}
			
			return sb.toString();
		}
		
		public String toString_csv() {
			StringBuilder sb = new StringBuilder();
			sb.append("found loops;"+bodySize.size()+";");
			
			if (bodySize.size() > 0) {
				int maxBody = 0;
				int avgBody = 0;
				for (int i : bodySize) {
					maxBody = Math.max(maxBody, i);
					avgBody += i;
				}
				avgBody /= bodySize.size();
				sb.append("avg. loop size;"+avgBody+";");
				sb.append("max. loop size;"+maxBody+";");
			}
			
			return sb.toString();
		}
	}
	
	public RepairStats _stats = new RepairStats();
	
	protected static boolean isValidLoopBody(PluginContext context, PetriNetWithMarkings body, XLog traces, XEventClassifier classifier, double allowedFails) {

		Set<String> eventClasses = new HashSet<String>();
		for (XTrace trace : traces) {
			for (XEvent event : trace) {
				eventClasses.add(classifier.getClassIdentity(event));
			}
		}
		
		Object _net[] = UmaPromUtil.toPromFormat(body, "loop body (net)");
		Petrinet net = (Petrinet)_net[0];
		
		@SuppressWarnings("unchecked")
		Map<hub.top.petrinet.Node, PetrinetNode> nodeMap = (Map<hub.top.petrinet.Node, PetrinetNode>)_net[2];
		
		Marking m_initial = new Marking();
		for (hub.top.petrinet.Place p : body.initialMarking) {
			m_initial.add((Place)nodeMap.get(p));
		}
		InitialMarkingConnection connInit = new InitialMarkingConnection(net, m_initial);
		context.addConnection(connInit);
		
		Marking m_final = new Marking();
		for (hub.top.petrinet.Place p : body.finalMarking) {
			m_final.add((Place)nodeMap.get(p));
		}
		FinalMarkingConnection connFinal = new FinalMarkingConnection(net, m_final);
		context.addConnection(connFinal);
		
		TransEvClassMapping mapping = PNLogReplayer.getEventClassMapping(context, net, traces, classifier);

		PNLogReplayer.ReplayParams params = PNLogReplayer.getReplayerParameters(context, net, m_initial, m_final, traces, classifier);
		for (XEventClass e : params.mapEvClass2Cost.keySet()) {
			params.mapEvClass2Cost.put(e, 1000);
		}
		PNRepResult result = PNLogReplayer.callReplayer(context, net, traces, mapping, params);
		
		Set<String> logMoveEvents = new HashSet<String>();
		
		int numLogMoves = 0;
		for (SyncReplayResult res : result) {
			for (int i=0; i<res.getStepTypes().size(); i++) {
				if (res.getStepTypes().get(i) == StepTypes.L) {
					numLogMoves++;
					logMoveEvents.add( ((XEventClass)res.getNodeInstance().get(i)).getId() );
				}
			}
		}
		double failed = (double)logMoveEvents.size()/eventClasses.size();
		
		System.out.println("loop requires "+numLogMoves+" log moves ("+failed+") to replay");
		
		return failed <= allowedFails;
	}
	
	/**
	 * @param simpleLog
	 * @param logName
	 * @return {@link XLog} representation of the loop hypothesis
	 */
	public static XLog toXLog(Collection<List<MoveOnLog>> loopHypothesis, String logName) {

		// create aligned log
		XFactory f = XFactoryRegistry.instance().currentDefault();

		XLog xLog = f.createLog();

		// log needs a name
		String alignedLogName = logName;
		XAttributeMap logAttr = f.createAttributeMap();
		xLog.setAttributes(logAttr);			

		XConceptExtension.instance().assignName(xLog, alignedLogName);


		int caseNum = 0;

		// create traces in the aligned log (each trace is one trace class from the replay)
		for (List<MoveOnLog> trace : loopHypothesis) {

			// create trace
			XTrace t = f.createTrace();

			// write trace attributes
			XAttributeMap traceAttr = f.createAttributeMap();
			t.setAttributes(traceAttr);
			XConceptExtension.instance().assignName(t, "case"+caseNum);


			// add events to trace
			for (MoveOnLog move : trace) {

				// split name into event name and life-cycle transition
				String qualified_eventName = move.event;
				String name;
				String life_cycle;
				int plus_pos = qualified_eventName.indexOf('+');
				if (plus_pos >= 0) {
					name = qualified_eventName.substring(0, plus_pos);
					life_cycle = qualified_eventName.substring(plus_pos+1);
				} else {
					name = qualified_eventName;
					life_cycle = "complete";
				}

				// write event attributes
				XEvent e = f.createEvent();
				XAttributeMap eventAttr = f.createAttributeMap();
				e.setAttributes(eventAttr);

				XConceptExtension.instance().assignName(e, name);
				XLifecycleExtension.instance().assignTransition(e, life_cycle);

				// add event to trace
				t.add(e);
			}

			// add trace to log
			xLog.add(t);
			caseNum++;
		}
		return xLog;
	}


	protected static Object[] cancel(PluginContext context, String message) {
		System.out.println("[ModelRepair/repair model]: "+message);
		context.log(message);
		context.getFutureResult(0).cancel(true);
		return null;
	}
	
}
