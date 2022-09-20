package org.processmining.plugins.modelrepair;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.collection.AlphanumComparator;
import org.processmining.models.connections.petrinets.EvClassLogPetrinetConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.astar.petrinet.PrefixBasedPetrinetReplayer;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.modelrepair.connectionfactories.EvClassLogPetrinetConnectionFactoryUI_2;
import org.processmining.plugins.petrinet.replayer.PNLogReplayer;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

@Plugin(name = "Align Log to Model",
		parameterLabels = { "a log", "a Petri net"}, //
		returnLabels = { "Aligned Log" },
		returnTypes = { XLog.class }, 
		userAccessible = true,
		help = "Create a 'filtered and extended log' that only contains events that can be replayed on the model and provides events for 'invisible' transitions of the model. Such logs can be used for model simplification in case the log does not fit the model.",
		mostSignificantResult = 1)
public class Uma_AlignLogToModel {
	
	// take log and net as input and guess initial marking
	@UITopiaVariant(
			affiliation="TU/e",
			author="D. Fahland",
			email="d.fahland@tue.nl",
			website = "http://service-technology.org/uma",
			pack="Uma")
	@PluginVariant(variantLabel = "Align Log to Model", requiredParameterLabels = { 0, 1 })
	public XLog alignLogToModel(UIPluginContext context, XLog log, Petrinet net) {
		
		// replay log on model (or obtain existing replay result)
		PNRepResult result;
		try {
			PNLogReplayer replayer = new PNLogReplayer();
			result = replayer.replayLog(context, net, log);
		} catch (ConnectionCannotBeObtained e) {
			return cancel(context, "Could not replay the log on the model. No replayer found.");
		} catch (Exception e) {
			return cancel(context, "Replay failed: "+e.toString());
		}
		return alignLogToModel(context, log, net, result);
	}
	
	// take log and net as input and guess initial marking
	@PluginVariant(variantLabel = "Align Log to Model", requiredParameterLabels = { 0, 1 })
	public XLog alignLogToModel(PluginContext context, XLog log, Petrinet net) {
		
		// replay log on model (or obtain existing replay result)
		PNRepResult result;

		PNLogReplayer replayer = new PNLogReplayer();
		
		EvClassLogPetrinetConnection conn = connect(context, log, net);
		TransEvClassMapping mapping = (TransEvClassMapping)conn.getObjectWithRole(EvClassLogPetrinetConnection.TRANS2EVCLASSMAPPING);
		
		CostBasedCompleteParamProvider_nonUI prov = new CostBasedCompleteParamProvider_nonUI(context, net, log, mapping);
		PrefixBasedPetrinetReplayer alg = new PrefixBasedPetrinetReplayer();
		IPNReplayParameter param = prov.constructReplayParameter(prov.constructUI());
		param.setGUIMode(false);
			
		try {
			result = replayer.replayLog(context, net, log, mapping, alg, param);
		} catch (Exception e) {
			return cancel(context, "Replay failed: "+e.toString());
		}

		return alignLogToModel(context, log, net, result);
	}
	
	public EvClassLogPetrinetConnection connect(PluginContext context, XLog log, Petrinet net) {

		// list possible classifiers
		Object[] availableEventClass = new Object[4];
		availableEventClass[0] = XLogInfoImpl.STANDARD_CLASSIFIER; 
		availableEventClass[1] = XLogInfoImpl.NAME_CLASSIFIER; 
		availableEventClass[2] = XLogInfoImpl.LIFECYCLE_TRANSITION_CLASSIFIER; 
		availableEventClass[3] = XLogInfoImpl.RESOURCE_CLASSIFIER; 
		
		EvClassLogPetrinetConnectionFactoryUI_2 ui = new EvClassLogPetrinetConnectionFactoryUI_2(log, net, availableEventClass);
		// create the connection or not according to the button pressed in the UI
		EvClassLogPetrinetConnection
			con = new EvClassLogPetrinetConnection("Connection between " + net.getLabel() + " and " + XConceptExtension.instance().extractName(log), net, log, ui.getSelectedClassifier(), ui.getMap());
		return con;
	}
	
	private XLog alignLogToModel(PluginContext context, XLog log, Petrinet net, PNRepResult result) {
		
		// create aligned log
		XFactory f = XFactoryRegistry.instance().currentDefault();
		
		XLog alignedLog = f.createLog();
		
		// log needs a name
		String alignedLogName = log.getAttributes().get("concept:name").toString()+" (aligned)";
		XAttributeMap logAttr = f.createAttributeMap();
		logAttr.put("concept:name",
					   f.createAttributeLiteral("concept:name", alignedLogName, XConceptExtension.instance()));
		alignedLog.setAttributes(logAttr);
		
		// create traces in the aligned log (each trace is one trace class from the replay)
		for (SyncReplayResult res : result) {
		
			// collect event order as determined by replayer
			ArrayList<Object> alignedEvents = new ArrayList<Object>();
			for (Object event : res.getNodeInstance()) {
				if (event instanceof Transition) {
					alignedEvents.add(((Transition) event).getLabel());
				} else if (event instanceof String) {
					alignedEvents.add(event);
				} else {
					alignedEvents.add(event.toString());
				}
			}
			
			// to preserve frequencies of the original log, create a separate copy
			// for each trace in the trace class: collect all caseIDs in the class
			SortedSet<String> caseIDs = new TreeSet<String>(new AlphanumComparator());
			XConceptExtension ce = XConceptExtension.instance();
			for (int index : res.getTraceIndex()) {
				caseIDs.add(ce.extractName(log.get(index)));
			}
			
			for (String caseID : caseIDs) {
			
				// create trace
				XTrace t = f.createTrace();
				
				// write trace attributes
				XAttributeMap traceAttr = f.createAttributeMap();
				traceAttr.put("concept:name",
							   f.createAttributeLiteral("concept:name", caseID, XConceptExtension.instance()));
				t.setAttributes(traceAttr);
				
				// add events to trace
				for (int i=0; i<alignedEvents.size(); i++) {
					// skip log events that cannot be replayed on the model
					if (res.getStepTypes().get(i) == org.processmining.plugins.petrinet.replayresult.StepTypes.L) continue;
					if (res.getStepTypes().get(i) == org.processmining.plugins.petrinet.replayresult.StepTypes.LMNOGOOD) continue;
					
					// split name into event name and life-cycle transition
					String qualified_eventName = alignedEvents.get(i).toString();
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
					eventAttr.put("concept:name",
								   f.createAttributeLiteral("concept:name", name, XConceptExtension.instance())); 
					eventAttr.put("lifecycle:transition",
							   f.createAttributeLiteral("lifecycle:transition", life_cycle, XLifecycleExtension.instance()));
					e.setAttributes(eventAttr);
					
					// add event to trace
					t.add(e);
				}
			
				// add trace to log
				alignedLog.add(t);
			}
		}
		
  		context.getFutureResult(0).setLabel(alignedLogName);
		return alignedLog;
	}

	protected static XLog cancel(PluginContext context, String message) {
		System.out.println("[Uma/align log to model]: "+message);
		context.log(message);
		context.getFutureResult(0).cancel(true);
		return null;
	}
}
